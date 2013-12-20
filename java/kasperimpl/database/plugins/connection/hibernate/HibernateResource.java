package kasperimpl.database.plugins.connection.hibernate;

import java.sql.Connection;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import kasper.kernel.util.Assertion;
import kasper.transaction.KTransactionResource;

import org.hibernate.Session;
import org.hibernate.ejb.EntityManagerImpl;

/**
 * Ressource hibernate.
 * Les "transactions" hibernate sont considérées comme une ressource d'une
 * transaction plus globale pouvant gérer d'autres ressources comme des mails,
 * des fichiers...
 *
 * @author pchretien
 * @version $Id: HibernateResource.java,v 1.1 2012/09/27 09:23:55 npiedeloup Exp $
 */
public final class HibernateResource implements KTransactionResource {
	private final EntityManager em;
	private final EntityTransaction tx;

	/**
	 * Constructeur.
	 *
	 * @param entityManagerFactory EntityManagerFactory
	 */
	HibernateResource(final EntityManagerFactory entityManagerFactory) {
		em = entityManagerFactory.createEntityManager();
		tx = em.getTransaction();
		// ---------------------------------------------------------------------
		Assertion.notNull(tx);
		Assertion.notNull(em);
		// ---------------------------------------------------------------------
		tx.begin();
	}

	/**
	 * @return Connection Connexion JDBC
	 */
	Connection getConnection() {
		/** TODO que peut-on utiliser à la place de Session.connection ?
		 * http://opensource.atlassian.com/projects/hibernate/browse/HHH-2603 */
		return getSession().connection();
	}

	/**
	 * @return Connection la session
	 */
	private Session getSession() {
		return ((EntityManagerImpl) em).getSession();
	}

	/** {@inheritDoc} */
	public void commit() throws Exception {
		tx.commit();
	}

	/** {@inheritDoc} */
	public void rollback() throws Exception {
		tx.rollback();
	}

	/** {@inheritDoc} */
	public void release() throws Exception {
		em.close();
	}

	/**
	 * @return EntityManager hibernate lié à la ressource
	 */
	public EntityManager getEntityManager() {
		return em;
	}
}
