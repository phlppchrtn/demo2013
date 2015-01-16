package kasper.validation;

import java.util.List;

import kasper.domain.model.DtObject;
import kasper.kernel.util.Assertion;

public final class ValidationResult<D extends DtObject> {
	private final List<ValidationRule<D>> acceptedRules;
	private final List<ValidationRule<D>> failedRules;

	//private final List<RuleViolation<D>> violations;

	public ValidationResult(final List<ValidationRule<D>> failedRules, final List<ValidationRule<D>> acceptedRules) {
		Assertion.notNull(acceptedRules);
		Assertion.notNull(failedRules);
		//-----
		this.acceptedRules = acceptedRules;
		this.failedRules = failedRules;
	}

	public boolean isValidated() {
		return failedRules.isEmpty();
	}

	public List<ValidationRule<D>> getFailedRules() {
		return failedRules;
	}

	public List<ValidationRule<D>> getAcceptedRules() {
		return acceptedRules;
	}
}
