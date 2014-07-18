package kasperimpl.behavior.plugins.memory;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import kasper.domain.metamodel.DtDefinition;
import kasper.domain.model.DtObject;
import kasper.kernel.metamodel.URI;
import kasper.kernel.util.Assertion;
import kasperimpl.behavior.plugins.BehaviorStorePlugin;

/**
 * Classe de stockage en mémoire des comportements.
 * Il ne peut pour chaque objet (uri) exister qu'un seul comportement de chaque type.
 * Par exemple : Un seul rating par objet.    
 *  
 * @author ntaha
 *
 */
public final class MemoryBehaviorStorePlugin implements BehaviorStorePlugin {
	/** stockage en mémoire*/
	private final static Map<URI<?>, Map<DtDefinition, DtObject>> behaviors = new HashMap<URI<?>, Map<DtDefinition, DtObject>>();

	/** {@inheritDoc} */
	public void addBehavior(final URI<?> uri, final DtObject dto) {
		Assertion.notNull(uri);
		Assertion.notNull(dto);
		//---------------------------------------------------------------------
		// Triplet = (uri + dto)
		obtainBehaviors(uri).put(dto.getDefinition(), dto);
	}

	private Map<DtDefinition, DtObject> obtainBehaviors(final URI<?> uri) {
		Map<DtDefinition, DtObject> map = behaviors.get(uri);
		if (map == null) {
			map = new HashMap<DtDefinition, DtObject>();
			behaviors.put(uri, map);
		}
		return map;
	}

	/** {@inheritDoc} */
	public Collection<DtObject> getBehaviors(final URI<?> uri) {
		Assertion.notNull(uri);
		//---------------------------------------------------------------------
		final Map<DtDefinition, DtObject> map = behaviors.get(uri);
		return map == null ? Collections.<DtObject> emptyList() : Collections.unmodifiableCollection(map.values());
	}
}
