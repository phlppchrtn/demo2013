package kasperimpl.persistence.plugins.hibernate;

/**
 * Interface servant à étiqueter les classes d'objets métiers devant effectuer du lock optimiste
 * avec Hibernate.
 *
 * Conformément à l'api JPA, ces classes doivent implémenter un getter et setter
 * "version" de type Long (et avec l'annotation @javax.persistence.Version).
 * Cette interface d'étiquetage sert en particulier à ce que le numéro de version dans l'instance de
 * l'objet soit correctement mis à jour après une sauvegarde par Hibernate.
 *
 * @author evernat
 * @version $Id: OptimisticLocking.java,v 1.1 2012/09/27 09:23:55 npiedeloup Exp $
 */
public interface OptimisticLocking {
	/**
	 * @return Pour le lock optimiste, récupère le numéro de version de l'objet.
	 */
	Long getVersion();

	/**
	 * @param version Pour le lock optimiste, définit le numéro de version de l'objet.
	 */
	void setVersion(Long version);
}
