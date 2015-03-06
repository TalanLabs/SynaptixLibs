package com.synaptix.swing.tooltip;

import java.awt.Component;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JWindow;

import com.jgoodies.validation.ValidationMessage;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;
import com.synaptix.swing.AWTUtilitiesWrapper;

public class ToolTipFeedbackComponentValidationResultFocusListener extends AbstractToolTipFocusListener {

	private static final MySeverityComparator SEVERITY_COMPARATOR = new MySeverityComparator();

	private ValidationResultModel model;

	private Map<Component, List<ValidationMessage>> componentMap;

	public ToolTipFeedbackComponentValidationResultFocusListener(ValidationResultModel model) {
		super();

		this.model = model;
		this.componentMap = new HashMap<Component, List<ValidationMessage>>();
		this.model.addPropertyChangeListener(ValidationResultModel.PROPERTYNAME_RESULT, new ValidationResultChangeHandler());
	}

	private void updateFeedbackComponents() {
		removeAllComponents();

		insertComponents();
	}

	private void insertComponents() {
		ValidationResult result = model.getResult();
		if (!result.isEmpty()) {
			for (ValidationMessage vm : result.getMessages()) {
				if (vm.key() instanceof JComponent) {
					Component c = (Component) vm.key();
					if (c instanceof IToolTippableComponent) {
						c = ((IToolTippableComponent) c).getToolTipableComponent();
					}
					List<ValidationMessage> vs = componentMap.get(c);
					if (vs == null) {
						vs = new ArrayList<ValidationMessage>();
						componentMap.put(c, vs);
						c.addFocusListener(this);
					}

					vs.add(vm);
					Collections.sort(vs, SEVERITY_COMPARATOR);
				}
			}

			showWindow();
		}
	}

	private void removeAllComponents() {
		for (Component c : componentMap.keySet()) {
			c.removeFocusListener(this);
		}
		componentMap.clear();

		hideWindow();
	}

	@Override
	protected Point getPosition() {
		return new Point(0, -toolTip.getHeight());
	}

	@Override
	public void showWindow() {
		hideWindow();
		if (realField != null && realField.hasFocus()) {
			List<ValidationMessage> vs = this.componentMap.get(realField);
			if (vs != null && !vs.isEmpty()) {
				ValidationMessage vm = vs.get(0);

				field.addHierarchyBoundsListener(hierachyBoundsListener);

				if (parent == null) {
					Component c = field;
					while (!(c instanceof Window) && c != null) {
						c = c.getParent();
					}
					parent = (Window) c;
				}

				window = new JWindow(parent);
				AWTUtilitiesWrapper.setWindowOpaque(window, false);

				toolTip = createToolTip(vm);
				toolTip.setPositionArrow(JDefaultToolTip.PositionArrow.BOTTOM);

				window.getContentPane().add(toolTip);
				window.pack();
				window.setVisible(true);
				computeLocation();

				parent.addComponentListener(componentListener);
			}
		}
	}

	private JDefaultToolTip createToolTip(ValidationMessage vm) {
		JDefaultToolTip res = null;
		switch (vm.severity()) {
		case ERROR:
			res = new JRedToolTip(vm.formattedText());
			break;
		case OK:
			res = new JGreenToolTip(vm.formattedText());
			break;
		case WARNING:
			res = new JOrangeToolTip(vm.formattedText());
			break;
		default:
			res = new JRedToolTip(vm.formattedText());
			break;
		}
		res.setInsetsToolTip(new Insets(2, 5, 2, 5));
		return res;
	}

	@Override
	public void hideWindow() {
		if (window != null) {
			window.setVisible(false);
			window = null;
			toolTip = null;
			parent.removeComponentListener(componentListener);
			field.removeHierarchyBoundsListener(hierachyBoundsListener);
		}
	}

	private final class ValidationResultChangeHandler implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			updateFeedbackComponents();
		}
	}

	private static final class MySeverityComparator implements Comparator<ValidationMessage> {

		@Override
		public int compare(ValidationMessage o1, ValidationMessage o2) {
			return o1.severity().compareTo(o2.severity());
		}
	}
}
