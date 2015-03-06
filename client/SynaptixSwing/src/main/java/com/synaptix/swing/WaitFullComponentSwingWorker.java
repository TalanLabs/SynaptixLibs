package com.synaptix.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.client.view.IWaitWorker;

public abstract class WaitFullComponentSwingWorker<T> {

	private static IAnimationComponentBuilder defaultAnimationComponentBuilder;

	private static Color defaultTextColor = Color.black;

	private static Color defaultBackgroundColor = new Color(255, 255, 255, 128);

	private static Font defaultTextFont = new Font("Arial", Font.BOLD, 20);

	private IAnimationComponent animationComponent;

	private MyFullWaitLabel label;

	private MySwingWorker swingWorker;

	private WaitComponentFeedbackPanel component;

	private MyComponentListener componentListener;

	private Color backgroundColor;

	private Color textColor;

	private Font textFont;

	static {
		defaultAnimationComponentBuilder = new IAnimationComponentBuilder() {
			public IAnimationComponent newInstance() {
				JTriangleWait t = new JTriangleWait(10, true);
				t.setNbTriangle(20);
				t.setTraineLenght(20);
				t.setTraineColor(Color.gray);
				return t;
			}
		};
	}

	public static IAnimationComponentBuilder getDefaultAnimationComponentBuilder() {
		return defaultAnimationComponentBuilder;
	}

	public static void setDefaultAnimationComponentBuilder(IAnimationComponentBuilder defaultAnimationComponentBuilder) {
		WaitFullComponentSwingWorker.defaultAnimationComponentBuilder = defaultAnimationComponentBuilder;
	}

	public static Color getDefaultBackgroundColor() {
		return defaultBackgroundColor;
	}

	public static void setDefaultBackgroundColor(Color defaultBackgroundColor) {
		WaitFullComponentSwingWorker.defaultBackgroundColor = defaultBackgroundColor;
	}

	public static Color getDefaultTextColor() {
		return defaultTextColor;
	}

	public static void setDefaultTextColor(Color defaultTextColor) {
		WaitFullComponentSwingWorker.defaultTextColor = defaultTextColor;
	}

	public static Font getDefaultTextFont() {
		return defaultTextFont;
	}

	public static void setDefaultTextFont(Font defaultTextFont) {
		WaitFullComponentSwingWorker.defaultTextFont = defaultTextFont;
	}

	public WaitFullComponentSwingWorker(WaitComponentFeedbackPanel component) {
		swingWorker = new MySwingWorker();

		this.component = component;

		this.backgroundColor = defaultBackgroundColor;
		this.textColor = defaultTextColor;
		this.textFont = defaultTextFont;
		this.animationComponent = defaultAnimationComponentBuilder.newInstance();

		initComponents();
	}

	private void createComponents() {
		label = new MyFullWaitLabel();

		componentListener = new MyComponentListener();
	}

	private void initComponents() {
		createComponents();
	}

	public void setAnimationComponent(IAnimationComponent animationComponent) {
		this.animationComponent = animationComponent;

		label = new MyFullWaitLabel();
	}

	public IAnimationComponent getAnimationComponent() {
		return animationComponent;
	}

	public final IWaitWorker execute() {
		component.addWaitComponent(label, true);
		component.addComponentListener(componentListener);

		component.blockFocusTraversalPolicy(label);

		label.setLocation(0, 0);
		Dimension d = component.getSize();
		label.setBounds(0, 0, d.width, d.height);

		label.start();
		swingWorker.execute();
		return swingWorker;
	}

	protected abstract T doInBackground() throws Exception;

	protected abstract void done();

	protected final T get() throws Exception {
		return swingWorker.get();
	}

	protected final void publish(String... chunks) {
		swingWorker.myPublish(chunks);
	}

	private final class MyComponentListener extends ComponentAdapter {

		public void componentResized(ComponentEvent e) {
			label.setBounds(0, 0, component.getWidth(), component.getHeight());
		}
	}

	private final class MySwingWorker extends SwingWorker<T, String> implements IWaitWorker {

		public void myPublish(String... chunks) {
			publish(chunks);
		}

		protected T doInBackground() throws Exception {
			return WaitFullComponentSwingWorker.this.doInBackground();
		}

		protected void process(List<String> chunks) {
			for (String string : chunks) {
				label.setText(string);
				Dimension d = component.getSize();
				label.setBounds(0, 0, d.width, d.height);
				label.revalidate();
				label.repaint();
			}
		}

		protected void done() {
			label.stop();

			component.removeWaitComponent(label);
			component.removeComponentListener(componentListener);

			component.deBlockFocusTraversalPolicy();

			WaitFullComponentSwingWorker.this.done();
		}
	}

	private final class MyFullWaitLabel extends JPanel {

		private static final long serialVersionUID = 8956351168383470606L;

		private JLabel label;

		public MyFullWaitLabel() {
			super(new BorderLayout());

			label = new JLabel("");
			label.setFont(textFont);
			label.setOpaque(false);
			label.setForeground(textColor);
			label.setHorizontalAlignment(JLabel.CENTER);

			MouseAdapter ma = new MouseAdapter() {
			};

			this.addMouseListener(ma);
			this.addMouseMotionListener(ma);
			this.addMouseWheelListener(ma);

			this.setFocusable(true);

			this.addKeyListener(new KeyAdapter() {
			});

			this.setOpaque(false);
			this.add(buildContents(), BorderLayout.CENTER);
		}

		private JComponent buildContents() {
			FormLayout layout = new FormLayout("FILL:DEFAULT:GROW(0.4),FILL:DEFAULT:GROW(0.2),FILL:DEFAULT:GROW(0.4)", //$NON-NLS-1$
					"FILL:DEFAULT:GROW(0.4),FILL:DEFAULT:GROW(0.15),FILL:DEFAULT:GROW(0.05),FILL:DEFAULT:GROW(0.4)"); //$NON-NLS-1$
			PanelBuilder builder = new PanelBuilder(layout);
			CellConstraints cc = new CellConstraints();
			builder.add(animationComponent.getComponent(), cc.xy(2, 2));
			builder.add(label, cc.xyw(1, 3, 3));

			JPanel panel = builder.getPanel();
			panel.setOpaque(false);
			return panel;
		}

		public void start() {
			animationComponent.start();
		}

		public void stop() {
			animationComponent.stop();
		}

		public void setText(String text) {
			label.setText(text);
		}

		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			Graphics2D g2 = (Graphics2D) g.create();

			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			g2.setColor(backgroundColor);
			g2.fillRect(0, 0, this.getWidth(), this.getHeight());

			g2.dispose();
		}
	}

	public interface IAnimationComponentBuilder {

		public abstract IAnimationComponent newInstance();
	}
}
