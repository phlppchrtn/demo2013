package kasper.ui.model;

import kasper.kernel.exception.KRuntimeException;
import kasper.kernel.util.Assertion;

/**
 * Propriété.
 * 
 * @author prahmoune
 *
 */
public final class UIProperty {
	private static final String TRUE = "true";
	private static final String FALSE = "false";

	/**
	 * Type de la propriété.
	 */
	private final UIPropertyType type;

	/**
	 * Valeur de la propriété.
	 */
	private final String value;

	/**
	 * Constructeur.
	 * 
	 * @param type Type de la propriété.
	 * @param value Valeur de la propriété.
	 */
	public UIProperty(final UIPropertyType type, final String value) {
		Assertion.notNull(type);
		Assertion.notNull(value);
		//----------------------------------------------------------------------
		this.type = type;
		this.value = value;
	}

	/**
	 * @return Nom de la propriété.
	 */
	public String getName() {
		return type.getName();
	}

	/**
	 * @return Type de la propriété.
	 */
	public UIPropertyType getType() {
		return type;
	}

	/**
	 * @return Valeur de la propriété.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @return Valeur de la propriété sous la forme d'un booléen.
	 */
	public boolean getBooleanValue() {
		if (!(TRUE.equalsIgnoreCase(value) || FALSE.equalsIgnoreCase(value))) {
			throw new KRuntimeException("La propriété '" + type.getName() + "' n'est pas convertible en 'boolean'");
		}
		return Boolean.parseBoolean(value);
	}

	/**
	 * @return Valeur de la propriété sous la forme d'un entier.
	 */
	public int getIntValue() {
		try {
			return Integer.parseInt(value);
		} catch (final NumberFormatException e) {
			throw new KRuntimeException("La propriété '" + type.getName() + "' n'est pas convertible en 'int'", e);
		}
	}

}
