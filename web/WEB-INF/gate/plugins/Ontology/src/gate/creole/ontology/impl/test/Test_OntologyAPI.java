/*
 *  TestOntologyAPI.java
 *
 *  $Id: Test_OntologyAPI.java 11944 2009-11-10 16:43:38Z johann_p $
 */
package gate.creole.ontology.impl.test;

import gate.creole.ontology.*;
import gate.util.GateException;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.creole.ResourceInstantiationException;
import gate.creole.ontology.OConstants.Closure;
import java.util.HashSet;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.log4j.Logger;

/**
 * Run all the essential regression tests for the OWLIMOntology LR.s
 */
public class Test_OntologyAPI extends TestCase {
  public static void main(String[] args) throws GateException, MalformedURLException {
    junit.textui.TestRunner.run(Test_OntologyAPI.class);
  }

  public Test_OntologyAPI(String arg0) throws GateException, MalformedURLException {
    super(arg0);
  }

  File ontologiesDir = null;
  File configDir = null;
  File tmpDir = null;

  Logger log =  Logger.getLogger(this.getClass());
  OConstants.Closure DIRECT_CLOSURE = Closure.DIRECT_CLOSURE;
  OConstants.Closure TRANSITIVE_CLOSURE = Closure.TRANSITIVE_CLOSURE;

  protected void init() throws GateException, MalformedURLException {
    System.out.println("Inititalizing ...");
    Gate.init();
    File pluginHome =
        new File(new File(Gate.getGateHome(), "plugins"),
                 "Ontology");
    System.out.println("Plugin home directory is "+pluginHome.getAbsolutePath());
    Gate.getCreoleRegister().registerDirectories(
            pluginHome.toURI().toURL());
    File testingDir = new File(pluginHome,"test");
    assertTrue(testingDir.exists());
    ontologiesDir = new File(testingDir, "ontologies");
    assertTrue(ontologiesDir.exists());
    tmpDir = getUniqueTmpDir();
    assertTrue(tmpDir.canWrite());
    System.out.println("Init complete");
  }


  /**
   * per-test set up stuff
   * @throws Exception
   */
  protected void setUp() throws Exception {
    super.setUp();
    init();
  }

  protected void tearDown() throws Exception {
    super.tearDown();
  }

  /** Test suite routine for the test runner */
  public static Test suite() {
    System.out.println("Running suite");
    return new TestSuite(Test_OntologyAPI.class);
  } // suite

  // ************************************ TESTS ***************************
  // **********************************************************************




  public void testAPI01()
      throws MalformedURLException,
          ResourceInstantiationException {

    Set<OClass> cs;
    Set<OInstance> is;
    Set<RDFProperty> rdfps;
    Set<OURI> us;
    Ontology ont;

    // --------------------- allValuesFrom
    //
    System.out.println("Testing allValuesFrom");
    ont = loadOntology("w3tests/allValuesFrom/premises001.rdf");
    ont.setDefaultNameSpace("http://www.w3.org/2002/03owlt/allValuesFrom/premises001#");

    cs = ont.getOClasses(false);
    System.out.println("getOClasses: "+cs);
    assertEquals(3, cs.size());

    is = ont.getOInstances();
    assertEquals(2,is.size());
    System.out.println("getOInstances: "+is);
    us = rset2uset(is);
    assertTrue(us.contains(ont.createOURIForName("i")));
    assertTrue(us.contains(ont.createOURIForName("o")));

    rdfps = ont.getRDFProperties();
    System.out.println("getRDFProperties: "+rdfps);
    //assertEquals(2,rdfps.size());

    rdfps = ont.getPropertyDefinitions();
    System.out.println("getPropertyDefintiions: "+rdfps);
    us = rset2uset(rdfps);
    assertTrue(us.contains(ont.createOURIForName("p")));

    // check: :o a :c
    OInstance i_o = ont.getOInstance(ont.createOURIForName("o"));
    cs = i_o.getOClasses(DIRECT_CLOSURE);
    System.out.println("Direct classes for o: "+cs);
    assertEquals(1,cs.size());
    assertEquals("c",cs.iterator().next().toString());
    OInstance i_i = ont.getOInstance(ont.createOURIForName("i"));
    cs = i_i.getOClasses(DIRECT_CLOSURE);
    System.out.println("Direct classes for i: "+cs);
    assertEquals(1,cs.size());
    assertEquals("r",cs.iterator().next().toString());

    // now create our own avf restriction
    OClass onClass = ont.addOClass(ont.createOURIForName("ourClass1"));
    ObjectProperty objProp = ont.addObjectProperty(
        ont.createOURIForName("ourProp1"), null, null);
    AllValuesFromRestriction restr1 = ont.addAllValuesFromRestriction(objProp, onClass);
    OResource res = restr1.getHasValue();
    System.out.println("Has value: "+res+" / "+res.getClass().getName());
    assertEquals("ourClass1",res.toString());
    RDFProperty prop = restr1.getOnPropertyValue();
    System.out.println("on property: "+prop+" / "+prop.getClass().getName());
    assertEquals("ourProp1",prop.toString());




    ont.cleanup();

    // --------------------- cardinality001
    // test if cardinality=1 implies min/max=1
    //
    System.out.println("Testing cardinality001");
    ont = loadOntology("w3tests/cardinality/premises001.rdf");
    ont.setDefaultNameSpace("http://www.w3.org/2002/03owlt/cardinality/premises001#");

    cs = ont.getOClasses(false);
    System.out.println("getOClasses: "+cs);
    assertEquals(4, cs.size());

    is = ont.getOInstances();
    assertEquals(0,is.size());
    System.out.println("getOInstances: "+is);
    us = rset2uset(is);

    rdfps = ont.getRDFProperties();
    System.out.println("getRDFProperties: "+rdfps);
    assertEquals(1,rdfps.size());

    rdfps = ont.getPropertyDefinitions();
    System.out.println("getPropertyDefintiions: "+rdfps);
    us = rset2uset(rdfps);
    assertTrue(us.contains(ont.createOURIForName("p")));

    ont.cleanup();

    // --------------------- cardinality002
    // test if both min/max=1 imply cardinality=1
    //
    System.out.println("Testing cardinality002");
    ont = loadOntology("w3tests/cardinality/premises002.rdf");
    ont.setDefaultNameSpace("http://www.w3.org/2002/03owlt/cardinality/premises002#");

    cs = ont.getOClasses(false);
    System.out.println("getOClasses: "+cs);
    //assertEquals(3, cs.size());

    is = ont.getOInstances();
    //assertEquals(2,is.size());
    System.out.println("getOInstances: "+is);
    us = rset2uset(is);
    //assertTrue(us.contains(ont.createOURIForName("i")));
    //assertTrue(us.contains(ont.createOURIForName("o")));

    rdfps = ont.getRDFProperties();
    System.out.println("getRDFProperties: "+rdfps);
    //assertEquals(2,rdfps.size());

    rdfps = ont.getPropertyDefinitions();
    System.out.println("getPropertyDefintiions: "+rdfps);
    us = rset2uset(rdfps);
    //assertTrue(us.contains(ont.createOURIForName("p")));

    ont.cleanup();

  }



