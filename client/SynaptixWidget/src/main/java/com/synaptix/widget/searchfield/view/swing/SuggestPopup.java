package com.synaptix.widget.searchfield.view.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.swing.AWTUtilitiesWrapper;
import com.synaptix.swing.utils.GenericObjectToString;
import com.synaptix.widget.renderer.view.swing.TypeGenericSubstanceListCellRenderer;
import com.synaptix.widget.util.StaticWidgetHelper;

class SuggestPopup<E> extends JPanel {

	private static final long serialVersionUID = -3215980893407236319L;

	private HierarchyBoundsListener hierachyBoundsListener;

	private ComponentListener componentListener;

	private final Component field;

	private final MyGenericObjectToString<E> objectToString;

	private Window parent;

	private JWindow window;

	private DefaultListModel listModel;

	private JList list;

	public SuggestPopup(Component field, GenericObjectToString<E> objectToString) {
		super(new BorderLayout());

		this.field = field;
		this.objectToString = new MyGenericObjectToString<E>(objectToString);

		initComponents();

		this.add(buildContents(), BorderLayout.CENTER);
	}

	private void initComponents() {
		this.componentListener = new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				computeLocation();
			}

			@Override
			public void componentHidden(ComponentEvent e) {
				computeLocation();
			}
		};

		this.hierachyBoundsListener = new HierarchyBoundsListener() {
			@Override
			public void ancestorMoved(HierarchyEvent e) {
				computeLocation();
			}

			@Override
			public void ancestorResized(HierarchyEvent e) {
				computeLocation();
			}
		};

		listModel = new DefaultListModel();
		list = new JList(listModel);
		list.setFocusCycleRoot(false);
		list.setFocusable(false);
		list.setCellRenderer(new TypeGenericSubstanceListCellRenderer<E>(objectToString));
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1 && list.locationToIndex(e.getPoint()) != -1) {
					if (list.getSelectedValue() == null) {
						fireShowAllActionPerfomed();
					} else {
						fireClickActionPerfomed();
					}
				}
			}
		});
		list.setOpaque(true);
	}

	private JComponent buildContents() {
		FormLayout layout = new FormLayout("FILL:150DLU", "FILL:DEFAULT");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.add(list, cc.xy(1, 1));
		builder.setBorder(BorderFactory.createLineBorder(new Color(0x777777), 1));
		return builder.getPanel();
	}

	public void addClickActionListener(ClickActionListener l) {
		listenerList.add(ClickActionListener.class, l);
	}

	public void removeClickActionListener(ClickActionListener l) {
		listenerList.remove(ClickActionListener.class, l);
	}

	protected void fireClickActionPerfomed() {
		ActionEvent ae = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "click");
		ClickActionListener[] cals = listenerList.getListeners(ClickActionListener.class);
		if (cals != null && cals.length > 0) {
			for (ClickActionListener cal : cals) {
				cal.actionPerformed(ae);
			}
		}
	}

	interface ClickActionListener extends ActionListener {

	}

	public void addShowAllActionListener(ShowAllActionListener l) {
		listenerList.add(ShowAllActionListener.class, l);
	}

	public void removeShowAllActionListener(ShowAllActionListener l) {
		listenerList.remove(ShowAllActionListener.class, l);
	}

	protected void fireShowAllActionPerfomed() {
		ActionEvent ae = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "showAll");
		ShowAllActionListener[] cals = listenerList.getListeners(ShowAllActionListener.class);
		if (cals != null && cals.length > 0) {
			for (ShowAllActionListener cal : cals) {
				cal.actionPerformed(ae);
			}
		}
	}

	interface ShowAllActionListener extends ActionListener {

	}

	protected void computeLocation() {
		if (field != null && window != null) {
			boolean visible = true;
			Component c = field.getParent();
			Point point = new Point(0, 0);
			while (c != null && visible) {
				Point p = SwingUtilities.convertPoint(field, point, c);
				visible = p.x >= 0 && p.y >= 0 && p.x + field.getWidth() <= c.getWidth() && p.y + field.getHeight() <= c.getHeight();
				c = c.getParent();
			}

			if (visible) {
				point = new Point(getPosition());
				SwingUtilities.convertPointToScreen(point, field);
				window.setLocation(point);
				if (!window.isVisible()) {
					window.setVisible(true);
				}
			} else if (window.isVisible()) {
				window.setVisible(false);
			}
		}
	}

	protected Point getPosition() {
		return new Point(0, field.getHeight());
	}

	public void showWindow(String name, List<E> values) {
		// hideWindow();

		listModel.clear();

		objectToString.setName(name);

		for (E value : values) {
			listModel.addElement(value);
		}
		listModel.addElement(null);
		list.setSelectedIndex(0);

		field.addHierarchyBoundsListener(hierachyBoundsListener);

		if (parent == null) {
			Component c = field;
			while (!(c instanceof Window) && c != null) {
				c = c.getParent();
			}
			parent = (Window) c;
		}

		if (window == null) {
			window = new JWindow(parent);
			window.addComponentListener(componentListener);
			AWTUtilitiesWrapper.setWindowOpaque(window, false);
			window.getContentPane().add(this);
			window.pack();
			computeLocation();
			window.setFocusable(false);
			window.setFocusableWindowState(false);
			window.setVisible(true);
		} else {
			window.pack();
		}
	}

	public void hideWindow() {
		if (window != null) {
			window.setVisible(false);
			window.removeComponentListener(componentListener);
			window = null;
			field.removeHierarchyBoundsListener(hierachyBoundsListener);
		}
	}

	public boolean isWindowVisible() {
		return window != null && window.isVisible();
	}

	public void downElement() {
		int index = list.getSelectedIndex();
		if (index == -1 || index == listModel.size() - 1) {
			index = 0;
		} else {
			index++;
		}
		list.setSelectedIndex(index);
	}

	public void upElement() {
		int index = list.getSelectedIndex();
		if (index == -1 || index == 0) {
			index = listModel.size() - 1;
		} else {
			index--;
		}
		list.setSelectedIndex(index);
	}

	@SuppressWarnings("unchecked")
	public E getSelectedValue() {
		return (E) list.getSelectedValue();
	}

	private final class MyGenericObjectToString<F> implements GenericObjectToString<F> {

		private final GenericObjectToString<F> genericObjectToString;

		private String name;

		public MyGenericObjectToString(GenericObjectToString<F> genericObjectToString) {
			super();
			this.genericObjectToString = genericObjectToString;
		}

		public void setName(String name) {
			this.name = name;
		}

		@Override
		public String getString(F t) {
			if (t != null) {
				String v = genericObjectToString.getString(t);
				StringBuilder sb = new StringBuilder();
				sb.append("<html>");
				int i = 0;
				while (v != null && i < v.length()) {
					if (v.regionMatches(true, i, name, 0, name.length())) {
						sb.append("<b>").append(v.substring(i, i + name.length())).append("</b>");
						i += name.length();
					} else {
						sb.append(v.charAt(i));
						i++;
					}
				}
				sb.append("</html>");
				return sb.toString();
			} else {
				return StaticWidgetHelper.getSynaptixWidgetConstantsBundle().showAllItems();
			}
		}
	}
}
