package kasperimpl.job.plugins.quartz;

import java.text.SimpleDateFormat;
import java.util.Date;

import kasper.job.JobDefinition;
import kasper.job.JobManager;
import kasper.job.SchedulerPlugin;
import kasper.kernel.exception.KRuntimeException;
import kasper.kernel.lang.Activeable;
import kasper.kernel.util.Assertion;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerUtils;
import org.quartz.impl.StdSchedulerFactory;

/**
 * Impl�mentation de JobManager utilisant la librairie Quartz d'OpenSymphony.
 * Elle est pr�vue notamment pour des jobs synchronis�s dans un cluster (plusieurs JVM).
 *
 * Cette impl�mentation est transactionnelle pour kasper : une transaction est d�marr�e avant
 * l'appelle de Job.execute() et elle est commit�e s'il n'y a pas d'erreur ou rollback�e sinon.
 *
 * Le scheduler par d�faut est d�fini dans le fichier "quartz.properties" trouv� dans le
 * r�pertoire courant, dans le classpath ou selon des valeurs par d�faut dans le jar de quartz.
 *
 * Le fichier "quartz.properties" dans le jar de quartz contient (avec un RAMJobStore) :
 * org.quartz.scheduler.instanceName = DefaultQuartzScheduler
 * org.quartz.scheduler.rmi.export = false
 * org.quartz.scheduler.rmi.proxy = false
 * org.quartz.scheduler.wrapJobExecutionInUserTransaction = false
 * org.quartz.threadPool.class = org.quartz.simpl.SimpleThreadPool
 * org.quartz.threadPool.threadCount = 10
 * org.quartz.threadPool.threadPriority = 5
 * org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread = true
 * org.quartz.jobStore.misfireThreshold = 60000
 * org.quartz.jobStore.class = org.quartz.simpl.RAMJobStore
 *
 * Un JobStore pour diff�rentes bases de donn�es (Oracle, PostgreSQL, etc) peut �tre configur�
 * selon http://www.opensymphony.com/quartz/wikidocs/ConfigJobStoreTX.html
 * et le scheduler peut �tre configur� pour un cluster
 * selon http://www.opensymphony.com/quartz/wikidocs/ConfigJDBCJobStoreClustering.html.
 *
 * @author evernat
 * @version $Id: QuartzSchedulerPlugin.java,v 1.2 2012/09/19 15:43:53 pchretien Exp $
 */
public final class QuartzSchedulerPlugin implements SchedulerPlugin, Activeable {
	private Scheduler scheduler;
	private boolean active;

	private Scheduler obtainScheduler() throws SchedulerException {
		final SchedulerFactory sf = new StdSchedulerFactory();
		return sf.getScheduler();
		// Scheduler will not execute jobs until it has been started
	}

	private void stopScheduler() throws SchedulerException {
		scheduler.shutdown(false);
	}

	private void checkActive() {
		Assertion.precondition(active, "le manager n'est pas dans un �tat actif");
	}

	/** {@inheritDoc}  */
	public void start() {
		try {
			scheduler = obtainScheduler();
			scheduler.start();
		} catch (final SchedulerException e) {
			throw new KRuntimeException("Job Erreur", e);
		}
		active = true;
	}

	/** {@inheritDoc} */
	public void stop() {
		active = false;
		try {
			stopScheduler();
		} catch (final SchedulerException e) {
			Logger.getLogger(this.getClass()).warn("shutdown scheduler", e);
		}
	}

	private void scheduleJob(final JobDefinition jobDefinition, final Trigger trigger) {
		final JobDetail jobDetail = new JobDetail(jobDefinition.getName(), Scheduler.DEFAULT_GROUP, QuartzJob.class);

		try {
			// Delete the job if it previously exists
			scheduler.deleteJob(jobDetail.getName(), jobDetail.getGroup());
			// Schedule the job with the trigger
			scheduler.scheduleJob(jobDetail, trigger);
		} catch (final SchedulerException e) {
			throw new KRuntimeException(e.toString(), e);
		}
	}

