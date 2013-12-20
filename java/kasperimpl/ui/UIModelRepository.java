package kasperimpl.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import kasper.kernel.exception.KRuntimeException;
import kasper.kernel.util.Assertion;
import kasper.ui.UINameSpace;
import kasper.ui.UIRegistry;
import kasper.ui.model.UIComponent;
import kasper.ui.model.UIComponentType;

/**
 * Registry pour le model d'interface graphique.
 *
 * @author  prahmoune
 * @version $Id: UIModelRepository.java,v 1.1 2012/12/05 14:10:23 pchretien Exp $
 */
public final class UIModelRepository implements UIRegistry, UINameSpace {
	private final Map<String, UIComponent> components = new HashMap<String, UIComponent>();
	private final Map<String, UIComponentType> types = new HashMap<String, UIComponentType>();

	/** {@inheritDoc} */
	public void register(final UIComponentType uiComponentType) {
		Assertion.notNull(uiComponentType);
		Assertion.notNull(uiComponentType.getName());
		Assertion.precondition(!types.containsKey(uiComponentType.getName()), "UIComponentType {0} deja enregistré.", uiComponentType.getName());
		//----------------------------------------------------------------------
		types.put(uiComponentType.getName(), uiComponentType);
	}

	/** {@inheritDoc} */
	public void register(final UIComponent uiComponent) {
		Assertion.notNull(uiComponent);
		Assertion.notNull(uiComponent.getName());
		Assertion.precondition(!components.containsKey(uiComponent.getName()), "UIComponent {0} deja enregistré.", uiComponent.getName());
		//----------------------------------------------------------------------
		components.put(uiComponent.getName(), uiComponent);

	}

	/** {@inheritDoc} */
	public UIComponentType getComponentType(final String name) {
		Assertion.notEmpty(name);
		//----------------------------------------------------------------------
		if (types.containsKey(name)) {
			return types.get(name);
		}
		throw new KRuntimeException("le type de composant graphique'" + name + "' n'est pas trouvé.");
	}

	/** {@inheritDoc} */
	public UIComponent getUIComponent(final String name) {
		Assertion.notEmpty(name);
		//----------------------------------------------------------------------
		if (components.containsKey(name)) {
			return components.get(name);
		}
		throw new KRuntimeException("le composant graphique'" + name + "' n'est pas trouvé.");
	}

	/** {@inheritDoc} */
	public Collection<UIComponentType> getUIComponentTypes() {
		return Collections.unmodifiableCollection(types.values());
	}

	/** {@inheritDoc} */
	public Collection<UIComponent> getUIComponents() {
		return Collections.unmodifiableCollection(components.values());
	}

	/** {@inheritDoc} */
	public Collection<UIComponent> getUIComponents(final UIComponentType uiComponentType) {
		Assertion.notNull(uiComponentType);
		//----------------------------------------------------------------------
		final Collection<UIComponent> typedComponents = new ArrayList<UIComponent>();
		for (final UIComponent uiComponent : components.values()) {
			if (uiComponentType.equals(uiComponent.getType())) {
				typedComponents.add(uiComponent);
			}
		}
		return Collections.unmodifiableCollection(typedComponents);
	}

}
