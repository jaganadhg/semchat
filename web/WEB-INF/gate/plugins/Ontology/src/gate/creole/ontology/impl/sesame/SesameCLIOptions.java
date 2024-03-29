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
 *  $Id: SesameCLIOptions.java 12006 2009-12-01 17:24:28Z thomas_heitz $
 */
package gate.creole.ontology.impl.sesame;

import uk.co.flamingpenguin.jewel.cli.CommandLineInterface;
import uk.co.flamingpenguin.jewel.cli.Option;
import uk.co.flamingpenguin.jewel.cli.Unparsed;

import java.util.List;

@CommandLineInterface(application="SesameCLI")
public interface SesameCLIOptions {

  // TODO: pattern does not work?!?!?
  @Option(shortName="u",longName="serverUrl",description="URL of where Sesame2 server is running")
  String getUrl();
  boolean isUrl();

  @Option(shortName="d",longName="sesameDir",description="The directory that should contain repositories")
  String getDir();
  boolean isDir();

  @Option(longName="configFile",description="The file containing a Sesame configuation")
  String getConfig();
  boolean isConfig();

  @Option(shortName="e",longName="do",description="what to do: query,import,export,clear,create,delete")
  String getCmd();

  @Option(shortName="i",longName="id",description="Repository id")
  String getId();
  boolean isId();

  @Option(shortName="f",longName="from",defaultValue="file",description="Where to read things in from: file or stdin")
  String getFrom();
  boolean isFrom();

  @Option(shortName="n",longName="file",description="Name of the file we read from or write to, or stdin/stdout")
  String getFile();
  boolean isFile();

  @Option(longName="baseuri",description="Base URI for importing data")
  String getBaseURI();
  boolean isBaseURI();

  @Option(shortName="t",longName="format",defaultValue="xml",description="Format: xml or turtle for triples, sparql or serql for queries ")
  String getFormat();
  boolean isFormat();

  @Option(longName="max",description="Maximum number of results to return")
  int getMax();
  boolean isMax();


  @Option(longName="colsep",description="column separator string")
  String getColsep();
  boolean isColsep();

  @Option(shortName="d")
  boolean isDebug();
  
  @Option(helpRequest=true,description="Display help and exit")
  boolean isHelp();

  @Unparsed
  List<String> getArgs();
  boolean isArgs();

}


