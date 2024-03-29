/*
 *  Copyright (c) 1995-2010, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Johann Petrak 2009-08-13
 *
 *  $Id: OWLIMOntology.java 12067 2009-12-08 13:34:15Z ian_roberts $
 */
package gate.creole.ontology.impl.sesame;

import gate.Gate;
import gate.Resource;
import gate.creole.ResourceData;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.Optional;
import gate.creole.ontology.OConstants;
import gate.creole.ontology.OConstants.OntologyFormat;

import java.io.File;
import java.io.IOException;
import java.net.URL;


import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;


/**
 * This ontology LR allows the creation of an ontology backed by a local
 * Sesame OWLIM repository. The ontology can be optionally loaded with 
 * an initial ontology at creation time and can be created as either
 * persistent or not persistent. If the ontology is created with the 
 * persistent parameter set to true, the directory created to
 * hold all the ontology data will be kept after the ontology is closed.
 * The ontology can be reused later by connecting to it with ConnectSesameOntology
 * LR specifying the directory that was created and the repository id "owlim3".
 * 
 * @author Johann Petrak
 * 
 */
@CreoleResource(
    name = "OWLIM Ontology",
    interfaceName = "gate.creole.ontology.Ontology",
    comment = "Ontology created as a temporary OWLIM3 in-memory repository",
    icon = "ontology",
    helpURL = "http://gate.ac.uk/userguide/sec:ontologies:ontoplugin:owlim")
