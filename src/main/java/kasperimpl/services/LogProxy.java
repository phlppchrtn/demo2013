package kasperimpl.services;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import kasper.kernel.util.Assertion;
import kasper.kernel.util.ClassUtil;

import org.apache.log4j.Logger;

/**
 * Proxy gérant les logs sur les méthodes du composant.
 * @version $Id: LogProxy.java,v 1.1 2012/03/23 13:22:03 pchretien Exp $
 * @author evernat
 * @deprecated Utiliser le LogInterceptor
 */
  //A valider

@Deprecated
final class LogProxy implements InvocationHandler {
	private static final char[] PADDING = "        ".toCharArray();

	private final Object services;
	private final String name;
	private final Logger logger;

	/**
	 * On crée un proxy qui intercepte toutes les méthodes de Facade des interfaces sous-typant Facade.
	 * @param services Services
	 */
	LogProxy(final Object services) {
		Assertion.notNull(services);
		//----------------------------------------------------------------------
		this.services = services;
		final String lname = buildName(services);
		name = lname;
		logger = Logger.getLogger(name);
	}

	private static String buildName(final Object services) {
		String lname = services.getClass().getSimpleName();
		if (lname.startsWith("Facade") && lname.endsWith("Bean")) {
			lname = lname.substring("Facade".length(), lname.length() - "Bean".length());
		}
		return lname;
	}

	/** {@inheritDoc} */
	public Object invoke(final Object proxy, final Method method, final Object[] args) {
		boolean ok = false;
		final long start = System.currentTimeMillis();
		try {
			//On invoke le bean (C'est à dire le vrai composant, celui qui implémente les méthodes au final.
			final Object o = ClassUtil.invoke(services, method, args);
			ok = true;
			return o;
		} finally {
			log(method, args, ok, start);
		}
	}

	private void log(final Method method, final Object[] args, final boolean ok, final long start) {
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
			sb.append("Facade ").append(name);
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
