package kasperimpl.persistence.plugins.hibernate;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import kasper.analytics.AnalyticsManager;
import kasper.database.ConnectionProviderPlugin;
import kasper.database.DataBaseManager;
import kasper.domain.metamodel.DtDefinition;
import kasper.domain.metamodel.DtField;
import kasper.domain.model.DtList;
import kasper.domain.model.DtObject;
import kasper.domain.util.DtObjectUtil;
import kasper.kernel.exception.KRuntimeException;
import kasper.kernel.metamodel.URI;
import kasper.kernel.util.Assertion;
import kasper.persistence.Criteria;
import kasper.work.WorkManager;
import kasperimpl.database.plugins.connection.hibernate.HibernateConnectionProviderPlugin;
import kasperimpl.database.plugins.connection.hibernate.HibernateResource;
import kasperimpl.persistence.plugins.AbstractSQLStorePlugin;
import kasperimpl.task.model.AbstractTaskEngine;
import kasperx.task.TaskEngineProc;

import org.hibernate.ReplicationMode;
import org.hibernate.Session;
import org.hibernate.ejb.EntityManagerImpl;

/**
 * Implémentation du Store en utilisant hibernate.
 *
 * @author pchretien
 * @version $Id: HibernateStorePlugin.java,v 1.1 2012/09/27 09:23:55 npiedeloup Exp $
 */
public final class HibernateStorePlugin extends AbstractSQLStorePlugin {
	private final HibernateListener hibernateListener;
	private final DataBaseManager dataBaseManager;

	/**
	 * Constructeur.
	 * @param workManager Manager des works
	 * @param analyticsManager Manager de la performance applicative
	 * @param dataBaseManager Manager de base de données
	 */
	@Inject
	public HibernateStorePlugin(final WorkManager workManager, final AnalyticsManager analyticsManager, final DataBaseManager dataBaseManager) {
		super(workManager);
		Assertion.notNull(dataBaseManager);
		//---------------------------------------------------------------------
		hibernateListener = new HibernateListenerImpl(analyticsManager);
		this.dataBaseManager = dataBaseManager;
	}

	private HibernateListener getDataBaseListener() {
		return hibernateListener;
	}

	@SuppressWarnings("unchecked")
	private <D extends DtObject> D loadWithoutClear(final URI<D> uri) {
		final DtDefinition dtDefinition = uri.getDefinition();

		final EntityManager em = getEntityManager();
		final Object id = uri.getKey();
		final String taskName = "Hibernate:find " + getTableName(dtDefinition);
		final long start = System.currentTimeMillis();
		boolean executed = false;
		getDataBaseListener().onSqlStart(taskName);
		try {
			final D result = (D) em.find(Class.forName(dtDefinition.getClassCanonicalName()), id);
			executed = true;
			return result;
			//Objet null géré par le broker
		} catch (final ClassNotFoundException e) {
			throw new KRuntimeException("StoreHibernate", e);
		} finally {
			getDataBaseListener().onSqlFinish(taskName, System.currentTimeMillis() - start, executed);
		}
	}

	/** {@inheritDoc} */
	@Override
	public <D extends DtObject> D load(final URI<D> uri) {
		final D dto = this.<D> loadWithoutClear(uri);
		//On détache le DTO du contexte hibernate
		//De cette façon on interdit à hibernate d'utiliser son cache
		getEntityManager().clear();
		return dto;
	}

	/** {@inheritDoc} */
	@Override
	public void put(final DtObject dto) {
		final EntityManager em = getEntityManager();
		final DtDefinition dtDefinition = DtObjectUtil.findDtDefinition(dto);

		final String taskName = "Hibernate:merge " + getTableName(dtDefinition);
		final long start = System.currentTimeMillis();
		boolean executed = false;
		getDataBaseListener().onSqlStart(taskName);
		try {
			final DtField pk = DtObjectUtil.getPrimaryKey(dtDefinition);
			if (pk.getDataAccessor().getValue(dto) == null) {
				//Si l'objet est en cours de création (pk null)
				//(l'objet n'est pas géré par hibernate car les objets sont toujours en mode détaché :
				// sinon on ferait persist aussi si em.contains(dto)).
				em.persist(dto);
			} else {
				em.merge(dto);
			}
			em.flush();
			em.clear();

			refreshVersion(dto);
			executed = true;
		} finally {
			getDataBaseListener().onSqlFinish(taskName, System.currentTimeMillis() - start, executed);
		}
	}

