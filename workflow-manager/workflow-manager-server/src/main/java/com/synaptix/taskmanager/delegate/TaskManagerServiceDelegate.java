package com.synaptix.taskmanager.delegate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.LocalDateTime;

import com.google.inject.Inject;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.component.helper.ComponentHelper;
import com.synaptix.component.model.IError;
import com.synaptix.entity.IEntity;
import com.synaptix.entity.IErrorEntity;
import com.synaptix.entity.IId;
import com.synaptix.entity.IdRaw;
import com.synaptix.mybatis.dao.IGUIDGenerator;
import com.synaptix.mybatis.delegate.ComponentServiceDelegate;
import com.synaptix.mybatis.delegate.EntityServiceDelegate;
import com.synaptix.service.ServiceException;
import com.synaptix.taskmanager.antlr.AbstractGraphNode;
import com.synaptix.taskmanager.antlr.GraphCalcHelper;
import com.synaptix.taskmanager.antlr.IdGraphNode;
import com.synaptix.taskmanager.antlr.NextGraphNode;
import com.synaptix.taskmanager.antlr.ParallelGraphNode;
import com.synaptix.taskmanager.dao.mapper.AssoTaskPreviousTaskMapper;
import com.synaptix.taskmanager.dao.mapper.ErrorMapper;
import com.synaptix.taskmanager.dao.mapper.StatusGraphMapper;
import com.synaptix.taskmanager.dao.mapper.TaskClusterDependencyMapper;
import com.synaptix.taskmanager.dao.mapper.TaskManagerMapper;
import com.synaptix.taskmanager.dao.mapper.TaskMapper;
import com.synaptix.taskmanager.dao.mapper.TodoMapper;
import com.synaptix.taskmanager.manager.IObjectTypeTaskFactory;
import com.synaptix.taskmanager.manager.TaskServiceDiscovery;
import com.synaptix.taskmanager.manager.taskservice.ITaskService;
import com.synaptix.taskmanager.model.AssoTaskPreviousTaskBuilder;
import com.synaptix.taskmanager.model.ClusterTaskResultFields;
import com.synaptix.taskmanager.model.IClusterTaskResult;
import com.synaptix.taskmanager.model.IStatusGraph;
import com.synaptix.taskmanager.model.ITask;
import com.synaptix.taskmanager.model.ITaskBackup;
import com.synaptix.taskmanager.model.ITaskChain;
import com.synaptix.taskmanager.model.ITaskChainCriteria;
import com.synaptix.taskmanager.model.ITaskCluster;
import com.synaptix.taskmanager.model.ITaskClusterDependency;
import com.synaptix.taskmanager.model.ITaskObject;
import com.synaptix.taskmanager.model.ITaskType;
import com.synaptix.taskmanager.model.ITodo;
import com.synaptix.taskmanager.model.TaskChainFields;
import com.synaptix.taskmanager.model.TaskFields;
import com.synaptix.taskmanager.model.TaskTypeFields;
import com.synaptix.taskmanager.model.TasksLists;
import com.synaptix.taskmanager.model.domains.EnumErrorMessages;
import com.synaptix.taskmanager.model.domains.TaskStatus;
import com.synaptix.taskmanager.model.domains.TodoOwner;
import com.synaptix.taskmanager.model.domains.TodoStatus;
import com.synaptix.taskmanager.service.AbstractDelegate;
import com.synaptix.taskmanager.service.ITodoService;

public class TaskManagerServiceDelegate extends AbstractDelegate {

	private static final Log LOG = LogFactory.getLog(TaskManagerServiceDelegate.class);

	@Inject
	private TaskServiceDiscovery taskServiceDiscovery;

	@Inject
	private EntityServiceDelegate entityServiceDelegate;

	@Inject
	private ComponentServiceDelegate componentServiceDelegate;

	@Inject
	private Map<Class<? extends ITaskObject<?>>, IObjectTypeTaskFactory<?>> objectTypeTaskFactoryMap;

	@Inject
	private IGUIDGenerator guidGenerator;

	@Inject
	private ITodoService todoService;

	@Inject
	private StatusGraphServiceDelegate statusGraphServiceDelegate;

	/**
	 * Clusters that should be restarted in task manager
	 */
	private ThreadLocal<Set<IId>> clusterIdsQueue = new InheritableThreadLocal<Set<IId>>();

	@Inject
	public TaskManagerServiceDelegate() {
		super();
	}

	private TaskMapper getTaskMapper() {
		return getMapper(TaskMapper.class);
	}

	private ErrorMapper getErrorMapper() {
		return getMapper(ErrorMapper.class);
	}

	private TaskClusterDependencyMapper getTaskClusterDependencyMapper() {
		return getMapper(TaskClusterDependencyMapper.class);
	}

	private StatusGraphMapper getStatusGraphMapper() {
		return getMapper(StatusGraphMapper.class);
	}

	private TodoMapper getTodoMapper() {
		return getMapper(TodoMapper.class);
	}

	private AssoTaskPreviousTaskMapper getAssoTaskPreviousTaskMapper() {
		return getMapper(AssoTaskPreviousTaskMapper.class);
	}

	private TaskManagerMapper getTaskManagerMapper() {
		return getMapper(TaskManagerMapper.class);
	}

	/**
	 * Archive a cluster : move all tasks to archive table (T_TASK_ARCH).
	 */
	public boolean archiveCluster(IId idTaskCluster) {
		ITaskCluster taskCluster = entityServiceDelegate.findEntityById(ITaskCluster.class, idTaskCluster);
		if (taskCluster == null) {
			LOG.error("Trying to archive cluster id=" + idTaskCluster + " that does not exist.");
			return false;
		}
		if (taskCluster.isCheckArchive()) {
			LOG.error("Trying to archive cluster id=" + idTaskCluster + " that is already archived.");
			return false;
		}

		List<ITask> taskList = componentServiceDelegate.findComponentsByIdParent(ITask.class, TaskFields.idCluster().name(), idTaskCluster);
		if (taskList.isEmpty()) {
			LOG.error("Trying to archive cluster id=" + idTaskCluster + " but it has no tasks.");
			return false;
		}
		for (ITask task : taskList) {
			if (task.getTaskStatus() == null || task.getTaskStatus() == TaskStatus.TODO || task.getTaskStatus() == TaskStatus.CURRENT) {
				LOG.error("Trying to archive cluster id=" + idTaskCluster + " but it still has current tasks.");
				return false;
			}
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("Archive cluster with id = " + taskCluster.getId());
		}
		getTaskManagerMapper().archiveTaskCluster(idTaskCluster);

		return true;
	}

