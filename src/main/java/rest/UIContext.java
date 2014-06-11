package rest;

import javax.servlet.http.HttpServletRequest;

/**
 * Context partag� des composants graphique.
 * @author npiedeloup
 * @version $Id: UIContext.java,v 1.6 2010/11/16 10:36:49 pchretien Exp $
 */
public interface UIContext {

	//=========================================================================
	// R�cup�ration du context (scope page)
	//=========================================================================

	/**
	 * @return Context des donn�es
	 */
	Context getContext();

	/**
	 * Retourne l'adresse de la page.
	 * @return Adresse de la page.
	 */
	String getAddress();

	Message refreshPage();

	//=========================================================================
	// R�cup�ration du context (scope request)
	//=========================================================================
	/**
	 * @return Structure des erreurs relatives � la page
	 */
	ErrorProcess getErrorPage();

	/**
	 * @return Exception li�e au traitement. (si null alors aucune exception)
	 */
	Exception getException();

	/**
	 * @return Request HTTP associ�e � la page.
	 */
	HttpServletRequest getRequest();

	//=========================================================================
	// Traitement
	//=========================================================================

	/**
	 * @param urlToEncode Url de d�part
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
	//	 * @return map des listener pos�s sur le body HTML de la page
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
	//	 * Ajoute un lien m�ta.
	//	 * @param newMetaLink link.
	//	 */
	//	void addMetaLink(MetaLink newMetaLink);
	//
	//	/**
	//	 * Ajoute un listener sur le body HTML de la page.
	//	 * @param eventType type d'evenement (ex: onLoad)
	//	 * @param functionCall fonction javascript � appeler au d�clenchement de cet evenement
	//	 */
	//	void addBodyListener(final String eventType, final String functionCall);
}
