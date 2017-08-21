package com.synaptix.taskmanager.service;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.exceptions.PersistenceException;

import com.google.inject.Inject;
import com.synaptix.common.helper.CollectionHelper;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.component.model.IError;
import com.synaptix.component.model.IServiceResult;
import com.synaptix.component.model.IStackResult;
import com.synaptix.entity.IId;
import com.synaptix.mybatis.dao.exceptions.VersionConflictDaoException;
import com.synaptix.mybatis.delegate.EntityServiceDelegate;
import com.synaptix.mybatis.service.EntityServerService;
import com.synaptix.mybatis.service.Transactional;
import com.synaptix.server.service.ServiceResultBuilder;
import com.synaptix.server.service.ServiceResultContainer;
import com.synaptix.service.ServiceException;
import com.synaptix.taskmanager.dao.mapper.TaskMapper;
import com.synaptix.taskmanager.delegate.TaskManagerServiceDelegate;
import com.synaptix.taskmanager.manager.TaskServiceDiscovery;
import com.synaptix.taskmanager.manager.taskservice.ITaskService;
import com.synaptix.taskmanager.model.ITask;
import com.synaptix.taskmanager.model.ITaskCluster;
import com.synaptix.taskmanager.model.ITaskObject;
import com.synaptix.taskmanager.model.ITodo;
import com.synaptix.taskmanager.model.TasksLists;
import com.synaptix.taskmanager.model.domains.EnumErrorMessages;
import com.synaptix.taskmanager.model.domains.ServiceNature;
import com.synaptix.taskmanager.model.domains.TaskStatus;
import com.synaptix.taskmanager.service.error.TaskManagerErrorEnum;

public class TaskManagerServerService extends AbstractSimpleService implements ITaskManagerService {

	private static final Log LOG = LogFactory.getLog(TaskManagerServerService.class);

	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	@Inject
	private TaskManagerServiceDelegate taskManagerServiceDelegate;

	@Inject
	private EntityServiceDelegate entityServiceDelegate;

	@Inject
	private EntityServerService entityServerService;

	@Inject
	private TaskServiceDiscovery taskServiceDiscovery;

	private TaskMapper getTaskMapper() {
		return getDaoSession().getMapper(TaskMapper.class);
	}

	/**
	 * Starts engine, creates cluster if cluster does not exist
	 */
	@Override
	public <E extends Enum<E>, F extends ITaskObject<E>> IServiceResult<Set<IError>> startEngine(F taskObject) {
		if (taskObject == null) {
			return new ServiceResultBuilder<TaskManagerErrorEnum>().compileResult(null);
		}
		return startEngine(taskObject.getId(), ComponentFactory.getInstance().getComponentClass(taskObject));
	}

	/**
	 * Start engine, create cluster if cluster not exist
	 */
	@Override
	public <E extends Enum<E>, F extends ITaskObject<E>> IServiceResult<Set<IError>> startEngine(IId idTaskObject, Class<F> objectClass) {
		if (idTaskObject == null) {
			return new ServiceResultBuilder<TaskManagerErrorEnum>().compileResult(null);
		}

		IId idCluster;

		try {
			getDaoSession().begin();

			F to = entityServiceDelegate.findEntityById(objectClass, idTaskObject);

			if (to.getIdCluster() == null) {
				taskManagerServiceDelegate.createTaskCluster(to);
			}
			idCluster = to.getIdCluster();

			getDaoSession().commit();
		} catch (Exception e) {
			throw new ServiceException(e);
		} finally {
			getDaoSession().end();
		}

		return _startEngine(idCluster);
	}

	/**
	 * Starts engine, creates cluster if cluster does not exist
	 */
	@Override
	public <E extends Enum<E>, F extends ITaskObject<E>> IServiceResult<Set<IError>> startEngine(List<IId> idTaskObjects, Class<F> objectClass) {
		ServiceResultBuilder<TaskManagerErrorEnum> resultBuilder = new ServiceResultBuilder<TaskManagerErrorEnum>();
		for (IId idTaskObject : idTaskObjects) {
			resultBuilder.ingest(startEngine(idTaskObject, objectClass));
		}

		return resultBuilder.compileResult(null);
	}

