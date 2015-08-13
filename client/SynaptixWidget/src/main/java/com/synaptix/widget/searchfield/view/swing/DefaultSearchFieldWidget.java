package com.synaptix.widget.searchfield.view.swing;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.lang.StringUtils;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.client.view.IView;
import com.synaptix.common.util.IResultCallback;
import com.synaptix.swing.JRemoveLabel;
import com.synaptix.swing.WaitComponentFeedbackPanel;
import com.synaptix.swing.tooltip.IToolTippableComponent;
import com.synaptix.swing.tooltip.ToolTipFocusListener;
import com.synaptix.swing.utils.GenericObjectToString;
import com.synaptix.swing.widget.JSyTextField;
import com.synaptix.widget.actions.view.swing.AbstractRemoveAction;
import com.synaptix.widget.actions.view.swing.AbstractSearchAction;
import com.synaptix.widget.searchfield.context.ISearchFieldWidgetContext;
import com.synaptix.widget.searchfield.view.swing.SuggestPopup.ClickActionListener;
import com.synaptix.widget.searchfield.view.swing.SuggestPopup.ShowAllActionListener;
import com.synaptix.widget.util.StaticWidgetHelper;
import com.synaptix.widget.view.swing.helper.StaticImage;

public class DefaultSearchFieldWidget<E> extends WaitComponentFeedbackPanel implements IView, IToolTippableComponent {

	private static final long serialVersionUID = -1796801499038427700L;

	private JTextField nameField;

	private Action searchButtonAction;

	private Action searchAction;

	private JRemoveLabel removeLabel;

	private E value;

	private boolean editable;

	private final ISearchFieldWidgetContext<E> searchFieldWidgetContext;

	private final GenericObjectToString<E> objectToString;

	private String promptText;

	private ToolTipFocusListener toolTipFocusListener;

	private final boolean withRemoveLabel;

	private JButton button;

	private Action removeAction;

	private DocumentListener nameDocumentListener;

	private IResultCallback<E> searchCallback;

	private IResultCallback<List<E>> suggestCallback;

	private SuggestPopup<E> suggestPopup;

	private boolean forceUppercase;

	/**
	 * Constructor for a search field widget. Not editable and without separate remove button
	 */
	public DefaultSearchFieldWidget(ISearchFieldWidgetContext<E> searchFieldWidgetContext, GenericObjectToString<E> objectToString) {
		this(searchFieldWidgetContext, objectToString, false, null, false);
	}

	/**
	 * Constructor for a search field widget. Without separate remove button
	 *
	 * @param editable
	 *            If set to true, the widget can be edited.
	 * @param promptText
	 *            Text displayed below the field while the user is editing. Used only if editable is true.
	 * @param forceUppercase
	 *            If set to true all text will be forced to uppercase. If false, the widget will behave like a default JTextField.
	 */
	public DefaultSearchFieldWidget(ISearchFieldWidgetContext<E> searchFieldWidgetContext, GenericObjectToString<E> objectToString, boolean editable, String promptText, boolean forceUppercase) {
		this(searchFieldWidgetContext, objectToString, editable, promptText, false, forceUppercase);
	}

	/**
	 * Constructor for a search field widget.
	 *
	 * @param editable
	 *            If set to true, the widget can be edited.
	 * @param promptText
	 *            Text displayed below the field while the user is editing. Used only if editable is true.
	 * @param withRemoveLabel
	 *            If false, the indicator label and the search button are replaced by a single button with two states.
	 * @param forceUppercase
	 *            If set to true all text will be forced to uppercase. If false, the widget will behave like a default JTextField.
	 */
	public DefaultSearchFieldWidget(ISearchFieldWidgetContext<E> searchFieldWidgetContext, GenericObjectToString<E> objectToString, boolean editable, String promptText, boolean withRemoveLabel,
			boolean forceUppercase) {
		super();

		this.searchFieldWidgetContext = searchFieldWidgetContext;
		this.objectToString = objectToString;
		this.editable = editable;
		this.promptText = promptText;
		this.withRemoveLabel = withRemoveLabel;
		this.forceUppercase = forceUppercase;

		initComponents();

		this.addContent(buildContents());
	}