	/**
	 * Add task object to cluster.
	 */
	public <E extends Enum<E>, F extends ITaskObject<E>, G extends Enum<G>, H extends ITaskObject<G>> void addTaskObjectToTaskCluster(F taskObjectWithCluster, H newTaskObject) {
		if (taskObjectWithCluster == null || taskObjectWithCluster.getId() == null || taskObjectWithCluster.getIdCluster() == null) {
			return;
		}

		F to = entityServiceDelegate.findEntityById(ComponentFactory.getInstance().getComponentClass(taskObjectWithCluster), taskObjectWithCluster.getId());
		IId idCluster = to.getIdCluster();

		addTaskObjectToTaskCluster(idCluster, newTaskObject);
	}

	/**
	 * Add task object to cluster. If object was already linked to a cluster, does not delete old cluster.
	 */
	public <E extends Enum<E>, F extends ITaskObject<E>> void addTaskObjectToTaskCluster(IId idTaskCluster, F taskObject) {
		addTaskObjectToTaskCluster(idTaskCluster, taskObject, false);
	}

	/**
	 * Add task object to cluster.
	 *
	 * @param deleteOldTaskCluster
	 *            If true and task object was already linked to a task cluster, this cluster will be deleted.
	 */
	public <E extends Enum<E>, F extends ITaskObject<E>> void addTaskObjectToTaskCluster(IId idTaskCluster, F taskObject, boolean deleteOldTaskCluster) {
		if (idTaskCluster == null || taskObject == null || taskObject.getId() == null) {
			return;
		}
		F to = entityServiceDelegate.findEntityById(ComponentFactory.getInstance().getComponentClass(taskObject), taskObject.getId());
		if (to.getIdCluster() == null) {
			List<ITask> changedStatusTasks = new ArrayList<ITask>();
			createTaskClusterDependencyToTaskCluster(idTaskCluster, taskObject);
			changedStatusTasks.addAll(createTasks(idTaskCluster, taskObject));

			onTaskStatusChanged(changedStatusTasks);
		} else {
			getTaskMapper().updateIdCluster(idTaskCluster, taskObject.getId());
			getTaskClusterDependencyMapper().updateDependency(idTaskCluster, taskObject.getId());
			if (deleteOldTaskCluster) {
				getTaskMapper().deleteCluster(taskObject.getIdCluster());
			}
			taskObject.setIdCluster(idTaskCluster);
			saveOrUpdateEntity(taskObject);
		}
		addToQueue(idTaskCluster);
	}

	/**
	 * Remove task object from cluster, add it to a new task cluster. Task manager must be restarted on task object after this method.
	 */
	public <E extends Enum<E>, F extends ITaskObject<E>> void removeTaskObjectFromTaskCluster(F taskObject) {
		if (taskObject == null || taskObject.getId() == null) {
			return;
		}
		if (taskObject.getIdCluster() == null) {
			throw new IllegalArgumentException("taskObject is not already in a cluster. Object ID=" + taskObject.getId());
		}

		ITaskCluster taskCluster = createTaskCluster();
		addTaskObjectToTaskCluster(taskCluster.getId(), taskObject);
	}

	// Tasks creation

	/**
	 * Create cluster for a taskObject.
	 *
	 * @return ID of the new cluster
	 */
	public <E extends Enum<E>, F extends ITaskObject<E>> IId createTaskCluster(F taskObject) {
		List<ITask> changedStatusTasks = new ArrayList<ITask>();

		ITaskCluster taskCluster = createTaskCluster();
		createTaskClusterDependencyToTaskCluster(taskCluster.getId(), taskObject);

		changedStatusTasks.addAll(createTasks(taskCluster.getId(), taskObject));
		onTaskStatusChanged(changedStatusTasks);
		return taskCluster.getId();
	}

	private ITaskCluster createTaskCluster() {
		ITaskCluster taskCluster = ComponentFactory.getInstance().createInstance(ITaskCluster.class);
		taskCluster.setCheckGraphCreated(true);
		saveEntity(taskCluster);
		return taskCluster;
	}

	/**
	 * Create status graph and tasks for tasks clusters dependencies.
	 */
	public void createTaskGraphs(ITaskCluster taskCluster) {
		List<ITask> changedStatusTasks = new ArrayList<ITask>();

		List<ITaskClusterDependency> taskClusterDependencies = getTaskClusterDependencyMapper().selectTaskClusterDependenciesByIdTaskCluster(taskCluster.getId());
		if (taskClusterDependencies != null && !taskClusterDependencies.isEmpty()) {
			for (ITaskClusterDependency taskClusterDependency : taskClusterDependencies) {
				ITaskObject<?> taskObject = entityServiceDelegate.findEntityById(taskClusterDependency.getObjectType(), taskClusterDependency.getIdObject());
				changedStatusTasks.addAll(createTasks(taskCluster.getId(), taskObject));
			}
		}

		taskCluster.setCheckGraphCreated(true);
		saveOrUpdateEntity(taskCluster);

		onTaskStatusChanged(changedStatusTasks);
	}

	/**
	 * create task cluster dependency and create status graph and tasks.
	 */
	private <E extends Enum<E>, F extends ITaskObject<E>> void createTaskClusterDependencyToTaskCluster(IId idTaskCluster, F taskObject) {
		Class<? extends ITaskObject<?>> taskObjectClass = ComponentFactory.getInstance().getComponentClass(taskObject);

		taskObject.setIdCluster(idTaskCluster);
		saveOrUpdateEntity(taskObject);

		ITaskClusterDependency taskClusterDependency = ComponentFactory.getInstance().createInstance(ITaskClusterDependency.class);
		taskClusterDependency.setIdCluster(idTaskCluster);
		taskClusterDependency.setIdObject(taskObject.getId());
		taskClusterDependency.setObjectType(taskObjectClass);
		saveEntity(taskClusterDependency);
	}

