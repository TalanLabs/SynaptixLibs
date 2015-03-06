package com.synaptix.swing;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class JOutlookBar extends JPanel {

	private static final long serialVersionUID = 3505296487863647891L;

	private JPanel topPanel;

	private JPanel bottomPanel;

	private Map<String, BarInfo> bars;

	private int visibleBar;

	private JComponent visibleComponent;

	private ActionListener buttonsActionListener;

	public JOutlookBar() {
		this.setLayout(new BorderLayout());

		initComponents();

		this.add(topPanel, BorderLayout.NORTH);
		this.add(bottomPanel, BorderLayout.SOUTH);
	}

	private void initComponents() {
		topPanel = new JPanel(new GridLayout(1, 1));
		bottomPanel = new JPanel(new GridLayout(1, 1));

		visibleBar = 0;
		visibleComponent = null;

		bars = new LinkedHashMap<String, BarInfo>();

		buttonsActionListener = new ButtonsActionListener();
	}

	public void addBar(String name, JComponent component) {
		addBar(name, null, component);
	}

	public void addBar(String name, Icon icon, JComponent component) {
		BarInfo barInfo = new BarInfo(name, icon, component);
		barInfo.getButton().addActionListener(buttonsActionListener);
		bars.put(name, barInfo);
		render();
	}

	public void removeBar(String name) {
		bars.get(name).getButton().removeActionListener(buttonsActionListener);
		bars.remove(name);
		render();
	}

	public int getVisibleBar() {
		return this.visibleBar;
	}

	public void setVisibleBar(int visibleBar) {
		if (visibleBar > 0 && visibleBar < this.bars.size() - 1) {
			this.visibleBar = visibleBar;
			render();
		}
	}

	public int getBarCount() {
		return bars.size();
	}
	
	private void render() {
		// Compute how many bars we are going to have where
		int totalBars = this.bars.size();
		int topBars = this.visibleBar + 1;
		int bottomBars = totalBars - topBars;

		Iterator<Entry<String, BarInfo>> itr = this.bars.entrySet().iterator();

		this.topPanel.removeAll();
		GridLayout topLayout = (GridLayout) this.topPanel.getLayout();
		topLayout.setRows(topBars);
		BarInfo barInfo = null;
		for (int i = 0; i < topBars; i++) {
			barInfo = itr.next().getValue();
			this.topPanel.add(barInfo.getButton());
		}
		this.topPanel.validate();

		if (this.visibleComponent != null) {
			this.remove(this.visibleComponent);
		}
		this.visibleComponent = barInfo.getComponent();
		this.add(visibleComponent, BorderLayout.CENTER);

		this.bottomPanel.removeAll();
		GridLayout bottomLayout = (GridLayout) this.bottomPanel.getLayout();
		bottomLayout.setRows(bottomBars);
		for (int i = 0; i < bottomBars; i++) {
			barInfo = itr.next().getValue();
			this.bottomPanel.add(barInfo.getButton());
		}
		this.bottomPanel.validate();

		this.revalidate();
	}

	private final class BarInfo {

		private String name;

		private JButton button;

		private JComponent component;

		public BarInfo(String name, JComponent component) {
			this(name, null, component);
		}

		public BarInfo(String name, Icon icon, JComponent component) {
			this.name = name;
			this.component = component;
			this.button = new JButton(name, icon);
		}
		
		public String getName() {
			return this.name;
		}

		public JButton getButton() {
			return this.button;
		}

		public JComponent getComponent() {
			return this.component;
		}
	}

	private final class ButtonsActionListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			int currentBar = 0;
			for (Entry<String, BarInfo> entry : bars.entrySet()) {
				BarInfo barInfo = entry.getValue();
				if (barInfo.getButton() == e.getSource()) {
					visibleBar = currentBar;
					render();
					return;
				}
				currentBar++;
			}
		}
	}

	public static JPanel getDummyPanel(String name) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new JLabel(name, JLabel.CENTER));
		return panel;
	}

	/**
	 * Debug test...
	 */
	public static void main(String[] args) {
		JFrame frame = new JFrame("JOutlookBar Test"); //$NON-NLS-1$
		JOutlookBar outlookBar = new JOutlookBar();
		outlookBar.addBar("One", getDummyPanel("One")); //$NON-NLS-1$ //$NON-NLS-2$
		outlookBar.addBar("Two", getDummyPanel("Two")); //$NON-NLS-1$ //$NON-NLS-2$
		outlookBar.addBar("Three", getDummyPanel("Three")); //$NON-NLS-1$ //$NON-NLS-2$
		outlookBar.addBar("Four", getDummyPanel("Four")); //$NON-NLS-1$ //$NON-NLS-2$
		outlookBar.addBar("Five", getDummyPanel("Five")); //$NON-NLS-1$ //$NON-NLS-2$
		outlookBar.setVisibleBar(2);
		frame.getContentPane().add(outlookBar);

		frame.setSize(800, 600);
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(d.width / 2 - 400, d.height / 2 - 300);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}