public class OWLIMOntology 
    extends OntologyLR
  {

  private static final long serialVersionUID = 1L;

  
  @CreoleParameter(comment="",defaultValue="false")
  public void setPersistent(Boolean persistent) {
    isPersistent = persistent;
  }
  public Boolean getPersistent() {
    return isPersistent;
  }
  protected Boolean isPersistent = false;

  @Optional
  @CreoleParameter(comment="",disjunction="url")
  public void setRdfXmlURL(URL theURL) {
    rdfXmlURL = theURL;
  }
  public URL getRdfXmlURL() {
    return rdfXmlURL;
  }
  protected URL rdfXmlURL;

  @Optional
  @CreoleParameter(comment="",disjunction="url")
  public void setN3URL(URL theURL) {
    n3URL = theURL;
  }
  public URL getN3URL() {
    return n3URL;
  }
  protected URL n3URL;

  @Optional
  @CreoleParameter(comment="",disjunction="url")
  public void setNtriplesURL(URL theURL) {
    ntriplesURL = theURL;
  }
  public URL getNtriplesURL() {
    return ntriplesURL;
  }
  protected URL ntriplesURL;

  @Optional
  @CreoleParameter(comment="",disjunction="url")
  public void setTurtleURL(URL theURL) {
    turtleURL = theURL;
  }
  public URL getTurtleURL() {
    return turtleURL;
  }
  protected URL turtleURL;

  @Optional
  @CreoleParameter(comment="Directory that should contain the repository director")
  /**
   * Set the name of the directory in which the directory "storage-folder"
   * which contains the ontology repository data will be created.
   * If the directory does not exist but its parent exists, it will be 
   * created.
   */
  public void setDataDirectoryURL(URL dataDirectoryURL) {
    this.dataDirectoryURL = dataDirectoryURL;
  }
  public URL getDataDirectoryURL() {
    return dataDirectoryURL;
  }
  protected URL dataDirectoryURL;

  @Optional
  @CreoleParameter(
      comment="Ontology base URI, default is http://gate.ac.uk/dummybaseuri#"
      )
  public void setBaseURI(String theURI) {
    baseURI = theURI;
  }
  public String getBaseURI() {
    return baseURI;
  }
  protected String baseURI;

  @Optional
  @CreoleParameter(
      comment="The URL of a file containing mappings between ontology import URIs and URLs or blank"
      )
  public void setMappingsURL(URL theMappings) {
    mappingsURL = theMappings;
  }
  public URL getMappingsURL() {
    return mappingsURL;
  }
  protected URL mappingsURL;

  @CreoleParameter(
      comment="If the ontology imports specified in the ontology should get automatically loaded",
      defaultValue = "true")
  public void setLoadImports(Boolean doit) {
    loadImports = doit;
  }
  public Boolean getLoadImports() {
    return loadImports;
  }
  protected Boolean loadImports;

  /* this does not seem to work?
  @CreoleParameter(
      comment="The format of the ontology file to load",
      defaultValue="rdfxml")
  public void setOntologyFileFormat(OntologyFormat theFormat) {
    ontologyFileFormat = theFormat;
  }
  public OntologyFormat getOntologyFileFormat() {
    return ontologyFileFormat;
  }
  private OntologyFormat ontologyFileFormat;
   * */

  private File dataDirectory;
  private File storageFolderDir;

  protected Logger logger;
  /**
   * Constructor
   */
  public OWLIMOntology()  {
    super();
    //logger = initLogger(this.getClass().getName());
    logger = Logger.getLogger(this.getClass().getName());
  }

  /** Initialises this resource, and returns it.
   * @return
   * @throws ResourceInstantiationException
   */
  @Override
  public Resource init() throws ResourceInstantiationException {
    load();
    Gate.getCreoleRegister().addCreoleListener(this);
    return this;
  }

  /**
   * Loads this ontology.
   * @throws ResourceInstantiationException
   */
  public void load() throws ResourceInstantiationException {
    try {
      logger.debug("Running load");


      // determine ontology file and format to load, if any.
      OntologyFormat ontologyFormat = OConstants.OntologyFormat.RDFXML;
      if(rdfXmlURL != null && rdfXmlURL.toString().trim().length() > 0) {
        ontologyURL = rdfXmlURL;
      }
      else if(ntriplesURL != null && ntriplesURL.toString().trim().length() > 0) {
        ontologyURL = ntriplesURL;
        ontologyFormat = OConstants.OntologyFormat.NTRIPLES;
      }
      else if(n3URL != null && n3URL.toString().trim().length() > 0) {
        ontologyURL = n3URL;
        ontologyFormat = OConstants.OntologyFormat.N3;
      }
      else if(turtleURL != null && turtleURL.toString().trim().length() > 0) {
        ontologyURL = turtleURL;
        ontologyFormat = OConstants.OntologyFormat.TURTLE;
      }
      else {
        ontologyURL = null;
        ontologyFormat = OConstants.OntologyFormat.RDFXML;
      }
      logger.debug("creating ontology resource");
      String ontoURLString = ontologyURL == null ? "" : ontologyURL
              .toExternalForm();

      logger.debug("Determined url and format: "+ontoURLString+"/"+ontologyFormat);
      // determine the URL to the plugin directory
      ResourceData myResourceData =
          Gate.getCreoleRegister().get(this.getClass().getName());
      URL creoleXml = myResourceData.getXmlFileUrl();
      logger.debug("creoleXML is "+creoleXml);
      getPluginDir();
      

      // determine where to store the repository data
      URL actualDataDirectoryURL = null;
      if(dataDirectoryURL == null) {
        // use the system tmp
        String tmplocation = System.getProperty("run.java.io.tmpdir");
        logger.debug("run.java.io.tmpdir is "+tmplocation);
        if(tmplocation == null) {
          tmplocation = System.getProperty("java.io.tmpdir");
          logger.debug("java.io.tmpdir is "+tmplocation);
        }
        if(tmplocation != null) {
            actualDataDirectoryURL = new File(tmplocation).toURI().toURL();
         }
      } else {
          actualDataDirectoryURL = dataDirectoryURL;
      }
      if(actualDataDirectoryURL == null) {
        throw new ResourceInstantiationException(
            "Could not determine location for the data directory");
      }
      logger.debug("dataDirectoryURL is now "+actualDataDirectoryURL);
      if(!actualDataDirectoryURL.getProtocol().equals("file")) {
        throw new ResourceInstantiationException("dataDirectoryURL must be a local file");
      }
      dataDirectory = new File(actualDataDirectoryURL.toURI());
      if(!dataDirectory.exists()) {
        if(!dataDirectory.mkdir()) {
          throw new ResourceInstantiationException(
              "Could not create data directory "+actualDataDirectoryURL);
        }
      } else {
        if(!dataDirectory.isDirectory()) {
          throw new ResourceInstantiationException(
              "Not a directory: "+dataDirectory.getAbsolutePath());
        }
      }
      String storageFolderName = "GATE_OWLIMOntology_" +
          Long.toString(System.currentTimeMillis(),36);
      storageFolderDir = new File(dataDirectory,storageFolderName);
      storageFolderDir.mkdir();
      // TODO: replace by logger.info
      System.out.println("Storing data in folder: "+storageFolderDir.getAbsolutePath());

      // get the configuration file , check if the system import files
      // are there
      File configDir = new File(pluginDir,"config");
      File repoConfig;

      // This was how it was done with the unmanaged repository: use a
      // persist configuration when the persist parameter is true.
      //if(getPersistent()) {
      //  repoConfig = new File(configDir,"owlim-max-nopartial-persist.ttl");
      //} else {
      //  repoConfig = new File(configDir,"owlim-max-nopartial.ttl");
      //}

      // with the managed repository always use the same config (not decided
      // yet wheter to use the persist variation
      repoConfig = new File(configDir,"owlim-max-nopartial.ttl");
      
      logger.debug("Using config "+repoConfig.getAbsolutePath());
      System.out.println("Using config file: "+repoConfig.getAbsolutePath());

      if(!repoConfig.exists()) {
        throw new ResourceInstantiationException(
            "Repository config file not found "+repoConfig.getAbsolutePath());
      }
      File owlDefFile = new File(configDir,"owl.rdfs");
      if(!owlDefFile.exists()) {
        throw new ResourceInstantiationException(
            "OWL definition file not found "+owlDefFile.getAbsolutePath());
      }
      File rdfsDefFile = new File(configDir,"rdf-schema.rdf");
      if(!rdfsDefFile.exists()) {
        throw new ResourceInstantiationException(
            "RDFS definition file not found "+rdfsDefFile.getAbsolutePath());
      }


      OntologyServiceImplSesame oService = new OntologyServiceImplSesame(this);

      // the following code would create an unmanaged repository, but in
      // order to make it easier to re-use a repository in the case of a
      // crash, we use a managed repository instead
      /*
      // read the config file
      String configData = FileUtils.readFileToString(repoConfig);
      Map<String,String> varsmap = new HashMap<String,String>();
      varsmap.put("id", "owlim3repository");
      varsmap.put("sf", storageFolderName);
      configData = SesameManager.substituteConfigTemplate(configData, varsmap);
      logger.debug("Using config data: "+configData);

      ((OntologyServiceImplSesame)ontologyService)
          .createRepository(dataDirectory, configData);
      */
      // create a managed repository
      oService.createManagedRepository(
        storageFolderDir.toURI().toURL(),
        "owlim3",repoConfig.toURI().toURL());
      ontologyService = oService;

      logger.debug("Repository created");
      
      loadSystemImports();

      logger.debug("System imports done");

      if(ontologyURL != null) {
        logger.debug("Loading ontology data from "+ontologyURL+
              " using format "+ontologyFormat+" and base URI "+getBaseURI());
        readOntologyData(ontologyURL, getBaseURI(), ontologyFormat, false);
        logger.debug("default name space after loading: "+getDefaultNameSpace());
        System.out.println("Default name space is "+getDefaultNameSpace());
        logger.debug("Ontology data loaded");
        if(loadImports) {
          Map<String,String> mappings = null;
          if(mappingsURL != null &&  // !mappingsURL.toString().isEmpty()
             (mappingsURL.toString().length() != 0)
            ) {
            mappings = loadImportMappingsFile(mappingsURL);
            logger.debug("mappings loaded: "+mappings);
          }
          logger.debug("Resolving imports");
          resolveImports(mappings);
          logger.debug("Import resolving done");
        }
      }

    } catch(Exception ioe) {
      throw new ResourceInstantiationException(ioe);
    }

    setURL(ontologyURL);
   
  }

  public void cleanup() {
    super.cleanup();
    if(!isPersistent && dataDirectory != null) {
      try {
        FileUtils.deleteDirectory(storageFolderDir);
        logger.info("Directory "+storageFolderDir.getAbsolutePath()+" removed");
      } catch (IOException ex) {
        logger.error("Could not remove the storage-folder in "+dataDirectory.getAbsolutePath());
      }
    }
  }

  public java.net.URL getSourceURL() {
    if(getRdfXmlURL() != null) {
      return getRdfXmlURL();
    } else if(getN3URL() != null) {
      return getN3URL();
    } else if(getTurtleURL() != null) {
      return getTurtleURL();
    } else if(getNtriplesURL() != null) {
      return getNtriplesURL();
    } else {
      return null;
    }
  }
}
