import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.entity.ITracable;

/**
 * Created by NicolasP on 06/06/2016.
 */
public class TracableSorterTest {

	@Test
	public void createdDateSortTest() throws Exception {
		Calendar cal = Calendar.getInstance();

		ITracable t1 = ComponentFactory.getInstance().createInstance(ITracable.class);
		t1.setCreatedDate(cal.getTime());

		ITracable t2 = ComponentFactory.getInstance().createInstance(ITracable.class);
		cal.add(Calendar.MINUTE, 1);
		t2.setCreatedDate(cal.getTime());
		t1.setCreatedDate(cal.getTime());

		ITracable t2_2 = ComponentFactory.getInstance().createInstance(ITracable.class);
		t2_2.setCreatedDate(cal.getTime());

		ITracable t3 = ComponentFactory.getInstance().createInstance(ITracable.class);
		cal.add(Calendar.MINUTE, 1);
		t3.setCreatedDate(cal.getTime());

		ITracable t0 = ComponentFactory.getInstance().createInstance(ITracable.class);
		cal.add(Calendar.MINUTE, -5);
		t0.setCreatedDate(cal.getTime());

		List<ITracable> tracableList = new ArrayList<ITracable>();
		tracableList.add(t1);
		tracableList.add(t2);
		tracableList.add(t2_2);
		tracableList.add(t3);
		tracableList.add(t0);

		Collections.sort(tracableList, ITracable.CREATED_DATE_COMPARATOR);
		Assert.assertEquals(tracableList.get(0), t0);
		Assert.assertEquals(tracableList.get(1), t1);
		if (tracableList.get(2) == t2) {
			Assert.assertEquals(tracableList.get(3), t2_2);
		} else if (tracableList.get(2) == t2_2) {
			Assert.assertEquals(tracableList.get(3), t2);
		} else {
			Assert.fail();
		}
		Assert.assertEquals(tracableList.get(4), t3);
	}
}
