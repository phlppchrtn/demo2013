package kasperimpl.persistence.plugins.hibernate;

import kasper.analytics.AnalyticsManager;
import kasper.kernel.util.Assertion;

import org.apache.log4j.Logger;

/**
 * Implémentation standard du Listener de réception des événements produits par l'exécution des tachess.
 * 
 * @author pchretien
 * @version $Id: HibernateListenerImpl.java,v 1.1 2012/09/27 09:23:55 npiedeloup Exp $
 */
final class HibernateListenerImpl implements HibernateListener {
	/** ProcessMetaData Requete SQL */
	private static final String MD_DB_SQL = "DB_SQL";
	/** ProcessMeasure Temps base de données */
	private static final String ME_DB_TIME = "DB_TIME";

	private static final String MS = " ms)";

	/** Mécanisme de log utilisé pour le sql. */
	private final Logger sqlLog;

	private final AnalyticsManager analyticsManager;

	/**
	 * Constructeur.
	 * @param analyticsManager Manager Analytics
	 */
	HibernateListenerImpl(final AnalyticsManager analyticsManager) {
		Assertion.notNull(analyticsManager);
		//---------------------------------------------------------------------
		sqlLog = Logger.getLogger("Hibernate");
		this.analyticsManager = analyticsManager;
	}

	/** {@inheritDoc} */
	public void onSqlStart(final String sql) {
		if (sqlLog.isTraceEnabled()) {
			sqlLog.trace("Execution du sql : " + sql);
		}
		analyticsManager.getAgent().addMetaData(MD_DB_SQL, sql);
	}

	/** {@inheritDoc} */
	public void onSqlFinish(final String sql, final long elapsedTime, final boolean success) {
		if (sqlLog.isInfoEnabled()) {
			// on passe le preparedStatement en argument pour éviter de
			// construire la query si pas nécessaire
			if (success) {
				sqlLog.info("Execution du sql : " + sql + " reussie en  ( " + elapsedTime + MS);
			} else {
				sqlLog.info("Execution du sql : " + sql + " interrompue apres ( " + elapsedTime + MS);
			}
		}
		//On choisit d'incrémenter l'indicateur.
		//Se faisant on perd le moyen de faire la moyenne par requete, 
		//Si le besoin apparaissait il faudrait creer un sous-process autour des appels
		analyticsManager.getAgent().incMeasure(ME_DB_TIME, elapsedTime);
	}
}
