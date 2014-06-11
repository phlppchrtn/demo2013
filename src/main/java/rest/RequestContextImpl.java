package rest;

import io.vertigo.kernel.exception.VRuntimeException;
import io.vertigo.kernel.lang.Assertion;

import javax.servlet.ServletRequest;

import org.w3c.dom.events.UIEvent;

/**
 * Implémentation du context de traitement des requêtes HTTP.
 * 
 * @author npiedeloup
 * @version $Id: RequestContextImpl.java,v 1.16 2010/11/16 10:36:49 pchretien Exp $
 */
public final class RequestContextImpl implements RequestContext {
	private static final String CURRENT_CONTROLLER = "CURRENT_CONTROLLER";
	private static final String CURRENT_EXCEPTION = "CURRENT_EXCEPTION";

	private final ServletRequest request;

	//on peut setter tous ca avec un setAttribute sur request. Mais ca restera lors des forward.
	private ControllerDefinition controllerDefinition;
	private Context context;
	private UIController controller;
	private UIEvent event;
	private final ErrorProcess errorStack = new ErrorProcessImpl();

	/**
	 * Constructeur.
	 * @param request Requete associée à ce context
	 */
	public RequestContextImpl(final ServletRequest request) {
		Assertion.checkNotNull(request);
		//---------------------------------------------------------------------
		this.request = request;
	}

	/** {@inheritDoc} */
	public ControllerDefinition getControllerDefinition() {
		return controllerDefinition;
	}

	/** {@inheritDoc} */
	public void setControllerDefinition(final ControllerDefinition controllerDefinition) {
		Assertion.checkNotNull(controllerDefinition);
		//---------------------------------------------------------------------
		this.controllerDefinition = controllerDefinition;
	}

	/** {@inheritDoc} */
	public Context getContext() {
		return context;
	}

	/** {@inheritDoc} */
	public void setContext(final Context context) {
		Assertion.checkNotNull(context);
		//---------------------------------------------------------------------
		this.context = context;
	}

	/** {@inheritDoc} */
	public UIController getUIController() {
		return controller;
	}

	/** {@inheritDoc} */
	public void setUIController(final UIController newController) {
		Assertion.checkNotNull(newController);
		//---------------------------------------------------------------------
		controller = newController;
		request.setAttribute(CURRENT_CONTROLLER, controller);
		if (controller instanceof Controller) {
			setControllerDefinition(((Controller) controller).getDefinition());
		}
	}

	/**
	 * Récupère le controller de page courant.
	 * @param request Requete courante
	 * @return Controller courant déclaré dans cette requete
	 */
	public static Controller getController(final ServletRequest request) {
		final Object currentController = request.getAttribute(CURRENT_CONTROLLER);
		Assertion.checkArgument(currentController instanceof Controller, "Le controller doit être un Controller de page");
		//---------------------------------------------------------------------
		return (Controller) currentController;
	}

	/** {@inheritDoc} */
	public UIEvent getEvent() {
		return event;
	}

	/** {@inheritDoc} */
	public void setEvent(final UIEvent event) {
		Assertion.checkNotNull(event);
		//---------------------------------------------------------------------
		this.event = event;
	}

	/** {@inheritDoc} */
	public ErrorProcess getErrorProcess() {
		return errorStack;
	}

	/** {@inheritDoc} */
	public boolean hasUserException() {
		return getException() != null;
	}

	/** {@inheritDoc} */
	public void setUserException(final VRuntimeException userException) {
		Assertion.checkNotNull(userException);
		Assertion.checkArgument(!hasUserException(), "L'exception à déjà été enregistrée");
		//---------------------------------------------------------------------
		request.setAttribute(CURRENT_EXCEPTION, userException);
	}

	/**
	 * Récupère l'exception déclarée, s'il y en a une.
	 * (NULLABLE)
	 * @return Exception déclarée dans cette requete, ou null si pas d'erreur
	 */
	public VRuntimeException getException() {
		return (VRuntimeException) request.getAttribute(CURRENT_EXCEPTION);
	}

	/** {@inheritDoc} */
	public RequestContext createForwardContext() {
		final RequestContextImpl newRequestContextImpl = new RequestContextImpl(request);
		newRequestContextImpl.setControllerDefinition(controllerDefinition);
		newRequestContextImpl.setContext(context);
		return newRequestContextImpl;
	}
}
