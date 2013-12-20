package kasperimpl.jsf.flow;

import java.beans.FeatureDescriptor;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.PropertyNotFoundException;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.faces.event.PostConstructCustomScopeEvent;
import javax.faces.event.PreDestroyCustomScopeEvent;
import javax.faces.event.ScopeContext;

import kasper.kernel.exception.KRuntimeException;

/**
 * ELResolver pour les customScope JSF
 * 
 * @author prahmoune
 * @version $Id: CustomScopeELResolver.java,v 1.1 2012/12/05 14:21:34 pchretien Exp $
 */
public final class CustomScopeELResolver extends ELResolver {
	private static final String SCOPE_NAME = "flow";

	// ------------------------------------------------- Methods From ELResolver
	/** {@inheritDoc} */
	@Override
	public Object getValue(final ELContext elContext, final Object base, final Object property) {
		if (property == null) {
			throw new PropertyNotFoundException();
		}
		if (base == null && SCOPE_NAME.equals(property.toString())) {
			// explicit scope lookup request
			final CustomScope customScope = getScope(elContext);
			elContext.setPropertyResolved(true);
			return customScope;
		} else if (base instanceof CustomScope) {
			// We're dealing with the custom scope that has been explicity referenced
			// by an expression. 'property' will be the name of some entity
			// within the scope.
			return lookup(elContext, (CustomScope) base, property.toString());
		} else if (base == null) {
			// bean may have already been created and is in scope.
			// check to see if the bean is present
			return lookup(elContext, getScope(elContext), property.toString());
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<?> getType(final ELContext elContext, final Object base, final Object property) {
		return Object.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setValue(final ELContext elContext, final Object base, final Object property, final Object value) {
		// this scope isn't writable in the strict sense, so do nothing.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isReadOnly(final ELContext elContext, final Object base, final Object property) {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<FeatureDescriptor> getFeatureDescriptors(final ELContext elContext, final Object base) {
		return Collections.<FeatureDescriptor> emptyList().iterator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<?> getCommonPropertyType(final ELContext elContext, final Object base) {
		if (base != null) {
			return null;
		}
		return String.class;
	}

	// ---------------------------------------------------------- Public Methods

	/**
	 * Supprime un scope
	 */
	public static void destroyScope(final FacesContext ctx) {
		final Map<String, Object> sessionMap = ctx.getExternalContext().getSessionMap();
		try {
			final CustomScope customScope = (CustomScope) sessionMap.remove(SCOPE_NAME);
			if (customScope != null) {
				customScope.notifyDestroy();
			}
		} catch (final Exception e) {
			throw new KRuntimeException("erreur inattendue", e);
		}
	}

	/**
	 * Supprime un context dans le scope 'flow'
	 * 
	 * @param context le nom du context
	 */
	public static void destroyContext(final FacesContext ctx, final String context) {
		final Map<String, Object> sessionMap = ctx.getExternalContext().getSessionMap();
		try {
			final CustomScope customScope = (CustomScope) sessionMap.get(SCOPE_NAME);
			if (customScope != null) {
				customScope.remove(context);
			}
		} catch (final Exception e) {
			throw new KRuntimeException("erreur inattendue", e);
		}
	}

	// --------------------------------------------------------- Private Methods
	/**
	 * Retourne un custom scope à partir de son nom
	 * 
	 * @param elContext l'elContext
	 */
	private CustomScope getScope(final ELContext elContext) {
		final FacesContext ctx = (FacesContext) elContext.getContext(FacesContext.class);
		final Map<String, Object> sessionMap = ctx.getExternalContext().getSessionMap();

		CustomScope customScope = (CustomScope) sessionMap.get(SCOPE_NAME);
		if (customScope == null) {
			customScope = new CustomScope(ctx.getApplication());
			sessionMap.put(SCOPE_NAME, customScope);
			customScope.notifyCreate();
		}

		return customScope;
	}

	/**
	 * Retourne le bean d'un scope à partir de son nom
	 * 
	 * @param elContext l'elContext
	 * @param scope le scope
	 * @param key le nom du bean
	 */
	private Object lookup(final ELContext elContext, final CustomScope scope, final String key) {
		final Object value = scope.get(key);
		elContext.setPropertyResolved(value != null);
		return value;

	}

	// ---------------------------------------------------------- Nested Classes

	private static final class CustomScope extends ConcurrentHashMap<String, Object> {
		private static final long serialVersionUID = 1L;
		private final Application application;

		// -------------------------------------------------------- Constructors

		CustomScope(final Application application) {
			this.application = application;
		}

		// ------------------------------------------------------ Public Methods

		/**
		 * Publishes <code>PostConstructCustomScopeEvent</code> to notify interested parties that this scope is now
		 * available.
		 */
		public void notifyCreate() {

			final ScopeContext context = new ScopeContext(SCOPE_NAME, this);
			application.publishEvent(FacesContext.getCurrentInstance(), PostConstructCustomScopeEvent.class, context);

		}

		/**
		 * Publishes <code>PreDestroyCustomScopeEvent</code> to notify interested parties that this scope is being
		 * destroyed.
		 */
		public void notifyDestroy() {

			// notify interested parties that this scope is being
			// destroyed
			final ScopeContext scopeContext = new ScopeContext(SCOPE_NAME, this);
			application.publishEvent(FacesContext.getCurrentInstance(), PreDestroyCustomScopeEvent.class, scopeContext);

		}

	}
}
