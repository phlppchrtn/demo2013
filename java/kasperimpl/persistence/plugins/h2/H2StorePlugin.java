package kasperimpl.persistence.plugins.h2;

import javax.inject.Inject;

import kasper.work.WorkManager;
import kasperimpl.persistence.plugins.AbstractSQLStorePlugin;

/**
 * Implémentation d'un Store H2.
 * Dans le cas de H2, la gestion des clés ......
 *
 * @author  pchretien
 * @version $Id: H2StorePlugin.java,v 1.1 2012/10/11 14:53:01 pchretien Exp $
 */
public abstract class H2StorePlugin extends AbstractSQLStorePlugin {
	//TODO 
	@Inject
	protected H2StorePlugin(final WorkManager workManager) {
		super(workManager);
	}

	//	private static final String DTO_SEQUENCE = "DTO_SEQUENCE";
	//	private static final String SEQUENCE_FIELD = "SEQUENCE";
	//	/**
	//	 * Prefix de la tache : SELECT
	//	 */
	//	private static final String TK_SELECT = "TK_SELECT";
	//
	//	/**
	//	 * Domaine à usage interne.
	//	 * Ce domaine n'est pas enregistré.
	//	 */
	//	private final Domain resultDomain = new DomainImpl(getDomainNameSpace(), KDataType.DtObject, new FormatterDefault());
	//	private final String sequencePrefix;
	//
	//	/**
	//	 * Constructeur.
	//	 * @param taskManager Manager des taches
	//	 * @param domainManager Manager de domain
	//	 * @param workManager Manager des works
	//	 * @param sequencePrefix Configuration du préfixe de la séquence
	//	 */
	//	public HsqlStorePlugin(@Named("sequencePrefix") final String sequencePrefix, final TaskManager taskManager, final DomainManager domainManager, final WorkManager workManager) {
	//		super(taskManager, domainManager, workManager);
	//		Assertion.notEmpty(sequencePrefix);
	//		//---------------------------------------------------------------------
	//		this.sequencePrefix = sequencePrefix;
	//	}
	//
	//	/** {@inheritDoc} */
	//	@Override
	//	protected Class<? extends AbstractTaskEngine> getTaskEngineClass(final boolean insert) {
	//		return TaskEngineProc.class;
	//	}
	//
	//	private Long getSequenceNextval(final String sequenceName)  {
	//		final String taskName = TK_SELECT + '_' + sequenceName;
	//		final TaskDefinitionFactory taskDefinitionFactory = createTaskDefinitionFactory();
	//
	//		final StringBuilder request = chooseDataBaseStyle(sequenceName);
	//
	//		taskDefinitionFactory.init(taskName, TaskEngineSelect.class, request.toString(), "");
	//
	//		// OUT, obligatoire
	//		taskDefinitionFactory.addAttribute(taskName, DTO_SEQUENCE, resultDomain, true, false);
	//		final TaskDefinition taskDefinition = taskDefinitionFactory.createTaskDefinition(taskName);
	//
	//		final Task task = createTask(taskDefinition);
	//		final TaskResult taskResult = process(task);
	//		final DtObject dto = taskResult.getValue(DTO_SEQUENCE);
	//		final DtField dtField = dto.getDefinition().getField(SEQUENCE_FIELD);
	//
	//		return Long.valueOf((Integer) dto.getValue(dtField));
	//	}
	//
	//	private StringBuilder chooseDataBaseStyle(final String sequenceName) {
	//		final StringBuilder sb = new StringBuilder();
	//		sb.append("select next value for " + sequenceName + "  as " + SEQUENCE_FIELD)//
	//				.append(" from information_schema.system_sequences where sequence_name = upper('" + sequenceName + "')");
	//		return sb;
	//	}
	//
	//	/** {@inheritDoc} */
	//	@Override
	//	protected void preparePrimaryKey(final DtObject dto)  {
	//		final DtField pk = DtFieldUtil.getPrimaryKey(dto.getDefinition());
	//		dto.setValue(pk, getSequenceNextval(sequencePrefix + getTableName(dto.getDefinition())));
	//		//			executeInsert(transaction, dto);
	//	}
	//
	//	/** {@inheritDoc} */
	//	@Override
	//	protected String createInsertQuery(final DtDefinition dtDefinition) {
	//		final String tableName = getTableName(dtDefinition);
	//		final StringBuilder request = new StringBuilder();
	//		request.append("insert into ").append(tableName).append(" (");
	//		String separator = "";
	//		for (final DtField dtField : dtDefinition.getFields()) {
	//			if (dtField.isPersistent()) {
	//				request.append(separator);
	//				request.append(dtField.getName());
	//				separator = ", ";
	//			}
	//		}
	//		request.append(") values (");
	//		separator = "";
	//		for (final DtField dtField : dtDefinition.getFields()) {
	//			if (dtField.isPersistent()) {
	//				request.append(separator);
	//				request.append(" #DTO.").append(dtField.getName()).append('#');
	//				separator = ", ";
	//			}
	//		}
	//		request.append(");");
	//		return request.toString();
	//	}
	//
	//	/** {@inheritDoc} */
	//	@Override
	//	protected void appendMaxRows(final String separator, final StringBuilder request, final Integer maxRows) {
	//		request.append(separator).append(" rownum <= ").append(maxRows.toString());
	//	}
}