	/** {@inheritDoc} */
	public void scheduleEverySecondInterval(final JobManager jobManager, final JobDefinition jobDefinition, final int periodInSecond) {
		checkActive();
		Assertion.precondition(periodInSecond <= 7 * 24 * 60 * 60, "La p�riode doit �tre inf�rieure � une semaine");
		//---------------------------------------------------------------------
		// Define a Trigger that will fire indefinitely at an interval
		final Trigger trigger = TriggerUtils.makeSecondlyTrigger(jobDefinition.getName(), periodInSecond, SimpleTrigger.REPEAT_INDEFINITELY);
		// starts in a period
		trigger.setStartTime(new Date(System.currentTimeMillis() + periodInSecond * 1000L));

		scheduleJob(jobDefinition, trigger);
		getLogger(jobDefinition.getName()).info("Job " + jobDefinition.getName() + " programm� toutes les " + periodInSecond + " s");
	}

	/** {@inheritDoc} */
	public void scheduleEveryDayAtHour(final JobManager jobManager, final JobDefinition jobDefinition, final int hour) {
		checkActive();
		//---------------------------------------------------------------------
		// Define a Trigger that will fire every day at given hour
		final Trigger trigger = TriggerUtils.makeDailyTrigger(jobDefinition.getName(), hour, 0);

		scheduleJob(jobDefinition, trigger);
		final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		getLogger(jobDefinition.getName()).info("Job " + jobDefinition.getName() + " programm� pour " + dateFormat.format(trigger.getNextFireTime()));
	}

	/** {@inheritDoc} */
	public void scheduleAtDate(final JobManager jobManager, final JobDefinition jobDefinition, final Date date) {
		checkActive();
		//---------------------------------------------------------------------
		// Define a Trigger that will fire at date
		final Trigger trigger = new SimpleTrigger(jobDefinition.getName(), Scheduler.DEFAULT_GROUP);
		trigger.setStartTime(date);

		scheduleJob(jobDefinition, trigger);
		final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		getLogger(jobDefinition.getName()).info("Job " + jobDefinition.getName() + " programm� pour " + dateFormat.format(date));
	}

	/** {@inheritDoc} */
	public void scheduleNow(final JobManager jobManager, final JobDefinition jobDefinition) {
		checkActive();
		//---------------------------------------------------------------------
		// Define a Trigger that will fire now
		final Trigger trigger = new SimpleTrigger(jobDefinition.getName(), Scheduler.DEFAULT_GROUP);
		trigger.setStartTime(new Date());

		scheduleJob(jobDefinition, trigger);
	}

	private Logger getLogger(final String jobName) {
		return Logger.getLogger(jobName);
	}
	//	//---------------------------------------------------------------------------
	//	//------------------Gestion du rendu et des interactions---------------------
	//	//---------------------------------------------------------------------------
	//	/** {@inheritDoc} */
	//	@Override
	//	public void toHtml(final PrintStream out) throws Exception {
	//		out.println("Noms des jobs: ");
	//		String separator = "";
	//		for (final String jobGroupName : scheduler.getJobGroupNames()) {
	//			for (final String jobName : scheduler.getJobNames(jobGroupName)) {
	//				out.print(separator);
	//				out.println(jobName);
	//				separator = ", ";
	//			}
	//		}
	//		out.println("<br/>");
	//		super.toHtml(out);
	//	}
	//
	//	/** {@inheritDoc} */
	//	public ManagerSummaryInfo getMainSummaryInfo() {
	//		final ManagerSummaryInfo managerSummaryInfo = new ManagerSummaryInfo();
	//		managerSummaryInfo.setValue(jobMap.size());
	//		managerSummaryInfo.setValueTitle("job");
	//		return managerSummaryInfo;
	//	}
	//
	//	/** {@inheritDoc} */
	//	public List<ManagerSummaryInfo> getSummaryInfos() {
	//		final ManagerSummaryInfo managerSummaryInfo = new ManagerSummaryInfo();
	//		managerSummaryInfo.setInfoTitle("MultiJVMJobManager");
	//		return Collections.<ManagerSummaryInfo> singletonList(managerSummaryInfo);
	//	}
}
