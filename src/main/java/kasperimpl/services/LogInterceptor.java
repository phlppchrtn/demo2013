package kasperimpl.services;

import java.lang.reflect.Method;

import javax.inject.Named;

import kasper.kernel.util.Assertion;
import kasper.kernel.util.ClassUtil;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;

/**
 * Intercepteur pour la gestion des log au niveau de la couche service.
 * @author prahmoune
 * @version $Id: LogInterceptor.java,v 1.3 2012/04/04 14:36:44 pchretien Exp $
 */
//A valider

@Named("LogInterceptor")
public class LogInterceptor implements MethodInterceptor {
	private static final char[] PADDING = "        ".toCharArray();

	//	@Inject
	//	private ComponentManager componentManager;
	//
	//	private Map<String, String> componentNamesMap;

	//	@PostConstruct
	//	public void init() {
	//		componentNamesMap = componentManager.getComponentNamesMap();
	//	}

	/** {@inheritDoc} */
	public Object invoke(final MethodInvocation invocation) throws Throwable {
		final String facadeName = null; //componentNamesMap.get(invocation.getThis().getClass().getName());
		Assertion.notEmpty(facadeName);
		//-----------------------------------------------------------------------------
		boolean ok = false;
		final long start = System.currentTimeMillis();
		try {
			//On invoke le bean (C'est à dire le vrai composant, celui qui implémente les méthodes au final.
			final Object o = ClassUtil.invoke(invocation.getThis(), invocation.getMethod(), invocation.getArguments());
			ok = true;
			return o;
		} finally {
			log(facadeName, invocation.getMethod(), invocation.getArguments(), ok, start);
		}
	}

	private void log(final String facadeName, final Method method, final Object[] args, final boolean ok, final long start) {
		final Logger logger = Logger.getLogger(facadeName);
		if (logger.isInfoEnabled()) {
			final long duration = System.currentTimeMillis() - start;
			final StringBuilder sb = new StringBuilder();
			final String timeLog = String.valueOf(duration);
			if (timeLog.length() < 7) {
				sb.append(PADDING, 0, 7 - timeLog.length());
			}
			sb.append(timeLog).append(" ms; ");
			if (!ok) {
				sb.append("with error; ");
			}
			sb.append("Facade ").append(facadeName);
			sb.append('.');
			sb.append(method.getName());
			sb.append('(');
			appendArgs(sb, args);
			sb.append(')');
			logger.info(sb.toString());
		}
	}

	private void appendArgs(final StringBuilder sb, final Object[] args) {
		if (args != null && args.length != 0) {
			final int length = args.length;
			Object arg;
			// si le log est fin ou plus fin on tronque à 256, sinon (info ou warning) on tronque à 40
			for (int i = 0; i < length; i++) {
				arg = args[i];
				if (i != 0) {
					sb.append(", ");
				}
				if (arg == null) {
					sb.append("null");
				} else {
					try {
						sb.append(truncString(arg.toString(), 30).replace('\n', ' '));
					} catch (final Throwable e) {
						sb.append("??");
					}
				}
			}
		}
	}

	/**
	 * Tronque une String à une taille maximum et rajoute ... si tronquée
	 * @param s chaine à tronquer
	 * @param sizeMax Taille maximum du texte
	 * @return Chaine modifiée
	 */
	private String truncString(final String s, final int sizeMax) {
		if (s == null) {
			return null;
		}
		if (s.length() > sizeMax) {
			final String etc = "...";
			return s.substring(0, sizeMax - etc.length()) + etc;
		}
		return s;
	}

}
