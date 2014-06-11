package rest;

import io.vertigo.kernel.lang.Assertion;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Impl�mentation de la Chaine de traitement des requ�tes HTTP.
 * 
 * @author npiedeloup
 * @version $Id: HandlerChainImpl.java,v 1.8 2010/11/16 10:36:49 pchretien Exp $
 */
public final class HandlerChainImpl implements HandlerChain {
	private final List<Handler> handlerList;
	private final int offset;
	private boolean isLock;

	/**
	 * Constructeur.
	 */
	public HandlerChainImpl() {
		handlerList = new ArrayList<>();
		offset = 0;
	}

	/**
	 * Constructeur priv�, pour g�rer l'avancement dans la chaine.
	 * @param previous Pr�c�dente chaine
	 */
	private HandlerChainImpl(final HandlerChainImpl previous) {
		Assertion.checkArgument(previous.offset < 50, "La chain a parcouru 50 Handlers, arr�t volontaire : une boucle infinie est suspect�e.");
		//---------------------------------------------------------------------
		handlerList = previous.handlerList;
		offset = previous.offset + 1; //on avance
		isLock = true;
	}

	/** {@inheritDoc} */
	public void doFilter(final HttpServletRequest request, final HttpServletResponse response, final RequestContext requestContext) throws Exception {
		isLock = true;
		if (offset < handlerList.size()) {
			final Handler nextHandler = handlerList.get(offset);
			//System.out.println(">>> before doFilter " + nextHandler);
			nextHandler.doFilter(request, response, requestContext, new HandlerChainImpl(this));
			//System.out.println("<<< after doFilter " + nextHandler);
		}
	}

	/**
	 * Ajout d'un handler � la chaine (uniquement pendant la phase d'initialisation).
	 * @param newHandler Handler � ajouter
	 */
	public void addHandler(final Handler newHandler) {
		Assertion.checkNotNull(newHandler);
		Assertion.checkArgument(!isLock, "Il n'est possible d'ajouter des handlers sur une chain en cours d'utilisation");
		//---------------------------------------------------------------------	
		//System.out.println("+++ addHandler " + newHandler);
		handlerList.add(newHandler);
	}

}
