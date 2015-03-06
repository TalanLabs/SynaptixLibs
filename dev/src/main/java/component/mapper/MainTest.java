package component.mapper;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

import com.synaptix.component.IComponent;

public class MainTest {

	public static void main(String[] args) throws Exception {
		Method m = TestComponentMapper.class.getMethod("count", new Class<?>[0]);

		if (m.getGenericReturnType() instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) m.getGenericReturnType();
			if (pt.getActualTypeArguments() != null && pt.getActualTypeArguments().length == 1 && pt.getActualTypeArguments()[0] instanceof Class
					&& IComponent.class.isAssignableFrom((Class<?>) pt.getActualTypeArguments()[0])) {
				System.out.println(pt.getActualTypeArguments()[0]);
			}
		}
	}

}
