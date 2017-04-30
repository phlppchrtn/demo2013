/**
 * 
 */
package kasper.behavior.domain;

import kasper.domain.metamodel.DtDefinition;
import kasper.domain.metamodel.annotation.Field;
import kasper.domain.model.DtObject;
import kasper.domain.util.DtObjectUtil;

/**
 * Classe pour le comportement Rating.
 * @author ntaha
 */
@kasper.domain.metamodel.annotation.DtDefinition
public class Rating implements DtObject {
	/** SerialVersionUID.*/
	private static final long serialVersionUID = 1L;

	private Integer rate;

	/** {@inheritDoc} */
	public final DtDefinition getDefinition() {
		return DtDefinition.findDtDefinition(Rating.class);
	}

	@Field(domain = "DO_INTEGER", label = "rate")
	public final Integer getRate() {
		return rate;
	}

	public final void setRate(final Integer rate) {
		this.rate = rate;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return DtObjectUtil.toString(this);
	}
}
