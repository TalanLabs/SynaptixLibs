package com.synaptix.taskmanager.manager.taskservice;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.SqlSessionManager;

import com.google.inject.Inject;
import com.synaptix.mybatis.dao.IDaoSession;
import com.synaptix.mybatis.dao.IDaoSessionExt;
import com.synaptix.service.ServiceException;
import com.synaptix.taskmanager.model.ITask;
import com.synaptix.taskmanager.model.ITaskObject;
import com.synaptix.taskmanager.model.domains.EnumErrorMessages;
import com.synaptix.taskmanager.model.domains.ServiceNature;
import com.synaptix.taskmanager.service.AbstractDelegate;

import de.jkeylockmanager.manager.KeyLockManager;
import de.jkeylockmanager.manager.KeyLockManagers;
import de.jkeylockmanager.manager.ReturnValueLockCallback;

public abstract class AbstractTaskService<F> extends AbstractDelegate implements ITaskService {

	private static final Log LOG = LogFactory.getLog(AbstractTaskService.class);

	@Inject
	private IDaoSession daoSession;

	@Inject
	private SqlSessionManager sqlSessionManager;

	private final String code;

	private KeyLockManager keyLockManager;

	private final ServiceNature nature;

	private final Class<? extends ITaskObject<?>> objectType;

	public AbstractTaskService(String code, ServiceNature nature, Class<? extends ITaskObject<?>> objectType) {
		super();

		this.code = code;
		this.nature = nature;
		this.objectType = objectType;

		keyLockManager = KeyLockManagers.newLock();
	}

	@Override
	public final String getCode() {
		return code;
	}

	@Override
	public final ServiceNature getNature() {
		return nature;
	}

	@Override
	public String getCategory() {
		return null;
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public final Class<? extends ITaskObject<?>> getObjectKinds() {
		return objectType;
	}

	@Override
	public void onTodo(ITask task) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("onTodo " + task);
		}
	}

	@Override
	public void onCurrent(ITask task) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("onCurrent " + task);
		}
	}

	@Override
	public void onDone(ITask task) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("onDone " + task);
		}
	}

	@Override
	public void onSkipped(ITask task) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("onSkipped " + task);
		}
	}

	@Override
	public void onCanceled(ITask task) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("onCanceled " + task);
		}
	}

	@Override
	public ITodoDescriptor getTodoDescriptor() {
		return null;
	}

	public String getLockKey(F object) {
		return null;
	}

	protected abstract F getObject(ITask task);

	@Override
	public IExecutionResult execute(final ITask task) {
		final F object;
		String lockKey;

		try {
			if (this.daoSession instanceof IDaoSessionExt && !this.sqlSessionManager.isManagedSessionStarted()) {
				((IDaoSessionExt) this.daoSession).setCheckVersionConflictDaoExceptionInSession(true);
			}
			daoSession.begin();
			object = getObject(task);
			lockKey = getLockKey(object);
			daoSession.commit();
		} catch (Exception e) {
			throw new ServiceException(e);
		} finally {
			daoSession.end();
		}

		IExecutionResult executionResult;
		if (lockKey != null) {
			executionResult = keyLockManager.executeLocked(lockKey, new ReturnValueLockCallback<IExecutionResult>() {
				@Override
				public IExecutionResult doInLock() throws Exception {
					return executeInSession(task, object);
				}
			});
		} else {
			executionResult = executeInSession(task, object);
		}
		return executionResult;
	}

	public abstract IExecutionResult execute(ITask task, F object);

	private IExecutionResult executeInSession(ITask task, F object) {
		try {
			if (this.daoSession instanceof IDaoSessionExt && !this.sqlSessionManager.isManagedSessionStarted()) {
				((IDaoSessionExt) this.daoSession).setCheckVersionConflictDaoExceptionInSession(true);
			}
			daoSession.begin();
			IExecutionResult executionResult = execute(task, object);
			if (executionResult.isFinished() || EnumErrorMessages.ERROR_MESSAGE_WAITING.getMessage().equals(executionResult.getErrorMessage())) {
				daoSession.commit();
			}
			return executionResult;
		} catch (Exception e) {
			throw new ServiceException(e);
		} finally {
			daoSession.end();
		}
	}
}