	@Override
	@Transactional(commit = true)
	public <E extends Enum<E>, F extends ITaskObject<E>, G extends Enum<G>, H extends ITaskObject<G>> void addTaskObjectToTaskCluster(F taskObjectWithCluster, H newTaskObject) {
		taskManagerServiceDelegate.addTaskObjectToTaskCluster(taskObjectWithCluster, newTaskObject);
	}

	@Override
	@Transactional(commit = true)
	public <E extends Enum<E>, F extends ITaskObject<E>> void addTaskObjectToTaskCluster(IId idTaskCluster, F taskObject) {
		taskManagerServiceDelegate.addTaskObjectToTaskCluster(idTaskCluster, taskObject);
	}

	@Transactional(commit = true)
	public <E extends Enum<E>, F extends ITaskObject<E>> IId createTaskCluster(F taskObject) {
		return taskManagerServiceDelegate.createTaskCluster(taskObject);
	}

	/**
	 * Check task current to create todo manager
	 */
	@Transactional(commit = true)
	public void checkTodoManagerTasks() {
		taskManagerServiceDelegate.checkTodoManagerTasks();
	}

	@Override
	@Transactional(commit = true)
	public void updateTodoDescription(ITodo todo) {
		taskManagerServiceDelegate.updateTodoDescription(todo);
	}

	@Override
	@Transactional(commit = true)
	public void updateTodoDescription(IId idObject, Class<? extends ITaskObject<?>> objectClass) {
		taskManagerServiceDelegate.updateTodoDescription(idObject, objectClass);
	}

	/**
	 * Set check user validation to true
	 */
	@Transactional(commit = true)
	@Override
	public void validateTask(IId idTask) {
		taskManagerServiceDelegate.validateTask(idTask);
	}

	@Transactional(commit = true)
	public void createTaskGraphs(ITaskCluster taskCluster) {
		taskManagerServiceDelegate.createTaskGraphs(taskCluster);
	}

	/**
	 * Select cluster's tasks which are at TO DO status.
	 */
	private List<ITask> selectCurrentTasksForCluster(IId idTaskCluster) {
		try {
			getDaoSession().begin();
			return getTaskMapper().selectCurrentTasksForCluster(idTaskCluster);
		} catch (Exception e) {
			throw new ServiceException(e);
		} finally {
			getDaoSession().end();
		}
	}

	@Override
	public IServiceResult<Set<IError>> startEngine(IId idTaskCluster) {
		return _startEngine(idTaskCluster);
	}

