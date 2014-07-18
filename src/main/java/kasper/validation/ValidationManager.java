package kasper.validation;

import kasper.domain.model.DtObject;

public interface ValidationManager {
	//<D extends DtObject> Validator<D> createValidator(final Class<D> dtObjectClass, final String action);
	//<D extends DtObject> void registerRule(final ValidationRule<D> validationRule, final Class<D> clazz);
	<D extends DtObject> ValidationResult<D> validate(D dto);
}
