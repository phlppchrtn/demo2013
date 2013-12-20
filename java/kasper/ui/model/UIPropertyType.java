package kasper.ui.model;

import kasper.kernel.metamodel.DataType;
import kasper.kernel.util.Assertion;
import kasper.ui.metamodel.UIDataType;

/**
 * Type de propri�t�.
 * 
 * @author prahmoune
 *
 */
public final class UIPropertyType {

	/**
	 * Nom du type de propri�t�. 
	 */
	private final String name;

	/**
	 * Type de la valeur du type de propri�t�.
	 */
	private final DataType dataType;

	/**
	 * Valeur par d�faut de la propri�t�.
	 */
	private final Object defaultValue;

	/**
	 * Constructeur.
	 * 
	 * @param name Nom du type de propri�t�.
	 * @param dataType Type de la valeur du type de propri�t�.
	 * @param defaultValue Valeur par d�faut de la propri�t�.
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
	 * @return Nom du type de propri�t�.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return Type de la valeur du type de propri�t�.
	 */
	public DataType getDataType() {
		return dataType;
	}

	/**
	 * @return Valeur par d�faut de la propri�t�.
	 */
	public Object getDefaultValue() {
		return defaultValue;
	}

}