  // ***************************************************************
  // ************************** HELPER METHODS 
  // ***************************************************************

  public Set<OURI> rset2uset(Set<? extends OResource> set) {
    Set<OURI> retset = new HashSet<OURI>();
    for (OResource r : set) {
      ONodeID n = r.getONodeID();
      if(!n.isAnonymousResource()) {
        retset.add((OURI)n);
      }
    }
    return retset;
  }

   public Ontology loadOntology(String relativePath) 
       throws MalformedURLException, ResourceInstantiationException {
    FeatureMap fm = Factory.newFeatureMap();
    File ontoFile = new File(ontologiesDir,relativePath);
    URL rdfXmlURL = ontoFile.toURI().toURL();
    fm.put("rdfXmlURL", rdfXmlURL);
    Ontology ontology = (Ontology)Factory.createResource(
            "gate.creole.ontology.impl.sesame.OWLIMOntology", fm);
    return ontology;
   }

  // this is for testing if we can recursively expand all classes  and
  // to see how many distinct IDs we find that way
  public void expandAllClasses(Set<OClass> classes, Set<ONodeID> allIDs, int depth) {
    depth = depth + 1;
    if(depth > 9) {
      // TODO: print everything about the node that gets us into a loop here!
      assertTrue("Depth must not exceed 9",false);
    }
    if(classes.size() == 0) {
      return;
    }
    //System.out.println("Expanding set: "+classes);
    for (OClass c : classes) {
      //System.out.println("Processing class: "+c);
      ONodeID n = c.getONodeID();
      if(allIDs.contains(n)) {

        //System.err.println("Node ID already found: "+n.toTurtle());
      }
      allIDs.add(n);
      // get the set of direct subclasses of this class
      Set<OClass> subclasses = c.getSubClasses(OConstants.Closure.DIRECT_CLOSURE);
      //System.out.println("Subclasses for "+c+": "+subclasses);
      expandAllClasses(subclasses,allIDs,depth);
    }
  }

  protected static File getUniqueTmpDir() {
    String tmplocation = System.getProperty("run.java.io.tmpdir");
    if(tmplocation == null) {
      tmplocation = System.getProperty("java.io.tmpdir");
    }
    if(tmplocation == null) {
      tmplocation = "/tmp";
    }
    if(tmplocation == null) {
       System.err.println("Not temp-directory found, cannot continue");
       System.exit(1);
    }
    File tmpdir = new File(tmplocation);
    if(!tmpdir.exists()) {
       System.err.println("Temp dir does not exist: "+tmpdir.getAbsolutePath());
       System.exit(1);
    }
    String tmpString = Long.toString(System.currentTimeMillis(),36);
    File uniqTmpDir = new File(tmpdir,"gate-towlim-"+tmpString);
    uniqTmpDir.mkdir();
    return uniqTmpDir;
  }

  protected OURI getURI(Ontology o, String uri) {
    return o.createOURI(uri);
  }

  protected OURI getURI4Name(Ontology o, String uri) {
    return o.createOURIForName(uri);
  }

}