	/**
	 * Create status graph and tasks
	 */
	private <E extends Enum<E>, F extends ITaskObject<E>> List<ITask> createTasks(IId idTaskCluster, F taskObject) {
		Class<? extends ITaskObject<?>> taskObjectClass = ComponentFactory.getInstance().getComponentClass(taskObject);
		String currentStatus = taskObject.getStatus() != null ? taskObject.getStatus().name() : null;

		List<IStatusGraph> statusGraphs = getStatusGraphMapper().selectStatusGraphsByTaskObjectType(taskObjectClass);

		String firstStatus = null;
		for (IStatusGraph statusGraph : statusGraphs) {
			if (statusGraph.getCurrentStatus() == null) {
				firstStatus = statusGraph.getNextStatus();
			}
		}

		// Create a first task, it does nothing
		ITask initTask = ComponentFactory.getInstance().createInstance(ITask.class);
		initTask.setCheckError(false);
		initTask.setCheckSkippable(false);
		initTask.setErrorMessage(null);
		initTask.setExecutantRole(null);
		initTask.setIdCluster(idTaskCluster);
		initTask.setIdObject(taskObject.getId());
		initTask.setIdParentTask(null);
		initTask.setIdTaskType(null);
		initTask.setManagerRole(null);
		initTask.setNature(null);
		initTask.setObjectType(taskObjectClass);
		initTask.setServiceCode(null);
		initTask.setTaskStatus(TaskStatus.CURRENT);
		initTask.setStartDate(new Date());
		initTask.setTaskType(null);
		initTask.setNextStatus(firstStatus);
		initTask.setCheckGroup(false);
		initTask.setIdPreviousUpdateStatusTask(null);
		initTask.setCheckTodoExecutantCreated(false);
		initTask.setCheckTodoManagerCreated(false);
		initTask.setFirstErrorDate(null);
		initTask.setTodoManagerDuration(null);
		saveEntity(initTask);

		return createTasks(idTaskCluster, taskObject, statusGraphs, initTask, currentStatus);
	}

	private <E extends Enum<E>, F extends ITaskObject<E>> List<ITask> createTasks(IId idTaskCluster, F taskObject, List<IStatusGraph> statusGraphs, ITask previousUpdateStatusTask,
			String currentStatus) {
		List<ITask> res = new ArrayList<ITask>();

		Class<? extends ITaskObject<?>> taskObjectClass = ComponentFactory.getInstance().getComponentClass(taskObject);

		List<IStatusGraph> sgs = findStatusGraphs(statusGraphs, currentStatus);
		if (sgs != null && !sgs.isEmpty()) {
			for (IStatusGraph sg : sgs) {
				// Create group task
				ITask groupTask = ComponentFactory.getInstance().createInstance(ITask.class);
				groupTask.setCheckError(false);
				groupTask.setCheckSkippable(false);
				groupTask.setErrorMessage(null);
				groupTask.setExecutantRole(null);
				groupTask.setIdCluster(idTaskCluster);
				groupTask.setIdObject(taskObject.getId());
				groupTask.setIdParentTask(null);
				groupTask.setIdTaskType(null);
				groupTask.setManagerRole(null);
				groupTask.setNature(null);
				groupTask.setObjectType(taskObjectClass);
				groupTask.setServiceCode(null);
				groupTask.setTaskStatus(TaskStatus.TODO);
				groupTask.setTaskType(null);
				groupTask.setNextStatus(sg.getNextStatus());
				groupTask.setCheckGroup(true);
				groupTask.setIdPreviousUpdateStatusTask(null);
				groupTask.setCheckTodoExecutantCreated(false);
				groupTask.setCheckTodoManagerCreated(false);
				groupTask.setFirstErrorDate(null);
				groupTask.setTodoManagerDuration(null);
				saveEntity(groupTask);
				res.add(groupTask);

				linkTwoTasks(previousUpdateStatusTask.getId(), groupTask.getId());

				// Create update status task
				ITaskType updateStatusTaskType = sg.getTaskType();
				ITask updateStatusTask = ComponentFactory.getInstance().createInstance(ITask.class);
				updateStatusTask.setCheckError(false);
				updateStatusTask.setCheckSkippable(updateStatusTaskType.isCheckSkippable());
				updateStatusTask.setErrorMessage(null);
				updateStatusTask.setExecutantRole(updateStatusTaskType.getExecutantRole());
				updateStatusTask.setIdCluster(idTaskCluster);
				updateStatusTask.setIdObject(taskObject.getId());
				updateStatusTask.setIdParentTask(null);
				updateStatusTask.setIdTaskType(sg.getIdTaskType());
				updateStatusTask.setManagerRole(updateStatusTaskType.getManagerRole());
				updateStatusTask.setNature(updateStatusTaskType.getNature());
				updateStatusTask.setObjectType(taskObjectClass);
				updateStatusTask.setServiceCode(updateStatusTaskType.getServiceCode());
				updateStatusTask.setTaskStatus(TaskStatus.TODO);
				updateStatusTask.setTaskType(updateStatusTaskType);
				updateStatusTask.setNextStatus(sg.getNextStatus());
				updateStatusTask.setCheckGroup(false);
				updateStatusTask.setIdPreviousUpdateStatusTask(previousUpdateStatusTask.getId());
				updateStatusTask.setCheckTodoExecutantCreated(false);
				updateStatusTask.setCheckTodoManagerCreated(false);
				updateStatusTask.setFirstErrorDate(null);
				updateStatusTask.setTodoManagerDuration(updateStatusTaskType.getTodoManagerDuration());
				saveEntity(updateStatusTask);
				res.add(updateStatusTask);

				linkTwoTasks(groupTask.getId(), updateStatusTask.getId());

				res.addAll(createTasks(idTaskCluster, taskObject, statusGraphs, updateStatusTask, sg.getNextStatus()));
			}
		}

		return res;
	}

