package com.synaptix.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class JAccordion extends JPanel {

	private static final long serialVersionUID = 8574667254501485212L;

	public enum Direction {
		HORIZONTAL, VERTICAL_UP, VERTICAL_DOWN
	}

	/**
	 * The top panel: contains the buttons displayed on the top of the
	 * JOutlookBar
	 */
	private JPanel topPanel = new JPanel(new GridLayout(1, 1));

	/**
	 * The bottom panel: contains the buttons displayed on the bottom of the
	 * JOutlookBar
	 */
	private JPanel bottomPanel = new JPanel(new GridLayout(1, 1));

	/**
	 * A LinkedHashMap of bars: we use a linked hash map to preserve the order
	 * of the bars
	 */
	private Map<String, BarInfo> bars = new LinkedHashMap<String, BarInfo>();

	private List<BarInfo> barList = new ArrayList<BarInfo>();

	/**
	 * The currently visible bar (zero-based index)
	 */
	private int visibleBar = -1;

	/**
	 * A place-holder for the currently visible component
	 */
	private JComponent visibleComponent = null;

	private Direction direction;

	private ActionListener buttonActionListener = new MyButtonActionListener();

	public JAccordion() {
		this(Direction.HORIZONTAL);
	}

	/**
	 * Creates a new JOutlookBar; after which you should make repeated calls to
	 * addBar() for each bar
	 * 
	 * @param orientation
	 *            SwingConstants.VERTICAL ou SwingConstants.HORIZONTAL
	 */
	public JAccordion(Direction direction) {
		super(new BorderLayout());

		this.direction = direction;

		this.add(topPanel,
				direction == Direction.HORIZONTAL ? BorderLayout.NORTH
						: BorderLayout.WEST);
		this.add(bottomPanel,
				direction == Direction.HORIZONTAL ? BorderLayout.SOUTH
						: BorderLayout.EAST);
	}

	public void addChangeListener(ChangeListener l) {
		listenerList.add(ChangeListener.class, l);
	}

	public void removeChangeListener(ChangeListener l) {
		listenerList.remove(ChangeListener.class, l);
	}

	protected void fireChangeListener() {
		ChangeListener[] ls = listenerList.getListeners(ChangeListener.class);
		ChangeEvent e = new ChangeEvent(this);
		for (ChangeListener l : ls) {
			l.stateChanged(e);
		}
	}

	/**
	 * Adds the specified component to the JOutlookBar and sets the bar's name
	 * 
	 * @param name
	 *            The name of the outlook bar
	 * @param componenet
	 *            The component to add to the bar
	 * @return Position de la bar
	 */
	public int addBar(String name, JComponent component) {
		return addBar(name, null, component);
	}

	/**
	 * Adds the specified component to the JOutlookBar and sets the bar's name
	 * 
	 * @param name
	 *            The name of the outlook bar
	 * @param icon
	 *            An icon to display in the outlook bar
	 * @param componenet
	 *            The component to add to the bar
	 * @return Position de la bar
	 */
	public int addBar(String name, Icon icon, JComponent component) {
		return insertBar(barList.isEmpty() ? 0 : barList.size(), name, icon,
				component);
	}

	/**
	 * Ajoute une bar à la position
	 * 
	 * @param index
	 * @param name
	 * @param icon
	 * @param component
	 * @return renvoie la position
	 */
	public int insertBar(int index, String name, Icon icon, JComponent component) {
		BarInfo barInfo = new BarInfo(name, icon, component);
		barInfo.getButton().addActionListener(buttonActionListener);
		this.bars.put(name, barInfo);
		barList.add(index, barInfo);

		render();

		if (visibleBar == -1) {
			setVisibleBar(barList.size() - 1);
		}

		return barList.indexOf(barInfo);
	}

	/**
	 * Efface une bar selon la position
	 * 
	 * @param index
	 */
	public void removeBar(int index) {
		removeBar(barList.get(index).name);
	}

	/**
	 * Removes the specified bar from the JOutlookBar
	 * 
	 * @param name
	 *            The name of the bar to remove
	 */
	public void removeBar(String name) {
		BarInfo barInfo = this.bars.get(name);
		barInfo.getButton().removeActionListener(buttonActionListener);
		int index = barList.indexOf(barInfo);
		barList.remove(barInfo);
		this.bars.remove(name);

		render();

		if (visibleBar == index) {
			setVisibleBar(barList.isEmpty() ? -1 : (index > 0 ? index - 1 : 0));
		}
	}

	public int indexOfBar(String name) {
		return barList.indexOf(bars.get(name));
	}

	public String barOfIndex(int index) {
		return barList.get(index).name;
	}

	/**
	 * Returns the index of the currently visible bar (zero-based)
	 * 
	 * @return The index of the currently visible bar
	 */
	public int getVisibleBar() {
		return this.visibleBar;
	}

	/**
	 * Programmatically sets the currently visible bar; the visible bar index
	 * must be in the range of 0 to size() - 1
	 * 
	 * @param visibleBar
	 *            The zero-based index of the component to make visible
	 */
	public void setVisibleBar(int visibleBar) {
		if (visibleBar >= 0 && visibleBar <= this.barList.size() - 1
				&& visibleBar != this.visibleBar) {
			this.visibleBar = visibleBar;
			render();

			fireChangeListener();
		}
	}

	/**
	 * Causes the outlook bar component to rebuild itself; this means that it
	 * rebuilds the top and bottom panels of bars as well as making the
	 * currently selected bar's panel visible
	 */
	public void render() {
		if (visibleBar == -1) {
			this.topPanel.removeAll();
			GridLayout topLayout = (GridLayout) this.topPanel.getLayout();
			if (direction == Direction.HORIZONTAL) {
				topLayout.setRows(0);
			} else {
				topLayout.setColumns(0);
			}

			if (this.visibleComponent != null) {
				this.remove(this.visibleComponent);
			}

			this.bottomPanel.removeAll();
			GridLayout bottomLayout = (GridLayout) this.bottomPanel.getLayout();
			if (direction == Direction.HORIZONTAL) {
				bottomLayout.setRows(0);
			} else {
				bottomLayout.setColumns(0);
			}
		} else {
			// Compute how many bars we are going to have where
			int totalBars = this.bars.size();
			int topBars = visibleBar + 1;
			int bottomBars = totalBars - topBars;

			// Get an iterator to walk through out bars with
			Iterator<BarInfo> itr = this.barList.iterator();

			// Render the top bars: remove all components, reset the GridLayout
			// to
			// hold to correct number of bars, add the bars, and "validate" it
			// to
			// cause it to re-layout its components
			this.topPanel.removeAll();
			GridLayout topLayout = (GridLayout) this.topPanel.getLayout();
			if (direction == Direction.HORIZONTAL) {
				topLayout.setRows(topBars);
			} else {
				topLayout.setColumns(topBars);
			}
			BarInfo barInfo = null;
			for (int i = 0; i < topBars; i++) {
				barInfo = itr.next();
				this.topPanel.add(barInfo.getButton());
			}
			this.topPanel.validate();

			// Render the center component: remove the current component (if
			// there
			// is one) and then put the visible component in the center of this
			// panel
			if (this.visibleComponent != null) {
				this.remove(this.visibleComponent);
			}
			this.visibleComponent = barInfo.getComponent();
			this.add(visibleComponent, BorderLayout.CENTER);

			// Render the bottom bars: remove all components, reset the
			// GridLayout
			// to
			// hold to correct number of bars, add the bars, and "validate" it
			// to
			// cause it to re-layout its components
			this.bottomPanel.removeAll();
			GridLayout bottomLayout = (GridLayout) this.bottomPanel.getLayout();
			if (direction == Direction.HORIZONTAL) {
				bottomLayout.setRows(bottomBars);
			} else {
				bottomLayout.setColumns(bottomBars);
			}
			for (int i = 0; i < bottomBars; i++) {
				barInfo = itr.next();
				this.bottomPanel.add(barInfo.getButton());
			}
			this.bottomPanel.validate();
		}

		// Validate all of our components: cause this container to re-layout its
		// subcomponents
		validate();
	}

	/**
	 * Invoked when one of our bars is selected
	 */
	public void actionPerformed(ActionEvent e) {

	}

	/**
	 * Internal class that maintains information about individual Outlook bars;
	 * specifically it maintains the following information:
	 * 
	 * name The name of the bar button The associated JButton for the bar
	 * component The component maintained in the Outlook bar
	 */
	private final class BarInfo {
		/**
		 * The name of this bar
		 */
		private String name;

		/**
		 * The JButton that implements the Outlook bar itself
		 */
		private JButton button;

		/**
		 * The component that is the body of the Outlook bar
		 */
		private JComponent component;

		/**
		 * Creates a new BarInfo
		 * 
		 * @param name
		 *            The name of the bar
		 * @param icon
		 *            JButton icon
		 * @param component
		 *            The component that is the body of the Outlook Bar
		 */
		public BarInfo(String name, Icon icon, JComponent component) {
			this.name = name;
			this.component = component;
			this.button = new JDirectionButton(name, icon);
		}

		/**
		 * Returns the name of the bar
		 * 
		 * @return The name of the bar
		 */
		public String getName() {
			return this.name;
		}

		/**
		 * Sets the name of the bar
		 * 
		 * @param The
		 *            name of the bar
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * Returns the outlook bar JButton implementation
		 * 
		 * @return The Outlook Bar JButton implementation
		 */
		public JButton getButton() {
			return this.button;
		}

		/**
		 * Returns the component that implements the body of this Outlook Bar
		 * 
		 * @return The component that implements the body of this Outlook Bar
		 */
		public JComponent getComponent() {
			return this.component;
		}
	}

	private final class JDirectionButton extends JButton {

		private static final long serialVersionUID = -5713338830104801836L;

		private boolean needsRotate;

		public JDirectionButton(String name, Icon icon) {
			super(name, icon);
		}

		public Dimension getPreferredSize() {
			// swap size for vertical alignments
			switch (direction) {
			case VERTICAL_UP:
			case VERTICAL_DOWN:
				return new Dimension(super.getPreferredSize().height, super
						.getPreferredSize().width);
			default:
				return super.getPreferredSize();
			}
		}

		public Dimension getSize() {
			if (!needsRotate) {
				return super.getSize();
			}
			Dimension size = super.getSize();
			switch (direction) {
			case VERTICAL_DOWN:
			case VERTICAL_UP:
				return new Dimension(size.height, size.width);
			default:
				return super.getSize();
			}
		}

		public int getHeight() {
			return getSize().height;
		}

		public int getWidth() {
			return getSize().width;
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D gr = (Graphics2D) g.create();
			switch (direction) {
			case VERTICAL_UP:
				gr.translate(0, getSize().getHeight());
				gr.transform(AffineTransform.getQuadrantRotateInstance(-1));
				break;
			case VERTICAL_DOWN:
				gr.transform(AffineTransform.getQuadrantRotateInstance(1));
				gr.translate(0, -getSize().getWidth());
				break;
			default:
			}
			needsRotate = true;
			super.paintComponent(gr);
			needsRotate = false;
		}
	}

	private final class MyButtonActionListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			int currentBar = -1;
			int i = 0;
			while (i < barList.size() && currentBar == -1) {
				BarInfo barInfo = barList.get(i);
				if (barInfo.getButton() == e.getSource()) {
					currentBar = i;
				}
				i++;
			}

			setVisibleBar(currentBar);
		}
	}
}