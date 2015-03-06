package com.synaptix.swing;

/*
 * Copyright (c) 2003-2007 JGoodies Karsten Lentzsch. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 *  o Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer. 
 *     
 *  o Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution. 
 *     
 *  o Neither the name of JGoodies Karsten Lentzsch nor the names of 
 *    its contributors may be used to endorse or promote products derived 
 *    from this software without specific prior written permission. 
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.text.JTextComponent;

import com.jgoodies.validation.ValidationMessage;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.view.ValidationComponentUtils;
import com.jgoodies.validation.view.ValidationResultViewFactory;

/**
 * Can display validation feedback icons "over" a content panel. It observes a ValidationResultModel and creates icon labels in a feedback layer of a {@link JLayeredPane} on top of the content layer.
 * To position the feedback labels, the content pane is traversed and searched for text components that match a validation message key in this panel's observed ValidationResultModel.
 * <p>
 * 
 * <strong>Note:</strong> This panel doesn't reserve space for the portion used to display the overlayed feedback components. It has been designed to not change the layout of the wrapped content.
 * Therefore you must reserve this space, or in other words, you must ensure that the wrapped content provides enough space to display the overlayed components. Since the current implementation
 * positions the overlay components in the lower left, just make sure that there are about 6 pixel to the left and bottom of the input components that can be marked.
 * <p>
 * 
 * This panel handles two event types:
 * <ol>
 * <li>the ValidationResultModel changes; in this case the set of visible feedback components shall mark the input components that match the new validation result. This is done by this class' internal
 * <code>ValidationResultChangeHandler</code> which in turn invokes <code>#updateFeedbackComponents</code>.
 * <li>the content layout changes; the feedback components must then be repositioned to reflect the position of the overlayed input components. This is done by overriding <code>#validateTree</code>
 * and invoking <code>#repositionFeedBackComponents</code> after the child tree has been layed out. The current simple but expensive implementation updates all components.
 * </ol>
 * <p>
 * 
 * TODO: Check how the wrapping mechanism shall work with JSplitPanes, JTabbedPanes and CardPanels. At least provide guidelines, how to wrap these panel types, or how to handle these cases.
 * <p>
 * 
 * TODO: Turn this class into an abstract superclass. Subclasses shall implement the feedback component creation and specify where to locate the feedback component relative to the underlying content
 * component.
 * <p>
 * 
 * TODO: Consider adding a mechanism, so that components can be added and removed later.
 * 
 * @author Karsten Lentzsch
 * @author Martin Skopp
 * @version $Revision: 1.16 $
 */

public abstract class AbstractIconFeedbackValidationResultPanel extends JLayeredPane {

	private static final long serialVersionUID = -9100049250111968121L;

	private static final int CONTENT_LAYER = 1;

	private static final int FEEDBACK_LAYER = 2;

	/**
	 * Holds the ValidationResult and reports changes in that result. Used to update the state of the feedback components.
	 */
	private final ValidationResultModel model;

	/**
	 * Refers to the content panel that holds the content components.
	 */
	private final JComponent content;

	// Instance Creation ******************************************************

	/**
	 * Creates an IconFeedbackPanel on the given ValidationResultModel using the specified content panel.
	 * <p>
	 * 
	 * <strong>Note:</strong> Typically you should wrap component trees with {@link #getWrappedComponentTree(ValidationResultModel, JComponent)}, not this constructor.
	 * <p>
	 * 
	 * <strong>Note:</strong> You must not add or remove components from the content once this constructor has been invoked.
	 * 
	 * @param model
	 *            the ValidationResultModel to observe
	 * @param content
	 *            the panel that contains the content components
	 * 
	 * @throws NullPointerException
	 *             if model or content is <code>null</code>.
	 */
	public AbstractIconFeedbackValidationResultPanel(ValidationResultModel model, JComponent content) {
		if (model == null) {
			throw new NullPointerException("The validation result model must not be null."); //$NON-NLS-1$
		}
		if (content == null) {
			throw new NullPointerException("The content must not be null."); //$NON-NLS-1$
		}

		this.model = model;
		this.content = content;
		this.setLayout(new SimpleLayout());
		this.add(content, CONTENT_LAYER);
		initEventHandling();
	}

