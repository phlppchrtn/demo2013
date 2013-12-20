package kasper.ui.model;

import java.util.List;

import kasper.kernel.exception.KRuntimeException;
import kasper.kernel.util.Assertion;

/**
 * Composant Graphique.
 * 
 * @author prahmoune
 *
 */
public final class UIComponent {
	/**
	 * Nom du composant.
	 */
	private final String name;

	/**
	 * Type du composant.
	 */
	private final UIComponentType type;

	/**
	 * Propriétés du composant.
	 */
	private final List<UIProperty> properties;

	/**
	 * Parent du composant.
	 */
	private final UIComponent parent;

	/**
	 * Fils du composant.
	 */
	private final List<UIComponent> children;

	/**
	 * Constructeur.
	 * @param name Nom du composant.
	 * @param type Type du composant. 
	 * @param parent Parent du composant.
	 */
	public UIComponent(final String name, final UIComponentType type, final List<UIProperty> properties, final UIComponent parent, final List<UIComponent> children) {
		Assertion.notEmpty(name);
		Assertion.notNull(type);
		Assertion.notNull(properties);
		//Assertion.notNull(parent);
		//Assertion.notNull(children);
		//---------------------------------------------------------------------
		this.name = name;
		this.type = type;
		this.properties = properties;
		this.parent = parent;
		this.children = children;
	}

	/**
	 * @return Nom
	 */
	public String getName() {
		return name;
	}

	/** 
	 * @return Type du composant.
	 */
	public UIComponentType getType() {
		return type;
	}

	/**
	 * @return Propriétés du composant.
	 */
	public List<UIProperty> getProperties() {
		return properties;
	}

	/**
	 * 
	 * @param propertyName Nom de la propriété
	 * @return Propriété
	 */
	public UIProperty getUIProperty(final String propertyName) {
		Assertion.notEmpty(propertyName);
		//----------------------------------------------------------------------
		for (final UIProperty property : properties) {
			if (property.getName().equals(propertyName)) {
				return property;
			}
		}
		throw new KRuntimeException("la propriété '" + propertyName + "' n'est pas trouvée.");
	}

	/**
	 * @return Parent du composant.
	 */
	public UIComponent getParent() {
		return parent;
	}

	/**
	 * @return Fils du composant.
	 */
	public List<UIComponent> getChildren() {
		return children;
	}
}
