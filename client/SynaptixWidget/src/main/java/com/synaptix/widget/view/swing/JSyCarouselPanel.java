package com.synaptix.widget.view.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTargetAdapter;
import org.jdesktop.animation.timing.interpolation.Interpolator;
import org.jdesktop.swingx.HorizontalLayout;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.internal.ui.common.BasicCommandButtonUI;
import org.pushingpixels.flamingo.internal.utils.DoubleArrowResizableIcon;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * A carousel for a list of components
 * 
 */
public class JSyCarouselPanel extends JPanel {

	private static final long serialVersionUID = -7590582904044164794L;

	public static final String PAGE = "page";

	private int currPage;

	private Point p;

	private JViewport viewport;

	private JPanel pageSelectorPanel;

	private JComponent[] components;

	private boolean useKeys;

	private Animator animator;

	private JCommandButton leftButton;

	private JCommandButton rightButton;

	private List<PercentageSquare> squareList;

	private JPanel componentsPanel;

	private boolean showDotIfAlone;

	public JSyCarouselPanel(final JComponent[] components) {
		this(components, false);
	}

	public JSyCarouselPanel(final JComponent[] components, boolean useKeys) {
		super(new BorderLayout());

		if (components == null) {
			this.components = new JComponent[] {};
			this.currPage = 0;
		} else {
			this.components = components;
			this.currPage = 1;
		}
		this.useKeys = useKeys;

		showDotIfAlone = true;

		initActions();
		initComponents();

		this.add(buildContents());

		if (isValidPage(1)) {
			setPage(1);
		} else {
			setPage(0);
		}
	}

	/**
	 * If true, displays a dot even when there is only one page. If false, hides the singular dot.
	 * 
	 * @param showDotIfAlone
	 */
	public void setShowDotIfAlone(boolean showDotIfAlone) {
		this.showDotIfAlone = showDotIfAlone;
	}

