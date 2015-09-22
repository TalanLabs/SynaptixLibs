package workflow;

import com.synaptix.component.annotation.SynaptixComponent;
import com.synaptix.entity.IEntity;
import com.synaptix.taskmanager.model.ITaskObject;

@SynaptixComponent
public interface ICustomerOrder extends IEntity, ITaskObject<CustomerOrderStatus> {

	public String getCustomerOrderNo();

	public void setCustomerOrderNo(String customerOrderNo);

}
