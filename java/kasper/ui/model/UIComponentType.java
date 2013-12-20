package kasper.ui.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kasper.kernel.exception.KRuntimeException;
import kasper.kernel.util.Assertion;

/**
 * Type de composant graphique.
 * 
 * @author prahmoune
 * @version $Id: UIComponentType.java,v 1.1 2012/12/05 14:10:23 pchretien Exp $
 */
public final class UIComponentType {
	/**
	 * Nom du type de composant.
	 */
	private final String name;

	/**
	 * Liste des propriétés types du type de composant.
	 */
	private final Map<String, UIPropertyType> propertyTypes;

	/**
	 * Constructeur.
	 * 	 
	 * @param name Nom du type de composant.
	 */
	public UIComponentType(final String name, final List<UIPropertyType> propertyTypes) {
		Assertion.notEmpty(name);
		Assertion.notNull(propertyTypes);
		//---------------------------------------------------------------------
		this.name = name;
		this.propertyTypes = new HashMap<String, UIPropertyType>();
		for (final UIPropertyType propertyType : propertyTypes) {
			this.propertyTypes.put(propertyType.getName(), propertyType);
		}
	}

	/**
	 * @return Nom
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return Liste des propriétés types du type de composant.
	 */
	public Collection<UIPropertyType> getUIPropertyTypes() {
		return propertyTypes.values();
	}

	/**
	 * 
	 * @param propertyName Nom du type de propriété
	 * @return Type de Propriété
	 */
	public UIPropertyType getUIPropertyType(final String propertyName) {
		Assertion.notEmpty(propertyName);
		//----------------------------------------------------------------------
		final UIPropertyType propertyType = propertyTypes.get(propertyName);
		if (propertyType == null) {
			throw new KRuntimeException("le type de propriété'" + propertyName + "' n'est pas trouvé.");
		}
		return propertyType;
	}
}
