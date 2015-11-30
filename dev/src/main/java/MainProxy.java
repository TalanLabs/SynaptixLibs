import com.synaptix.component.IComponent;
import com.synaptix.component.factory.AbstractComponentFactoryListener;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.entity.IEntity;
import com.synaptix.entity.IdRaw;
import com.synaptix.mybatis.dao.impl.DefaultGUIDGenerator;

import model.IZip;

public class MainProxy {

	public static void main(String[] args) throws Exception {
		final DefaultGUIDGenerator guid = new DefaultGUIDGenerator();

		ComponentFactory.getInstance().addComponentFactoryListener(new AbstractComponentFactoryListener() {
			@Override
			public <G extends IComponent> void afterCreated(Class<G> interfaceClass, G instance) {
				System.out.println("ici " + interfaceClass + " " + instance);

				if (instance instanceof IEntity) {
					((IEntity) instance).setId(new IdRaw(guid.newGUID()));
				}
			}
		});

		IZip zip = ComponentFactory.getInstance().createInstance(IZip.class);
		System.out.println(zip.getId());
	}
}
