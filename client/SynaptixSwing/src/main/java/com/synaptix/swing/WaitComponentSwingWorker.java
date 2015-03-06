package com.synaptix.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.client.view.IWaitWorker;

public abstract class WaitComponentSwingWorker<T> {

	private static IAnimationComponentBuilder defaultAnimationComponentBuilder;

	private static Color defaultTextColor = Color.black;

	private static Color defaultBackgroundColor = null;

	private static Font defaultTextFont = new Font("Arial", Font.BOLD, 15);

	private IAnimationComponent animationComponent;

	private MyWaitLabel label;

	private MySwingWorker swingWorker;

	private WaitComponentFeedbackPanel component;

	private Color backgroundColor;

	private Color textColor;

	private Font textFont;

	static {
		defaultAnimationComponentBuilder = new IAnimationComponentBuilder() {
			public IAnimationComponent newInstance() {
				JCircleWait circleWait = new JCircleWait();
				circleWait.setWaitHeight(24);
				circleWait.setCircleSize(7);
				circleWait.setTraineColor(Color.red);
				circleWait.setDeltaEcartement(2);
				return circleWait;
			}
		};
	}

	public static IAnimationComponentBuilder getDefaultAnimationComponentBuilder() {
		return defaultAnimationComponentBuilder;
	}

	public static void setDefaultAnimationComponentBuilder(
			IAnimationComponentBuilder defaultAnimationComponentBuilder) {
		WaitComponentSwingWorker.defaultAnimationComponentBuilder = defaultAnimationComponentBuilder;
	}

	public static Color getDefaultBackgroundColor() {
		return defaultBackgroundColor;
	}

	public static void setDefaultBackgroundColor(Color defaultBackgroundColor) {
		WaitComponentSwingWorker.defaultBackgroundColor = defaultBackgroundColor;
	}

	public static Color getDefaultTextColor() {
		return defaultTextColor;
	}

	public static void setDefaultTextColor(Color defaultTextColor) {
		WaitComponentSwingWorker.defaultTextColor = defaultTextColor;
	}

	public static Font getDefaultTextFont() {
		return defaultTextFont;
	}

	public static void setDefaultTextFont(Font defaultTextFont) {
		WaitComponentSwingWorker.defaultTextFont = defaultTextFont;
	}

	public WaitComponentSwingWorker(WaitComponentFeedbackPanel component) {
		super();

		swingWorker = new MySwingWorker();

		this.component = component;

		this.backgroundColor = defaultBackgroundColor;
		this.textColor = defaultTextColor;
		this.animationComponent = defaultAnimationComponentBuilder
				.newInstance();
		this.textFont = defaultTextFont;

		initComponents();
	}

	private void createComponents() {
		label = new MyWaitLabel();
	}

	private void initComponents() {
		createComponents();
	}

	public void setAnimationComponent(IAnimationComponent animationComponent) {
		this.animationComponent = animationComponent;

		label = new MyWaitLabel();
	}

	public IAnimationComponent getAnimationComponent() {
		return animationComponent;
	}

	public final IWaitWorker execute() {
		component.addWaitComponent(label);
		label.setLocation(0, 0);
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

	private final class MySwingWorker extends SwingWorker<T, String> implements IWaitWorker {

		public void myPublish(String... chunks) {
			publish(chunks);
		}

		protected T doInBackground() throws Exception {
			return WaitComponentSwingWorker.this.doInBackground();
		}

		protected void process(List<String> chunks) {
			for (String string : chunks) {
				label.setText(string);
				Dimension d = label.getPreferredSize();
				label.setBounds(0, 0, d.width, d.height);
				label.revalidate();
				label.repaint();
			}
		}

		protected void done() {
			label.stop();

			component.removeWaitComponent(label);

			WaitComponentSwingWorker.this.done();
		}
	}

	private final class MyWaitLabel extends JPanel {

		private static final long serialVersionUID = 8956351168383470606L;

		private JLabel label;

		public MyWaitLabel() {
			super(new BorderLayout());

			label = new JLabel("");
			label.setFont(textFont);
			label.setOpaque(false);
			label.setForeground(textColor);

			this.setOpaque(backgroundColor != null);
			if (backgroundColor != null) {
				this.setBackground(backgroundColor);
			}
			// this.add(animationComponent.getComponent(), BorderLayout.WEST);
			this.add(buildContents(), BorderLayout.CENTER);
		}

		private JComponent buildContents() {
			FormLayout layout = new FormLayout(
					"FILL:24PX:NONE,FILL:DEFAULT:GROW(1)", //$NON-NLS-1$
					"FILL:DEFAULT"); //$NON-NLS-1$
			PanelBuilder builder = new PanelBuilder(layout);
			CellConstraints cc = new CellConstraints();
			builder.add(animationComponent.getComponent(), cc.xy(1, 1));
			builder.add(label, cc.xy(2, 1));

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
	}

	public interface IAnimationComponentBuilder {

		public abstract IAnimationComponent newInstance();
	}
}
