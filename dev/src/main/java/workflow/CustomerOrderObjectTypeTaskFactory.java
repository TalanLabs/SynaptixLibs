package workflow;

import java.io.Serializable;

import com.synaptix.entity.IEntity;
import com.synaptix.taskmanager.manager.AbstractObjectTypeTaskFactory;
import com.synaptix.taskmanager.model.ITask;
import com.synaptix.taskmanager.model.ITaskChainCriteria;

public class CustomerOrderObjectTypeTaskFactory extends AbstractObjectTypeTaskFactory<ICustomerOrder> {

	public CustomerOrderObjectTypeTaskFactory() {
		super(ICustomerOrder.class);
	}

	@Override
	public String getTaskObjectDescription(Serializable idObject) {
		return "Aucune description pour " + idObject;
	}

	@Override
	public ITaskChainCriteria<? extends Enum<?>> getTaskChainCriteria(ITask task) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITaskChainCriteria<? extends Enum<?>> getTaskChainCriteria(Object context, String status, String nextStatus) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IEntity getExecutant(ITask task) {
		return new UserBuilder().name("Gabriel").build();
	}

	@Override
	public IEntity getManager(ITask task) {
		return new UserBuilder().name("Sandra").build();
	}
}
