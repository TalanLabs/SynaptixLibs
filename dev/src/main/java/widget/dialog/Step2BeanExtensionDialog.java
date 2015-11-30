package widget.dialog;

import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.widget.view.swing.dialog.AbstractBeanExtensionDialog;

import toto.IPerson;

public class Step2BeanExtensionDialog extends AbstractBeanExtensionDialog<IPerson> {

	private static final long serialVersionUID = -3446165388669489384L;

	private JTable table;

	public Step2BeanExtensionDialog() {
		super("Step2");

		initialize();
	}

	@Override
	protected void initComponents() {
		table = new JTable(new DefaultTableModel(100, 5));
	}

	@Override
	protected JComponent buildEditorPanel() {
		FormLayout layout = new FormLayout("FILL:300DLU:GROW(1.0)");
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		builder.appendRow("FILL:150DLU:GROW(1.0)");
		builder.append(new JScrollPane(table));
		return builder.getPanel();
	}

	@Override
	public void openDialog() {

		updateValidation();
	}

	@Override
	public void commit(IPerson bean, Map<String, Object> valueMap) {
	}
}
