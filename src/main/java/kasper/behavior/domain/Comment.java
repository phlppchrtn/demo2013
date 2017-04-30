/**
 * 
 */
package kasper.behavior.domain;

import kasper.domain.metamodel.DtDefinition;
import kasper.domain.metamodel.annotation.Field;
import kasper.domain.model.DtObject;
import kasper.domain.util.DtObjectUtil;

/**
 * Classe pour le comportement Comment.
 * @author ntaha
 */
@kasper.domain.metamodel.annotation.DtDefinition
public class Comment implements DtObject {
	/** SerialVersionUID.*/
	private static final long serialVersionUID = 1L;

	private String comment;

	/** {@inheritDoc} */
	public final DtDefinition getDefinition() {
		return DtDefinition.findDtDefinition(Comment.class);
	}

	@Field(domain = "DO_STRING", label = "comment")
	public final String getComment() {
		return comment;
	}

	public final void setComment(final String comment) {
		this.comment = comment;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return DtObjectUtil.toString(this);
	}
}
