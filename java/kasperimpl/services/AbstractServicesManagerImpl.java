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
 * centralis�s via la notion de Facade : "Services". On ne fournit par cons�quent que ces "Services".
 * 
 * Un utilitaire est propos� permettant de cr�er dynamiquemnt des proxies; Ces proxy poss�de le m�me contrat que
 * l'interface facade du composant et permettent de g�rer de fa�on centralis�e des probl�matiques transverses de cache,
 * de gestion des performances de tunneling rmi ou HTTP. (Etc)
 * 
 * @author pchretien
 * @version $Id: AbstractServicesManagerImpl.java,v 1.2 2012/05/25 13:33:35 pchretien Exp $
 * @deprecated Plus utilis�, remplacer par le Namespace. Les services sont r�cup�r�s par l'injection.
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
		// On parcourt toutes les m�thodes � la recherche de celle qui permet de r�cup�rer la facade.
		for (final Method method : getClass().getMethods()) {
			if (method.getReturnType().isAssignableFrom(facadeClass)) {
				final F facade = (F) kasper.kernel.util.ClassUtil.invoke(this, method);
				Assertion.notNull(facade);
				return facade;
			}
		}
		throw new IllegalArgumentException(facadeClass + "non d�fini sur " + this.getClass().getName());
	}

	/**
	 * Cr�e dynamiquement une fa�ade interceptant toutes les m�thodes de la fa�ade du composant.
	 * 
	 * @param <F> Type de la fa�ade impl�mentant Services
	 * @param componentFacadeBean Composant impl�mentant la fa�ade.
	 * @param ih InvocationHandler Interception et traitement des m�thodes de la fa�ade
	 * @return Object Proxy, interceptant les m�thodes de la fa�ade.
	 */
	public final <F> F createProxyFacade(final F componentFacadeBean, final Class<F> componentFacadeClass, final InvocationHandler ih) {
		// On r�cup�re la liste de toutes les interfaces d�clar�es sur le composant d'impl�mentation de la FacadeM�tier
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
	 * Cr�e dynamiquement une fa�ade � partir d'une autre fa�ade.
	 * @param <F> Type de la fa�ade impl�mentant Services
	 * @param classLoader ClassLoader
	 * @param interfaceArray Interfaces
	 * @param ih InvocationHandler Interception et traitement des m�thodes de la fa�ade
	 * @return Object Proxy, interceptant les m�thodes de la fa�ade.
	 */
	private static <F> F createProxyFacade(final ClassLoader classLoader, final Class<F> facadeClass, final Class[] interfaceArray, final InvocationHandler ih) {
		//On ne conserve que l'interface h�ritant de fa�ade. (il ne doit y en avoir une et une seule
		int found = -1;
		for (int i = 0; i < interfaceArray.length; i++) {
			if (facadeClass.isAssignableFrom(interfaceArray[i])) {
				if (found == -1) {
					found = i;
				} else {
					throw new RuntimeException("Il existe plusieurs interfaces impl�mentant 'Services'");
				}
			}
		}
		if (found == -1) {
			throw new RuntimeException("Il n'existe aucune interface impl�mentant 'Services'");
		}

		//Le tableau des interfaces que le proxy impl�mente est constitu� du singleton trouv�.
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
