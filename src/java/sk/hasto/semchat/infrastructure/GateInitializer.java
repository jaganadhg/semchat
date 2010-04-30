package sk.hasto.semchat.infrastructure;

import gate.CreoleRegister;
import gate.Gate;
import gate.creole.ANNIEConstants;
import gate.util.GateException;
import java.io.File;
import java.net.MalformedURLException;
import javax.servlet.ServletContext;

/**
 * Sluzi na inicializaciu GATE kniznice.
 * @author Branislav Hasto
 */
public final class GateInitializer
{

	/**
	 * Inicializuje GATE s potrebnymi pluginmi.
	 * @param gateHome adresar, kde je umiestneny gate
	 * @throws GateException
	 */
	public static void init(File gateHome) throws GateException
	{
		if (!Gate.isInitialised()) {
			Gate.setGateHome(gateHome);
			Gate.setUserConfigFile(new File(gateHome, "user-gate.xml"));
			Gate.init();

			loadGatePlugins();
		}
	}

	
	/**
	 * Prida vsetky potrebne GATE pluginy.
	 */
	private static void loadGatePlugins()
	{
		try {
			File pluginsHome = Gate.getPluginsHome();
			CreoleRegister creole = Gate.getCreoleRegister();

			// TODO: DRY
			creole.addDirectory(new File(pluginsHome, ANNIEConstants.PLUGIN_DIR).toURI().toURL());
			creole.addDirectory(new File(pluginsHome, "Tools").toURI().toURL());
			creole.addDirectory(new File(pluginsHome, "Ontology").toURI().toURL());
			creole.addDirectory(new File(pluginsHome, "Gazetteer_Ontology_Based").toURI().toURL());
		}
		catch (MalformedURLException e) {
			// url creation under control
			throw new AssertionError(e);
		}
	}


	/**
	 * Prida vsetky potrebne GATE pluginy, pricom
	 * ich nacita zo zadaneho servlet kontextu.
	 * @param context
	 */
	private static void loadGatePlugins(ServletContext context)
	{
		try {
			String basePath = "/WEB-INF/gate/plugins/";
			CreoleRegister creole = Gate.getCreoleRegister();

			creole.addDirectory(context.getResource(basePath + ANNIEConstants.PLUGIN_DIR));
			creole.addDirectory(context.getResource(basePath + "Tools"));
			creole.addDirectory(context.getResource(basePath + "Ontology"));
			creole.addDirectory(context.getResource(basePath + "Gazetteer_Ontology_Based"));
		}
		catch (MalformedURLException ex) {
			// url creation under control
			throw new AssertionError(ex);
		}
	}

}
