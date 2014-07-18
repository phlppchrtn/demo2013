package kasperimpl.persistence.plugins.hibernate;

/**
 * Interface servant � �tiqueter les classes d'objets m�tiers devant effectuer du lock optimiste
 * avec Hibernate.
 *
 * Conform�ment � l'api JPA, ces classes doivent impl�menter un getter et setter
 * "version" de type Long (et avec l'annotation @javax.persistence.Version).
 * Cette interface d'�tiquetage sert en particulier � ce que le num�ro de version dans l'instance de
 * l'objet soit correctement mis � jour apr�s une sauvegarde par Hibernate.
 *
 * @author evernat
 * @version $Id: OptimisticLocking.java,v 1.1 2012/09/27 09:23:55 npiedeloup Exp $
 */
public interface OptimisticLocking {
	/**
	 * @return Pour le lock optimiste, r�cup�re le num�ro de version de l'objet.
	 */
	Long getVersion();

	/**
	 * @param version Pour le lock optimiste, d�finit le num�ro de version de l'objet.
	 */
	void setVersion(Long version);
}
