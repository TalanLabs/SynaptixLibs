package workflow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.synaptix.component.helper.ComponentHelper;
import com.synaptix.entity.IdRaw;

public class StaticWorkflow {

	static List<ICustomerOrder> lists = new ArrayList<ICustomerOrder>();

	static {
		lists.add(new CustomerOrderBuilder().id(new IdRaw("0")).version(0).customerOrderNo("GABY").build());
	}

	public static ICustomerOrder selectCustomerOrderById(Serializable id) {
		return ComponentHelper.findComponentBy(lists, CustomerOrderFields.id().name(), id);
	}
}
