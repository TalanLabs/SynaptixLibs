package workflow.tasks;

import com.synaptix.taskmanager.manager.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.manager.taskservice.ExecutionResultBuilder;
import com.synaptix.taskmanager.model.ITask;
import com.synaptix.taskmanager.model.domains.ServiceNature;

import workflow.CustomerOrderStatus;
import workflow.ICustomerOrder;
import workflow.StaticWorkflow;

public class TCOTaskService extends AbstractTaskService<ICustomerOrder> {

	public TCOTaskService() {
		super("TCO_TASK", ServiceNature.UPDATE_STATUS, ICustomerOrder.class);
	}

	@Override
	public IExecutionResult execute(ITask task, ICustomerOrder object) {
		object.setStatus(CustomerOrderStatus.TCO);
		return new ExecutionResultBuilder().finished();
	}

	@Override
	protected ICustomerOrder getObject(ITask task) {
		return StaticWorkflow.selectCustomerOrderById(task.getIdObject());
	}
}
