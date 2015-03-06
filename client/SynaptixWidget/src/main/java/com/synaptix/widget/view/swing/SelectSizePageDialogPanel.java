package com.synaptix.widget.view.swing;

import java.util.Dictionary;
import java.util.Hashtable;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.validation.ValidationResult;
import com.synaptix.client.view.IView;
import com.synaptix.common.util.IResultCallback;
import com.synaptix.widget.util.StaticWidgetHelper;
import com.synaptix.widget.view.ISelectSizePageDialogView;
import com.synaptix.widget.view.swing.dialog.AbstractSimpleDialog2;

public class SelectSizePageDialogPanel extends AbstractSimpleDialog2 implements ISelectSizePageDialogView {

	private static final long serialVersionUID = 3266831978199360780L;

	private JLabel pageSizeLabel;

	private JSlider pageSizeSlider;

	private IView view;

	private int min;
	private int max;

	public SelectSizePageDialogPanel(IView view, int min, int max) {
		super();

		this.view = view;
		this.min = min;
		this.max = max;

		initialize();
	}

	@Override
	protected void initialize() {
		pageSizeSlider = new JSlider(min, max);
		pageSizeSlider.setPaintTicks(true);
		pageSizeSlider.setSnapToTicks(false);
		pageSizeSlider.setMinorTickSpacing(10);
		pageSizeSlider.setMajorTickSpacing(50);
		pageSizeSlider.setPaintTrack(true);
		pageSizeSlider.setPaintLabels(true);
		pageSizeSlider.setValue(min);
		Dictionary<Integer, JLabel> labels = new Hashtable<Integer, JLabel>();
		labels.put(min, new JLabel(Integer.toString(min)));
		if ((max + min) % 2 == 0) {
			labels.put((max + min) / 2, new JLabel(Integer.toString((max + min) / 2)));
		}
		labels.put(max, new JLabel(Integer.toString(max)));
		pageSizeSlider.setLabelTable(labels);
		pageSizeSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				updateToleranceLabel();
			}
		});

		pageSizeLabel = new JLabel();
		updateToleranceLabel();

		super.initialize();
	}

	private void updateToleranceLabel() {
		pageSizeLabel.setText(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().lines(pageSizeSlider.getValue()));
		revalidate();
	}

	@Override
	protected JComponent buildEditorPanel() {
		FormLayout layout = new FormLayout("CENTER:DEFAULT:GROW(1.0)", "FILL:DEFAULT:NONE,4DLU,FILL:DEFAULT:NONE");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.add(pageSizeSlider, cc.xy(1, 1));
		builder.add(pageSizeLabel, cc.xy(1, 3));
		return builder.getPanel();
	}

	@Override
	protected void updateValidation(ValidationResult result) {
	}

	@Override
	protected void openDialog() {
	}

	@Override
	public void selectSizePage(IResultCallback<Integer> resultCallback, int sizePage) {
		pageSizeSlider.setValue(sizePage);

		updateValidation();

		int res = showDialog(view, StaticWidgetHelper.getSynaptixWidgetConstantsBundle().pageSizeEdition(), StaticWidgetHelper.getSynaptixWidgetConstantsBundle().selectANumberOfLines(), false, false);
		if (res == ACCEPT_OPTION) {
			resultCallback.setResult(pageSizeSlider.getValue());
		} else {
			resultCallback.setResult(null);
		}
	}

}
