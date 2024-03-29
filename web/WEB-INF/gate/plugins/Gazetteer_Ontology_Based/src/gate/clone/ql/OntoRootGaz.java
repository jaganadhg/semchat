/*
 * OntoRootGaz.java
 * 
 * Copyright (c) 1998-2008, The University of Sheffield.
 * 
 * This file is part of GATE (see http://gate.ac.uk/), and is free software,
 * licenced under the GNU Library General Public License, Version 2, June 1991
 * (in the distribution as file licence.html, and also available at
 * http://gate.ac.uk/gate/licence.html).
 */
package gate.clone.ql;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import gate.Annotation;
import gate.Corpus;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.Resource;
import gate.clone.ql.CATConstants;
import gate.clone.ql.FakeSentenceSplitter;
import gate.clone.ql.Ontology2MapManager;
import gate.clone.ql.regex.ExpressionFinder;
import gate.creole.ANNIEConstants;
import gate.creole.ExecutionException;
import gate.creole.POSTagger;
import gate.creole.ResourceInstantiationException;
import gate.creole.SerialAnalyserController;
import gate.creole.gazetteer.DefaultGazetteer;
import gate.creole.gazetteer.FSMState;
import gate.creole.gazetteer.LinearDefinition;
import gate.creole.gazetteer.Lookup;
import gate.creole.morph.Morph;
import gate.creole.ontology.*;
import gate.creole.tokeniser.DefaultTokeniser;
import gate.util.OffsetComparator;

/**
 * 
 * @author Valentin Tablan, Danica Damljanovic
 * 
 */
public class OntoRootGaz extends DefaultGazetteer {
  private static final long serialVersionUID = 0L;

  protected POSTagger posTagger;

  protected DefaultTokeniser tokeniser;

  protected FakeSentenceSplitter sentenceSplitter;

  protected Morph morpher;

  protected SerialAnalyserController rootFinderApplication;

  protected OffsetComparator offsetComparator;

  protected Ontology ontology;

  /**
   * should camelCased words be separated so that projectName becomes project
   * Name
   */
  protected Boolean separateCamelCasedWords;

  /**
   * should resource URI (usually called a fragment identifier - a set of
   * characters after / or #) be considered; for example, if there is a resource
   * with URI http://gate.ac.uk/ns/gate-ontology#POSTagger, should POSTagger be
   * considered or not
   */
  protected Boolean useResourceUri;

  /**
   * should properties be considered or not; NOTE: if this parameter is set to
   * false, than propertiesToInlcude and propertiesToExclude will be ignored
   */
  protected Boolean considerProperties;

  /**
   * a list of lookups that will be created after processing of all relevant
   * data
   */
  protected List<Lookup> allLookups = new ArrayList<Lookup>();

  protected Corpus applicationCorpus;

  /**
   * a map of roots: a key is a lookup.list value, e.g. 'projects', and the
   * value is a root of that key, in this case that would be 'project'
   */
  Map<String, String> listRoots = new HashMap<String, String>();

  /**
   * Should the rules be followed or not: if true then, few heuristic rules will
   * apply: the words containing spaces will be split; for example, if 'pos
   * tagger for spanish' would be analysed, 'for' would be considered a stop
   * word and heuristically derived would be 'pos tagger' and this would be
   * further used to add 'pos tagger' with heuristical level 0, and 'tagger'
   * with hl 1 to the gazetteer list; at runtime lower heuristical level should
   * be prefered
   */
  protected Boolean considerHeuristicRules;

  /**
   * comma separated values of property names that will be considered when
   * initializing the gazetteer
   */
  protected String propertiesToInclude;

  /**
   * comma separated values of property names that will be excluded when
   * initializing the gazetteer NOTE: setting propertiesToInclude to be
   * different from "" automatically means that all properties not in the list
   * will be excluded (in other words, if propertiesToInclude is set, it is not
   * necessary to set propertiesToExclude as all properties not listed in
   * propertiesToInclude will be excluded);
   */
  protected String propertiesToExclude;

