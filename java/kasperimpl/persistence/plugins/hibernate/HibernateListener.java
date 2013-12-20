package kasperimpl.persistence.plugins.hibernate;

/**
* Interface de r�ception des  �v�nements produits par l'ex�cution de Hibernate.
*
* @author pchretien
* @version $Id: HibernateListener.java,v 1.1 2012/09/27 09:23:55 npiedeloup Exp $
*/
interface HibernateListener {
	/**
	 * Enregistre le d�but d'ex�cution d'un PreparedStatement.
	 * @param sql Requ�te SQL
	 */
	void onSqlStart(String sql);

	/**
	 * Enregistre la fin d'une ex�cution de PreparedStatement avec le temps d'ex�cution en ms et son statut (OK/KO).
	 * @param sql Requ�te SQL
	 * @param elapsedTime Temps d'ex�cution en ms
	 * @param success Si l'ex�cution a r�ussi
	 */
	void onSqlFinish(String sql, long elapsedTime, boolean success);
}