	/*
	 * Create a tasks for parent and task chain criteria
	 */
	private CreateTasksResult _createTasks(ITask parentTask, ITaskChainCriteria<? extends Enum<?>> taskChainCriteria) {
		CreateTasksResult res = null;
		if (taskChainCriteria != null) {
			if (taskChainCriteria.getGraphRule() != null && !taskChainCriteria.getGraphRule().isEmpty()) {
				AbstractGraphNode graphNode = _replace(taskChainCriteria.getTaskChains(), GraphCalcHelper.buildGraphRule(taskChainCriteria.getGraphRule()));
				List<ITaskType> taskTypes = new ArrayList<ITaskType>();
				for (ITaskChain taskChain : taskChainCriteria.getTaskChains()) {
					taskTypes.addAll(taskChain.getTaskTypes());
				}

				res = _createTasks(parentTask, graphNode, taskTypes);
			}
		}
		return res;
	}

	private CreateTasksResult _createTasks(ITask parentTask, AbstractGraphNode node, List<ITaskType> taskTypes) {
		CreateTasksResult res = null;
		if (node != null) {
			res = new CreateTasksResult();
			if (node instanceof IdGraphNode) {
				IdGraphNode ign = (IdGraphNode) node;
				ITaskType taskType = ComponentHelper.findComponentBy(taskTypes, TaskTypeFields.id().name(), new IdRaw(ign.getId()));
				if (taskType != null) {
					ITask task = ComponentFactory.getInstance().createInstance(ITask.class);
					task.setCheckError(false);
					task.setCheckSkippable(taskType.isCheckSkippable());
					task.setErrorMessage(null);
					task.setExecutantRole(taskType.getExecutantRole());
					task.setIdCluster(parentTask.getIdCluster());
					task.setIdObject(parentTask.getIdObject());
					task.setIdParentTask(null);
					task.setIdTaskType(taskType.getId());
					task.setManagerRole(taskType.getManagerRole());
					task.setNature(taskType.getNature());
					task.setObjectType(taskType.getObjectType());
					task.setServiceCode(taskType.getServiceCode());
					task.setTaskStatus(TaskStatus.TODO);
					task.setTaskType(taskType);
					task.setNextStatus(parentTask.getNextStatus());
					task.setCheckGroup(false);
					task.setIdPreviousUpdateStatusTask(null);
					task.setCheckTodoExecutantCreated(false);
					task.setCheckTodoManagerCreated(false);
					task.setFirstErrorDate(null);
					task.setTodoManagerDuration(taskType.getTodoManagerDuration());
					saveEntity(task);

					res.firstTasks = Collections.singletonList(task);
					res.lastTasks = Collections.singletonList(task);
					res.allTasks = Collections.singletonList(task);
				} else {
					throw new RuntimeException("No task with id " + ign.getId());
				}
			} else if (node instanceof ParallelGraphNode) {
				ParallelGraphNode pgn = (ParallelGraphNode) node;

				res.firstTasks = new ArrayList<ITask>();
				res.lastTasks = new ArrayList<ITask>();
				res.allTasks = new ArrayList<ITask>();
				for (AbstractGraphNode subNode : pgn.getNodes()) {
					CreateTasksResult cr = _createTasks(parentTask, subNode, taskTypes);
					if (cr != null) {
						res.firstTasks.addAll(cr.firstTasks);
						res.lastTasks.addAll(cr.lastTasks);
						res.allTasks.addAll(cr.allTasks);
					}
				}
			} else if (node instanceof NextGraphNode) {
				NextGraphNode ngn = (NextGraphNode) node;

				CreateTasksResult firstCr = _createTasks(parentTask, ngn.getFirstNode(), taskTypes);
				CreateTasksResult nextCr = _createTasks(parentTask, ngn.getNextNode(), taskTypes);

				if (firstCr != null && nextCr != null) {
					if (firstCr.lastTasks != null && !firstCr.lastTasks.isEmpty() && nextCr.firstTasks != null && !nextCr.firstTasks.isEmpty()) {
						for (ITask firstTask : firstCr.lastTasks) {
							for (ITask nextTask : nextCr.firstTasks) {
								linkTwoTasks(firstTask.getId(), nextTask.getId());
							}
						}
					}
					res.firstTasks = firstCr.firstTasks;
					res.lastTasks = nextCr.lastTasks;
					res.allTasks = new ArrayList<ITask>();
					res.allTasks.addAll(firstCr.allTasks);
					res.allTasks.addAll(nextCr.allTasks);
				}
			}
		}
		return res;
	}

	private AbstractGraphNode _replace(List<ITaskChain> taskChains, AbstractGraphNode node) {
		AbstractGraphNode res = null;
		if (node != null) {
			if (node instanceof IdGraphNode) {
				IdGraphNode idGraphNode = (IdGraphNode) node;
				ITaskChain taskChain = ComponentHelper.findComponentBy(taskChains, TaskChainFields.id().name(), new IdRaw(idGraphNode.getId()));
				if (taskChain != null) {
					res = GraphCalcHelper.buildGraphRule(taskChain.getGraphRule());
				} else {
					throw new ServiceException("TASK_CHAIN_NOT_FOUND", null);
				}
			} else {
				if (node instanceof ParallelGraphNode) {
					List<AbstractGraphNode> gns = new ArrayList<AbstractGraphNode>();
					for (AbstractGraphNode subNode : ((ParallelGraphNode) node).getNodes()) {
						gns.add(_replace(taskChains, subNode));
					}
					res = new ParallelGraphNode(gns);
				} else if (node instanceof NextGraphNode) {
					AbstractGraphNode firstNode = _replace(taskChains, ((NextGraphNode) node).getFirstNode());
					AbstractGraphNode nextNode = _replace(taskChains, ((NextGraphNode) node).getNextNode());
					res = new NextGraphNode(firstNode, nextNode);
				}
			}
		}
		return res;
	}

	/**
	 * Create IAssoTaskPreviousTask.
	 *
	 * @param idFirstTask
	 *            will be set as previous task in the association.
	 * @param idNextTask
	 *            will be set as task in the association.
	 */
	private void linkTwoTasks(IId idFirstTask, IId idNextTask) {
		getAssoTaskPreviousTaskMapper().insertAssoTaskPreviousTask(new AssoTaskPreviousTaskBuilder().idTask(idNextTask).idPreviousTask(idFirstTask).build());
	}