  /*****************************************************************************
   * setting logger to log entries to the gazetteer list
   ****************************************************************************/
  private static Logger logger = null;
  static {
    logger = Logger.getLogger("OntoRootGaz");
    logger.setUseParentHandlers(false);
    File logDir = null;
    // find the top directory
    String classFileName = OntoRootGaz.class.getCanonicalName();
    classFileName = classFileName.replace('.', '/');
    classFileName = "/" + classFileName + ".class";
    URL classUrl = OntoRootGaz.class.getResource(classFileName);
    if(classUrl.getProtocol().equalsIgnoreCase("jar")) {
      String pathStr = classUrl.getPath();
      pathStr = pathStr.substring(0, pathStr.indexOf('!'));
      File jarFile = null;
      try {
        jarFile = new File(new URL(pathStr).getPath());
      } catch(MalformedURLException e) {
        e.printStackTrace();
      }
      File jarDir = new File(jarFile.getParent());
      logDir = new File(jarDir, "logs");
    }
    if(logDir != null) {
      if(!logDir.exists()) logDir.mkdirs();
      try {
        FileHandler logHandler =
                new FileHandler(logDir.getCanonicalPath() + "/"
                        + OntoRootGaz.class.getSimpleName() + "-%u.log", false);
        logHandler.setFormatter(new Formatter() {
          /**
           * This method format log record to show *message only*
           */
          public String format(LogRecord record) {
            return record.getMessage();
          }
        });
        logHandler.setLevel(Level.ALL);
        // add the new file handler for everything
        logger.addHandler(logHandler);
        // add the handler for Output messages
        ConsoleHandler outHandler = new ConsoleHandler();
        outHandler.setLevel(Level.parse(CATConstants.LOGGER_OUPUT_LEVEL));
        outHandler.setFormatter(new Formatter() {
          /**
           * This method format log record to show *message only*
           */
          public String format(LogRecord record) {
            return record.getMessage();
          }
        });
        logger.addHandler(outHandler);
      } catch(SecurityException e) {
        e.printStackTrace();
      } catch(IOException e) {
        e.printStackTrace();
      }
    }
    /***************************************************************************
     * end setting the logger
     **************************************************************************/
  }

  public void reInit() throws ResourceInstantiationException {
    this.init();
  }

