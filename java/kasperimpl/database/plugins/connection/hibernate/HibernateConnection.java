package kasperimpl.database.plugins.connection.hibernate;

import java.sql.Connection;

import kasper.database.DataBase;
import kasper.database.connection.KConnection;
import kasper.kernel.util.Assertion;

/**
 * Connexion Hibernate.
 *
 * @author pchretien
 * @version $Id: HibernateConnection.java,v 1.1 2012/09/27 09:23:55 npiedeloup Exp $
 */
final class HibernateConnection implements KConnection {
	private final Connection jdbcConnection;
	private final DataBase dataBase;

	/**
	 * Constructeur.
	 *
	 * @param jdbcConnection Connexion JDBC
	 * @param dataBase Base de données
	 */
	public HibernateConnection(final Connection jdbcConnection, final DataBase dataBase) {
		Assertion.notNull(jdbcConnection);
		Assertion.notNull(dataBase);
		//----------------------------------------------------------------------
		this.jdbcConnection = jdbcConnection;
		this.dataBase = dataBase;
	}

	/** {@inheritDoc} */
	public void commit() {
		//géré par hibernate.
	}

	/** {@inheritDoc} */
	public void rollback() {
		//géré par hibernate.
	}

	/** {@inheritDoc} */
	public Connection getJdbcConnection() {
		return jdbcConnection;
	}

	/** {@inheritDoc} */
	public DataBase getDataBase() {
		return dataBase;
	}

	/** {@inheritDoc} */
	public void release() throws Exception {
		jdbcConnection.close();
	}
}