	private List<IStatusGraph> findStatusGraphs(List<IStatusGraph> statusGraphs, String currentStatus) {
		List<IStatusGraph> res = new ArrayList<IStatusGraph>();

		if (statusGraphs != null && !statusGraphs.isEmpty()) {
			for (IStatusGraph statusGraph : statusGraphs) {
				if ((currentStatus == null && statusGraph.getCurrentStatus() == null)
						|| (currentStatus != null && statusGraph.getCurrentStatus() != null && statusGraph.getCurrentStatus().equals(currentStatus))) {
					res.add(statusGraph);
				}
			}
		}

		return res;
	}

	private void onTaskStatusChanged(List<ITask> tasks) {
		if (tasks != null && !tasks.isEmpty()) {
			for (ITask task : tasks) {
				if (task.getServiceCode() != null) {
					ITaskService taskService = taskServiceDiscovery.getTaskService(task.getServiceCode());
					if (taskService != null) {
						IObjectTypeTaskFactory<?> objectTypeTaskFactory = null;
						if (objectTypeTaskFactoryMap != null && !objectTypeTaskFactoryMap.isEmpty()) {
							objectTypeTaskFactory = objectTypeTaskFactoryMap.get(task.getObjectType());
						}

						switch (task.getTaskStatus()) {
						case TODO:
							if (objectTypeTaskFactory != null) {
								if (isTodoExecutant(task)) {
									saveTodoExecutant(task, objectTypeTaskFactory);
								}
							}

							taskService.onTodo(task);
							break;
						case CURRENT:
							if (objectTypeTaskFactory != null) {
								if (isTodoExecutant(task)) {
									saveTodoExecutant(task, objectTypeTaskFactory);
								}
								if (!task.isCheckTodoExecutantCreated() || !task.isCheckTodoManagerCreated()) {
									updateTodoToCurrentStatus(task.getId());
								}
							}

							taskService.onCurrent(task);
							break;
						case DONE:
							taskService.onDone(task);
							break;
						case CANCELED:
							taskService.onCanceled(task);
							break;
						case SKIPPED:
							taskService.onSkipped(task);
							break;
						}
					}
				}
			}
		}
	}

	private void updateTodoToCurrentStatus(IId idTask) {
		getTodoMapper().updatePendingTodos(idTask);
	}

	// todo management

	private boolean isTodoExecutant(ITask task) {
		if (!task.isCheckTodoExecutantCreated() && task.getExecutantRole() != null && task.getServiceCode() != null) {
			ITaskService.ITodoDescriptor todoDescriptor = getToDoDescriptor(task);
			if (todoDescriptor != null && ((todoDescriptor.isCreateToTodoTask() && TaskStatus.TODO == task.getTaskStatus()) || TaskStatus.CURRENT == task.getTaskStatus())) {
				return true;
			}
		}
		return false;
	}

	private ITaskService.ITodoDescriptor getToDoDescriptor(ITask task) {
		ITaskService.ITodoDescriptor todoDescriptor = null;
		ITaskService taskService = taskServiceDiscovery.getTaskService(task.getServiceCode());
		if (taskService != null) {
			todoDescriptor = taskService.getTodoDescriptor();
		}
		if (todoDescriptor == null && objectTypeTaskFactoryMap != null && !objectTypeTaskFactoryMap.isEmpty()) {
			IObjectTypeTaskFactory<?> objectTypeTaskFactory = objectTypeTaskFactoryMap.get(task.getObjectType());
			todoDescriptor = objectTypeTaskFactory.getDefaultTodoDescriptor();
		}
		return todoDescriptor;
	}

	/*
	 * Create and save a todo for the manager. <br> This method must be called inside a dao session.
	 */
	private void saveTodoManager(ITask task, IObjectTypeTaskFactory<?> objectTypeTaskFactory) {
		if (task.isCheckGroup() || task.isCheckTodoManagerCreated() || objectTypeTaskFactory == null) {
			return;
		}

		IEntity ownerEntity = objectTypeTaskFactory.getManager(task);
		if (ownerEntity == null) {
			return;
		}
		IEntity contactEntity = objectTypeTaskFactory.getExecutant(task);

		ITodo todo = createTodo(task, TodoOwner.MANAGER, objectTypeTaskFactory, ownerEntity, contactEntity);

		saveEntity(todo);

		task.setCheckTodoManagerCreated(true);
		saveOrUpdateEntity(task);
	}

	/*
	 * Create and save todo executant
	 */
	private void saveTodoExecutant(ITask task, IObjectTypeTaskFactory<?> objectTypeTaskFactory) {
		if (task.isCheckGroup() || task.isCheckTodoExecutantCreated() || objectTypeTaskFactory == null) {
			return;
		}

		IEntity ownerEntity = objectTypeTaskFactory.getExecutant(task);
		if (ownerEntity == null) {
			return;
		}

		IEntity contactEntity = objectTypeTaskFactory.getManager(task);

		ITodo todo = createTodo(task, TodoOwner.EXECUTANT, objectTypeTaskFactory, ownerEntity, contactEntity);

		saveEntity(todo);

		task.setCheckTodoExecutantCreated(true);
		saveOrUpdateEntity(task);

	}

	/*
	 * Delete all todo to task
	 */
	private void deleteTodos(ITask task) {
		if (task.isCheckTodoExecutantCreated() || task.isCheckTodoManagerCreated()) {
			getTodoMapper().deleteTasksTodo(task.getId());
		}
	}

