package kasperimpl.services;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import kasper.analytics.AnalyticsManager;
import kasper.kernel.manager.Describable;
import kasper.kernel.manager.Manager;
import kasper.kernel.manager.ManagerDescription;
import kasper.kernel.util.Assertion;

/**
 * Fournisseur des composants. Les composants ne sont vus que par les services qu'ils offrent. Ces services sont
 * centralisés via la notion de Facade : "Services". On ne fournit par conséquent que ces "Services".
 * 
 * Un utilitaire est proposé permettant de créer dynamiquemnt des proxies; Ces proxy possède le même contrat que
 * l'interface facade du composant et permettent de gérer de façon centralisée des problématiques transverses de cache,
 * de gestion des performances de tunneling rmi ou HTTP. (Etc)
 * 
 * @author pchretien
 * @version $Id: AbstractServicesManagerImpl.java,v 1.2 2012/05/25 13:33:35 pchretien Exp $
 * @deprecated Plus utilisé, remplacer par le Namespace. Les services sont récupérés par l'injection.
 */
//A valider
@Deprecated
abstract class AbstractServicesManagerImpl implements Manager, Describable {
	private final List<PerformanceProxy> list = new ArrayList<PerformanceProxy>();
	private final PerformanceListenerImpl performanceListener;

	/**
	 * Constructeur.
	 * @param analyticsManager Manager de la performance applicative
	 */
	protected AbstractServicesManagerImpl(final AnalyticsManager analyticsManager) {
		super();
		performanceListener = new PerformanceListenerImpl(analyticsManager);
	}

	public final <F> F getServices(final Class<F> facadeClass) {
		// On parcourt toutes les méthodes à la recherche de celle qui permet de récupérer la facade.
		for (final Method method : getClass().getMethods()) {
			if (method.getReturnType().isAssignableFrom(facadeClass)) {
				final F facade = (F) kasper.kernel.util.ClassUtil.invoke(this, method);
				Assertion.notNull(facade);
				return facade;
			}
		}
		throw new IllegalArgumentException(facadeClass + "non défini sur " + this.getClass().getName());
	}

	/**
	 * Crée dynamiquement une façade interceptant toutes les méthodes de la façade du composant.
	 * 
	 * @param <F> Type de la façade implémentant Services
	 * @param componentFacadeBean Composant implémentant la façade.
	 * @param ih InvocationHandler Interception et traitement des méthodes de la façade
	 * @return Object Proxy, interceptant les méthodes de la façade.
	 */
	public final <F> F createProxyFacade(final F componentFacadeBean, final Class<F> componentFacadeClass, final InvocationHandler ih) {
		// On récupère la liste de toutes les interfaces déclarées sur le composant d'implémentation de la FacadeMétier
		final Class<?>[] interfaceArray = componentFacadeBean.getClass().getInterfaces(); // .getClass().getSuperclass().
		// .getClass(). .getInterfaces();
		return AbstractServicesManagerImpl.<F> createProxyFacade(componentFacadeBean.getClass().getClassLoader(), componentFacadeClass, interfaceArray, ih);
	}

	public final <F> F createLogFacade(final F componentFacadeBean, final Class<F> componentFacadeClass) {
		final LogProxy proxy = new LogProxy(componentFacadeBean);
		return this.<F> createProxyFacade(componentFacadeBean, componentFacadeClass, proxy);
	}

	public final <F> F createPerfomanceFacade(final F componentFacadeBean, final Class<F> componentFacadeClass) {
		final PerformanceProxy proxy = new PerformanceProxy(componentFacadeBean, performanceListener);
		list.add(proxy);
		return this.<F> createProxyFacade(componentFacadeBean, componentFacadeClass, proxy);
	}

	/**
	 * Crée dynamiquement une façade à partir d'une autre façade.
	 * @param <F> Type de la façade implémentant Services
	 * @param classLoader ClassLoader
	 * @param interfaceArray Interfaces
	 * @param ih InvocationHandler Interception et traitement des méthodes de la façade
	 * @return Object Proxy, interceptant les méthodes de la façade.
	 */
	private static <F> F createProxyFacade(final ClassLoader classLoader, final Class<F> facadeClass, final Class[] interfaceArray, final InvocationHandler ih) {
		//On ne conserve que l'interface héritant de façade. (il ne doit y en avoir une et une seule
		int found = -1;
		for (int i = 0; i < interfaceArray.length; i++) {
			if (facadeClass.isAssignableFrom(interfaceArray[i])) {
				if (found == -1) {
					found = i;
				} else {
					throw new RuntimeException("Il existe plusieurs interfaces implémentant 'Services'");
				}
			}
		}
		if (found == -1) {
			throw new RuntimeException("Il n'existe aucune interface implémentant 'Services'");
		}

		//Le tableau des interfaces que le proxy implémente est constitué du singleton trouvé.
		final Class[] dynamicFacadeArray = { interfaceArray[found] };
		return (F) Proxy.newProxyInstance(classLoader, dynamicFacadeArray, ih);
	}

	// ---------------------------------------------------------------------------
	// ------------------Gestion du rendu et des interactions---------------------
	// ---------------------------------------------------------------------------
	/** {@inheritDoc} */
	public final ManagerDescription getDescription() {
		return performanceListener;
	}
}