	private void initActions() {
		leftButton = createLeadingScroller();
		leftButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (isValidPage(currPage - 1)) {
					updatePage(currPage, currPage - 1);
				}
			}
		});

		rightButton = createTrailingScroller();
		rightButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (isValidPage(currPage + 1)) {
					updatePage(currPage, currPage + 1);
				}
			}
		});

		// ActionMap
		if (useKeys) {
			this.getActionMap().put("PREV", new AbstractAction() {
				private static final long serialVersionUID = -877208244635591552L;

				@Override
				public void actionPerformed(ActionEvent e) {
					if (isValidPage(currPage - 1)) {
						updatePage(currPage, currPage - 1);
					}
				}
			});
			this.getActionMap().put("NEXT", new AbstractAction() {
				private static final long serialVersionUID = -877208244635591552L;

				@Override
				public void actionPerformed(ActionEvent e) {
					if (isValidPage(currPage + 1)) {
						updatePage(currPage, currPage + 1);
					}
				}
			});
			this.getActionMap().put("FIRST", new AbstractAction() {
				private static final long serialVersionUID = -877208244635591552L;

				@Override
				public void actionPerformed(ActionEvent e) {
					if (isValidPage(1)) {
						goToPage(1);
					}
				}
			});
			this.getActionMap().put("LAST", new AbstractAction() {
				private static final long serialVersionUID = -877208244635591552L;

				@Override
				public void actionPerformed(ActionEvent e) {
					if (isValidPage(components.length)) {
						goToPage(components.length);
					}
				}
			});

			this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "PREV");
			this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "NEXT");
			this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0), "FIRST");
			this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_END, 0), "LAST");
		}
	}

	protected JCommandButton createLeadingScroller() {
		JCommandButton b = new JCommandButton(null, new DoubleArrowResizableIcon(new Dimension(9, 9), SwingConstants.WEST));

		b.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		b.setFocusable(false);
		b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		b.putClientProperty(BasicCommandButtonUI.EMULATE_SQUARE_BUTTON, Boolean.TRUE);
		b.putClientProperty(BasicCommandButtonUI.DONT_DISPOSE_POPUPS, Boolean.TRUE);
		return b;
	}

	protected JCommandButton createTrailingScroller() {
		JCommandButton b = new JCommandButton(null, new DoubleArrowResizableIcon(new Dimension(9, 9), SwingConstants.EAST));

		b.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		b.setFocusable(false);
		b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		b.putClientProperty(BasicCommandButtonUI.EMULATE_SQUARE_BUTTON, Boolean.TRUE);
		b.putClientProperty(BasicCommandButtonUI.DONT_DISPOSE_POPUPS, Boolean.TRUE);
		return b;
	}

	private void buildPageSelector() {
		squareList = new ArrayList<PercentageSquare>();
		if ((showDotIfAlone) || (components.length > 1)) {
			for (int i = 0; i < components.length; i++) {
				final Integer idx = i + 1;
				PercentageSquare ps = new PercentageSquare();
				ps.setPercentage((currPage == idx ? 100f : 0f));
				squareList.add(ps);

				pageSelectorPanel.add(ps);
				ps.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						goToPage(idx);
					};
				});
			}
		}
	}

	private void buildComponentPages() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		componentsPanel.add(panel);
		for (JComponent c : components) {
			componentsPanel.add(c);
		}
		JPanel panel2 = new JPanel();
		panel2.setOpaque(false);
		componentsPanel.add(panel2);
	}

	private void initComponents() {

		pageSelectorPanel = new JPanel(new HorizontalLayout(5));
		pageSelectorPanel.setOpaque(false);

		componentsPanel = new JPanel(new ComponentsLayout());
		componentsPanel.setOpaque(false);

		buildPageSelector();
		buildComponentPages();

		viewport = new JViewport();
		viewport.setView(componentsPanel);
		viewport.setOpaque(false);
		viewport.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				componentsPanel.getLayout().layoutContainer(componentsPanel);
				setPage(currPage);
			}
		});
	}

	private JComponent buildContents() {
		MainPanel layeredPane = new MainPanel(viewport, pageSelectorPanel);

		FormLayout layout = new FormLayout("FILL:PREF:NONE,FILL:DEFAULT:GROW(1.0),FILL:PREF:NONE", "FILL:DEFAULT:GROW(1.0)");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.add(layeredPane, cc.xy(2, 1));
		builder.add(leftButton, cc.xy(1, 1));
		builder.add(rightButton, cc.xy(3, 1));
		layeredPane.setOpaque(false);
		leftButton.setOpaque(false);
		rightButton.setOpaque(false);
		JPanel panel = builder.getPanel();
		panel.setOpaque(false);
		return panel;
	}

	/**
	 * Creates an animation that goes from previous page to next
	 * 
	 * @param previous
	 * @param next
	 */
	private void updatePage(final int previous, final int next) {
		updatePage(previous, next, true);

		pageChanged(next);
	}

	private void updatePage(final int previous, final int next, boolean stopIfBusy) {
		final Dimension d = viewport.getSize();

		if ((animator == null) || (!animator.isRunning())) {
			animator = new Animator(500, new TimingTargetAdapter() {
				@Override
				public void begin() {
					viewport.setViewPosition(new Point(d.width * previous, 0));
				}

				@Override
				public void end() {
					viewport.setViewPosition(new Point(d.width * next, 0));
					drawPageSelector(previous, next, 100f);

					pageChanged(next);
					// firePageChanged(previous, next);
				}

				@Override
				public void timingEvent(float fraction) {
					p = new Point((int) (d.width * previous + fraction * (d.width * next - d.width * previous)), 0);
					viewport.setViewPosition(p);
					drawPageSelector(previous, next, fraction * 100f);

				}
			});
			animator.setInterpolator(new CarouselInterpolator());
			animator.start();
			firePageChanged(previous, next);
		} else /* if (stopIfBusy) */{
			animator.stop();
			updatePage(this.currPage, next, stopIfBusy);
		}
	}

	/**
	 * Called when a page is being changed or has changed
	 * 
	 * @param page
	 * @return
	 */
	private int pageChanged(int page) {
		int oldPage = currPage;
		this.currPage = page;

		if (isValidPage(page)) {
			leftButton.setEnabled((isValidPage(currPage - 1) ? true : false));
			rightButton.setEnabled((isValidPage(currPage + 1) ? true : false));
		} else {
			leftButton.setEnabled(false);
			rightButton.setEnabled(false);
		}
		return oldPage;
	}

	private void firePageChanged(int oldPage, int page) {
		if (isValidPage(page)) {
			firePropertyChange(PAGE, oldPage, page);
		}
	}

	/**
	 * Goes to specified page without animation If page does not exist, goes to page 0
	 * 
	 * @param page
	 */
	public void setPage(int page) {
		int x = 0;
		if (isValidPage(page)) {
			final Dimension d = viewport.getSize();
			x = d.width * page;
		}
		drawPageSelector(currPage, page, 100f);
		viewport.setViewPosition(new Point(x, 0));

		int oldPage = pageChanged(page);
		firePageChanged(oldPage, page);
	}

	public int getPage() {
		return currPage;
	}

	public void setComponents(JComponent[] components) {
		if (components == null) {
			this.components = new JComponent[] {};
		} else {
			this.components = components;
		}

		pageSelectorPanel.removeAll();
		componentsPanel.removeAll();

		buildPageSelector();
		buildComponentPages();

		componentsPanel.getLayout().layoutContainer(componentsPanel);
		if (getPage() > this.components.length) {
			setPage(this.components.length);
		} else if (getPage() < 1) {
			if (isValidPage(1)) {
				setPage(1);
			} else {
				setPage(0);
			}
		} else {
			pageChanged(getPage());
		}
		this.validate();
	}

	/**
	 * Goes to specified page from current with an animation
	 * 
	 * @param page
	 */
	public void goToPage(int page) {
		if (isValidPage(page)) {
			updatePage(currPage, page, true);
		}
	}

	public boolean isValidPage(int page) {
		if ((page >= 1) && (page <= components.length)) {
			return true;
		}
		return false;
	}

	private void drawPageSelector(int previous, int next, float percentage) {
		if (squareList != null && !squareList.isEmpty()) {
			if (isValidPage(previous)) {
				PercentageSquare precPS = squareList.get(previous - 1);
				precPS.setPercentage(100f - percentage);
			}
			if (isValidPage(next)) {
				PercentageSquare suivPS = squareList.get(next - 1);
				suivPS.setPercentage(percentage);
			}
		}
		pageSelectorPanel.repaint();
	}

	private class ComponentsLayout implements LayoutManager {

		@Override
		public void addLayoutComponent(String name, Component comp) {
		}

		@Override
		public void removeLayoutComponent(Component comp) {
		}

		@Override
		public void layoutContainer(Container parent) {
			Dimension d = viewport.getSize();
			for (int i = 0; i < parent.getComponentCount(); i++) {
				parent.getComponent(i).setBounds(i * d.width, 0, d.width, d.height);
			}
		}

		@Override
		public Dimension minimumLayoutSize(Container parent) {
			return preferredLayoutSize(parent);
		}

		@Override
		public Dimension preferredLayoutSize(Container parent) {
			Dimension d = viewport.getSize();
			return new Dimension(d.width * parent.getComponentCount(), 0);
		}
	}

	private final class CarouselInterpolator implements Interpolator {

		private final float mTension;

		public CarouselInterpolator() {
			mTension = 1.0f;
		}

		@Override
		public float interpolate(float t) {
			t -= 1.0f;
			float test = t * t * ((mTension + 1) * t + mTension) + 1.0f;
			return test;
		}
	}

	/**
	 * A square (big or small, depending whether it is selected or not)
	 * 
	 */
	private final class PercentageSquare extends JComponent {

		private static final long serialVersionUID = -3974866402033573172L;

		protected static final int DEFAULT_SIZE = 16;

		protected static final float MIN_CIRCLE = 4.0f;

		protected static final float MAX_CIRCLE = 12.0f;

		private final Dimension size = new Dimension(DEFAULT_SIZE, DEFAULT_SIZE);

		private final Color circleBackColor = new Color(230, 230, 230, 255);

		private final Color circleBorderColor = new Color(70, 70, 70, 255);

		private final Color bigCircleBackColor = new Color(230, 230, 230, 192);

		private final Color bigCircleBorderColor = new Color(70, 70, 70, 192);

		private float percentage;

		public PercentageSquare() {
			super();

			this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}

		public float getPercentage() {
			return percentage;
		}

		public void setPercentage(float percentage) {
			this.percentage = percentage;
		}

		@Override
		public Dimension getSize() {
			return size;
		}

		@Override
		public Dimension getPreferredSize() {
			return getSize();
		}

		@Override
		public void paint(Graphics g) {
			Graphics2D g2 = (Graphics2D) g.create();

			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			float per = getPercentage();
			if (per > 100) {
				per = 100f;
			}
			if (per < 0) {
				per = 0f;
			}

			g2.setColor(per >= 100 ? bigCircleBackColor : circleBackColor);
			Shape shape = getShape((per >= 100 ? true : false), per);
			g2.fill(shape);
			g2.setColor(per >= 100 ? bigCircleBorderColor : circleBorderColor);
			g2.draw(shape);

			g2.dispose();
		}
	}

	protected static Shape getShape(boolean selected, float percentage) {
		float circle = percentage * (PercentageSquare.MAX_CIRCLE - PercentageSquare.MIN_CIRCLE) / 100.0f + PercentageSquare.MIN_CIRCLE;
		float cxy = (PercentageSquare.DEFAULT_SIZE - circle) / 2.0f;

		return new Ellipse2D.Float(cxy, cxy, circle, circle);
	}

	private static class MainPanel extends JLayeredPane {

		private static final long serialVersionUID = -9100049250111968121L;

		private static final int CONTENT_LAYER = 1;

		private static final int FEEDBACK_LAYER = 2;

		private Component content;

		private Component layer2;

		public MainPanel(Component content, Component layer2) {
			super();

			this.content = content;
			this.layer2 = layer2;

			this.setLayout(new SimpleLayout());

			add(content, CONTENT_LAYER);
			add(layer2, new Integer(FEEDBACK_LAYER));
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

					Dimension d = layer2.getPreferredSize();

					layer2.setBounds((size.width - d.width) / 2, size.height - d.height - 5, size.width, d.height);
				}
			}
		}
	}
}