	/**
	 * Creates a todo. <br>
	 * No daoSession is opened in this method.
	 *
	 * @param task
	 *            Task that created the todo.
	 * @param objectTypeTaskFactory
	 *            IObjectTypeTaskFactory
	 * @return the new ITodo
	 */
	private ITodo createTodo(ITask task, TodoOwner owner, IObjectTypeTaskFactory<?> objectTypeTaskFactory, IEntity ownerEntity, IEntity contactEntity) {
		ITaskService.ITodoDescriptor todoDescriptor = getToDoDescriptor(task);
		ITodo todo = todoService.createTodo(ownerEntity, contactEntity);

		todo.setIdTask(task.getId());
		String description = objectTypeTaskFactory.getTaskObjectDescription(task.getIdObject());
		todo.setCode(todoDescriptor.getCode());
		todo.setDescription(description);
		todo.setUri(todoDescriptor.getUri());
		todo.setObjectType(task.getObjectType());
		todo.setIdObject(task.getIdObject());
		todo.setOwner(owner);
		IId idTodoFolder = task.getTaskType().getIdTodoFolder();
		if (idTodoFolder == null) {
			ITaskChainCriteria<? extends Enum<?>> taskChainCriteria = objectTypeTaskFactory.getTaskChainCriteria(task);
			if (taskChainCriteria != null) {
				idTodoFolder = taskChainCriteria.getIdTodoFolder();
			}
		}
		todo.setIdTodoFolder(idTodoFolder);

		switch (task.getTaskStatus()) {
		case TODO:
			todo.setStatus(TodoStatus.PENDING);
			break;
		case CURRENT:
			todo.setStatus(TodoStatus.TODO);
			break;
		default:
			todo.setStatus(null);
			break;
		}

		return todo;
	}

