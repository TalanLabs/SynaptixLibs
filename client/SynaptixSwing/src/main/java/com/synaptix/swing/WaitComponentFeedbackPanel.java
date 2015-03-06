package com.synaptix.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.KeyboardFocusManager;
import java.awt.LayoutManager;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLayeredPane;

public class WaitComponentFeedbackPanel extends JLayeredPane {

	private static final long serialVersionUID = -9100049250111968121L;

	private static final int CONTENT_LAYER = 1;

	private static final int FEEDBACK_LAYER = 2;

	private static final int FEEDBACK_FULL_LAYER = 2000;

	private MyComponentListener componentListener;

	private Component content;

	private boolean focusTraversalPolicyProvider;

	private FocusTraversalPolicy focusTraversalPolicy;

	private List<Component> blockComponentList;

	public WaitComponentFeedbackPanel() {
		super();

		this.setLayout(new SimpleLayout());

		this.blockComponentList = new ArrayList<Component>();

		componentListener = new MyComponentListener();
	}

	public void addContent(Component content) {
		this.content = content;
		add(content, CONTENT_LAYER);
		repaint();
	}

	public void addWaitComponent(Component component) {
		addWaitComponent(component, false);
	}

	public void addWaitComponent(Component component, boolean fullPanel) {
		add(component, new Integer(fullPanel ? FEEDBACK_FULL_LAYER : FEEDBACK_LAYER));
		component.addComponentListener(componentListener);

		calculate();

		repaint();
	}

	public synchronized void blockFocusTraversalPolicy(Component blockComponent) {
		if (blockComponentList.size() == 0) {
			KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();

			focusTraversalPolicyProvider = this.isFocusTraversalPolicyProvider();
			focusTraversalPolicy = this.getFocusTraversalPolicy();

			this.setFocusTraversalPolicyProvider(true);
			this.setFocusTraversalPolicy(new MyFocusTraversalPolicy());
		}
		blockComponentList.add(blockComponent);
	}

	public synchronized void deBlockFocusTraversalPolicy() {
		blockComponentList.remove(0);
		if (blockComponentList.size() == 0) {
			this.setFocusTraversalPolicyProvider(focusTraversalPolicyProvider);
			this.setFocusTraversalPolicy(focusTraversalPolicy);
		}
	}

	private void calculate() {
		int y = 0;
		int componentCount = getComponentCount();
		for (int i = componentCount - 1; i >= 0; i--) {
			Component child = getComponent(i);
			int layer = getLayer(child);
			if (layer == FEEDBACK_LAYER) {
				int h = child.getHeight();
				child.setLocation(0, y);
				y += h;
			}
			if (layer == FEEDBACK_FULL_LAYER) {
				child.setLocation(0, 0);
			}
		}
	}

	public void removeWaitComponent(Component component) {
		component.removeComponentListener(componentListener);
		remove(component);

		calculate();

		repaint();
	}

	private final class MyComponentListener extends ComponentAdapter {

		@Override
		public void componentResized(ComponentEvent e) {
			calculate();
		}
	}

	private final class SimpleLayout implements LayoutManager {

		@Override
		public void addLayoutComponent(String name, Component comp) {
		}

		@Override
		public void removeLayoutComponent(Component comp) {
		}

		@Override
		public Dimension preferredLayoutSize(Container parent) {
			if (content != null) {
				return content.getPreferredSize();
			}
			return new Dimension();
		}

		@Override
		public Dimension minimumLayoutSize(Container parent) {
			if (content != null) {
				return content.getMinimumSize();
			}
			return new Dimension();
		}

		@Override
		public void layoutContainer(Container parent) {
			if (content != null) {
				Dimension size = parent.getSize();
				content.setBounds(0, 0, size.width, size.height);
			}
		}
	}

	private final class MyFocusTraversalPolicy extends FocusTraversalPolicy {

		@Override
		public Component getLastComponent(Container aContainer) {
			return blockComponentList.get(0);
		}

		@Override
		public Component getFirstComponent(Container aContainer) {
			return blockComponentList.get(0);
		}

		@Override
		public Component getDefaultComponent(Container aContainer) {
			return blockComponentList.get(0);
		}

		@Override
		public Component getComponentBefore(Container aContainer, Component aComponent) {
			return blockComponentList.get(0);
		}

		@Override
		public Component getComponentAfter(Container aContainer, Component aComponent) {
			return blockComponentList.get(0);
		}
	}

}
