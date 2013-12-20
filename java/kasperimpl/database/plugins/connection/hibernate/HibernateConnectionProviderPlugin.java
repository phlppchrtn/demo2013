package kasperimpl.database.plugins.connection.hibernate;

import java.sql.SQLException;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import kasper.database.DataBase;
import kasper.database.connection.KConnection;
import kasper.kernel.util.Assertion;
import kasper.transaction.KTransaction;
import kasper.transaction.KTransactionManager;
import kasper.transaction.KTransactionResourceId;
import kasperimpl.database.plugins.connection.AbstractConnectionProviderPlugin;

/**
 * Wrapping des connexions hibernate.
 *
 * @author pchretien
 * @version $Id: HibernateConnectionProviderPlugin.java,v 1.1 2012/09/27 09:23:55 npiedeloup Exp $
 */
public final class HibernateConnectionProviderPlugin extends AbstractConnectionProviderPlugin {
	private static final KTransactionResourceId<HibernateResource> HIBERNATE_RESOURCE_ID = new KTransactionResourceId<HibernateResource>(KTransactionResourceId.Priority.NORMAL,
			"Hibernate");
	private final EntityManagerFactory entityManagerFactory;
	private final KTransactionManager transactionManager;

	/**
	 * Constructeur.
	 * @param dataBase Type de base de données
	 * @param entityManagerFactoryName Nom de l'entityManagerFactory déclaré dans le persistence.xml d'hibertnate.
	 * @param transactionManager Manager des transactions
	 */
	public HibernateConnectionProviderPlugin(final DataBase dataBase, final String entityManagerFactoryName, final KTransactionManager transactionManager) {
		super(dataBase);
		Assertion.notNull(transactionManager);
		//---------------------------------------------------------------------
		this.transactionManager = transactionManager;
		entityManagerFactory = Persistence.createEntityManagerFactory(entityManagerFactoryName);
	}

	/** {@inheritDoc} */
	public KConnection obtainConnection() throws SQLException {
		final HibernateResource resource = obtainHibernateResource();
		return new HibernateConnection(resource.getConnection(), getDataBase());
	}

	/**
	 * @return resource hibernate
	 */
	public HibernateResource obtainHibernateResource() {
		final KTransaction transaction = transactionManager.getCurrentTransaction();
		HibernateResource ktr = transaction.getResource(HIBERNATE_RESOURCE_ID);
		if (ktr == null) {
			ktr = new HibernateResource(entityManagerFactory);
			transaction.addResource(HIBERNATE_RESOURCE_ID, ktr);
		}
		return ktr;
	}
}
