package kasperimpl.job.plugins.quartz;

import kasper.job.JobDefinition;
import kasper.job.JobManager;
import kasper.kernel.Home;

import org.quartz.JobExecutionContext;
import org.quartz.StatefulJob;

/**
 * Job Quartz encapsulant l'ex�cution du job kasper.
 * Cette classe h�rite de StatefulJob pour �viter les ex�cutions concurrentes.
 * Elle est publique et statique pour pouvoir �tre instanci�e par Quartz.
 * @author dchallas
 */
public final class QuartzJob implements StatefulJob {
	private final JobManager jobManager = Home.getContainer().getManager(JobManager.class);

	/** {@inheritDoc} */
	public void execute(final JobExecutionContext context) {
		// pour �viter d'enregistrer les jobs kasper avec une cl� unique, on pourrait peut-�tre
		// s�rialiser l'instance du job kasper lors du schedule dans la jobDataMap Quartz
		// ou alors il faudrait que les jobs kasper utilis�s avec ce jobManager impl�menentent
		// l'interface Job de Quartz

		//	final QuartzSchedulerPlugin quartzSchedulerPlugin = Home.getContainer().getPlugin(SchedulerPlugin.class.getSimpleName(), QuartzSchedulerPlugin.class, JobManager.class);
		final String jobName = context.getJobDetail().getName();
		//		final Class<? extends Runnable> runnableClass = quartzSchedulerPlugin.getJob(jobName);
		// Le job peut �tre sch�dul� par une autre instance et l'instance en cours ne le connait pas
		final JobDefinition jobDefinition = Home.getNameSpace().resolve(jobName, JobDefinition.class);
		jobManager.execute(jobDefinition);
	}
}
