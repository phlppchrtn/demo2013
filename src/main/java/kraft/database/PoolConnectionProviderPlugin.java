package kraft.database;

import java.sql.SQLException;

import javax.inject.Inject;

import kasper.database.connection.KConnection;
import kasperimpl.database.connection.KConnectionImpl;
import kasperimpl.database.plugins.connection.AbstractConnectionProviderPlugin;

public final class PoolConnectionProviderPlugin extends AbstractConnectionProviderPlugin {
	private final MiniConnectionPoolManager poolMgr;

	@Inject
	public PoolConnectionProviderPlugin() throws SQLException {
		super(new kasperimpl.database.vendor.oracle.OracleDataBase());

		// ----------------------------------------------------------------------
		final oracle.jdbc.pool.OracleConnectionPoolDataSource dataSource = new oracle.jdbc.pool.OracleConnectionPoolDataSource();
		dataSource.setDriverType("thin");
		dataSource.setServerName("mira.dev.klee.lan.net");
		dataSource.setPortNumber(1521);
		dataSource.setServiceName("O11W1252");
		dataSource.setUser("kasper_demo");
		dataSource.setPassword("kasper");
		poolMgr = new MiniConnectionPoolManager(dataSource, 10);
	}

	/** {@inheritDoc} */
	public KConnection obtainConnection() throws SQLException {
		return new KConnectionImpl(poolMgr.getConnection(), getDataBase());
	}
}