  public Resource init() throws ResourceInstantiationException {
    
    //list of namespaces to be ignored when creating gazetteer list
    List<String> nsToIgnore = new ArrayList<String>();
    nsToIgnore.add("http://www.w3.org/2002/07/owl#");
    nsToIgnore.add("http://www.w3.org/2000/01/rdf-schema#");
    nsToIgnore.add("http://www.w3.org/1999/02/22-rdf-syntax-ns#");
    
    logger.info("--------------------------------------\n");
    logger.info(" Initializing gazetteer for ontology from location:\n");
    logger.info(ontology.getURL().toString());
    logger.info("--------------------------------------\n");
    long startedInit = System.currentTimeMillis();
    List<String> propertiesToIncludeList = new ArrayList<String>();
    List<String> propertiesToExcludeList = new ArrayList<String>();
    if(tokeniser == null)
      throw new ResourceInstantiationException("No tokeniser provided!");
    if(sentenceSplitter == null) {
      sentenceSplitter =
              (FakeSentenceSplitter)Factory
                      .createResource("gate.clone.ql.FakeSentenceSplitter");
    }
    if(posTagger == null)
      throw new ResourceInstantiationException(
              "No Part-of-speach Tagger provided!");
    if(morpher == null)
      throw new ResourceInstantiationException(
              "No Morphological Analyzer provided!");
    if(ontology == null) {
      throw new ResourceInstantiationException("No ontology provided!");
    } else {
      Ontology2MapManager.getInstance().addOntologyToIndex(ontology);
    }
    /* set default values if they are not set already */
    if(this.useResourceUri == null) useResourceUri = true;
    if(considerProperties == null) considerProperties = true;
    if(separateCamelCasedWords == null) separateCamelCasedWords = true;
    if(considerHeuristicRules == null) considerHeuristicRules = false;
    fsmStates = new HashSet();
    initialState = new FSMState(this);
    /* set the hidden feature to true */
    FeatureMap features = Factory.newFeatureMap();
    FeatureMap parameters = Factory.newFeatureMap();
    Gate.setHiddenAttribute(features, true);
    rootFinderApplication =
            (SerialAnalyserController)Factory.createResource(
                    "gate.creole.SerialAnalyserController", parameters,
                    features);
    rootFinderApplication.add(tokeniser);
    rootFinderApplication.add(sentenceSplitter);
    rootFinderApplication.add(posTagger);
    rootFinderApplication.add(morpher);
    /* create a corpus and hide it inside the GATE GUI */
    FeatureMap corpusParams = Factory.newFeatureMap();
    corpusParams.put("name", this.getClass().getCanonicalName());
    FeatureMap corpusFeatures = Factory.newFeatureMap();
    Gate.setHiddenAttribute(corpusFeatures, true);
    applicationCorpus =
            (Corpus)Factory.createResource("gate.corpora.CorpusImpl",
                    corpusParams, corpusFeatures);
    rootFinderApplication.setCorpus(applicationCorpus);
    offsetComparator = new OffsetComparator();
    /*
     * move properties to include and exclude from the list of CSV to the actual
     * List objects
     */
    if(considerProperties && propertiesToInclude != null
            && propertiesToExclude != null) {
      String[] listInclude = propertiesToInclude.split(",");
      for(String item : listInclude) {
        if(!"".equals(item.trim())) propertiesToIncludeList.add(item.trim());
      }
      String[] listExclude = propertiesToExclude.split(",");
      for(String item : listExclude) {
        if(!"".equals(item.trim())) propertiesToExcludeList.add(item.trim());
      }
    }
    /*
     * check validity: if a property is in both 'to be excluded' and 'to be
     * included' list throw an exception
     */
    if(propertiesToExcludeList.size() > 0 && propertiesToIncludeList.size() > 0) {
      for(String propertyUri : propertiesToExcludeList) {
        if(propertiesToIncludeList.contains(propertyUri))
          throw new ResourceInstantiationException(
                  "You specified that the same property should be both included and "
                          + "excluded!");
      }
    }
    if(considerProperties) {
      /*************************************************************************
       * instances with all set properties returned in a table with 3 columns:
       * ... instanceUri, propertyUri, propertyValue [new line] instanceUri,
       * propertyUri, propertyValue [new line] ...
       ************************************************************************/
      String[] rows =
              Ontology2MapManager.getInstance().getOntology2Map(
                      ontology.getURL().toString()).getListOfInstances().split(
                      CATConstants.NEW_LINE);
      for(String eachRow : rows) {
        String[] columns = eachRow.split("\\|");
        if(columns.length == 3) {
          String uri = columns[0].trim();
          try {
            /* create uriURI for validation purposes */
            URI uriUri = new URI(uri, false);
            String propUri = columns[1].trim();
            if((propertiesToIncludeList.size() == 0 || propertiesToIncludeList
                    .contains(propUri))
                    && (propertiesToExcludeList.size() == 0 || !(propertiesToExcludeList
                            .contains(propUri)))) {
              if(!nsToIgnore.contains(uriUri.getNameSpace())) {
                String propValue = columns[2].trim();
                Map<String, Object> lookupFeatures =
                        new HashMap<String, Object>();
                lookupFeatures.put(CATConstants.ONTORES_TYPE,
                        CATConstants.TYPE_INSTANCE);
                lookupFeatures.put(CATConstants.FEATURE_URI, uri);
                lookupFeatures.put(CATConstants.FEATURE_PROPERTY_URI, propUri);
                lookupFeatures.put(CATConstants.FEATURE_PROPERTY_VALUE,
                        propValue);
                lookupFeatures.put(CATConstants.CLASS_URI_LIST,
                        Ontology2MapManager.getInstance().getOntology2Map(
                                ontology.getURL().toString())
                                .getInstanceTypes().get(uri));
                lookupFeatures.put(CATConstants.CLASS_URI,
                        new ArrayList<String>(Ontology2MapManager.getInstance()
                                .getOntology2Map(ontology.getURL().toString())
                                .getInstanceTypes().get(uri)).get(0));
                Lookup aLookup = new Lookup(propValue, "", null, null);
                aLookup.features = lookupFeatures;
                allLookups.add(aLookup);
              }// if uri is in the list of ignored namespaces: nsToIgnore
            }// end if propertiesToIncludeList==0 ...
          } catch(InvalidURIException e) {
            logger.info("URI:'" + uri + "' is not valid. Skipping...\n");
          }
        }
      }
      /*************************************************************************
       * classes with all set properties returned in a table with 3 columns:
       * classUri, propertyUri, propertyValue
       * ************************************************************ *
       ************************************************************************/
      rows =
              Ontology2MapManager.getInstance().getOntology2Map(
                      ontology.getURL().toString()).getListOfClasses().split(
                      CATConstants.NEW_LINE);
      for(String eachRow : rows) {
        String[] columns = eachRow.split("\\|");
        if(columns.length == 3) {
          String uri = columns[0].trim();
          try {
            URI uriUri = new URI(uri, false);
            String propUri = columns[1].trim();
            if((propertiesToIncludeList.size() == 0 || propertiesToIncludeList
                    .contains(propUri))
                    && (propertiesToExcludeList.size() == 0 || !(propertiesToExcludeList
                            .contains(propUri)))) {
              if(!nsToIgnore.contains(uriUri.getNameSpace())) {
                String propValue = columns[2].trim();
                Map<String, Object> lookupFeatures =
                        new HashMap<String, Object>();
                lookupFeatures.put(CATConstants.ONTORES_TYPE,
                        CATConstants.TYPE_CLASS);
                lookupFeatures.put(CATConstants.FEATURE_URI, uri);
                lookupFeatures.put(CATConstants.FEATURE_PROPERTY_URI, propUri);
                Lookup aLookup = new Lookup(propValue, "", null, null);
                aLookup.features = lookupFeatures;
                allLookups.add(aLookup);
              }// end if propertiesToIncludeList==0 ...
            }// if uri is in the list of ignored namespaces: nsToIgnore
          } catch(InvalidURIException e) {
            logger.info("URI:'" + uri + "' is not valid.\n");
          }
        }
      }
      /*************************************************************************
       * properties with all set properties returned in a table with 3 columns:
       * propertyUri, setPropertyUri, propertyValue
       * ************************************************************ *
       ************************************************************************/
      rows =
              Ontology2MapManager.getInstance().getOntology2Map(
                      ontology.getURL().toString()).getListOfProperties()
                      .split(CATConstants.NEW_LINE);
      for(String eachRow : rows) {
        String[] columns = eachRow.split("\\|");
        if(columns.length == 3) {
          String uri = columns[0].trim();
          try {
            URI uriUri = new URI(uri, false);
            String propUri = columns[1].trim();
            if((propertiesToIncludeList.size() == 0 || propertiesToIncludeList
                    .contains(propUri))
                    && (propertiesToExcludeList.size() == 0 || !(propertiesToExcludeList
                            .contains(propUri)))) {
              if(!nsToIgnore.contains(uriUri.getNameSpace())) {
                String propValue = columns[2].trim();
                Map<String, Object> lookupFeatures =
                        new HashMap<String, Object>();
                lookupFeatures.put(CATConstants.ONTORES_TYPE,
                        CATConstants.TYPE_PROPERTY);
                lookupFeatures.put(CATConstants.FEATURE_URI, uri);
                lookupFeatures.put(CATConstants.FEATURE_PROPERTY_URI, propUri);
                lookupFeatures.put(CATConstants.FEATURE_PROPERTY_VALUE,
                        propValue);
                Lookup aLookup = new Lookup(propValue, "", null, null);
                aLookup.features = lookupFeatures;
                allLookups.add(aLookup);
              }// end if propertiesToIncludeList==0 ...
            }
          } catch(InvalidURIException e) {
            logger.info("URI:'" + uri + "' is not valid.\n");
          }
        }
      }
    }// end consider properties
    /* uri retrieval */
    if(useResourceUri) {
      /*************************************************************************
       * class uris
       ************************************************************************/
      String[] rows =
              Ontology2MapManager.getInstance().getOntology2Map(
                      ontology.getURL().toString()).getClassURIs().split(
                      CATConstants.NEW_LINE);
      for(String eachRow : rows) {
        String uri = eachRow.trim();
        try {
          URI uriUri = new URI(uri, false);
          String shortName = uriUri.getResourceName();
          if(!nsToIgnore.contains(uriUri.getNameSpace())) {
            Map<String, Object> lookupFeatures = new HashMap<String, Object>();
            lookupFeatures.put(CATConstants.ONTORES_TYPE,
                    CATConstants.TYPE_CLASS);
            lookupFeatures.put(CATConstants.FEATURE_URI, uri.trim());
            Lookup aLookup = new Lookup(shortName, "", null, null);
            aLookup.features = lookupFeatures;
            allLookups.add(aLookup);
          }
        } catch(InvalidURIException e) {
          logger.info("URI:" + uri + " is not valid.\n");
        }
      }
      /*************************************************************************
       * instance uris
       ************************************************************************/
      Set<String> setOfInstanceTypes =
              Ontology2MapManager.getInstance().getOntology2Map(
                      ontology.getURL().toString()).getInstanceTypes().keySet();
      for(String uri : setOfInstanceTypes) {
        try {
          URI uriUri = new URI(uri, false);
          String shortName = uriUri.getResourceName();
          if(!nsToIgnore.contains(uriUri.getNameSpace())) {
            Map<String, Object> lookupFeatures = new HashMap<String, Object>();
            lookupFeatures.put(CATConstants.ONTORES_TYPE,
                    CATConstants.TYPE_INSTANCE);
            lookupFeatures.put(CATConstants.FEATURE_URI, uri);
            Set<String> l =
                    Ontology2MapManager.getInstance().getOntology2Map(
                            ontology.getURL().toString()).getInstanceTypes()
                            .get(uri);
            lookupFeatures.put(CATConstants.CLASS_URI_LIST, l);
            lookupFeatures.put(CATConstants.CLASS_URI, new ArrayList<String>(l)
                    .get(0));
            Lookup aLookup = new Lookup(shortName, "", null, null);
            aLookup.features = lookupFeatures;
            allLookups.add(aLookup);
          }
        } catch(InvalidURIException e) {
          logger.info("URI:" + uri + " is not valid.\n");
        }
      }
      /*************************************************************************
       * property uris
       ************************************************************************/
      rows =
              Ontology2MapManager.getInstance().getOntology2Map(
                      ontology.getURL().toString()).getPropertyURIs().split(
                      CATConstants.NEW_LINE);
      for(String eachRow : rows) {
        String uri = eachRow.trim();
        try {
          URI uriUri = new URI(uri, false);
          String shortName = uriUri.getResourceName();
          if(!nsToIgnore.contains(uriUri.getNameSpace())) {
            Map<String, Object> lookupFeatures = new HashMap<String, Object>();
            lookupFeatures.put(CATConstants.ONTORES_TYPE,
                    CATConstants.TYPE_PROPERTY);
            lookupFeatures.put(CATConstants.FEATURE_URI, uri);
            Lookup aLookup = new Lookup(shortName, "", null, null);
            aLookup.features = lookupFeatures;
            allLookups.add(aLookup);
          }
        } catch(InvalidURIException e) {
          logger.info("URI:" + uri + " is not valid.\n");
        }
      }
    }
    addLookups(allLookups);
    allLookups = new ArrayList<Lookup>();
    /* release GATE resources */
    Factory.deleteResource(applicationCorpus);
    applicationCorpus = null;
    rootFinderApplication.remove(morpher);
    rootFinderApplication.remove(posTagger);
    rootFinderApplication.remove(sentenceSplitter);
    Factory.deleteResource(sentenceSplitter);
    sentenceSplitter = null;
    rootFinderApplication.remove(tokeniser);
    Factory.deleteResource(rootFinderApplication);
    rootFinderApplication = null;
    long currentTime = System.currentTimeMillis();
    logger.info("OntoRootGaz initialized for:" + (currentTime - startedInit)
            + " ms");
    return this;
  }

