package com.synaptix.swing.widget;

import javax.swing.JTextField;

/**
 * A {@link JTextField} with a maximum length and a "CAPS LOCK" mode where all text entered in the document is converted to upper case.
 * 
 * @author Nicolas Sauvage
 */
public class JSyTextField extends JTextField {

	private static final long serialVersionUID = -6745587132814875844L;

	/**
	 * Constructs a new JTextArea with a SyPlainDocument model, so that the length of the text is limited.
	 * 
	 * @param length
	 *            Max length of the field
	 */
	public JSyTextField(int length) {
		super(new SyPlainDocument(length), null, 0);
	}

	/**
	 * Constructs a new JTextArea with a maximum length.
	 * 
	 * @param length
	 *            Max length of the field. Setting this parameter to 0 will allow any length.
	 * @param capsLock
	 *            If set to true all text will be forced to uppercase. If false, the widget will behave like a default JTextField.
	 */
	public JSyTextField(int length, boolean capsLock) {
		super(new SyPlainDocument(length, capsLock), null, 0);
	}
}