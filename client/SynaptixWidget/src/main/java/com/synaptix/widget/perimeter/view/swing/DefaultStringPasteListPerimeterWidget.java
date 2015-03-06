package com.synaptix.widget.perimeter.view.swing;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.lang.StringUtils;

import com.synaptix.common.util.IResultCallback;
import com.synaptix.swing.utils.ClipboardHelper;
import com.synaptix.swing.widget.JSyTextField;

/**
 * A default perimeter widget which can add several strings.<br/>
 * It is editable and checks unicity (when editing, reverts to old value if it is already contained).
 * 
 * @author Nicolas P
 * 
 */
public class DefaultStringPasteListPerimeterWidget extends AbstractStringPasteListPerimeterWidget {

	private static final long serialVersionUID = -6403106919452690785L;

	private final int length;

	private final boolean capsLock;

	private JTextField textField;

	private IResultCallback<String> resultCallback;

	private String oldValue;

	public DefaultStringPasteListPerimeterWidget(String title) {
		this(title, 0, false);
	}

	public DefaultStringPasteListPerimeterWidget(String title, int length, boolean capsLock) {
		super(title, true);

		this.length = length;
		this.capsLock = capsLock;

		initialize();

		listenerList.add(PerimeterWidgetListener.class, new PerimeterWidgetListener() {

			@Override
			public void valuesChanged(IPerimeterWidget source) {
				resultCallback = null;
			}

			@Override
			public void titleChanged(IPerimeterWidget source) {
			}
		});
	}

	@Override
	protected List<String> createObjectsToString(String s) {
		if (s == null) {
			return null;
		}
		String[] split = s.split("\n");
		return Arrays.asList(split);
	}

	@Override
	protected JComponent getInputComponent() {
		if (textField == null) {
			textField = buildTextField(length, capsLock);
			ClipboardHelper.installClipboardPasteListener(textField);

			textField.addKeyListener(new KeyAdapter() {

				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						if (StringUtils.isNotBlank(textField.getText())) {
							if (resultCallback != null) {
								if (hasManyValue(textField.getText())) {
									resultCallback.setResult(oldValue);
								} else {
									updateCallback();
								}
								oldValue = null;
								resultCallback = null;
							} else {
								List<String> list = new ArrayList<String>(1);
								list.add(textField.getText());
								addObjects(list);
							}
							textField.setText(null);
						}
					} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
						resultCallback = null;
					}
				}
			});
			textField.getDocument().addDocumentListener(new DocumentListener() {

				@Override
				public void removeUpdate(DocumentEvent e) {
					updateCallback();
				}

				@Override
				public void insertUpdate(DocumentEvent e) {
					updateCallback();
				}

				@Override
				public void changedUpdate(DocumentEvent e) {
					updateCallback();
				}
			});
		}
		return textField;
	}

	protected JTextField buildTextField(int length, boolean capsLock) {
		return new JSyTextField(length, capsLock);
	}

	private void updateCallback() {
		if (resultCallback != null) {
			resultCallback.setResult(textField.getText());
		}
	}

	@Override
	protected void add(IResultCallback<List<String>> resultCallback) {
		if (StringUtils.isNotBlank(textField.getText())) {
			List<String> list = new ArrayList<String>(1);
			list.add(textField.getText());
			resultCallback.setResult(list);
		} else {
			resultCallback.setResult(null);
		}
		textField.setText(null);
	}

	@Override
	protected void edit(String value, IResultCallback<String> resultCallback) {
		this.resultCallback = resultCallback;
		this.oldValue = value;
		textField.setText(value);
		textField.requestFocus();
		resultCallback.setResult(null);
	}
}