  /**
   * This method takes a list of lookups as a parameter, process them and
   * returns a list of new Lookups that are than added to the gazetteer.
   * 'Processing' means replacing lookup.list feature with its root.
   * Additionally during the processing a new list if Lookups is created called
   * additionalList: this list contains a new Lookups that needs to be processed
   * by calling this method again afterwards: - if lookup.list contains "-" or
   * "_", replace these chars by space, add new lookups to the additionalList
   * and then extract the root in the next call to this method - if
   * separateCamelCasedWords=true, separate them by adding a space, add new
   * lookups to the additionalList and then extract the root later - if
   * considerHeuristicRules=true then separate words as proposed by these rules,
   * add new lookups to the additionalList and then extract the root later
   * 
   * @param List
   *          <Lookup> lookups
   * @throws ResourceInstantiationException
   */
  protected void addLookups(List<Lookup> lookups)
          throws ResourceInstantiationException {
    List<Lookup> lookupsToBeAdded = runRootFinderApplication(lookups);
    List<Lookup> additionalListTemp = new ArrayList<Lookup>();
    additionalListTemp.addAll(additionalList);
    additionalList = new ArrayList<Lookup>();
    List<Lookup> addition = runRootFinderApplication(additionalListTemp);
    List<Lookup> all = new ArrayList<Lookup>();
    all.addAll(lookupsToBeAdded);
    all.addAll(addition);
    for(Lookup aLookup : all) {
      String root = listRoots.get(aLookup.list);
      int hLevel = 0;
      if(root != null) {
        /*
         * check if the root has spaces and if considerHeuristicRules is set to
         * true, if yes, than split words and add heuristical_level to each
         */
        if(root.contains(" ") && considerHeuristicRules == true) {
          Lookup aNewLookup = new Lookup(aLookup.list, "", null, null);
          Map<String, Object> newFeatures = new HashMap<String, Object>();
          for(Object key : aLookup.features.keySet()) {
            newFeatures.put((String)key, aLookup.features.get(key));
          }
          aNewLookup.features = newFeatures;
          aNewLookup.features.put(CATConstants.FEATURE_HEURISTIC_LEVEL, hLevel);
          aNewLookup.features.put(CATConstants.FEATURE_HEURISTIC_VALUE, root);
          addLookup(root.trim(), aNewLookup);
          logger.info("NEW ENTRY: " + root + "\n");
          int firstIndex = root.trim().indexOf(" ");
          String newRoot = root.trim();
          while(firstIndex >= 0) {
            newRoot = newRoot.substring(firstIndex + 1, newRoot.length());
            hLevel++;
            Lookup anotherLookup = new Lookup(aLookup.list, "", null, null);
            Map<String, Object> anotherFeatures = new HashMap<String, Object>();
            for(Object key : aLookup.features.keySet()) {
              anotherFeatures.put((String)key, aLookup.features.get(key));
            }
            anotherLookup.features = anotherFeatures;
            anotherLookup.features.put(CATConstants.FEATURE_HEURISTIC_LEVEL,
                    hLevel);
            anotherLookup.features.put(CATConstants.FEATURE_HEURISTIC_VALUE,
                    newRoot.trim());
            addLookup(newRoot.trim(), anotherLookup);
            logger.info("NEW ENTRY: " + newRoot + "\n");
            firstIndex = newRoot.trim().indexOf(" ");
          }
        } else {// if it doesn't have spaces or
          // considerHeuristicRules=false
          aLookup.features.put(CATConstants.FEATURE_HEURISTIC_LEVEL, 0);
          addLookup(root.trim(), aLookup);
          logger.info("NEW ENTRY: " + root + "\n");
        }
      } else {
        logger.info("root is null for lookup:" + aLookup);
      }
    }
  }

