package kasper.validation;

import kasper.domain.model.DtObject;

public interface ValidationRule<D extends DtObject> {
	/**
	 * @return Code de la règle.
	 */
	String getCode();

	boolean accept(D dto);
}
