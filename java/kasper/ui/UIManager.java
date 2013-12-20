package kasper.ui;

import kasper.kernel.manager.Manager;

/**
 * Interface du gestionnaire de l'interface graphique.
 * 
 * @author prahmoune
 * @version $Id: UIManager.java,v 1.1 2012/12/05 14:10:23 pchretien Exp $
 */
public interface UIManager extends Manager {

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
