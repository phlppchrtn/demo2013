package rest;

import javax.servlet.http.HttpServletRequest;

/**
 * Context partagé des composants graphique.
 * @author npiedeloup
 * @version $Id: UIContext.java,v 1.6 2010/11/16 10:36:49 pchretien Exp $
 */
public interface UIContext {

	//=========================================================================
	// Récupération du context (scope page)
	//=========================================================================

	/**
	 * @return Context des données
	 */
	Context getContext();

	/**
	 * Retourne l'adresse de la page.
	 * @return Adresse de la page.
	 */
	String getAddress();

	Message refreshPage();

	//=========================================================================
	// Récupération du context (scope request)
	//=========================================================================
	/**
	 * @return Structure des erreurs relatives à la page
	 */
	ErrorProcess getErrorPage();

	/**
	 * @return Exception liée au traitement. (si null alors aucune exception)
	 */
	Exception getException();

	/**
	 * @return Request HTTP associée à la page.
	 */
	HttpServletRequest getRequest();

	//=========================================================================
	// Traitement
	//=========================================================================

	/**
	 * @param urlToEncode Url de départ
	 * @return Url encoder
	 */
	String encodeURL(String urlToEncode);

	//=========================================================================
	// Peuplement du context de la page
	//=========================================================================
	//	/**
	//	 * @return List des scriptlets
	//	 */
	//	Collection<Scriptlet> getScripts();
	//
	//	/**
	//	 * @return List des MetaLink
	//	 */
	//	Collection<MetaLink> getMetaLink();
	//
	//	/**
	//	 * @return map des listener posés sur le body HTML de la page
	//	 */
	//	Map<String, Collection<String>> getBodyListeners();
	//
	//	/**
	//	 * Ajoute un bloc de script.
	//	 * @param newScriptlet Script.
	//	 */
	//	void addScript(Scriptlet newScriptlet);
	//
	//	/**
	//	 * Ajoute un lien méta.
	//	 * @param newMetaLink link.
	//	 */
	//	void addMetaLink(MetaLink newMetaLink);
	//
	//	/**
	//	 * Ajoute un listener sur le body HTML de la page.
	//	 * @param eventType type d'evenement (ex: onLoad)
	//	 * @param functionCall fonction javascript à appeler au déclenchement de cet evenement
	//	 */
	//	void addBodyListener(final String eventType, final String functionCall);
}
