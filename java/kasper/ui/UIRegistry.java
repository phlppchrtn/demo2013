package kasper.ui;

import kasper.ui.model.UIComponent;
import kasper.ui.model.UIComponentType;

/**
 * Registry pour le model graphique.
 *
 * @author  prahmoune
 * @version $Id: UIRegistry.java,v 1.1 2012/12/05 14:10:23 pchretien Exp $
 */
public interface UIRegistry {
	/**
	 * Enregistre un type de composant graphique.
	 */
	void register(final UIComponentType uiComponentType);

	/**
	 * Enregistre une permission.
	 */
	void register(final UIComponent uiComponent);

}
