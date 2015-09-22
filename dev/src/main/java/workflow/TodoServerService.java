package workflow;

import com.synaptix.entity.IEntity;
import com.synaptix.taskmanager.model.ITodo;
import com.synaptix.taskmanager.model.TodoBuilder;
import com.synaptix.taskmanager.service.AbstractSimpleService;
import com.synaptix.taskmanager.service.ITodoService;

public class TodoServerService extends AbstractSimpleService implements ITodoService {

	@Override
	public ITodo createTodo(IEntity ownerEntity, IEntity contactEntity) {
		return new TodoBuilder().build();
	}
}
