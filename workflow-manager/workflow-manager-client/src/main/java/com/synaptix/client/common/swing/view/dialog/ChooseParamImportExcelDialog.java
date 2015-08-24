package com.synaptix.client.common.swing.view.dialog;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.validation.Severity;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.message.SimpleValidationMessage;
import com.jgoodies.validation.util.ValidationUtils;
import com.synaptix.client.common.util.StaticCommonHelper;
import com.synaptix.swing.utils.GenericObjectToString;
import com.synaptix.swing.utils.SwingComponentFactory;
import com.synaptix.swing.utils.ValidationUtils2;
import com.synaptix.taskmanager.model.XlsSheet;
import com.synaptix.widget.renderer.view.swing.TypeGenericSubstanceComboBoxRenderer;
import com.synaptix.widget.view.swing.ValidationListener;
import com.synaptix.widget.view.swing.dialog.AbstractSimpleDialog2;


public class ChooseParamImportExcelDialog extends AbstractSimpleDialog2 {

	private static final long serialVersionUID = -7864100177220669740L;

	private final boolean useBundleName;

	private final XlsSheet[] sheets;

	private JComboBox sheetBox;

	private JFormattedTextField columnBundleNameField;

	private JFormattedTextField columnKeyField;

	private JFormattedTextField rowStartField;

	private JFormattedTextField columnTraductionField;

	private XlsSheet sheet;

	private int columnBundleName;

	private int columnKey;

	private int columnTraduction;

	private int rowStart;

	public ChooseParamImportExcelDialog(boolean useBundleName, XlsSheet[] sheets) {
		super();

		this.useBundleName = useBundleName;
		this.sheets = sheets;

		initComponents();

		initialize();
	}

	private void initComponents() {
		sheetBox = new JComboBox(sheets);
		if (useBundleName) {
			columnBundleNameField = SwingComponentFactory.createIntegerField();
		}
		columnKeyField = SwingComponentFactory.createIntegerField();
		columnTraductionField = SwingComponentFactory.createIntegerField();
		rowStartField = SwingComponentFactory.createIntegerField();

		sheetBox.setRenderer(new TypeGenericSubstanceComboBoxRenderer<XlsSheet>(sheetBox, new GenericObjectToString<XlsSheet>() {
			public String getString(XlsSheet t) {
				return t != null ? t.getName() : StaticCommonHelper.getCommonConstantsBundle().none();
			}
		}));

		ValidationListener l = new ValidationListener(this);
		sheetBox.addActionListener(l);
		if (useBundleName) {
			columnBundleNameField.getDocument().addDocumentListener(l);
		}
		columnKeyField.getDocument().addDocumentListener(l);
		rowStartField.getDocument().addDocumentListener(l);
		columnTraductionField.getDocument().addDocumentListener(l);
	}

	protected JComponent buildEditorPanel() {
		FormLayout layout = new FormLayout("RIGHT:MAX(40DLU;PREF):NONE,FILL:4DLU:NONE,FILL:150DLU:NONE", //$NON-NLS-1$
				""); //$NON-NLS-1$
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		builder.append(StaticCommonHelper.getCommonConstantsBundle().dataSheets(), sheetBox);
		builder.append(StaticCommonHelper.getCommonConstantsBundle().startingLine(), rowStartField);
		if (useBundleName) {
			builder.append(StaticCommonHelper.getCommonConstantsBundle().columnOfBundlesNames(), columnBundleNameField);
		}
		builder.append(StaticCommonHelper.getCommonConstantsBundle().columnOfKeys(), columnKeyField);
		builder.append(StaticCommonHelper.getCommonConstantsBundle().columnTranslations(), columnTraductionField);
		return builder.getPanel();
	}

	public XlsSheet getSheet() {
		return sheet;
	}

	public int getColumnBundleName() {
		return columnBundleName;
	}

	public int getColumnKey() {
		return columnKey;
	}

	public int getRowStart() {
		return rowStart;
	}

	public int getColumnTraduction() {
		return columnTraduction;
	}

	@Override
	protected void openDialog() {
		updateValidation();
	}

	@Override
	protected void accept() {
		super.accept();

		sheet = (XlsSheet) sheetBox.getSelectedItem();
		if (useBundleName) {
			columnBundleName = (Integer) columnBundleNameField.getValue();
		}
		columnKey = (Integer) columnKeyField.getValue();
		rowStart = (Integer) rowStartField.getValue();
		columnTraduction = (Integer) columnTraductionField.getValue();
	}

	@Override
	protected void updateValidation(ValidationResult result) {
		if (sheetBox.getSelectedItem() == null) {
			result.add(new SimpleValidationMessage(StaticCommonHelper.getCommonConstantsBundle().mandatoryField(), Severity.ERROR, sheetBox));
		}
		if (useBundleName) {
			if (ValidationUtils.isBlank(columnBundleNameField.getText()) || !ValidationUtils2.isInteger(columnBundleNameField.getText())) {
				result.add(new SimpleValidationMessage(StaticCommonHelper.getCommonConstantsBundle().mandatoryField(), Severity.ERROR, columnBundleNameField));
			}
		}

		if (ValidationUtils.isBlank(columnKeyField.getText()) || !ValidationUtils2.isInteger(columnKeyField.getText())) {
			result.add(new SimpleValidationMessage(StaticCommonHelper.getCommonConstantsBundle().mandatoryField(), Severity.ERROR, columnKeyField));
		}

		if (ValidationUtils.isBlank(rowStartField.getText()) || !ValidationUtils2.isInteger(rowStartField.getText())) {
			result.add(new SimpleValidationMessage(StaticCommonHelper.getCommonConstantsBundle().mandatoryField(), Severity.ERROR, rowStartField));
		}

		if (ValidationUtils.isBlank(columnTraductionField.getText()) || !ValidationUtils2.isInteger(columnTraductionField.getText())) {
			result.add(new SimpleValidationMessage(StaticCommonHelper.getCommonConstantsBundle().mandatoryField(), Severity.ERROR, columnTraductionField));
		}
	}
}
