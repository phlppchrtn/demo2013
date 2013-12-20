package kasper.behavior;

import java.util.Collection;

import kasper.domain.model.DtObject;
import kasper.kernel.manager.Manager;
import kasper.kernel.metamodel.URI;

/**
 * Gestionnaire centralis� des triplets
 * @author ntaha
 *
 */
public interface BehaviorManager extends Manager {
	/**
	 * Ajoute un comportement � un objet.
	 */
	void addBehavior(final URI<?> uri, DtObject dto);

	/**
	 * Liste les triplets associ�s � une URI.
	 * @param uri
	 * @return Liste des triplets
	 */
	Collection<DtObject> getBehaviors(URI<?> uri);
}
