import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.entity.IEntity;
import com.synaptix.entity.extension.DatabaseComponentExtensionProcessor;
import com.synaptix.entity.extension.IDatabaseComponentExtension;

public class MainComponent {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ComponentFactory.getInstance().addExtension(IDatabaseComponentExtension.class, new DatabaseComponentExtensionProcessor());

		IEntity toto2 = ComponentFactory.getInstance().createInstance(IEntity.class);

		IToto toto1 = ComponentFactory.getInstance().createInstance(IToto.class);
		toto1.setToto(true);

		toto1.straightSetProperty("toto", true);
		toto1.straightSetProperty("rien", toto2);

		System.out.println(toto1);
	}
}
