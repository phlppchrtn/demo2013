package kasperimpl.ui;

import javax.inject.Inject;

import kasper.kernel.util.Assertion;
import kasper.locale.LocaleManager;
import kasper.ui.UIManager;
import kasper.ui.UINameSpace;
import kasper.ui.UIRegistry;

/**
 * Implémentation du gestionnaire de model d'interface graphique.
 * @author prahmoune
 * @version $Id: UIManagerImpl.java,v 1.1 2012/12/05 14:10:23 pchretien Exp $ 
 *
 */
public final class UIManagerImpl implements UIManager {
	private final UIModelPlugin uiModelPlugin;

	/**
	 * Constructeur.
	 * @param localeManager Manager des locales
	 * @param uiModelPlugin UIModelPlugin
	 */
	@Inject
	public UIManagerImpl(final LocaleManager localeManager, final UIModelPlugin uiModelPlugin) {
		Assertion.notNull(localeManager);
		Assertion.notNull(uiModelPlugin);
		//---------------------------------------------------------------------
		this.uiModelPlugin = uiModelPlugin;

		localeManager.add("kasperimpl.ui.model.UI", Resources.values());
		localeManager.add("kasperimpl.jsf.resources.messages", kasperimpl.jsf.resources.Resources.values());
	}

	/** {@inheritDoc} */
	public UINameSpace getUINameSpace() {
		return uiModelPlugin.getUINameSpace();
	}

	/** {@inheritDoc} */
	public UIRegistry getUIRegistry() {
		return uiModelPlugin.getUIRegistry();
	}
}