	// Initialization *********************************************************

	/**
	 * Registers a listener with the validation result model that updates the feedback components.
	 */
	private void initEventHandling() {
		model.addPropertyChangeListener(ValidationResultModel.PROPERTYNAME_RESULT, new ValidationResultChangeHandler());
	}

	private JComponent createFeedbackComponent(ValidationResult result, Component contentComponent) {
		Icon icon = ValidationResultViewFactory.getSmallIcon(result.getSeverity());
		JLabel label = new JLabel(icon);
		label.setToolTipText(getMessagesToolTipText(result));
		label.setSize(label.getPreferredSize());
		return label;
	}

	private static String getMessagesToolTipText(ValidationResult result) {
		List<ValidationMessage> messages = result.getMessages();
		StringBuilder buffer = new StringBuilder("<html>"); //$NON-NLS-1$
		for (Iterator<ValidationMessage> it = messages.iterator(); it.hasNext();) {
			ValidationMessage message = it.next();
			buffer.append(message.formattedText());
			if (it.hasNext()) {
				buffer.append("<br/>"); //$NON-NLS-1$
			}
		}
		buffer.append("</html>"); //$NON-NLS-1$

		return buffer.toString();
	}

	private static Point getFeedbackComponentOrigin(JComponent feedbackComponent, Component contentComponent) {
		boolean isLTR = contentComponent.getComponentOrientation().isLeftToRight();
		int x = contentComponent.getX() + (isLTR ? 0 : contentComponent.getWidth() - 1) - feedbackComponent.getWidth() / 2;
		int y = contentComponent.getY() + contentComponent.getHeight() - feedbackComponent.getHeight() + 2;

		return new Point(x, y);
	}

	private void removeAllFeedbackComponents() {
		int componentCount = getComponentCount();
		for (int i = componentCount - 1; i >= 0; i--) {
			Component child = getComponent(i);
			int layer = getLayer(child);
			if (layer == FEEDBACK_LAYER) {
				remove(i);
			}
		}
	}

	private void visitComponentTree(Container container, Map<Object, ValidationResult> keyMap, int xOffset, int yOffset) {
		int componentCount = container.getComponentCount();
		for (int i = 0; i < componentCount; i++) {
			Component child = container.getComponent(i);
			if (!child.isVisible()) {
				continue;
			}
			if (isMarkable(child)) {
				if (isScrollPaneView(child)) {
					Component containerParent = container.getParent();
					addFeedbackComponent(containerParent, (JComponent) child, keyMap, xOffset - containerParent.getX(), yOffset - containerParent.getY());
				} else {
					addFeedbackComponent(child, (JComponent) child, keyMap, xOffset, yOffset);
				}
			} else if (isScrollPaneView(child)) {
				if (child instanceof JTable) {
					Component containerParent = container.getParent();

					addFeedbackComponent(containerParent, (JComponent) child, keyMap, xOffset - containerParent.getX() - container.getX(), yOffset - containerParent.getY() - container.getY());
				}
			} else if (child instanceof JComponent) {
				addFeedbackComponent(child, (JComponent) child, keyMap, xOffset, yOffset);
				visitComponentTree((Container) child, keyMap, xOffset + child.getX(), yOffset + child.getY());
			}
		}
	}

	private static boolean isScrollPaneView(Component c) {
		Container container = c.getParent();
		Container containerParent = container.getParent();
		return (container instanceof JViewport) && (containerParent instanceof JScrollPane);
	}

	protected static boolean isMarkable(Component component) {
		return component instanceof JTextComponent || component instanceof JComboBox;
	}

