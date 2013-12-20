package kasperimpl.persistence.plugins.hibernate;

/**
* Interface de réception des  événements produits par l'exécution de Hibernate.
*
* @author pchretien
* @version $Id: HibernateListener.java,v 1.1 2012/09/27 09:23:55 npiedeloup Exp $
*/
interface HibernateListener {
	/**
	 * Enregistre le début d'exécution d'un PreparedStatement.
	 * @param sql Requête SQL
	 */
	void onSqlStart(String sql);

	/**
	 * Enregistre la fin d'une exécution de PreparedStatement avec le temps d'exécution en ms et son statut (OK/KO).
	 * @param sql Requête SQL
	 * @param elapsedTime Temps d'exécution en ms
	 * @param success Si l'exécution a réussi
	 */
	void onSqlFinish(String sql, long elapsedTime, boolean success);
}
