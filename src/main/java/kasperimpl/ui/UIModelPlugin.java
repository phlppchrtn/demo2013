package kasperimpl.ui;

import kasper.kernel.manager.Plugin;
import kasper.ui.UINameSpace;
import kasper.ui.UIRegistry;

/**
 * Interface d'un plugin de gestion du model de l'interface graphique.
 * 
 * @author prahmoune
 * @version $Id: UIModelPlugin.java,v 1.1 2012/12/05 14:10:23 pchretien Exp $ 
 * 
 */
public interface UIModelPlugin extends Plugin {

	/**
	 * Retourne la UIRegistry.
	 * @return UIRegistry
	 */
	UIRegistry getUIRegistry();

	/**
	 * Retourne le UINameSpace.
	 * @return UINameSpace
	 */
	UINameSpace getUINameSpace();
}
