package kasperimpl.services;

import java.io.PrintStream;
import java.util.Collections;
import java.util.List;

import kasper.analytics.AnalyticsManager;
import kasper.kernel.exception.KUserException;
import kasper.kernel.manager.ManagerDescription;
import kasper.kernel.manager.ManagerSummaryInfo;
import kasper.kernel.util.Assertion;

/**
 *
 * @author  pchretien
 * @version $Id: PerformanceListenerImpl.java,v 1.1 2012/03/23 13:22:03 pchretien Exp $
 */
final class PerformanceListenerImpl implements PerformanceListener, ManagerDescription {
	/**
	 * Base de données gérant les statistiques des façades.
	 */
	private static final String PROCESS_TYPE = "FACADE";

	private final AnalyticsManager analyticsManager;

	PerformanceListenerImpl(final AnalyticsManager analyticsManager) {
		Assertion.notNull(analyticsManager);
		//---------------------------------------------------------------------
		this.analyticsManager = analyticsManager;
	}

	public void start(final String processName) {
		analyticsManager.getAgent().startProcess(PROCESS_TYPE, processName);
	}

	public void onException(final Throwable throwable) {
		if (isUserException(throwable)) {
			analyticsManager.getAgent().setMeasure("PP_USER_EXCEPTION_COUNT", 100);
		} else {
			analyticsManager.getAgent().setMeasure("PP_SYSTEM_EXCEPTION_COUNT", 100);
		}
	}

	public void stop() {
		analyticsManager.getAgent().stopProcess();
	}

	private boolean isUserException(final Throwable t) {
		return t instanceof KUserException;
	}

	/** {@inheritDoc}*/
	public void toHtml(final PrintStream out) {
		//analyticsManager.getDashboard().toHtml(DB, out);
	}

	/** {@inheritDoc} */
	public ManagerSummaryInfo getMainSummaryInfo() {
		final ManagerSummaryInfo managerSummaryInfo = new ManagerSummaryInfo();
		//		managerSummaryInfo.setDataBaseName(DB);
		managerSummaryInfo.setValueTitle("tps réponse");
		return managerSummaryInfo;
	}

	/** {@inheritDoc} */
	public List<ManagerSummaryInfo> getSummaryInfos() {
		return Collections.emptyList();
	}

	/** {@inheritDoc} */
	public String getName() {
		return "Statistiques des façades";
	}

	/** {@inheritDoc} */
	public String getImage() {
		return "frontpage.png";
	}
}