  /*
   * this list is populated during the processing of all lookups, when some
   * entries have multiple interpretations; for example, when processing
   * Project-Name, 'Project-Name' would be added in the first iteration, while
   * 'Project Name' would be added to the additionalList for later processing
   */
  List<Lookup> additionalList = new ArrayList<Lookup>();

  /**
   * This method process given lookups so that their entries are converted to
   * the root of the entry i.e. lookup.list is processed and 'root' feature is
   * used to be lookup.list for resulting lookups. All unprocessed lookups are
   * added to the additionalList and they are processed later with the same
   * method
   */
  private List<Lookup> runRootFinderApplication(List<Lookup> lookups)
          throws ResourceInstantiationException {
    List<Lookup> lookupsToBeReturned = new ArrayList<Lookup>();
    for(Lookup lookup : lookups) {
      String list = lookup.list;
      if(list != null && list.trim().length() > 0) {
        if(list.contains("_")) {
          String newText = list.replace('_', ' ');
          Lookup aLookup = new Lookup(newText, "", null, null);
          aLookup.features = lookup.features;
          additionalList.add(aLookup);
        }
        // if text is camel cased add space between words
        if(separateCamelCasedWords && list.indexOf(" ") < 0) {
          String separatedCamelCase =
                  ExpressionFinder.findAndSeparateCamelCases(list,
                          CATConstants.REGEX_CAMEL_CASE, " ");
          if(list != null && (!list.equals(separatedCamelCase))) {
            Lookup aLookup = new Lookup(separatedCamelCase, "", null, null);
            aLookup.features = lookup.features;
            additionalList.add(aLookup);
          }
        }
        lookupsToBeReturned.add(lookup);
        /* set new documents to be hidden inside the GATE GUI */
        FeatureMap docParams = Factory.newFeatureMap();
        docParams.put("stringContent", list);
        FeatureMap docFeatures = Factory.newFeatureMap();
        Gate.setHiddenAttribute(docFeatures, true);
        Document aDocument = null;
        try {
          aDocument =
                  (Document)Factory.createResource("gate.corpora.DocumentImpl",
                          docParams, docFeatures);
          applicationCorpus.add(aDocument);
          rootFinderApplication.execute();
        } catch(ExecutionException ee) {
          throw new ResourceInstantiationException(ee);
        }
        Iterator it = applicationCorpus.iterator();
        while(it.hasNext()) {
          Document doc = (Document)it.next();
          Set<String> tokenTypes = new HashSet<String>();
          tokenTypes.add(ANNIEConstants.TOKEN_ANNOTATION_TYPE);
          tokenTypes.add(ANNIEConstants.SPACE_TOKEN_ANNOTATION_TYPE);
          List<Annotation> tokenList =
                  new ArrayList<Annotation>(aDocument.getAnnotations().get(
                          tokenTypes));
          Collections.sort(tokenList, offsetComparator);
          StringBuffer rootForText = new StringBuffer("");
          boolean lastAnnWasSpace = false;
          for(Annotation ann : tokenList) {
            if(ann.getType().equals(ANNIEConstants.TOKEN_ANNOTATION_TYPE)) {
              lastAnnWasSpace = false;
              String category =
                      (String)ann.getFeatures().get(
                              ANNIEConstants.TOKEN_CATEGORY_FEATURE_NAME);
              /*
               * category "IN" means it is a preposition, and these are used to
               * be a stop words, so crop everything afterwards, but ONLY if
               * parameter considerHeuristicRules is set to be true
               */
              if(considerHeuristicRules == true && category.equals("IN")) {
                break;
              } else {
                String root = (String)ann.getFeatures().get("root");
                if(root != null) {
                  rootForText.append(root);
                } else {
                  throw new ResourceInstantiationException(
                          "No root found for annotation " + ann.toString());
                }
              }
            } else if(ann.getType().equals(
                    ANNIEConstants.SPACE_TOKEN_ANNOTATION_TYPE)) {
              if(!lastAnnWasSpace) {
                rootForText.append(' ');
              }
              lastAnnWasSpace = true;
            } else {
              // malfunction
              throw new ResourceInstantiationException(
                      "Invalid annotation type: " + ann);
            }
          }
          listRoots.put(doc.getContent().toString(), rootForText.toString());
        }
        applicationCorpus.clear();
        Factory.deleteResource(aDocument);
        aDocument = null;
      }
    }
    return lookupsToBeReturned;
  }

