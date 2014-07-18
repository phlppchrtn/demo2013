package kasperimpl.behavior.plugins;

import java.util.Collection;

import kasper.domain.model.DtObject;
import kasper.kernel.manager.Plugin;
import kasper.kernel.metamodel.URI;

/**
 * Implementation de la gestion du stockage des triplets
 * @author ntaha
 *
 */
public interface BehaviorStorePlugin extends Plugin {
	/**
	 * Ajoute un Triplet
	 */
	void addBehavior(final URI<?> uri, DtObject dto);

	/**
	 * Renvoie une liste des comportements associés à un objet identifié par son URI
	 * @return map <predicate, triplet>
	 */
	Collection<DtObject> getBehaviors(URI<?> uri);
}