	private IServiceResult<Set<IError>> _startEngine(IId idTaskCluster) {

		if (LOG.isDebugEnabled()) {
			LOG.debug("TM - StartEngine for cluster " + idTaskCluster);
		}
		if (idTaskCluster == null) {
			return restart();
		}

		ServiceResultContainer serviceResultContainer = new ServiceResultContainer();
		boolean restart = false;
		Set<IError> errorSet = new HashSet<IError>();

		// Find all current task for cluster
		List<ITask> tasks = selectCurrentTasksForCluster(idTaskCluster);

		if (tasks == null || tasks.isEmpty()) {
			ITaskCluster taskCluster = entityServerService.findEntityById(ITaskCluster.class, idTaskCluster);
			if (taskCluster != null && !taskCluster.isCheckGraphCreated()) {
				createTaskGraphs(taskCluster);
				taskManagerServiceDelegate.addToQueue(idTaskCluster);
			} else {
				tasks = selectCurrentTasksForCluster(idTaskCluster); // double verification for multithreading issues
				if (tasks == null || tasks.isEmpty()) {
					if (LOG.isDebugEnabled()) {
						LOG.debug(String.format("Archiving cluster %s because there are no current tasks.", idTaskCluster));
					}
					archiveCluster(idTaskCluster);
				} // no need for an else, in this case there is a second thread which is already running the tasks
			}
		} else {
			LinkedList<ITask> tasksQueue = new LinkedList<ITask>(tasks);
			List<ITask> recycleList = new ArrayList<ITask>();

			while (!tasksQueue.isEmpty()) {
				ITask task = tasksQueue.removeFirst();

				boolean done = true;
				String errorMessage = null;
				if (task.getServiceCode() != null) {
					final ITaskService taskService = taskServiceDiscovery.getTaskService(task.getServiceCode());
					if (taskService == null) {
						errorMessage = "Service code does not exist";
					} else {
						TaskExecutionResult taskExecutionResult = serviceResultContainer.ingest(executeTask(taskService, task));
						errorMessage = taskExecutionResult.errorMessage;
						if (EnumErrorMessages.CONFLICTING_SAVE_ERROR.getMessage().equals(errorMessage)) {
							task = entityServerService.findEntityById(ITask.class, task.getId());
							if (task.getTaskStatus() == TaskStatus.DONE) { // In case the task is already executed by another thread
								restart = true;
								break;
							}
						}
						done = taskExecutionResult.done;
						errorSet.addAll(taskExecutionResult.errorSet);
						if (taskExecutionResult.stopAndRestart) {
							task = entityServerService.findEntityById(ITask.class, task.getId());
							restart = true;
						}
					}
				}

				if (done) {
					TasksLists tasksLists = setTaskDone(task);
					// Add new tasks to top of the queue
					for (ITask iTask : tasksLists.getNewTasksToDo()) {
						tasksQueue.addFirst(iTask);
					}

					for (IId idTask : tasksLists.getIdTasksToRemove()) {
						for (Iterator<ITask> iterator = recycleList.iterator(); iterator.hasNext();) {
							ITask iTask = iterator.next();
							if (idTask.equals(iTask.getId())) {
								iterator.remove();
								break;
							}
						}
						for (Iterator<ITask> iterator = tasksQueue.iterator(); iterator.hasNext();) {
							ITask iTask = iterator.next();
							if (idTask.equals(iTask.getId())) {
								iterator.remove();
								break;
							}
						}
					}

					if (task.getNature() != ServiceNature.DATA_CHECK) {
						// Add previously failed tasks to end of deque. Not done when service nature is not DATA_CHECK because DATA_CHECK does not update objects.
						for (ITask iTask : recycleList) {
							tasksQueue.addLast(iTask);
						}
						recycleList.clear();
					}
				} else {
					setTaskNothing(task, errorMessage);
					recycleList.add(task);
				}
				if (restart) {
					break;
				}
			}
			if (!restart && recycleList.isEmpty()) {
				if (LOG.isDebugEnabled()) {
					LOG.debug(String.format("Archiving cluster %s because if finished", idTaskCluster));
				}
				archiveCluster(idTaskCluster);
			}
		}

		errorSet.addAll(serviceResultContainer.ingest(restart()));
		return serviceResultContainer.compileResult(errorSet);
	}

	private void setTaskNothing(ITask task, String errorMessage) {
		try {
			getDaoSession().begin();
			taskManagerServiceDelegate.setTaskNothing(task, errorMessage);
			getDaoSession().commit();
		} catch (Exception e) {
			throw new ServiceException(e);
		} finally {
			getDaoSession().end();
		}
	}

