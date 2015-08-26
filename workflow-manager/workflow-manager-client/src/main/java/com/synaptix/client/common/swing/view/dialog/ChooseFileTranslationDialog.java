package com.synaptix.client.common.swing.view.dialog;

import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.validation.Severity;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.message.SimpleValidationMessage;
import com.jgoodies.validation.util.ValidationUtils;
import com.synaptix.client.common.util.StaticCommonHelper;
import com.synaptix.swing.utils.DialogErrorMessage;
import com.synaptix.swing.utils.GenericObjectToString;
import com.synaptix.view.swing.WaitFullComponentSwingWaitWorker;
import com.synaptix.widget.filefilter.view.ExcelFileFilter;
import com.synaptix.widget.filefilter.view.IFileFilter;
import com.synaptix.widget.renderer.view.swing.TypeGenericSubstanceComboBoxRenderer;
import com.synaptix.widget.view.swing.ValidationListener;
import com.synaptix.widget.view.swing.dialog.AbstractSimpleDialog2;
import com.synaptix.widget.viewworker.view.AbstractLoadingViewWorker;

public class ChooseFileTranslationDialog extends AbstractSimpleDialog2 {

	private static final long serialVersionUID = -6573957865349907628L;

	private final boolean useSave;

	private JComboBox localeBox;

	private JTextField pathFileField;

	private Action parcourirAction;

	private File file;

	private Locale locale;

	public ChooseFileTranslationDialog(boolean useSave) {
		super();

		this.useSave = useSave;

		initComponents();

		initialize();
	}

	private void initComponents() {
		parcourirAction = new ParcourirAction();

		localeBox = new JComboBox();
		pathFileField = new JTextField();

		pathFileField.setEnabled(false);

		localeBox.setRenderer(new LocaleCellRenderer(localeBox));

		ValidationListener l = new ValidationListener(this);
		localeBox.addActionListener(l);
		pathFileField.getDocument().addDocumentListener(l);
	}

	protected JComponent buildEditorPanel() {
		FormLayout layout = new FormLayout("right:max(40dlu;p), 4dlu, 160dlu, 3dlu, 40dlu, p:grow", //$NON-NLS-1$
				"p, 3dlu, p, 3dlu, 22dlu"); //$NON-NLS-1$
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.addLabel(StaticCommonHelper.getCommonConstantsBundle().language(), cc.xy(1, 1));
		builder.add(localeBox, cc.xyw(3, 1, 3));
		builder.addLabel(StaticCommonHelper.getCommonConstantsBundle().file(), cc.xy(1, 3));
		builder.add(pathFileField, cc.xy(3, 3));
		builder.add(new JButton(parcourirAction), cc.xy(5, 3));
		return builder.getPanel();
	}

	public File getSelectedFile() {
		return file;
	}

	public Locale getSelectedLocale() {
		return locale;
	}

	@Override
	protected void openDialog() {
		WaitFullComponentSwingWaitWorker.waitFullComponentSwingWaitWorker(ChooseFileTranslationDialog.this, new AbstractLoadingViewWorker<List<Locale>>() {

			protected List<Locale> doLoading() throws Exception {
				List<Locale> locales = Arrays.asList(Locale.getAvailableLocales());
				Collections.sort(locales, new Comparator<Locale>() {
					@Override
					public int compare(Locale o1, Locale o2) {
						return o1.getDisplayName().compareTo(o2.getDisplayName());
					}
				});
				return locales;
			}

			public void success(List<Locale> e) {
				localeBox.setModel(new DefaultComboBoxModel(e != null ? e.toArray() : new Locale[0]));
				localeBox.setSelectedItem(null);

				updateValidation();
			}

			public void fail(Throwable t) {
				new DialogErrorMessage().showDialog(ChooseFileTranslationDialog.this, t);
			}
		});
	}

	@Override
	protected void accept() {
		super.accept();

		this.locale = (Locale) localeBox.getSelectedItem();
	}

	@Override
	protected void updateValidation(ValidationResult result) {
		if (localeBox.getSelectedItem() == null) {
			result.add(new SimpleValidationMessage(StaticCommonHelper.getCommonConstantsBundle().mandatoryField(), Severity.ERROR, localeBox)); //$NON-NLS-1$
		}

		if (ValidationUtils.isEmpty(pathFileField.getText())) {
			result.add(new SimpleValidationMessage(StaticCommonHelper.getCommonConstantsBundle().mandatoryField(), Severity.ERROR, pathFileField)); //$NON-NLS-1$
		} else {
			String path = pathFileField.getText();
			if (!(path.matches(".*[.]xlsx$"))) {
				result.add(new SimpleValidationMessage(StaticCommonHelper.getCommonConstantsBundle().mandatoryField(), Severity.ERROR, pathFileField)); //$NON-NLS-1$	
			}
		}
	}

	private final class ParcourirAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public ParcourirAction() {
			super(StaticCommonHelper.getCommonConstantsBundle().chooseAFileEllipsis());
		}

		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser();
			final IFileFilter fileFilter = new ExcelFileFilter();
			chooser.setFileFilter(new FileFilter() {
				@Override
				public String getDescription() {
					return fileFilter.getDescription();
				}

				@Override
				public boolean accept(File f) {
					return fileFilter.accept(f);
				}
			});
			chooser.setDialogType(useSave ? JFileChooser.SAVE_DIALOG : JFileChooser.OPEN_DIALOG);

			try {
				if (chooser.showDialog(ChooseFileTranslationDialog.this, null) == JFileChooser.APPROVE_OPTION) {
					file = fileFilter.format(chooser.getSelectedFile());
					pathFileField.setText(file.getAbsolutePath());
				}
			} catch (HeadlessException ex) {
				ex.printStackTrace();// à remplacer
			}
		}
	}

	private final class LocaleCellRenderer extends TypeGenericSubstanceComboBoxRenderer<Locale> {

		private static final long serialVersionUID = 7388584552867300961L;

		public LocaleCellRenderer(JComboBox comboBox) {
			super(comboBox, new GenericObjectToString<Locale>() {
				public String getString(Locale t) {
					return t != null ? t.getDisplayName() : StaticCommonHelper.getCommonConstantsBundle().none();
				}
			});
		}
	}
}
