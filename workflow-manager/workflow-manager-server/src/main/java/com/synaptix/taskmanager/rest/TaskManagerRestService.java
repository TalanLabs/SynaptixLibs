package com.synaptix.taskmanager.rest;

import java.io.Serializable;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.synaptix.entity.IdRaw;
import com.synaptix.mybatis.service.EntityServerService;
import com.synaptix.service.IEntityService;
import com.synaptix.service.IServiceFactory;
import com.synaptix.service.ServicesManager;
import com.synaptix.taskmanager.model.ITaskObject;
import com.synaptix.taskmanager.service.ITaskManagerService;
import com.synaptix.taskmanager.service.TaskManagerServerService;

@Path("/taskManager")
public class TaskManagerRestService {

	private static final Log LOG = LogFactory.getLog(TaskManagerRestService.class);

	private final IServiceFactory getPscNormalServiceFactory() {
		return ServicesManager.getInstance().getServiceFactory("psc-normal");
	}

	private final TaskManagerServerService getTaskManagerServerService() {
		return (TaskManagerServerService) getPscNormalServiceFactory().getService(ITaskManagerService.class);
	}

	private final EntityServerService getEntityServerService() {
		return (EntityServerService) getPscNormalServiceFactory().getService(IEntityService.class);
	}

	/**
	 * Check task current to create todo manager
	 * 
	 * @return
	 */
	@GET
	@Path("/checkTodoManagerTasks")
	public Response checkTodoManagerTasks() {
		try {
			getTaskManagerServerService().checkTodoManagerTasks();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return Response.serverError().build();
		}
		return Response.ok().build();
	}

	/**
	 * Start engine from cluster
	 * 
	 * @param idTaskClusterString
	 * @return
	 */
	@GET
	@Path("/startEngineFromCluster")
	public Response startEngineFromCluster(@QueryParam("idTaskCluster") String idTaskClusterString) {
		try {
			Serializable idTaskCluster = new IdRaw(idTaskClusterString);
			getTaskManagerServerService().startEngine(idTaskCluster);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return Response.serverError().build();
		}
		return Response.ok().build();
	}

	/**
	 * Start Engin from task object class and id
	 * 
	 * @param taskObjectClassString
	 * @param idTaskObjectString
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@GET
	@Path("/startEngineFromTaskObject")
	public Response startEngineFromTaskObject(@QueryParam("taskObjectClass") String taskObjectClassString, @QueryParam("idTaskObject") String idTaskObjectString) {
		try {
			Class<? extends ITaskObject<?>> taskObjectClass = (Class<? extends ITaskObject<?>>) TaskManagerRestService.class.getClassLoader().loadClass(taskObjectClassString);
			Serializable idTaskObject = new IdRaw(idTaskObjectString);
			ITaskObject<?> taskObject = getEntityServerService().findEntityById(taskObjectClass, idTaskObject);
			getTaskManagerServerService().startEngine(taskObject);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return Response.serverError().build();
		}
		return Response.ok().build();
	}
}
