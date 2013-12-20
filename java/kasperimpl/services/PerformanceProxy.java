package kasperimpl.services;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import kasper.kernel.util.Assertion;

/**
 * Proxy permettant de mesurer les performances sur les composants métier. On rappelle que les composants facade sont
 * définis par leur facade.
 * 
 * @author pchretien, evernat
 * @version $Id: PerformanceProxy.java,v 1.1 2012/03/23 13:22:03 pchretien Exp $
 * @deprecated Utiliser le PerformanceInterceptor
 */
@Deprecated
final class PerformanceProxy implements InvocationHandler {
	// Nom des facades commence par
	private static final String END_CLASS_NAME = "Services";
	private final Object services;
	private final String name;
	private final PerformanceListener performanceListener;

	/**
	 * On crée un proxy qui intercepte toutes les méthodes de Facade des interfaces sous-typant Facade.
	 * 
	 * @param services Services
	 */
	PerformanceProxy(final Object services, final PerformanceListener performanceListener) {
		super();
		Assertion.notNull(services);
		Assertion.notNull(performanceListener);
		// ---------------------------------------------------------------------
		this.performanceListener = performanceListener;
		// on détermine le nom de la façade en cherchant une interface dont le nom commence par "Facade"
		// car en général servicesBean est déjà un proxy créé par ailleurs (pour logs par exemple)
		final String tmp = buildName(services);
		// ----------------------------------------------------------------------
		name = tmp;
		this.services = services;
	}

	private String buildName(final Object servicesBean) {
		for (final Class facadeInterface : servicesBean.getClass().getInterfaces()) {
			if (facadeInterface.getSimpleName().endsWith(END_CLASS_NAME)) {
				return facadeInterface.getSimpleName();
			}
		}
		// si pas d'interface Facade trouvée, on prend au pire le nom de la classe
		// ($ProxyN si c'est un proxy)
		return getClass().getSimpleName();
	}

	String getFacadeName() {
		return name;
	}

	/** {@inheritDoc} */
	public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
		performanceListener.start(getFacadeName() + '.' + method.getName());
		try {
			// On invoke le bean. (C'est à dire le vrai composant, celui qui implémente les méthodes au final.
			return kasper.kernel.util.ClassUtil.invoke(services, method, args);

		} catch (final Throwable throwable) {
			performanceListener.onException(throwable);
			throw throwable;
		} finally {
			performanceListener.stop();
		}
	}
}
