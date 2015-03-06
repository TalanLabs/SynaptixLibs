package com.synaptix.widget.view.swing;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.validation.Severity;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.message.SimpleValidationMessage;
import com.synaptix.client.view.IView;
import com.synaptix.common.helper.CollectionHelper;
import com.synaptix.swing.utils.GenericObjectToString;
import com.synaptix.widget.renderer.view.swing.TypeGenericSubstanceListCellRenderer;
import com.synaptix.widget.util.StaticWidgetHelper;
import com.synaptix.widget.view.swing.dialog.AbstractSimpleDialog2;

public class ListFilterDialog<E> extends AbstractSimpleDialog2 {

	private static final long serialVersionUID = -6379379100104950593L;

	private GenericObjectToString<E> objectToString;

	private JLabel descriptionLabel;
	private JTextField filter;
	private JList jList;
	private MyListModel listModel;

	private boolean isCaseSensitive;
	private boolean isMultiSel;

	public ListFilterDialog(GenericObjectToString<E> objectToString) {
		super();

		this.objectToString = objectToString;

		initComponents();

		initialize();
	}

	private void initComponents() {
		this.descriptionLabel = new JLabel(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().enterSearchText());
		this.filter = new JTextField();
		this.listModel = new MyListModel();
		this.jList = new JList(listModel);

		// Renderer
		jList.setCellRenderer(new TypeGenericSubstanceListCellRenderer<E>(objectToString));

		// Listener
		filter.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				listModel.updateSubList(filter.getText());
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				listModel.updateSubList(filter.getText());
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				listModel.updateSubList(filter.getText());
			}
		});
		MyListener listener = new MyListener();
		jList.addListSelectionListener(listener);
		jList.addMouseListener(listener);
	}

	public void setCellRenderer(ListCellRenderer cellRenderer) {
		jList.setCellRenderer(cellRenderer);
	}

	@Override
	protected JComponent buildEditorPanel() {
		FormLayout layout = new FormLayout("FILL:220DLU:GROW(1.0)", //$NON-NLS-1$
				"FILL:PREF,CENTER:3DLU:NONE,FILL:PREF,CENTER:3DLU:NONE,FILL:160DLU:GROW(1.0)"); //$NON-NLS-1$
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.add(descriptionLabel, cc.xy(1, 1));
		builder.add(filter, cc.xy(1, 3));
		builder.add(new JScrollPane(jList), cc.xy(1, 5));
		return builder.getPanel();
	}

	/**
	 * Opens the default search dialog (not case sensitive, no multi selection)
	 * 
	 * @param parent
	 * @param title
	 *            The title of the search dialog
	 * @param list
	 *            The list in which the user has to select items
	 * @return
	 */
	public int showDialog(IView parent, String title, List<E> list) {
		return showDialog(parent, title, list, false, false);
	}

	/**
	 * Opens the search dialog
	 * 
	 * @param parent
	 * @param title
	 *            The title of the search dialog
	 * @param list
	 *            The list in which the user has to select items
	 * @param isCaseSensitive
	 *            true if the search has to be case sensitive, false otherwise
	 * @param isMultiSelection
	 *            true if the user can select many items, false otherwise
	 * @return
	 */
	public int showDialog(IView parent, String title, List<E> list, boolean isCaseSensitive, boolean isMultiSelection) {
		this.isCaseSensitive = isCaseSensitive;
		this.isMultiSel = isMultiSelection;
		listModel.setList(list);
		listModel.updateSubList(filter.getText());
		return super.showDialog(parent, title, null);
	}

	@Override
	protected void updateValidation(ValidationResult result) {
		if (getValues() == null) {
			result.add(new SimpleValidationMessage(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().pleaseSelectItem(), Severity.ERROR));
		}
	}

	@Override
	protected void openDialog() {
		if (isMultiSel) {
			jList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		} else {
			jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		}
	}

	/**
	 * If the user cancels the selection, we empty the result.
	 */
	@Override
	protected void cancel() {
		jList.getSelectionModel().clearSelection();
	}

	/**
	 * The value choosen by the user
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public E getValue() {
		if (jList.getSelectedIndices().length > 0) {
			return (E) this.jList.getSelectedValue();
		}
		return null;
	}

	/**
	 * The values choosen by the user, in case of a multiselection mode
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<E> getValues() {
		List<E> values = null;
		if (jList.getSelectedIndices().length > 0) {
			values = (List<E>) CollectionHelper.asList(this.jList.getSelectedValues());
		}
		return values;
	}

	private class MyListModel extends DefaultListModel {

		private static final long serialVersionUID = 2527516828575891190L;

		private List<E> list;
		private List<E> subList;

		private void setList(List<E> list) {
			this.list = list;
			this.subList = list;
		}

		@SuppressWarnings("unchecked")
		private void updateSubList(String filterText) {
			E[] selected = null;
			if (jList.getSelectedIndices().length > 0) {
				selected = (E[]) jList.getSelectedValues();
			}

			this.subList = new ArrayList<E>();
			if (list != null) {
				if ((filterText == null) || (filterText.isEmpty())) {
					subList.addAll(list);
				} else {
					Pattern patt = convertFilterToPattern(filterText);
					for (E e : list) {
						String toS = objectToString.getString(e);
						if ((toS != null) && (patt.matcher(toS).matches())) {
							subList.add(e);
						}
					}
				}
			}
			fireContentsChanged(this, 0, CollectionHelper.size(list));
			// does the new list contains some of the previous selected items?
			List<Integer> indexes = new ArrayList<Integer>();
			if (selected != null) {
				for (E sel : selected) {
					if (subList.contains(sel)) {
						indexes.add(subList.indexOf(sel));
					}
				}
			}
			if (!indexes.isEmpty()) {
				// if still has some items, we select them
				int[] indices = new int[indexes.size()];
				for (int i = 0; i < indexes.size(); i++) {
					indices[i] = indexes.get(i);
				}
				jList.setSelectedIndices(indices);
			} else {
				// else, we clear the selection
				jList.getSelectionModel().clearSelection();
			}
			updateValidation();
		}

		/**
		 * Converts the filter to a pattern. Also depends on the case sensitivity parameter
		 * 
		 * @param filterText
		 * @return Corresponding pattern
		 */
		private Pattern convertFilterToPattern(String filterText) {
			String regex = filterText;
			int flags = 0;
			// neutralisation
			regex = regex.replaceAll("\\.", "\\\\\\.");
			// evolution for jokers
			regex = regex.replaceAll("\\*", ".*").replaceAll("\\?", ".");
			// case sensitivity
			if (!isCaseSensitive) {
				flags = Pattern.CASE_INSENSITIVE;
			}
			// searches even if doesn't end the same way
			regex = regex.concat(".*");
			Pattern patt = Pattern.compile(regex, flags);
			return patt;
		}

		@Override
		public int getSize() {
			return CollectionHelper.size(subList);
		}

		@Override
		public E getElementAt(int index) {
			if (index >= getSize()) {
				return null;
			}
			return subList.get(index);
		}
	}

	private class MyListener extends MouseAdapter implements ListSelectionListener {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (!e.getValueIsAdjusting()) {
				updateValidation();
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if ((e.getClickCount() >= 2) && (!isMultiSel)) {
				acceptAction.actionPerformed(null);
			}
		}
	}
}
