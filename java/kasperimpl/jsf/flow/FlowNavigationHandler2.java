package kasperimpl.jsf.flow;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.NavigationCase;
import javax.faces.context.FacesContext;

import kasper.jsf.flow.Flow;
import kasper.kernel.exception.KRuntimeException;
import kasper.kernel.util.Assertion;
import kasper.kernel.util.ClassUtil;

import org.apache.log4j.Logger;

/**
 * Intercepteur pour la libération explicite du Flow context.
 * Seconde version pour corriger l'appel depuis un composite JSF. 
 * 
 * Attention : cette implémentation n'est pas neutre, la classe du managed bean 
 * est récupérée en faisant getInstance().getClass().
 * 
 * Note : Il s'agit d'une version test 
 *   
 * @author prahmoune, jmainaud
 * @version $Id: FlowNavigationHandler2.java,v 1.1 2012/12/04 12:35:33 pchretien Exp $
 */
public final class FlowNavigationHandler2 extends ConfigurableNavigationHandler {
	/** Loggeur. */
	private static Logger log = Logger.getLogger(FlowNavigationHandler2.class);
	private static final Pattern PATTERN = Pattern.compile("#\\{(\\w+(?:\\.\\w+)*)\\.(\\w+)\\}");

	/** Original navigation handler (decorator pattern). */
	private final ConfigurableNavigationHandler original;

	/**
	 * Constructeur.
	 * 
	 * @param original original navigation handler
	 */
	public FlowNavigationHandler2(final ConfigurableNavigationHandler original) {
		Assertion.notNull(original);
		// ---------------------------------------------------------------------
		this.original = original;
	}

	/** {@inheritDoc} */
	@Override
	public void handleNavigation(final FacesContext context, final String actionMethod, final String actionName) {
		if (log.isDebugEnabled()) {
			log.debug("navigation handler, actionMethod '" + actionMethod + "', actionName '" + actionName + "'");
		}
		original.handleNavigation(context, actionMethod, actionName);

		if (actionMethod != null) {
			final Matcher m = PATTERN.matcher(actionMethod);
			if (m.matches()) {
				final String beanName = m.group(1);
				final String methodName = m.group(2);
				handleFlowContext(context, beanName, methodName);
			}
		}
	}

	private void handleFlowContext(final FacesContext context, final String beanName, final String methodName) {
		if (log.isDebugEnabled()) {
			log.debug("Appel handleFlowContext pour le 'managed bean' '" + beanName + "', method '" + methodName + "'");
		}
		try {
			final ExpressionFactory elFactory = ExpressionFactory.newInstance();
			final ValueExpression beanExpr = elFactory.createValueExpression(context.getELContext(), "#{" + beanName + "}", Object.class);
			final Class<?> clazz = beanExpr.getValue(context.getELContext()).getClass();
			final Method method = ClassUtil.findMethod(clazz, methodName);

			if (method.isAnnotationPresent(Flow.class)) {
				final Flow flow = method.getAnnotation(Flow.class);
				if (!Flow.NONE.equals(flow.clear())) {
					if (log.isDebugEnabled()) {
						log.debug("clear : suppression du context '" + flow.clear() + "'");
					}
					CustomScopeELResolver.destroyContext(context, flow.clear());
				}
			}
		} catch (final Throwable t) {
			throw new KRuntimeException("erreur inattendue", t);
		}
	}

	/** {@inheritDoc} */
	@Override
	public NavigationCase getNavigationCase(final FacesContext arg0, final String arg1, final String arg2) {
		return original.getNavigationCase(arg0, arg1, arg2);
	}

	/** {@inheritDoc} */
	@Override
	public Map<String, Set<NavigationCase>> getNavigationCases() {
		return original.getNavigationCases();
	}
}
