package kasperimpl.ui.plugins.javamodel;

import kasper.ui.UINameSpace;
import kasper.ui.UIRegistry;
import kasperimpl.ui.UIModelPlugin;
import kasperimpl.ui.UIModelRepository;

public final class JavaUIModelPlugin implements UIModelPlugin {
	private final UIModelRepository uiModelRepository;

	public JavaUIModelPlugin() {
		uiModelRepository = new UIModelRepository();
	}

	/** {@inheritDoc} */
	public UINameSpace getUINameSpace() {
		return uiModelRepository;
	}

	/** {@inheritDoc} */
	public UIRegistry getUIRegistry() {
		return uiModelRepository;
	}
}