	private void addFeedbackComponent(Component contentComponent, JComponent messageComponent, Map<Object, ValidationResult> keyMap, int xOffset, int yOffset) {
		ValidationResult result = getAssociatedResult(messageComponent, keyMap);
		JComponent feedbackComponent = createFeedbackComponent(result, contentComponent);
		if (feedbackComponent == null) {
			return;
		}
		add(feedbackComponent, new Integer(FEEDBACK_LAYER));
		Point overlayPosition = getFeedbackComponentOrigin(feedbackComponent, contentComponent);
		overlayPosition.translate(xOffset, yOffset);
		feedbackComponent.setLocation(overlayPosition);
	}

	/**
	 * Returns the ValidationResult associated with the given component using the specified validation result key map. Unlike {@link ValidationComponentUtils#getAssociatedResult(JComponent, Map)} this
	 * method returns the empty result if the component has no keys set.
	 * 
	 * @param comp
	 *            the component may be marked with a validation message key
	 * @param keyMap
	 *            maps validation message keys to ValidationResults
	 * @return the ValidationResult associated with the given component as provided by the specified validation key map or <code>ValidationResult.EMPTY</code> if the component has no message key set,
	 *         or <code>ValidationResult.EMPTY</code> if no result is associated with the component
	 */
	protected abstract ValidationResult getAssociatedResult(JComponent comp, Map<Object, ValidationResult> keyMap);

	// Event Handling *********************************************************

	private void updateFeedbackComponents() {
		removeAllFeedbackComponents();
		visitComponentTree(content, model.getResult().keyMap(), 0, 0);
		repaint();
	}

	/**
	 * Ensures that the feedback components are repositioned. Invoked by <code>#validate</code>, i. e. if this panel is layed out.
	 * <p>
	 * 
	 * TODO: Improve this implementation to set only positions. The current implementation removes all components and re-adds them later.
	 */
	private void repositionFeedbackComponents() {
		updateFeedbackComponents();
	}

	@Override
	protected void validateTree() {
		super.validateTree();
		if (isVisible()) {
			repositionFeedbackComponents();
		}
	}

	private final class ValidationResultChangeHandler implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			updateFeedbackComponents();
		}

	}

	// Layout *****************************************************************

	/**
	 * Used to lay out the content layer in the icon feedback JLayeredPane. The content fills the parent's space; minimum and preferred size of this layout are requested from the content panel.
	 */
	private final class SimpleLayout implements LayoutManager {

		/**
		 * If the layout manager uses a per-component string, adds the component <code>comp</code> to the layout, associating it with the string specified by <code>name</code>.
		 * 
		 * @param name
		 *            the string to be associated with the component
		 * @param comp
		 *            the component to be added
		 */
		@Override
		public void addLayoutComponent(String name, Component comp) {
			// components are well known by the container
		}

		/**
		 * Removes the specified component from the layout.
		 * 
		 * @param comp
		 *            the component to be removed
		 */
		@Override
		public void removeLayoutComponent(Component comp) {
			// components are well known by the container
		}

		/**
		 * Calculates the preferred size dimensions for the specified container, given the components it contains.
		 * 
		 * @param parent
		 *            the container to be laid out
		 * @return the preferred size of the given container
		 * @see #minimumLayoutSize(Container)
		 */
		@Override
		public Dimension preferredLayoutSize(Container parent) {
			return content.getPreferredSize();
		}

		/**
		 * Calculates the minimum size dimensions for the specified container, given the components it contains.
		 * 
		 * @param parent
		 *            the component to be laid out
		 * @return the minimum size of the given container
		 * @see #preferredLayoutSize(Container)
		 */
		@Override
		public Dimension minimumLayoutSize(Container parent) {
			return content.getMinimumSize();
		}

		/**
		 * Lays out the specified container.
		 * 
		 * @param parent
		 *            the container to be laid out
		 */
		@Override
		public void layoutContainer(Container parent) {
			Dimension size = parent.getSize();
			content.setBounds(0, 0, size.width, size.height);
		}
	}
}
