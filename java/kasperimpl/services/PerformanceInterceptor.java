package kasperimpl.services;

import java.lang.reflect.Method;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import kasper.analytics.AnalyticsManager;
import kasper.kernel.util.Assertion;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * Intercepteur pour la gestion des log au niveau de la couche service.
 * @author prahmoune
 * @version $Id: PerformanceInterceptor.java,v 1.2 2012/04/04 12:32:48 pchretien Exp $
 */
@Named("LogInterceptor")
public final class PerformanceInterceptor implements MethodInterceptor {
	@Inject
	private AnalyticsManager analyticsManager;
	//	@Inject
	//	private ComponentManager componentManager;

	private PerformanceListener performanceListener;
	private Map<String, String> componentNamesMap;

	@PostConstruct
	public void init() {
		performanceListener = new PerformanceListenerImpl(analyticsManager);
		//componentNamesMap = componentManager.getComponentNamesMap();
	}

	/** {@inheritDoc} */
	public Object invoke(final MethodInvocation invocation) throws Throwable {
		final String facadeName = componentNamesMap.get(invocation.getThis().getClass().getName());
		Assertion.notEmpty(facadeName);
		//-----------------------------------------------------------------------------
		final Method method = invocation.getMethod();
		performanceListener.start(facadeName + '.' + method.getName());
		try {
			// On invoke le bean. (C'est à dire le vrai composant, celui qui implémente les méthodes au final.
			return kasper.kernel.util.ClassUtil.invoke(invocation.getThis(), method, invocation.getArguments());
		} catch (final Throwable throwable) {
			performanceListener.onException(throwable);
			throw throwable;
		} finally {
			performanceListener.stop();
		}
	}
}
