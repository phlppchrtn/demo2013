package kasper.ui.metamodel;

import java.util.Date;

import kasper.kernel.metamodel.DataType;
import kasper.kernel.util.Assertion;

/**
 * Types.
 *
 * @author  pchretien
 * @version $Id: UIDataType.java,v 1.1 2012/12/05 14:10:23 pchretien Exp $
 */
public enum UIDataType implements DataType {
	/** Integer. */
	Integer(Integer.class),
	/** Double. */
	Double(Double.class),
	/** Boolean. */
	Boolean(Boolean.class),
	/** String. */
	String(String.class),
	/** Date. */
	Date(Date.class),
	//	/** BigDecimal. */
	//	BigDecimal(java.math.BigDecimal.class, true),
	/** Long. */
	Long(Long.class);

	/**
	 * Classe java que le Type encapsule.
	 */
	private final Class<?> javaClass;

	/**
	 * Constructeur.
	 *
	 * @param javaClass Classe java encapsul�e
	 */
	private UIDataType(final Class<?> javaClass) {
		Assertion.notNull(javaClass);
		//----------------------------------------------------------------------
		//Le nom est �gal au type sous forme de String
		this.javaClass = javaClass;
	}

	/** {@inheritDoc} */
	public void checkValue(final Object value) {
		//Il suffit de v�rifier que la valeur pass�e est une instance de la classe java d�finie pour le type kasper.
		//Le test doit �tre effectu� car le cast est non fiable par les generics
		if (value != null && !javaClass.isInstance(value)) {
			throw new ClassCastException("Valeur " + value + " ne correspond pas au type :" + this);
		}
	}

	/** {@inheritDoc} */
	public Class<?> getJavaClass() {
		return javaClass;
	}
}
