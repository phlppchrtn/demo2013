package kasperimpl.behavior;

import java.util.Collection;

import javax.inject.Inject;

import kasper.behavior.BehaviorManager;
import kasper.domain.model.DtObject;
import kasper.kernel.metamodel.URI;
import kasper.kernel.util.Assertion;
import kasperimpl.behavior.plugins.BehaviorStorePlugin;

public final class BehaviorManagerImpl implements BehaviorManager {
	private final BehaviorStorePlugin behaviorStorePlugin;

	/**
	 * Constructeur.
	 * @param tripletStorePlugin Gestionnaire du stockage des triplets
	 */
	@Inject
	public BehaviorManagerImpl(final BehaviorStorePlugin tripletStorePlugin) {
		Assertion.notNull(tripletStorePlugin);
		//---------------------------------------------------------------------
		behaviorStorePlugin = tripletStorePlugin;
	}

	/** {@inheritDoc} */
	public void addBehavior(final URI<?> uri, final DtObject dto) {
		behaviorStorePlugin.addBehavior(uri, dto);
	}

	/** {@inheritDoc} */
	public Collection<DtObject> getBehaviors(final URI<?> uri) {
		return behaviorStorePlugin.getBehaviors(uri);
	}
}