  public Morph getMorpher() {
    return morpher;
  }

  public void setMorpher(Morph morpher) {
    this.morpher = morpher;
  }

  public POSTagger getPosTagger() {
    return posTagger;
  }

  public void setPosTagger(POSTagger posTagger) {
    this.posTagger = posTagger;
  }

  public DefaultTokeniser getTokeniser() {
    return tokeniser;
  }

  public void setTokeniser(DefaultTokeniser tokeniser) {
    this.tokeniser = tokeniser;
  }

  public Ontology getOntology() {
    return ontology;
  }

  public void setOntology(Ontology ontology) {
    this.ontology = ontology;
  }

  public Boolean getConsiderProperties() {
    return considerProperties;
  }

  public void setConsiderProperties(Boolean considerProperties) {
    this.considerProperties = considerProperties;
  }

  public Boolean getUseResourceUri() {
    return useResourceUri;
  }

  public void setUseResourceUri(Boolean useResourceUri) {
    this.useResourceUri = useResourceUri;
  }

  /**
   * @return the separateCamelCasedWords
   */
  public Boolean getSeparateCamelCasedWords() {
    return separateCamelCasedWords;
  }

  /**
   * @param separateCamelCasedWords
   *          the separateCamelCasedWords to set
   */
  public void setSeparateCamelCasedWords(Boolean separateCamelCasedWords) {
    this.separateCamelCasedWords = separateCamelCasedWords;
  }

