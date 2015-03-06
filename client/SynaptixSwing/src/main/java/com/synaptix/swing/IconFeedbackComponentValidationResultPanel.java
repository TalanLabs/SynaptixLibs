package com.synaptix.swing;

import java.awt.Component;
import java.awt.Container;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;

public final class IconFeedbackComponentValidationResultPanel extends
		AbstractIconFeedbackValidationResultPanel {

	private static final long serialVersionUID = -9100049250111968121L;

	/**
	 * Creates an IconFeedbackPanel on the given ValidationResultModel using the
	 * specified content panel.
	 * <p>
	 * 
	 * <strong>Note:</strong> Typically you should wrap component trees with
	 * {@link #getWrappedComponentTree(ValidationResultModel, JComponent)}, not
	 * this constructor.
	 * <p>
	 * 
	 * <strong>Note:</strong> You must not add or remove components from the
	 * content once this constructor has been invoked.
	 * 
	 * @param model
	 *            the ValidationResultModel to observe
	 * @param content
	 *            the panel that contains the content components
	 * 
	 * @throws NullPointerException
	 *             if model or content is <code>null</code>.
	 */
	public IconFeedbackComponentValidationResultPanel(
			ValidationResultModel model, JComponent content) {
		super(model, content);
	}

	// Convenience Code *******************************************************

	/**
	 * Wraps the components in the given component tree with instances of
	 * IconFeedbackPanel where necessary. Such a wrapper is required for all
	 * JScrollPanes that contain multiple children and for the root - unless
	 * it's a JScrollPane with multiple children.
	 * 
	 * @param root
	 *            the root of the component tree to wrap
	 * @return the wrapped component tree
	 */
	public static JComponent getWrappedComponentTree(
			ValidationResultModel model, JComponent root) {
		wrapComponentTree(model, root);
		return isScrollPaneWithUnmarkableView(root) ? root
				: new IconFeedbackComponentValidationResultPanel(model, root);
	}

	private static void wrapComponentTree(ValidationResultModel model,
			Container container) {
		if (!(container instanceof JScrollPane)) {
			int componentCount = container.getComponentCount();
			for (int i = 0; i < componentCount; i++) {
				Component child = container.getComponent(i);
				if (child instanceof Container)
					wrapComponentTree(model, (Container) child);
			}
			return;
		}
		JScrollPane scrollPane = (JScrollPane) container;
		JViewport viewport = scrollPane.getViewport();
		JComponent view = (JComponent) viewport.getView();
		if (isMarkable(view))
			return;
		// TODO: Consider adding the following sanity check:
		// the view must not be an IconFeedbackPanel
		Component wrappedView = new IconFeedbackComponentValidationResultPanel(
				model, view);
		viewport.setView(wrappedView);
		wrapComponentTree(model, view);
	}

	private static boolean isScrollPaneWithUnmarkableView(Component c) {
		if (!(c instanceof JScrollPane))
			return false;
		JScrollPane scrollPane = (JScrollPane) c;
		JViewport viewport = scrollPane.getViewport();
		JComponent view = (JComponent) viewport.getView();
		return !isMarkable(view);
	}

	protected ValidationResult getAssociatedResult(JComponent comp,
			Map<Object, ValidationResult> keyMap) {
		ValidationResult result = keyMap.get(comp);
		return result == null ? ValidationResult.EMPTY : result;
	}
}
