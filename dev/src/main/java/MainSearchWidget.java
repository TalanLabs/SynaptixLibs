import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.client.view.AbstractViewWorker;
import com.synaptix.client.view.IWaitWorker;
import com.synaptix.common.util.IResultCallback;
import com.synaptix.swing.utils.GenericObjectToString;
import com.synaptix.view.swing.WaitComponentSwingViewWorker;
import com.synaptix.widget.searchfield.context.AbstractSearchFieldWidgetContext;
import com.synaptix.widget.searchfield.view.swing.DefaultSearchFieldWidget;

import helper.MainHelper;

public class MainSearchWidget {

	private static final List<String> NAMES = Arrays.asList("gaby", "nico P", "nico T", "nico S", "tom", "sandra", "sophie", "sos", "sy", "s1", "s3", "s4");

	private static final List<String> find(String value) {
		List<String> ls = new ArrayList<String>();
		if (value != null) {
			for (String name : NAMES) {
				if (name.toUpperCase().contains(value.toUpperCase())) {
					ls.add(name);
				}
			}
		}
		return ls;
	}

	static IWaitWorker waitWorker;

	static DefaultSearchFieldWidget<String> defaultSearchFieldWidget;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				MainHelper.init();

				defaultSearchFieldWidget = new DefaultSearchFieldWidget<String>(new AbstractSearchFieldWidgetContext<String>() {

					@Override
					public void search(String name, IResultCallback<String> resultCallback) {
						System.out.println("ici " + name);
					}

					@Override
					public void suggest(final String name, final IResultCallback<List<String>> resultCallback) {
						if (waitWorker != null && !waitWorker.isDone()) {
							waitWorker.cancel(true);
							waitWorker = null;
						}
						waitWorker = WaitComponentSwingViewWorker.waitComponentSwingViewWorker(defaultSearchFieldWidget, new AbstractViewWorker<List<String>, String>() {
							@Override
							public List<String> doBackground() throws Exception {
								publish("Loading");
								// Thread.sleep(500);
								return find(name);
							}

							@Override
							public void success(List<String> e) {
								resultCallback.setResult(e);
							}

							@Override
							public void fail(Throwable t) {
								resultCallback.setResult(null);
							}
						});

					}
				}, new GenericObjectToString<String>() {
					@Override
					public String getString(String t) {
						return t;
					}
				}, true, "Saisir un text");

				FormLayout layout = new FormLayout("150DLU", "CENTER:DEFAULT,3DLU,CENTER:DEFAULT");
				PanelBuilder builder = new PanelBuilder(layout);
				builder.setDefaultDialogBorder();
				CellConstraints cc = new CellConstraints();
				builder.add(defaultSearchFieldWidget, cc.xy(1, 1));
				builder.add(new JTextField(), cc.xy(1, 3));

				JTextField t = new JTextField();

				JFrame frame = MainHelper.createFrame();
				frame.getContentPane().add(builder.getPanel());
				frame.setSize(300, 100);
				frame.setVisible(true);

			}
		});
	}
}