	private IServiceResult<TaskExecutionResult> executeTask(ITaskService taskService, ITask task) {
		ServiceResultBuilder<TaskManagerErrorEnum> serviceResultBuilder = new ServiceResultBuilder<TaskManagerErrorEnum>();
		TaskExecutionResult taskExecutionResult = new TaskExecutionResult();

		try {
			ITaskService.IExecutionResult executionResult = taskService.execute(task);
			if (LOG.isDebugEnabled()) {
				LOG.debug("TM " + task.getIdCluster() + " - " + task.getServiceCode() + (executionResult != null && executionResult.isFinished() ? " - Success" : " - Failure"));
			}
			if (executionResult == null) {
				throw new Exception("Task execution result is null");
			} else {

				if (executionResult.mustStopAndRestartTaskManager()) {
					taskExecutionResult.stopAndRestart = true;
					// if we have to stop and restart, the id cluster of the task might have been modified, we'd better reload it
					updateTaskWithResult(entityServerService.findEntityById(ITask.class, task.getId()), executionResult);
				} else {
					updateTaskWithResult(task, executionResult);
				}
				if (executionResult.isFinished()) {
					taskExecutionResult.done = true;
				} else if (StringUtils.isNotEmpty(executionResult.getErrorMessage())) {
					taskExecutionResult.errorMessage = executionResult.getErrorMessage();
				} else if (CollectionUtils.isNotEmpty(executionResult.getErrors())) {
					taskExecutionResult.errorMessage = EnumErrorMessages.DEFAULT_ERROR_MESSAGE_LIST.getMessage();
				} else {
					taskExecutionResult.errorMessage = null;
				}
				saveErrors(task, executionResult.getErrors());
				if (executionResult.getErrors() != null) {
					taskExecutionResult.errorSet.addAll(executionResult.getErrors());
				}
			}
		} catch (Throwable t) {
			if ((t instanceof VersionConflictDaoException) && (t.getCause() instanceof PersistenceException) && (t.getCause().getCause() instanceof SQLException)
					&& (((SQLException) t.getCause().getCause()).getErrorCode() == 60)) {
				LOG.error(String.format("A conflicting error has been raised for task %s", task.getId()));
				serviceResultBuilder.addError(TaskManagerErrorEnum.CONFLICT, "CONFLICTING_ERROR", null);
			} else {
				serviceResultBuilder.addError(TaskManagerErrorEnum.TASK, "SERVICE_CODE", task.getServiceCode());
			}

			LOG.error(String.format("%s (%s) - TM %s - TaskCode = %s - Id = %s", t.getMessage(), t.getClass(), task.getIdCluster(), task.getServiceCode(), task.getId()), t);

			task = entityServerService.findEntityById(ITask.class, task.getId()); // reload task in case of a conflict error
			taskExecutionResult.errorMessage = EnumErrorMessages.CONFLICTING_SAVE_ERROR.getMessage();
			task.setResultDetail(StringUtils.left(ExceptionUtils.getFullStackTrace(t), 2000));
			updateTask(task);
		}

		return serviceResultBuilder.compileResult(taskExecutionResult);
	}

	private void updateTaskWithResult(ITask task, ITaskService.IExecutionResult executionResult) {
		task.setResultStatus(executionResult.getResultStatus());

		IStackResult stackResult = executionResult.getStackResult();
		task.setResultDesc(StringUtils.left(executionResult.getResultDesc(), 1024));
		if (stackResult != null) {
			if (StringUtils.isBlank(task.getResultDesc())) { // if result desc is null, we use the one from the first stack
				task.setResultDesc(StringUtils.left(stackResult.getResultText(), 1024));
			}
			if ((task.getTaskType().getResultDepth() > 0) || (task.getTaskType().getResultDepth() == -1)) {
				StringBuilder sb = new StringBuilder();
				buildStack(stackResult, 0, task.getTaskType().getResultDepth(), sb);
				task.setResultDetail(StringUtils.left(sb.toString(), 2000));
			}
		}
		updateTask(task);
	}

	private void buildStack(IStackResult stackResult, int currentResultDepth, final int maxResultDepth, StringBuilder sb) {
		if ((stackResult.getClassName() != null) && (stackResult.getResultText() != null)) { // if resultText is null, we ignore that level
			if (sb.length() > 0) {
				sb.append("\n");
				sb.append(StringUtils.repeat("    ", currentResultDepth));
			}
			sb.append(stackResult.getClassName());
			if (stackResult.getResultCode() != null) {
				sb.append(", ").append(stackResult.getResultCode());
			}
			if (!stackResult.getResultText().isEmpty()) {
				sb.append(": ").append(stackResult.getResultText());
			}
			if (stackResult.getResultDateTime() != null) {
				sb.append(", ended at ").append(sdf.format(stackResult.getResultDateTime()));
			}
		} else {
			currentResultDepth -= 1;
		}
		if ((maxResultDepth == -1) || (currentResultDepth + 1 < maxResultDepth)) {
			if (CollectionHelper.isNotEmpty(stackResult.getStackResultList())) {
				for (IStackResult child : stackResult.getStackResultList()) {
					buildStack(child, currentResultDepth + 1, maxResultDepth, sb);
				}
			}
		}
	}

