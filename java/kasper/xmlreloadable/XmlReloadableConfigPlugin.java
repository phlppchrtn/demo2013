/**
 * Kasper-kernel - v6 - Simple Java Framework
 *
 * Copyright (C) 2013, KleeGroup, direction.technique@kleegroup.com (http://www.kleegroup.com)
 * KleeGroup, Centre d'affaire la Boursidière - BP 159 - 92357 Le Plessis Robinson Cedex - France
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package kasperimpl.config.plugins.xmlreloadable;

import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;
import javax.inject.Named;

import kasper.kernel.lang.Activeable;
import kasper.kernel.util.Assertion;
import kasper.resource.ResourceManager;
import kasperimpl.config.ConfigPlugin;
import kasperimpl.config.plugins.xml.XmlConfig;

/**
 * Interface d'un plugin de gestion de configuration applicative XML rechargeable.
 * @author npiedeloup
 * @version $Id: XmlReloadableConfigPlugin.java,v 1.1 2013/02/15 13:41:22 pchretien Exp $
 */
public final class XmlReloadableConfigPlugin implements ConfigPlugin, Activeable {
	private final URL configURL;
	private final int reloadtime;
	private XmlConfig xmlConfig;

	/**
	 * Constructeur.
	 * @param resourceManager Resource manager
	 * @param url Url du fichier XML de configuration
	 */
	@Inject
	public XmlReloadableConfigPlugin(final ResourceManager resourceManager, @Named("url") final String url, @Named("reloadtime") final int reloadtime) {
		Assertion.notNull(resourceManager);
		Assertion.notEmpty(url);
		Assertion.precondition(reloadtime > 0 && reloadtime < 3000, "Le temps de rechargement est obligatoire et exprimé en minutes");
		// ---------------------------------------------------------------------
		configURL = resourceManager.resolve(url);
		this.reloadtime = reloadtime;
	}

	/** {@inheritDoc} */
	public synchronized void start() {
		reloadConfig();
		final Timer reloadConfigurationTimer = new Timer("ReloadConfigurationTimer", true);
		final TimerTask task = new ReloadConfigTask();
		reloadConfigurationTimer.scheduleAtFixedRate(task, 0, reloadtime * 60L * 1000L);
	}

	/** {@inheritDoc} */
	public synchronized void stop() {
		xmlConfig = null;
	}

	private synchronized XmlConfig getConfig() {
		return xmlConfig;
	}

	/**
	 * Rechargement de la config.
	 */
	synchronized void reloadConfig() {
		xmlConfig = new XmlConfig(configURL);
	}

	/** {@inheritDoc} */
	public boolean hasProperty(final String config, final String property) {
		return getConfig().hasProperty(config, property);
	}

	/** {@inheritDoc} */
	public String getValue(final String config, final String property) {
		return getConfig().getValue(config, property);
	}

	/**
	 * TimerTask de rechargement de config.
	 * @author npiedeloup
	 * @version $Id: XmlReloadableConfigPlugin.java,v 1.1 2013/02/15 13:41:22 pchretien Exp $
	 */
	private final class ReloadConfigTask extends TimerTask {

		/** {@inheritDoc} */
		@Override
		public void run() {
			reloadConfig();
		}
	}
}
