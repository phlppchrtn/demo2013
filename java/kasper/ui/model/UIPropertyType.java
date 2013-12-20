package kasper.ui.model;

import kasper.kernel.metamodel.DataType;
import kasper.kernel.util.Assertion;
import kasper.ui.metamodel.UIDataType;

/**
 * Type de propriété.
 * 
 * @author prahmoune
 *
 */
public final class UIPropertyType {

	/**
	 * Nom du type de propriété. 
	 */
	private final String name;

	/**
	 * Type de la valeur du type de propriété.
	 */
	private final DataType dataType;

	/**
	 * Valeur par défaut de la propriété.
	 */
	private final Object defaultValue;

	/**
	 * Constructeur.
	 * 
	 * @param name Nom du type de propriété.
	 * @param dataType Type de la valeur du type de propriété.
	 * @param defaultValue Valeur par défaut de la propriété.
	 */
	public UIPropertyType(final String name, final UIDataType dataType, final Object defaultValue) {
		Assertion.notEmpty(name);
		Assertion.notNull(dataType);
		//---------------------------------------------------------------------
		dataType.checkValue(defaultValue);
		this.name = name;
		this.dataType = dataType;
		this.defaultValue = defaultValue;
	}

	/**
	 * @return Nom du type de propriété.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return Type de la valeur du type de propriété.
	 */
	public DataType getDataType() {
		return dataType;
	}

	/**
	 * @return Valeur par défaut de la propriété.
	 */
	public Object getDefaultValue() {
		return defaultValue;
	}

}