	private boolean isTodoManager(ITask task) {
		if (!task.isCheckTodoManagerCreated() && task.getManagerRole() != null && task.getServiceCode() != null && task.isCheckError() && task.getFirstErrorDate() != null
				&& (task.getTodoManagerDuration() == null || (!new LocalDateTime().isBefore(task.getFirstErrorDate().plus(task.getTodoManagerDuration()))))
				&& TaskStatus.CURRENT == task.getTaskStatus()) {
			ITaskService.ITodoDescriptor todoDescriptor = getToDoDescriptor(task);
			if (todoDescriptor != null) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check task current to create todo manager
	 */
	public void checkTodoManagerTasks() {
		List<IClusterTaskResult> clusterTaskResults = getTaskMapper().selectTodoManagerTasks(new LocalDateTime());
		if (clusterTaskResults != null && !clusterTaskResults.isEmpty()) {
			Map<IId, List<IClusterTaskResult>> map = ComponentHelper.buildComponentsMap(clusterTaskResults, ClusterTaskResultFields.idCluster().name());
			if (map != null && !map.isEmpty()) {
				for (Entry<IId, List<IClusterTaskResult>> entry : map.entrySet()) {
					IId idTaskCluster = entry.getKey();
					if (idTaskCluster != null) {
						List<IClusterTaskResult> ctrs = entry.getValue();
						if (ctrs != null && !ctrs.isEmpty()) {
							for (IClusterTaskResult ctr : ctrs) {
								ITask task = entityServiceDelegate.findEntityById(ITask.class, ctr.getIdTask());
								if (objectTypeTaskFactoryMap != null && !objectTypeTaskFactoryMap.isEmpty()) {
									IObjectTypeTaskFactory<?> objectTypeTaskFactory = objectTypeTaskFactoryMap.get(task.getObjectType());
									if (objectTypeTaskFactory != null) {
										if (isTodoManager(task)) {
											saveTodoManager(task, objectTypeTaskFactory);
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	/*
	 * Clean errors for given task. Save new errors if there are any.
	 */
	public void saveErrors(ITask task, Set<IError> errors) {
		if (EnumErrorMessages.DEFAULT_ERROR_MESSAGE_LIST.getMessage().equals(task.getErrorMessage())) {
			getErrorMapper().deleteErrorsByIdTask(task.getId());
		}

		if (CollectionUtils.isNotEmpty(errors)) {
			for (IError error : errors) {
				IErrorEntity errorEntity = ComponentFactory.getInstance().createInstance(IErrorEntity.class);
				errorEntity.setCode(error.getErrorCode().name());
				errorEntity.setLabel(error.getErrorCode().getLabel());
				errorEntity.setType(error.getErrorCode().getType().name());
				errorEntity.setAttribute(error.getAttribute());
				errorEntity.setValue(error.getValue());
				errorEntity.setIdObject(task.getIdObject());
				errorEntity.setIdTask(task.getId());
				errorEntity.setObjectType(task.getObjectType());
				saveOrUpdateEntity(errorEntity);
			}
		}
	}

	/*
	 * Task nothing, if same error with previous data then nothing else save
	 */
	public void setTaskNothing(ITask task, String errorMessage) {
		if (task.isCheckError()
				&& ((errorMessage == null && task.getErrorMessage() == null) || (errorMessage != null && task.getErrorMessage() != null && errorMessage.equals(task.getErrorMessage())))) {
			return;
		}
		if (!task.isCheckError()) {
			task.setFirstErrorDate(new LocalDateTime());
		}

		task.setErrorMessage(StringUtils.substring(errorMessage, 0, 2000));
		if (errorMessage != null && !EnumErrorMessages.ERROR_MESSAGE_WAITING.getMessage().equals(errorMessage)) {
			task.setCheckError(true);
		}

		if (!EnumErrorMessages.ERROR_MESSAGE_WAITING.getMessage().equals(errorMessage) && !task.isCheckGroup() && objectTypeTaskFactoryMap != null && !objectTypeTaskFactoryMap.isEmpty()) {
			IObjectTypeTaskFactory<?> objectTypeTaskFactory = objectTypeTaskFactoryMap.get(task.getObjectType());
			saveTodoExecutant(task, objectTypeTaskFactory);
			saveTodoManager(task, objectTypeTaskFactory);
		} else {
			deleteTodos(task);
			task.setCheckTodoExecutantCreated(false);
			task.setCheckTodoManagerCreated(false);
		}

		saveOrUpdateEntity(task);
	}

	public TasksLists nextTasks(ITask task, boolean skip) {
		List<ITask> todoTasks = getTaskMapper().selectNextTodoToCurrentTasks(task.getId());

		List<ITask> changedStatusTasks = new ArrayList<ITask>();
		List<ITask> toCurrentTasks = new ArrayList<ITask>();

		TasksLists tasksLists = new TasksLists();

		if (task.getIdPreviousUpdateStatusTask() != null) {
			// Cancel tasks of other branches
			tasksLists.getIdTasksToRemove().addAll(deleteOtherChildPreviousUpdateStatusTasks(task));
		}

		if (skip) {
			saveTaskSkipped(task);
		} else {
			saveTaskDone(task);
		}
		changedStatusTasks.add(task);

		if (todoTasks != null && !todoTasks.isEmpty()) {
			for (ITask todoTask : todoTasks) {
				if (todoTask.isCheckGroup()) {
					// Replace group with actual tasks.
					IObjectTypeTaskFactory<?> objectTypeTaskFactory = null;
					if (objectTypeTaskFactoryMap != null && !objectTypeTaskFactoryMap.isEmpty()) {
						objectTypeTaskFactory = objectTypeTaskFactoryMap.get(todoTask.getObjectType());
					}
					ITaskChainCriteria<? extends Enum<?>> taskChainCriteria = null;
					if (objectTypeTaskFactory != null) {
						taskChainCriteria = objectTypeTaskFactory.getTaskChainCriteria(todoTask);
					}

					CreateTasksResult ctr = _createTasks(todoTask, taskChainCriteria);
					if (ctr != null) {
						if (ctr.allTasks != null) {
							changedStatusTasks.addAll(ctr.allTasks);
						}

						if (ctr.firstTasks != null) {
							toCurrentTasks.addAll(ctr.firstTasks);
							tasksLists.getNewTasksToDo().addAll(ctr.firstTasks);

							for (ITask iTask : ctr.firstTasks) {
								List<IId> previousTasks = getTaskMapper().findPreviousTasks(todoTask.getId());
								for (IId idPreviousTask : previousTasks) {
									linkTwoTasks(idPreviousTask, iTask.getId());
								}
							}
						}

						for (ITask iTask : ctr.lastTasks) {
							List<IId> nextTasks = getTaskMapper().findNextTasks(todoTask.getId());
							for (IId idNextTask : nextTasks) {
								linkTwoTasks(iTask.getId(), idNextTask);
							}
						}
					} else {
						List<IId> previousTasks = getTaskMapper().findPreviousTasks(todoTask.getId());
						List<ITask> nextTasks = getTaskMapper().selectNextTasks(todoTask.getId(), null);
						if (previousTasks != null && !previousTasks.isEmpty() && nextTasks != null && !nextTasks.isEmpty()) {
							linkTwoTasks(previousTasks.get(0), nextTasks.get(0).getId());
						}
						if (nextTasks != null) {
							toCurrentTasks.addAll(nextTasks);
							tasksLists.getNewTasksToDo().addAll(nextTasks);
						}
					}
					deleteTask(todoTask.getId());
					tasksLists.getIdTasksToRemove().add(todoTask.getId());
				} else {
					saveTaskCurrent(todoTask);
					changedStatusTasks.add(todoTask);
					tasksLists.getNewTasksToDo().add(todoTask);
				}
			}
		}

		onTaskStatusChanged(changedStatusTasks);

		if (!toCurrentTasks.isEmpty()) {
			for (ITask t : toCurrentTasks) {
				saveTaskCurrent(t);
			}
		}

		onTaskStatusChanged(toCurrentTasks);

		return tasksLists;
	}

	/*
	 * Set task done, save history and delete todos
	 */
	private void saveTaskDone(ITask task) {
		task.setTaskStatus(TaskStatus.DONE);
		task.setEndDate(new Date());
		task.setErrorMessage(null);
		task.setCheckError(false);
		saveOrUpdateEntity(task);

		deleteTodos(task);
	}

	/*
	 * Set task current, save history
	 */
	private void saveTaskCurrent(ITask task) {
		task.setTaskStatus(TaskStatus.CURRENT);
		task.setStartDate(new Date());
		saveOrUpdateEntity(task);
	}

	/*
	 * Set task skipped, save history, delete todos and errors
	 */
	private void saveTaskSkipped(ITask task) {
		task.setTaskStatus(TaskStatus.SKIPPED);
		task.setEndDate(new Date());
		saveOrUpdateEntity(task);

		getErrorMapper().deleteErrorsByIdTask(task.getId());
		deleteTodos(task);
	}

	/*
	 * Delete a task.
	 */
	private void deleteTask(IId idTask) {
		getAssoTaskPreviousTaskMapper().deleteTaskAsso(idTask);
		getTaskMapper().deleteTask(idTask);
	}

	private List<IId> deleteOtherChildPreviousUpdateStatusTasks(ITask task) {
		List<IId> tasksToDelete = getTaskMapper().findTasksToDelete(task.getIdPreviousUpdateStatusTask(), task.getNextStatus());
		if (tasksToDelete != null && !tasksToDelete.isEmpty()) {
			getTodoMapper().deleteTasksTodos(tasksToDelete);
			getTodoMapper().deleteTaskErrors(tasksToDelete);
			getAssoTaskPreviousTaskMapper().deleteTasksAssos(tasksToDelete);
			getTaskMapper().deleteTasks(tasksToDelete);
		}
		return tasksToDelete;
	}

	public void updateTodoDescription(ITodo todo) {
		if (todo == null) {
			return;
		}
		IObjectTypeTaskFactory<?> objectTypeTaskFactory = objectTypeTaskFactoryMap.get(todo.getObjectType());
		String objectDescription = objectTypeTaskFactory.getTaskObjectDescription(todo.getIdObject());
		todo.setDescription(objectDescription);
		entityServiceDelegate.editEntity(todo, false);
	}

	public void updateTodoDescription(IId idObject, Class<? extends ITaskObject<?>> objectClass) {
		if (idObject == null) {
			return;
		}
		IObjectTypeTaskFactory<?> objectTypeTaskFactory = objectTypeTaskFactoryMap.get(objectClass);
		String objectDescription = objectTypeTaskFactory.getTaskObjectDescription(idObject);
		getTodoMapper().updateDescription(objectDescription, idObject);
	}

	public LocalDateTime getTaskEndTime(IId idTaskObject, String taskCode) {
		return getTaskMapper().getTaskEndTime(idTaskObject, taskCode);
	}

	public void validateTask(IId idTask) {
		getTaskMapper().validateTask(idTask);
	}

	public void changeServiceCode(ITaskBackup taskBackup, String serviceCode) {
		if (!ObjectUtils.equals(taskBackup.getServiceCode(), serviceCode)) {
			taskBackup.setNbRetry(0);
		}
		taskBackup.setServiceCode(serviceCode);
		saveOrUpdateEntity(taskBackup);
	}

	public ITaskBackup findBackup(IId idTaskCluster) {
		return getTaskManagerMapper().findBackupByIdCluster(idTaskCluster);
	}

	public <E extends Enum<E>, F extends ITaskObject<E>> ITaskBackup findBackup(IId idTaskObject, Class<F> objectClass) {
		return getTaskManagerMapper().findBackupByIdObjectAndClass(idTaskObject, objectClass);
	}

	public List<ITaskBackup> findTasksBackupToLaunch(int nbLines, int maxRetry, int createdDateDelay, int updatedDateDelay) {
		IId idProcess = new IdRaw(guidGenerator.newGUID());
		getTaskManagerMapper().flagTasksBackupToLaunch(idProcess, nbLines, maxRetry, new Date(), createdDateDelay, updatedDateDelay);
		return getTaskManagerMapper().findTasksBackupToLaunch(idProcess);
	}

	/**
	 * Add a cluster id to the queue.
	 */
	public void addToQueue(IId idCluster) {
		if (idCluster != null) {
			getQueue().add(idCluster);
		}
	}

	private Set<IId> getQueue() {
		if (clusterIdsQueue.get() == null) {
			clusterIdsQueue.set(new CopyOnWriteArraySet<IId>());
		}

		return clusterIdsQueue.get();
	}

	public IId getNextFromQueue() {
		if (clusterIdsQueue.get() == null || getQueue().isEmpty()) {
			return null;
		}

		IId nextIdCluster = getQueue().iterator().next();
		getQueue().remove(nextIdCluster);
		return nextIdCluster;
	}

	public boolean hasNextInQueue() {
		return clusterIdsQueue.get() != null && !clusterIdsQueue.get().isEmpty();
	}

	public <E extends Enum<E>, F extends ITaskObject<E>> void addToQueue(IId idObject, Class<F> objectClass) {
		F taskObject = entityServiceDelegate.findEntityById(objectClass, idObject);
		addToQueue(taskObject);
	}

	public <E extends Enum<E>, F extends ITaskObject<E>> void addToQueue(F taskObject) {
		IId idCluster = taskObject.getIdCluster();
		if (idCluster == null) {
			idCluster = createTaskCluster(taskObject);
		}
		addToQueue(idCluster);
	}

	/**
	 * Delete all task manager objects linked to a task object. Call this method before deleting a task object.
	 */
	public <E extends Enum<E>, F extends ITaskObject<E>> void cancelTaskObject(F object) {
		getTodoMapper().deleteObjectTodos(object.getId());
		getErrorMapper().deleteErrorsByIdObject(object.getId());
		getTaskMapper().deleteObjectTaskAssos(object.getId());
		getTaskMapper().deleteObjectTasks(object.getId());
		getTaskMapper().removeObjectFromCluster(object.getId());
	}

	private class CreateTasksResult {

		List<ITask> firstTasks;

		List<ITask> lastTasks;

		List<ITask> allTasks;

	}

	/**
	 * Get shortest status path between two statuses, as a string. Example : "CUR EXE CLO" may be the shortest status path between CUR and CLO. Shortest path between CUR and CUR will be "CUR". Returns
	 * an empty string if no path was found. For more examples, see TaskManagerServiceDelegateTest unit test.
	 */
	public String getStatusPath(Class<? extends ITaskObject<?>> taskObjectClass, String currentStatus, String nextStatus) {
		List<IStatusGraph> statusGraphs = statusGraphServiceDelegate.findStatusGraphsBy(taskObjectClass);
		return getStatusesPaths(statusGraphs, currentStatus, nextStatus);
	}

	public String getStatusesPaths(List<IStatusGraph> statusGraphs, String currentStatus, String nextStatus) {
		List<String> statusesPath = getStatusesPaths(statusGraphs, currentStatus, nextStatus, "");
		if (CollectionUtils.isEmpty(statusesPath)) {
			return currentStatus.equals(nextStatus) ? nextStatus : "";
		}

		String result = statusesPath.get(0);
		for (String s : statusesPath) {
			if (s.length() < result.length()) {
				result = s;
			}
		}

		return currentStatus + " " + result.trim();
	}

	private List<String> getStatusesPaths(List<IStatusGraph> statusGraphs, String currentStatus, String nextStatus, String path) {
		List<String> nextStatuses = getNextStatuses(statusGraphs, currentStatus);
		List<String> result = new ArrayList<String>();

		if (CollectionUtils.isEmpty(nextStatuses)) {
			return result;
		}

		for (String status : nextStatuses) {
			if (status.equals(nextStatus)) {
				result.add(path + " " + status);
			}
			List<String> statusesPaths = getStatusesPaths(statusGraphs, status, nextStatus, path + " " + status);
			result.addAll(statusesPaths);
		}

		return result;
	}

	private List<String> getNextStatuses(List<IStatusGraph> statusGraphs, String status) {
		List<String> statuses = new ArrayList<String>();
		for (IStatusGraph statusGraph : statusGraphs) {
			if (status.equals(statusGraph.getCurrentStatus())) {
				statuses.add(statusGraph.getNextStatus());
			}
		}
		return statuses;
	}

	/**
	 * Delete an archived task cluster, and all tasks that were linked to this cluster.
	 */
	public void deleteTasksCluster(IId idTaskCluster) {
		if (idTaskCluster == null) {
			return;
		}

		ITaskCluster taskCluster = entityServiceDelegate.findEntityById(ITaskCluster.class, idTaskCluster);
		if (taskCluster != null && !taskCluster.isCheckTaskArchDeleted()) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Delete cluster ID=" + taskCluster.getId());
			}
			getTaskManagerMapper().deleteTaskCluster(idTaskCluster);
		}
	}
}
