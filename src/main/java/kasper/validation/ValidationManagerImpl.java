package kasper.validation;

import java.util.ArrayList;
import java.util.List;

import kasper.domain.metamodel.DtDefinition;
import kasper.domain.model.DtObject;

public final class ValidationManagerImpl implements ValidationManager {

	private <D extends DtObject> ValidationResult<D> validate(final Validator<D> validator, final D dto) {
		final List<ValidationRule<D>> failedRules = new ArrayList<ValidationRule<D>>();
		final List<ValidationRule<D>> acceptedRules = new ArrayList<ValidationRule<D>>();
		for (final ValidationRule<D> validationRule : validator.getValidationRules()) {
			if (validationRule.accept(dto)) {
				acceptedRules.add(validationRule);
			} else {
				failedRules.add(validationRule);
			}
		}
		return new ValidationResult<D>(failedRules, acceptedRules);
	}

	public <D extends DtObject> Validator<D> createValidator(final DtDefinition dtDefinition/*, final String action*/) {
		return null;
	}

	public <D extends DtObject> ValidationResult<D> validate(final D dto) {
		final Validator<D> validator = createValidator(dto.getDefinition());
		return validate(validator, dto);
	}

}