	/**
	 * Constructor for a search field widget. (No max length, no caps lock, no remove label)
	 *
	 * @param editable
	 *            If set to true, the widget can be edited.
	 * @param promptText
	 *            Text displayed below the field while the user is editing. Used only if editable is true.
	 */
	public DefaultSearchFieldWidget(ISearchFieldWidgetContext<E> searchFieldWidgetContext, GenericObjectToString<E> objectToString, boolean editable, String promptText) {
		this(searchFieldWidgetContext, objectToString, editable, promptText, false, false);
	}

	private void initComponents() {
		searchButtonAction = new SearchButtonAction();
		searchAction = new SearchAction();

		nameField = new JSyTextField(0, forceUppercase);
		nameField.addActionListener(searchAction);
		nameField.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if ((e.getClickCount() == 2) && (e.getButton() == MouseEvent.BUTTON3)) {
					if (isEnabled()) {
						removeValue();
						fireChangeListener();
					}
				}
			}
		});
		nameField.setEditable(editable);

		if (editable) {
			nameDocumentListener = new NameDocumentListener();
			nameField.getDocument().addDocumentListener(nameDocumentListener);
			nameField.setFocusTraversalKeysEnabled(false);
			nameField.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					if (!e.isConsumed()) {
						if (e.getKeyCode() == KeyEvent.VK_DOWN) {
							if (suggestPopup.isWindowVisible()) {
								suggestPopup.downElement();
								e.consume();
							}
						} else if (e.getKeyCode() == KeyEvent.VK_UP) {
							if (suggestPopup.isWindowVisible()) {
								suggestPopup.upElement();
								e.consume();
							}
						} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
							if (suggestPopup.isWindowVisible()) {
								hideSuggestBox();
								e.consume();
							}
							if (getValue() != null) {
								removeValue();
								fireChangeListener();
								e.consume();
							}
						} else if (e.getKeyCode() == KeyEvent.VK_TAB && e.isShiftDown()) {
							if (suggestPopup.isWindowVisible()) {
								boolean valueSelected = chooseSelectedSuggestBox();
								if (valueSelected) {
									button.transferFocusBackward();
									e.consume();
								}
							} else {
								nameField.transferFocusBackward();
								e.consume();
							}
						} else if (e.getKeyCode() == KeyEvent.VK_TAB) {
							if (chooseSelectedSuggestBox()) {
								button.setFocusable(true);
								button.transferFocus();
							} else {
								if ((e.isControlDown()) || (!isTextValid())) {
									button.setFocusable(true);
								} else {
									button.setFocusable(false);
								}
								nameField.transferFocus();
							}
							e.consume();
						}
					}
				}
			});
			nameField.addFocusListener(new FocusAdapter() {
				@Override
				public void focusLost(final FocusEvent e) {
					if (suggestPopup.isWindowVisible()) {
						hideSuggestBox();
					}
					paintNameFieldBackground();
				}

				@Override
				public void focusGained(FocusEvent e) {
					nameField.selectAll();
				}
			});

			suggestPopup = new SuggestPopup<E>(nameField, objectToString);
			suggestPopup.addClickActionListener(new ClickActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					chooseSelectedSuggestBox();
				}
			});
			suggestPopup.addShowAllActionListener(new ShowAllActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String text = nameField.getText();
					search(text);
				}
			});

			if (promptText != null && !promptText.isEmpty()) {
				toolTipFocusListener = new ToolTipFocusListener(promptText);
				nameField.addFocusListener(toolTipFocusListener);
			}
		}

		if (withRemoveLabel) {
			removeLabel = new JRemoveLabel();
			removeLabel.showErrorIcon(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().noneSelected());
			removeLabel.addActionListener(new RemoveActionListener());
		}

		button = new JButton(searchButtonAction);
		button.setFocusable(false);
		button.registerKeyboardAction(button.getActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false)), KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), JComponent.WHEN_FOCUSED);
		button.registerKeyboardAction(button.getActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true)), KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), JComponent.WHEN_FOCUSED);

		ImageIcon eraseIcon = StaticImage.getImageScale(new ImageIcon(AbstractRemoveAction.class.getResource("iconClearBig.png")), 16); //$NON-NLS-1$
		removeAction = new AbstractAction(null, eraseIcon) {

			private static final long serialVersionUID = 4102703144330237406L;

			@Override
			public void actionPerformed(ActionEvent e) {
				removeValue();
				fireChangeListener();
			}
		};
		removeAction.putValue(Action.SHORT_DESCRIPTION, StaticWidgetHelper.getSynaptixWidgetConstantsBundle().clear());
	}

	private JComponent buildContents() {
		FormLayout layout;
		if (withRemoveLabel) {
			layout = new FormLayout("FILL:80DLU:grow,FILL:2DLU:NONE,FILL:20DLU:NONE,FILL:26DLU:NONE", //$NON-NLS-1$
					"CENTER:DEFAULT:NONE"); //$NON-NLS-1$
		} else {
			layout = new FormLayout("FILL:80DLU:grow,FILL:2DLU:NONE,FILL:20DLU:NONE", //$NON-NLS-1$
					"CENTER:DEFAULT:NONE"); //$NON-NLS-1$
		}
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.add(nameField, cc.xy(1, 1));
		builder.add(button, cc.xy(3, 1));
		if (withRemoveLabel) {
			builder.add(removeLabel, cc.xy(4, 1));
		}
		return builder.getPanel();
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

	protected String valueToString(E value) {
		return objectToString.getString(value);
	}

	public E getValue() {
		return value;
	}

	public void setValue(E value) {
		E oldValue = this.value;
		if (value != null) {
			nameField.getDocument().removeDocumentListener(nameDocumentListener);

			this.value = value;

			nameField.setText(valueToString(value));

			nameField.setEditable(false);
			if (toolTipFocusListener != null) {
				toolTipFocusListener.setEnabled(false);
			}

			nameField.setCaretPosition(0);

			button.setFocusable(true);
			nameField.setFocusable(false);

			nameField.setEditable(editable && value == null && isEnabled());

			button.transferFocus();

			updateRemoveLabel();

			nameField.getDocument().addDocumentListener(nameDocumentListener);
			firePropertyChange("value", oldValue, this.value);
		} else {
			removeValue();
		}

		paintNameFieldBackground();
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.nameField.setEditable(editable && value == null && enabled);
		setEnabledNotEditable(enabled);
	}

	private void setEnabledNotEditable(boolean enabled) {
		this.button.setEnabled(enabled);
		this.removeAction.setEnabled(enabled);
		this.searchAction.setEnabled(enabled);
		this.searchButtonAction.setEnabled(enabled);
		if (this.toolTipFocusListener != null) {
			this.toolTipFocusListener.setEnabled(editable && enabled && value == null);
		}
		super.setEnabled(enabled);
	}

	private void updateRemoveLabel() {
		if (withRemoveLabel) {
			if (nameField.getText().equals("")) { //$NON-NLS-1$
				removeLabel.showErrorIcon(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().noneSelected());
			} else {
				removeLabel.showValidateIcon(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().doubleClickToClear());
			}
		} else {
			if (nameField.getText().equals("")) { //$NON-NLS-1$
				button.setAction(searchButtonAction);
			} else {
				button.setAction(removeAction);
			}
		}
	}

	private void removeValue() {
		E oldValue = this.value;
		this.value = null;

		nameField.setText(null);
		nameField.setEditable(editable && isEnabled());
		nameField.setFocusable(editable && isEnabled());
		if (button.hasFocus()) {
			nameField.grabFocus();
		}
		if (toolTipFocusListener != null) {
			toolTipFocusListener.setEnabled(editable && isEnabled());
		}
		paintNameFieldBackground();

		firePropertyChange("value", oldValue, this.value);
		updateRemoveLabel();
	}

	private void suggest() {
		String text = nameField.getText();

		if (text != null && text.length() > 0) {
			suggestCallback = createSuggestCallback();
			searchFieldWidgetContext.suggest(text, suggestCallback);
		} else {
			hideSuggestBox();
		}
	}

	private final class RemoveActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			removeValue();
			fireChangeListener();
		}
	}

	protected IResultCallback<E> createSearchCallback() {
		return new IResultCallback<E>() {
			@Override
			public void setResult(E value) {
				if (searchCallback != null && searchCallback == this) {
					setValue(value);
					fireChangeListener();
				}
			}
		};
	}

	private boolean chooseSelectedSuggestBox() {
		if ((suggestPopup == null) || (!suggestPopup.isWindowVisible())) {
			return false;
		}
		E value = suggestPopup.getSelectedValue();
		if (value != null) {
			searchFieldWidgetContext.convert(value, new IResultCallback<E>() {
				@Override
				public void setResult(E e) {
					if (e != null) {
						setValue(e);

						button.transferFocus();

						fireChangeListener();

						hideSuggestBox();
					}
				}
			});
			return true;
		}
		return false;
	}

	private void paintNameFieldBackground() {
		if ((!isTextValid()) && (!nameField.hasFocus())) {
			nameField.setBackground(new Color(255, 130, 130));
		} else {
			nameField.setBackground(null);
		}
	}

	private boolean isTextValid() {
		return value != null || StringUtils.isBlank(nameField.getText());
	}

	private void hideSuggestBox() {
		suggestCallback = null;
		suggestPopup.hideWindow();

		toolTipFocusListener.setEnabled(isEnabled() && editable);
	}

	protected void search(String text) {
		searchCallback = createSearchCallback();
		searchFieldWidgetContext.search(text != null && !text.isEmpty() ? text : null, searchCallback);
	}

	@Override
	public JComponent getToolTipableComponent() {
		return nameField;
	}

	protected IResultCallback<List<E>> createSuggestCallback() {
		return new IResultCallback<List<E>>() {
			@Override
			public void setResult(List<E> values) {
				if (suggestCallback != null && suggestCallback == this && DefaultSearchFieldWidget.this.value == null) {
					if (values != null && !values.isEmpty()) {
						toolTipFocusListener.hideWindow();
						toolTipFocusListener.setEnabled(false);

						if (nameField.hasFocus()) {
							suggestPopup.showWindow(nameField.getText(), values);
						}
					} else {
						hideSuggestBox();
					}
				}
			}
		};
	}

	/**
	 * Get the current text on the widget
	 */
	public final String getText() {
		return nameField.getText();
	}

	private final class SearchAction extends AbstractSearchAction {

		private static final long serialVersionUID = -398767435122391168L;

		@Override
		public void actionPerformed(ActionEvent e) {
			if (value == null) {
				if (!chooseSelectedSuggestBox()) {
					String text = nameField.getText();
					search(text);
				}
			}
		}
	}

	private final class SearchButtonAction extends AbstractSearchAction {

		private static final long serialVersionUID = -398767435122391168L;

		@Override
		public void actionPerformed(ActionEvent e) {
			if (suggestPopup != null && suggestPopup.isWindowVisible()) {
				hideSuggestBox();
			}
			String text = nameField.getText();
			search(text);
		}
	}

	private final class NameDocumentListener implements DocumentListener {

		@Override
		public void insertUpdate(DocumentEvent e) {
			suggest();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			if (StringUtils.isBlank(nameField.getText())) {
				paintNameFieldBackground();
			}
			suggest();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			suggest();
		}
	}
}
