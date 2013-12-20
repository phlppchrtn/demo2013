package kasper.validation;

import java.util.List;

import kasper.domain.model.DtObject;
import kasper.kernel.util.Assertion;

public final class Validator<D extends DtObject> {
	private final List<ValidationRule<D>> validationRules;

	public Validator(final List<ValidationRule<D>> validationRules) {
		Assertion.notNull(validationRules);
		//---------------------------------------------------------------------
		this.validationRules = validationRules;
	}

	public List<ValidationRule<D>> getValidationRules() {
		return validationRules;
	}
}
