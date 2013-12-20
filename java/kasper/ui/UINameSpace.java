package kasper.ui;

import java.util.Collection;

import kasper.ui.model.UIComponent;
import kasper.ui.model.UIComponentType;

/**
 * NameSpace pour le model graphique.
 *
 * @author  prahmoune
 * @version $Id: UINameSpace.java,v 1.1 2012/12/05 14:10:23 pchretien Exp $
 */
public interface UINameSpace {

	/**
	 * @return Liste des types de composants graphiques.
	 */
	Collection<UIComponentType> getUIComponentTypes();

	/**
	 * Retourne la definition d'un type de composant graphique
	 * 
	 * @param name
	 *            Nom du type de composant
	 * @return Valeur de la définition
	 */
	UIComponentType getComponentType(final String name);

	/**
	 * @return Liste des composants graphiques.
	 */
	Collection<UIComponent> getUIComponents();

	/**
	 * @param uiComponentType Type de composant.
	 * @return Liste des composants graphiques.
	 */
	Collection<UIComponent> getUIComponents(UIComponentType uiComponentType);

	/**
	 * Retourne la definition d'un composant graphique
	 * 
	 * @param name
	 *            Nom du composant
	 * @return Valeur de la définition
	 */
	UIComponent getUIComponent(final String name);
}
