/*
 *  SymmetricPropertyImpl.java
 *
 *  Niraj Aswani, 09/March/07
 *
 *  $Id: SymmetricPropertyImpl.java 12067 2009-12-08 13:34:15Z ian_roberts $
 */
package gate.creole.ontology.impl;

import gate.creole.ontology.OURI;
import gate.creole.ontology.Ontology;
import gate.creole.ontology.SymmetricProperty;

/**
 * Implementation of the SymmetricProperty
 * @author niraj
 *
 */
public class SymmetricPropertyImpl extends ObjectPropertyImpl implements
                                                             SymmetricProperty {
  /**
   * Constructor
   * @param aURI
   * @param ontology
   * @param repositoryID
   * @param owlimPort
   */
  public SymmetricPropertyImpl(OURI aURI, Ontology ontology,
          OntologyService owlimPort) {
    super(aURI, ontology, owlimPort);
  }
}
