package com.synaptix.widget.perimeter.view.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jdesktop.swingx.VerticalLayout;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.swing.utils.GenericObjectToString;
import com.synaptix.swing.utils.ToolBarFactory;
import com.synaptix.widget.util.StaticWidgetHelper;
import com.synaptix.widget.view.swing.helper.StaticImage;

/**
 * List of checkboxes for a perimeter
 * 
 * @author Nicolas P
 * 
 * @param <E extends IComponent>
 */
public abstract class AbstractListCheckBoxPerimeterWidget<E> extends AbstractPerimeterWidget implements IPerimeterWidget {

	private static final long serialVersionUID = 8861063576317421383L;

	private static final Icon SELECT_ALL_ICON = StaticImage.getImageScale(new ImageIcon(AbstractListPerimeterWidget.class.getResource("/com/synaptix/widget/actions/view/swing/iconCheck.png")), 10); //$NON-NLS-1$

	private static final Icon UNSELECT_ALL_ICON = StaticImage.getImageScale(new ImageIcon(AbstractListPerimeterWidget.class.getResource("/com/synaptix/widget/actions/view/swing/iconRemove.png")), 10); //$NON-NLS-1$

	protected Action selectAllAction;

	protected Action unSelectAllAction;

	private MyActionListener actionListener;

	private String title;

	private int gap;

	private JPanel panel;

	private Map<E, JCheckBox> checkMap;

	public AbstractListCheckBoxPerimeterWidget(String title) {
		this(title, 0);
	}

	public AbstractListCheckBoxPerimeterWidget(String title, int gap) {
		super();

		this.title = title;
		this.gap = gap;

		initActions();
		initComponents();

		this.addContent(buildContents());
	}

	private void initActions() {
		selectAllAction = new SelectAllAction();
		selectAllAction.setEnabled(false);
		unSelectAllAction = new UnselectAllAction();
		unSelectAllAction.setEnabled(false);
	}

	private void initComponents() {
		checkMap = new HashMap<E, JCheckBox>();
		panel = new JPanel(new VerticalLayout(gap));
		actionListener = new MyActionListener();
	}

	private JComponent buildContents() {
		FormLayout layout = new FormLayout("FILL:MAX(75DLU;PREF):GROW(1.0)", //$NON-NLS-1$
				"CENTER:DEFAULT:NONE,FILL:DEFAULT:GROW(1.0)"); //$NON-NLS-1$
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.add(ToolBarFactory.buildToolBar(selectAllAction, unSelectAllAction), cc.xy(1, 1));
		builder.add(panel, cc.xy(1, 2));
		return builder.getPanel();
	}

	protected JPanel getPanel() {
		return panel;
	}

	@Override
	public String getTitle() {
		return title;
	}

	private final class SelectAllAction extends AbstractAction {

		private static final long serialVersionUID = 6086415459494068719L;

		public SelectAllAction() {
			super("", SELECT_ALL_ICON); //$NON-NLS-1$

			this.putValue(Action.SHORT_DESCRIPTION, StaticWidgetHelper.getSynaptixWidgetConstantsBundle().selectAll());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			for (Map.Entry<E, JCheckBox> entry : checkMap.entrySet()) {
				entry.getValue().setSelected(true);
			}
			selectAllAction.setEnabled(false);
			unSelectAllAction.setEnabled(true);

			fireValuesChanged();
		}
	}

	private final class UnselectAllAction extends AbstractAction {

		private static final long serialVersionUID = 3516871339960902689L;

		public UnselectAllAction() {
			super("", UNSELECT_ALL_ICON); //$NON-NLS-1$

			this.putValue(Action.SHORT_DESCRIPTION, StaticWidgetHelper.getSynaptixWidgetConstantsBundle().unselectAll());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			unselectAll();
		}
	}

	private void unselectAll() {
		for (Map.Entry<E, JCheckBox> entry : checkMap.entrySet()) {
			entry.getValue().setSelected(false);
		}
		selectAllAction.setEnabled(true);
		unSelectAllAction.setEnabled(false);

		updateView();

		fireValuesChanged();
	}

	/**
	 * Returns the selected components
	 * 
	 * @return
	 */
	public List<E> getSelectedValues() {
		List<E> selected = new ArrayList<E>();
		for (Map.Entry<E, JCheckBox> e : checkMap.entrySet()) {
			if (e.getValue().isSelected()) {
				selected.add(e.getKey());
			}
		}
		if (selected == null || selected.isEmpty()) {
			selected = null;
		}
		return selected;
	}

	@Override
	public void setValue(Object value) {
		if (value != null) {
			@SuppressWarnings("unchecked")
			List<E> values = (List<E>) value;
			if (checkMap != null) {
				for (Map.Entry<E, JCheckBox> e : checkMap.entrySet()) {
					if (values.contains(e.getKey())) {
						e.getValue().setSelected(true);
					} else {
						e.getValue().setSelected(false);
					}
				}
				updateView();

				fireValuesChanged();
			}
		} else {
			unselectAll();
		}
	}

	@Override
	public Object getValue() {
		return getSelectedValues();
	}

	public void setList(List<E> list, GenericObjectToString<E> toString) {
		panel.removeAll();

		checkMap.clear();
		if (list != null && !list.isEmpty()) {
			for (E e : list) {
				JCheckBox check = new JCheckBox(toString.getString(e));
				check.addActionListener(actionListener);
				checkMap.put(e, check);
				panel.add(check);
			}
			selectAllAction.setEnabled(true);
			unSelectAllAction.setEnabled(false);
		}
		updateView();

		panel.revalidate();
		panel.repaint();

		fireValuesChanged();
	}

	private final class MyActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			updateView();

			fireValuesChanged();
		}
	}

	private void updateView() {
		List<E> list = getSelectedValues();
		int count = list != null ? list.size() : 0;
		if (count > 0) {
			unSelectAllAction.setEnabled(true);
		} else {
			unSelectAllAction.setEnabled(false);
		}
		if (count == checkMap.size()) {
			selectAllAction.setEnabled(false);
		} else {
			selectAllAction.setEnabled(true);
		}
	}
}
