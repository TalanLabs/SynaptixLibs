package workflow.tasks;

import com.synaptix.taskmanager.manager.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.model.ITask;
import com.synaptix.taskmanager.model.domains.ServiceNature;

import workflow.ICustomerOrder;
import workflow.StaticWorkflow;

public class CLOTaskService extends AbstractTaskService<ICustomerOrder> {

	public CLOTaskService() {
		super("CLO_TASK", ServiceNature.UPDATE_STATUS, ICustomerOrder.class);
	}

	@Override
	public IExecutionResult execute(ITask task, ICustomerOrder object) {
		return null;
	}

	@Override
	protected ICustomerOrder getObject(ITask task) {
		return StaticWorkflow.selectCustomerOrderById(task.getIdObject());
	}
}
