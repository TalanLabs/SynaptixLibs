import model.IZip;

import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.component.factory.DefaultComputedFactory;
import com.synaptix.entity.extension.BusinessComponentExtensionProcessor;
import com.synaptix.entity.extension.CacheComponentExtensionProcessor;
import com.synaptix.entity.extension.DatabaseComponentExtensionProcessor;
import com.synaptix.entity.extension.IBusinessComponentExtension;
import com.synaptix.entity.extension.ICacheComponentExtension;
import com.synaptix.entity.extension.IDatabaseComponentExtension;
import com.synaptix.entity.helper.EntityHelper;

public class MainChild {

	public static void main(String[] args) {
		ComponentFactory.getInstance().addExtension(IDatabaseComponentExtension.class, new DatabaseComponentExtensionProcessor());
		ComponentFactory.getInstance().addExtension(IBusinessComponentExtension.class, new BusinessComponentExtensionProcessor());
		ComponentFactory.getInstance().addExtension(ICacheComponentExtension.class, new CacheComponentExtensionProcessor());
		ComponentFactory.getInstance().setComputedFactory(new DefaultComputedFactory());

		System.out.println(EntityHelper.findAllChildren(IZip.class));
	}

}
