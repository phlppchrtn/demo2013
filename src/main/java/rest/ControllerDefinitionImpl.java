package rest;

import org.apache.log4j.Logger;

/**
 * Implémentation standard de ControllerDefinition.
 *
 * @author  pchretien
 * @version $Id: ControllerDefinitionImpl.java,v 1.26 2010/12/06 15:45:41 dchallas Exp $
 */
public final class ControllerDefinitionImpl implements ControllerDefinition {

	/** 
	 * Marqueur dans les packages, indiquant le début des modules fonctionnels.
	 */
	private static final String CONTROLLER_ROOT_PATH_SEP = "/controller";

	/** Logger. */
	private final Logger log = Logger.getLogger(getClass());

	/**
	 * Si la Page nécessite ou non une authentification
	 */
	private final Boolean needsAuthentification;

	/**
	 * Si la page nécessite ou non une session
	 */
	private final Boolean needsSession;

	/**
	 * Construteur de la définition d'un controller.
	 * @param urnParam urn
	 * @param newPackageName nom du package
	 * @param needsAuthentification Si besoin d'une authentification pour la page
	 * @param needsSession Si besoin d'une session  pour la page.
	 */
	@Deprecated
	public ControllerDefinitionImpl(final String urnParam, final String newPackageName, final Boolean needsAuthentification, final Boolean needsSession) {
		//	super(UIMetaDefinition.CONTROLLER_DEFINITION);
		//----------------------------------------------------------------------
		/*
		Assertion.notNull(controllerClassName);
		*/
		//----------------------------------------------------------------------
		this.needsAuthentification = needsAuthentification;
		this.needsSession = needsSession;

		init(urnParam, newPackageName);

		if (needsAuthentification != null) {
			if (needsAuthentification2() == needsAuthentification) {
				log.debug("  --needsAuthentification " + needsAuthentification + " redondant à supprimer");
			}
		}

		if (needsSession != null) {
			if (needsSession == needsSession2()) {
				log.debug("  --needsSession " + needsSession + " redondant à supprimer");
			}
		}
		//System.out.println("<!--##CONTROLLER : " + toURN() + " : " + ok + " -->");
	}

	//	/**
	//	 * Construteur de la définition d'un controller.
	//	 */
	//	public ControllerDefinitionImpl() {
	//		super(ControllerMetaDefinition.CONTROLLER_DEFINITION);
	//		//----------------------------------------------------------------------
	//		this.jspUri = null;
	//		this.controllerClassName = null;
	//		this.needsAuthentification = null;
	//		this.needsSession = null;
	//	}

	/** {@inheritDoc} */
	public boolean needsAuthentification() {
		return needsAuthentification == null ? needsAuthentification2() : needsAuthentification;
	}

	/** {@inheritDoc} */
	public boolean needsSession() {
		return needsSession == null ? needsSession2() : needsSession;
	}

	/** {@inheritDoc} */
	public String getControllerClassName() {
		return getPackageName() + "." + StringHelper.constToCamelCase(getShortName(), true) + "Controller";
	}

	//Nouvelles méthodes 

	/** {@inheritDoc} */
	public String getFriendlyName() {
		return StringHelper.constToCamelCase(getShortName(), false) + ".html";
	}

	/** {@inheritDoc} */
	public String getJspUri() {
		String path = getPackageName().replace('.', '/');
		path = path.substring(path.indexOf(CONTROLLER_ROOT_PATH_SEP) + CONTROLLER_ROOT_PATH_SEP.length());
		return "/jsp" + path + "/" + StringHelper.constToCamelCase(getShortName(), false) + ".jsp";
	}

	private boolean needsAuthentification2() {
		return true;
	}

	private boolean needsSession2() {
		return true;
	}

	//
	//	/**
	//	 * Enregistre un nouveau type d'action à la définition.
	//	 * @param actionDefinition Type d'action
	//	 */
	//	public void registerActionDefinition(final ActionDefinition actionDefinition) {
	//		Assertion.notNull(actionDefinition);
	//		Assertion.precondition(!actionDefinitionMap.containsKey(actionDefinition.getName()), "Action {0} déjà enregistré sur {1}", actionDefinition.getName(), this);
	//		//----------------------------------------------------------------------
	//		actionDefinitionMap.put(actionDefinition.getName(), actionDefinition);
	//	}
	//
	//	/** {@inheritDoc} */
	//	public ActionDefinition getActionDefinition(final String actionName) {
	//		final ActionDefinition actionDefinition = actionDefinitionMap.get(actionName);
	//		Assertion.notNull(actionDefinition, "action ''{0}'' non trouvée pour le controller ''{1}''. Liste des actions disponibles {2}", actionName, this, actionDefinitionMap.keySet());
	//		return actionDefinition;
	//	}
	//
	//	/**
	//	 * Rend la définition non modifiable, non mutable.
	//	 */
	//	public void makeUnmodifiable() {
	//		//On rend non mutable la définition des actions
	//		actionDefinitionMap = java.util.Collections.unmodifiableMap(actionDefinitionMap);
	//	}
	//
	//	/** {@inheritDoc} */
	//	public Collection<ActionDefinition> getActionDefinitionCollection() {
	//		return actionDefinitionMap.values();
	//	}
}
