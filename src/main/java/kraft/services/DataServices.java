package kraft.services;

import kasper.domain.metamodel.DtDefinition;
import kasper.domain.metamodel.DtField;
import kasper.domain.model.DtList;
import kasper.domain.model.DtObject;
import kasper.domain.util.DtObjectUtil;
import kasper.kernel.Home;
import kasper.kernel.metamodel.URI;
import kasper.persistence.Criteria;
import kasper.persistence.PersistenceManager;
import kasper.transaction.Transactional;

/**
 * On mappe les fonctions du broker en services JSON sur REST.
 * 
 * @author pchretien
 */
public class DataServices {
	@Transactional
	public DtObject getObject(final String dtDefinitionName, final String id) {
		final DtDefinition dtDefinition = Home.getNameSpace().resolve(dtDefinitionName, DtDefinition.class);
		final URI uri = createURI(dtDefinition, id);
		return Home.getContainer().getManager(PersistenceManager.class).getBroker().get(uri);
	}

	private static URI createURI(final DtDefinition dtDefinition, final String id) {
		final DtField pk = DtObjectUtil.getPrimaryKey(dtDefinition);
		return new URI(dtDefinition, pk.getDomain().getFormatter().stringToValue(id, pk.getDomain().getDataType()));
	}

	@Transactional
	public DtList getList(final String dtDefinitionName) {
		final Criteria criteria = null;
		final Integer maxRows = null;
		final DtDefinition dtDefinition = Home.getNameSpace().resolve(dtDefinitionName, DtDefinition.class);

		return Home.getContainer().getManager(PersistenceManager.class).getBroker().getList(dtDefinition, criteria, maxRows);

	}

	@Transactional
	public void save(final DtObject dto) {
		Home.getContainer().getManager(PersistenceManager.class).getBroker().save(dto);
	}

	@Transactional
	public void delete(final String dtDefinitionName, final String id) {
		final DtDefinition dtDefinition = Home.getNameSpace().resolve(dtDefinitionName, DtDefinition.class);
		final URI uri = createURI(dtDefinition, id);
		Home.getContainer().getManager(PersistenceManager.class).getBroker().delete(uri);
	}
}
