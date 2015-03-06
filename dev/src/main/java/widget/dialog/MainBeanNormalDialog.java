package widget.dialog;

import helper.MainHelper;

import javax.swing.SwingUtilities;

import toto.IPerson;
import toto.PersonBuilder;

import com.synaptix.widget.view.ISynaptixViewFactory;
import com.synaptix.widget.view.dialog.IBeanDialogView;
import com.synaptix.widget.view.swing.SwingSynaptixViewFactory;

public class MainBeanNormalDialog {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				MainHelper.init();

				ISynaptixViewFactory viewFactory = new SwingSynaptixViewFactory();
				IBeanDialogView<IPerson> beanDialogView = viewFactory.newBeanDialogView(new Step1BeanExtensionDialog(), new Step2BeanExtensionDialog(), new Step3BeanExtensionDialog());
				beanDialogView.showDialog(null, "Test", null, new PersonBuilder().build(), null, false, false);
			}
		});
	}
}
