package kasper.ui.model;

import kasper.kernel.exception.KRuntimeException;
import kasper.kernel.util.Assertion;

/**
 * Propri�t�.
 * 
 * @author prahmoune
 *
 */
public final class UIProperty {
	private static final String TRUE = "true";
	private static final String FALSE = "false";

	/**
	 * Type de la propri�t�.
	 */
	private final UIPropertyType type;

	/**
	 * Valeur de la propri�t�.
	 */
	private final String value;

	/**
	 * Constructeur.
	 * 
	 * @param type Type de la propri�t�.
	 * @param value Valeur de la propri�t�.
	 */
	public UIProperty(final UIPropertyType type, final String value) {
		Assertion.notNull(type);
		Assertion.notNull(value);
		//----------------------------------------------------------------------
		this.type = type;
		this.value = value;
	}

	/**
	 * @return Nom de la propri�t�.
	 */
	public String getName() {
		return type.getName();
	}

	/**
	 * @return Type de la propri�t�.
	 */
	public UIPropertyType getType() {
		return type;
	}

	/**
	 * @return Valeur de la propri�t�.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @return Valeur de la propri�t� sous la forme d'un bool�en.
	 */
	public boolean getBooleanValue() {
		if (!(TRUE.equalsIgnoreCase(value) || FALSE.equalsIgnoreCase(value))) {
			throw new KRuntimeException("La propri�t� '" + type.getName() + "' n'est pas convertible en 'boolean'");
		}
		return Boolean.parseBoolean(value);
	}

	/**
	 * @return Valeur de la propri�t� sous la forme d'un entier.
	 */
	public int getIntValue() {
		try {
			return Integer.parseInt(value);
		} catch (final NumberFormatException e) {
			throw new KRuntimeException("La propri�t� '" + type.getName() + "' n'est pas convertible en 'int'", e);
		}
	}

}
