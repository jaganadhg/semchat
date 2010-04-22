package sk.hasto.semchat.infrastructure.services.gate;

import gate.Gate;
import gate.util.GateException;
import java.io.File;
import java.net.MalformedURLException;

/**
 * Sluzi na inicializaciu GATE kniznice.
 * @author Branislav Hasto
 */
public final class GateInitializer
{

	/**
	 * Inicializuje GATE s potrebnymi pluginmi.
	 * @throws GateException
	 */
	public void init() throws GateException
	{
		if (!Gate.isInitialised()) {
			Gate.setGateHome(new File("C:/Program Files/GATE-5.1"));
			Gate.init();
			loadGatePlugins();
		}
	}


	/**
	 * Prida vsetky GATE pluginy, ktore aplikacia potrebuje.
	 */
	private void loadGatePlugins()
	{
		try {
			File anniePath = new File(Gate.getPluginsHome(), "ANNIE");
			Gate.getCreoleRegister().addDirectory(anniePath.toURI().toURL());

			File toolsPath = new File(Gate.getPluginsHome(), "Tools");
			Gate.getCreoleRegister().addDirectory(toolsPath.toURI().toURL());

			File ontologyPath = new File(Gate.getPluginsHome(), "Ontology");
			Gate.getCreoleRegister().addDirectory(ontologyPath.toURI().toURL());

			File ontoGazPath = new File(Gate.getPluginsHome(), "Gazetteer_Ontology_Based");
			Gate.getCreoleRegister().addDirectory(ontoGazPath.toURI().toURL());
		}

		catch (MalformedURLException e) {
			throw new AssertionError(e);
		}
	}

}