	/** {@inheritDoc} */
	@Override
	protected String createInsertQuery(final DtDefinition dtDefinition) {
		throw new UnsupportedOperationException();
	}

	private void refreshVersion(final DtObject dto) {
		// hibernate semble mettre à jour la valeur de version dans la bdd
		// mais pas dans l'objet
		if (dto instanceof OptimisticLocking) {
			final OptimisticLocking optimisticLocking = (OptimisticLocking) dto;
			Long version = optimisticLocking.getVersion();
			if (version == null) {
				version = 0L;
			} else {
				version = version.longValue() + 1L;
			}
			optimisticLocking.setVersion(version);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void merge(final DtObject dto) {
		final EntityManager em = getEntityManager();
		final String taskName = "Hibernate:replicate " + getTableName(DtObjectUtil.findDtDefinition(dto));
		final long start = System.currentTimeMillis();
		boolean executed = false;
		getDataBaseListener().onSqlStart(taskName);
		try {
			getSession().replicate(dto, ReplicationMode.LATEST_VERSION);
			em.flush();
			em.clear();

			refreshVersion(dto);
			executed = true;
		} finally {
			getDataBaseListener().onSqlFinish(taskName, System.currentTimeMillis() - start, executed);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void remove(final URI<? extends DtObject> uri) {
		final EntityManager em = getEntityManager();
		final DtDefinition dtDefinition = uri.getDefinition();

		final String taskName = "Hibernate:remove " + getTableName(dtDefinition);
		final long start = System.currentTimeMillis();
		boolean executed = false;
		getDataBaseListener().onSqlStart(taskName);
		try {
			final Object dto = loadWithoutClear(uri);
			if (dto == null) {
				throw new KRuntimeException("Aucune ligne supprimée", null);
			}
			em.remove(dto);
			em.flush();
			em.clear();
			executed = true;
		} finally {
			getDataBaseListener().onSqlFinish(taskName, System.currentTimeMillis() - start, executed);
		}
	}

	/** {@inheritDoc} */
	@Override
	@SuppressWarnings("unchecked")
	public <D extends DtObject> DtList<D> loadList(final DtDefinition dtDefinition, final Criteria<D> criteria, final Integer maxRows) {
		if (criteria != null) {
			//On ne passe pas par hibernate
			return super.loadList(dtDefinition, criteria, maxRows);
		}
		final String tableName = getTableName(dtDefinition);
		final EntityManager em = getEntityManager();
		final String taskName = "Hibernate:list " + tableName;
		final long start = System.currentTimeMillis();
		boolean executed = false;
		getDataBaseListener().onSqlStart(taskName);
		try {
			//La query Hibernate travaille avec le nom de la classe et non avec le nom de la table.
			final Query query = em.createQuery("select o from " + dtDefinition.getClassSimpleName() + " as o");

			if (maxRows != null) {
				query.setMaxResults(maxRows.intValue());
			}
			final List<D> queryResultList = query.getResultList();
			final DtList<D> result = new DtList<D>(dtDefinition);
			for (final D o : queryResultList) {
				result.add(o);
			}
			em.clear();
			executed = true;
			return result;
		} finally {
			//On apparente cette exécution à une 
			getDataBaseListener().onSqlFinish(taskName, System.currentTimeMillis() - start, executed);
		}
	}

	/** {@inheritDoc} */
	@Override
	protected Class<? extends AbstractTaskEngine> getTaskEngineClass(final boolean insert) {
		return TaskEngineProc.class;
	}

	private HibernateResource obtainHibernateResource() {
		final ConnectionProviderPlugin connectionProvider = dataBaseManager.getConnectionProviderPlugin();
		Assertion.precondition(connectionProvider instanceof HibernateConnectionProviderPlugin, "Le StoreHibernate nécessite un HibernateConnectionProvider pour fonctionner.");
		//---------------------------------------------------------------------
		final HibernateConnectionProviderPlugin hibernateConnectionProvider = (HibernateConnectionProviderPlugin) connectionProvider;
		return hibernateConnectionProvider.obtainHibernateResource();
	}

	private EntityManager getEntityManager() {
		return obtainHibernateResource().getEntityManager();
	}

	/**
	 * @return Connection la session
	 */
	private Session getSession() {
		return ((EntityManagerImpl) getEntityManager()).getSession();
	}

	//	private Session getSession() {
	//		return obtainHibernateResource().getSession();
	//	}

	/*    private <D extends DtObject> DtList<D> loadListFromSimpleAssociation(
	 KTransaction transaction, DtListURI dtcUri) throws Exception {
	 final EntityManager em = getEntityManager(transaction);

	 final DtDefinition dtDefinition = dtcUri.getDefinition();
	 final DtObjectURI uri = dtcUri.getSource();
	 final String fkFieldName = dtcUri.getAssociationDefinition()
	 .getAssociationNode(dtcUri.getRoleName()).getFieldName();
	 DtList<D> result = new DtListStandard<D>(dtcUri
	 .getDefinition());
	 List<D> tempList = em.createQuery(
	 "select a from "
	 + Class.forName(definition.getJavaClassName())
	 .getName() + " as a where " + fkFieldName + "="
	 + uri.getKey()).getResultList();
	 for (D d : tempList) {
	 result.add(d);
	 }
	 return result;
	 }
	 */
	/*    private <D extends DtObject> DtList<D> loadListFromNNAssociation(
	 KTransaction transaction, DtListURI dtcUri) throws Exception {
	 final DtDefinition dtDefinition = dtcUri.getDefinition();
	 // ----------------------------------------------------------------------
	 final AssociationNNDefinition associationNNDefinition = dtcUri
	 .getAssociationDefinition().castAsAssociationNNDefinition();
	 final String tableName = getTableName(definition);

	 final String joinTableName = associationNNDefinition.getTableName();

	 final DtObjectURI uri = dtcUri.getSource();

	 // PK de la DtList recherchée
	 final String pkFieldName = kasper.core.XRepository.getPrimaryKey(
	 definition).getFieldName();
	 // FK dans la table nn correspondant à la collection recherchée. (clé de
	 // jointure ).
	 final String joinFieldName = dtcUri.getAssociationDefinition()
	 .getAssociationNode(dtcUri.getRoleName()).getFieldName();

	 // La condition s'applique sur l'autre noeud de la relation (par rapport
	 // à la collection attendue)
	 final AssociationNode associationNode = dtcUri
	 .getAssociationDefinition().getAssociationNodeTarget(
	 dtcUri.getRoleName());
	 final String fkFieldName = associationNode.getFieldName();

	 HibernateResource resource = obtainHibernateResource(transaction);
	 EntityManager em = resource.getEntityManager();
	 DtList<D> result = new DtListStandard<D>(dtcUri
	 .getDefinition());
	 StringBuilder request = new StringBuilder(" Select t.* from ").append(
	 tableName).append(" t");
	 request.append(" , ").append(joinTableName).append(" j");
	 // Condition de la recherche
	 request.append(" where j.").append(fkFieldName).append(" = ").append(
	 uri.getKey());
	 // On établit une jointure fermée entre la pk et la fk de la collection
	 // recherchée.
	 request.append(" and j.").append(joinFieldName).append(" = t.").append(
	 pkFieldName);
	 List<D> tempList = em.createNativeQuery(request.toString())
	 .getResultList();
	 for (D d : tempList) {
	 result.add(d);
	 }
	 return result;
	 }*/
	/** {@inheritDoc} */
	@Override
	protected void appendMaxRows(final String separator, final StringBuilder request, final Integer maxRows) {
		request.append(separator).append(" rownum <= ").append(maxRows.toString());
	}
}