  /**
   * @return the propertiesToExclude
   */
  public String getPropertiesToExclude() {
    return propertiesToExclude;
  }

  /**
   * @param propertiesToExclude
   *          the propertiesToExclude to set
   */
  public void setPropertiesToExclude(String propertiesToExclude) {
    this.propertiesToExclude = propertiesToExclude;
  }

  /**
   * @return the propertiesToInclude
   */
  public String getPropertiesToInclude() {
    return propertiesToInclude;
  }

  /**
   * @param propertiesToInclude
   *          the propertiesToInclude to set
   */
  public void setPropertiesToInclude(String propertiesToInclude) {
    this.propertiesToInclude = propertiesToInclude;
  }

  /**
   * 
   * @return
   */
  public Boolean getConsiderHeuristicRules() {
    return considerHeuristicRules;
  }

  /**
   * 
   * @param considerHeuristicRules
   */
  public void setConsiderHeuristicRules(Boolean considerHeuristicRules) {
    this.considerHeuristicRules = considerHeuristicRules;
  }

  /**
   * Gets the linear definition of the gazetteer. This method is added so that
   * Gaze does not complain when rendering views and showing initialisation
   * parameters.
   * 
   * @return the linear definition of the gazetteer
   */
  public LinearDefinition getLinearDefinition() {
    return new LinearDefinition();
  }
}
