package com.synaptix.widget.view.swing;

import javax.swing.JTextField;

/**
 * 
 * @author E407780
 * @deprecated use JSyTextField
 */
@Deprecated
public class JTextFieldMaxLength extends JTextField {
	private static final long serialVersionUID = 7713858080022367477L;

	/**
	 * Constructs a new JTextArea with the PlainDocumentMaxLength model, so that the length of the text is limited .
	 * 
	 * @param length
	 */
	public JTextFieldMaxLength(int length) {
		super(new PlainDocumentMaxLength(length), null, 0);
	}

	public JTextFieldMaxLength(int length, boolean capsLock) {
		super(new PlainDocumentMaxLength(length), null, 0);
	}

}