	@Override
	public void saveErrors(ITask task, Set<IError> errors) {
		try {
			getDaoSession().begin();
			taskManagerServiceDelegate.saveErrors(task, errors);
			getDaoSession().commit();
		} catch (Exception e) {
			throw new ServiceException(e);
		} finally {
			getDaoSession().end();
		}
	}

	private void archiveCluster(IId idTaskCluster) {
		try {
			getDaoSession().begin();
			taskManagerServiceDelegate.archiveCluster(idTaskCluster);
			getDaoSession().commit();
		} catch (Exception e) {
			throw new ServiceException(e);
		} finally {
			getDaoSession().end();
		}
	}

	public <E extends Enum<E>, F extends ITaskObject<E>> void startEngine(List<F> taskObjects) {
		for (ITaskObject<?> taskObject : taskObjects) {
			startEngine(taskObject);
		}
	}

	private void updateTask(ITask task) {
		entityServerService.editEntity(task);
	}

	private TasksLists setTaskDone(ITask task) {
		try {
			getDaoSession().begin();
			TasksLists tasksLists = taskManagerServiceDelegate.nextTasks(task, false);
			getDaoSession().commit();
			return tasksLists;
		} catch (Exception e) {
			throw new ServiceException(e);
		} finally {
			getDaoSession().end();
		}
	}

	/**
	 * Skip task
	 */
	@Override
	public boolean skipTask(IId idTask, String skipComments) {
		if (idTask == null) {
			return false;
		}
		ITask task = null;

		try {
			getDaoSession().begin();
			task = entityServiceDelegate.findEntityById(ITask.class, idTask);
			if (task == null || !task.isCheckSkippable() || TaskStatus.CURRENT != task.getTaskStatus()) {
				return false;
			}
			task.setResultDesc(StringUtils.left(task.getResultDesc() != null ? task.getResultDesc() + " " + skipComments : skipComments, 1024));
			entityServiceDelegate.editEntity(task, false);
			taskManagerServiceDelegate.nextTasks(task, true);
			getDaoSession().commit();
		} catch (Exception e) {
			throw new ServiceException(e);
		} finally {
			getDaoSession().end();
		}

		startEngine(task.getIdCluster());

		return true;
	}

	@Override
	public IServiceResult<Set<IError>> restart() {
		Set<IError> errorSet = new HashSet<IError>();
		ServiceResultBuilder<TaskManagerErrorEnum> serviceResultBuilder = new ServiceResultBuilder<TaskManagerErrorEnum>();
		IId nextFromQueue = taskManagerServiceDelegate.getNextFromQueue();
		if (nextFromQueue != null) {
			errorSet.addAll(serviceResultBuilder.ingest(startEngine(nextFromQueue)));
		}
		return serviceResultBuilder.compileResult(errorSet);
	}

	@Override
	@Transactional(commit = true)
	public <E extends Enum<E>, F extends ITaskObject<E>> void cancelTaskObject(F object) {
		taskManagerServiceDelegate.cancelTaskObject(object);
	}

	@Override
	public <E extends Enum<E>, F extends ITaskObject<E>> void addToQueue(F taskObject) {
		IId idCluster = taskObject.getIdCluster();
		if (idCluster == null) {
			idCluster = createTaskCluster(taskObject);
		}
		taskManagerServiceDelegate.addToQueue(idCluster);
	}

	@Override
	@Transactional
	public ITask selectCurrentTaskByIdObject(IId idObject) {
		return getTaskMapper().selectCurrentTaskByIdObject(idObject);
	}

	@Override
	@Transactional
	public String getStatusPath(Class<? extends ITaskObject<?>> taskObjectClass, String currentStatus, String nextStatus) {
		return taskManagerServiceDelegate.getStatusPath(taskObjectClass, currentStatus, nextStatus);
	}

	@Override
	public void deleteTasksCluster(IId idCluster) {
		try {
			getDaoSession().begin();
			taskManagerServiceDelegate.deleteTasksCluster(idCluster);
			getDaoSession().commit();
		} catch (Exception e) {
			throw new ServiceException(e);
		} finally {
			getDaoSession().end();
		}
	}

	private class TaskExecutionResult {
		public final Set<IError> errorSet = new HashSet<IError>();
		public boolean done;
		public String errorMessage;
		public boolean stopAndRestart;
	}
}
