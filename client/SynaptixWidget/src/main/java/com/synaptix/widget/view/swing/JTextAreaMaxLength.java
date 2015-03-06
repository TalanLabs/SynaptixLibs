package com.synaptix.widget.view.swing;
import javax.swing.JTextArea;

import com.synaptix.swing.widget.SyPlainDocument;

/**
 * A JTextArea with maximum length
 * 
 * @author E407780
 * 
 */
public class JTextAreaMaxLength extends JTextArea {
	private static final long serialVersionUID = -2884257982101175611L;

	/**
	 * Constructs a new JTextArea with the PlainDocumentMaxLength model, so that
	 * the length of the text is limited .
	 * 
	 * @param length
	 *            Maximum length of the text
	 */
	public JTextAreaMaxLength(int length) {
		super(new SyPlainDocument(length));
	}

	/**
	 * Constructs a new JTextArea with the specified number of rows and columns,
	 * and the PlainDocumentMaxLength model.
	 * 
	 * @param length
	 *            Maximum length of the text
	 * @param text
	 *            the text to be displayed, null if none
	 * @param rows
	 *            the number of rows >= 0
	 * @param columns
	 *            the number of rows >= 0
	 */
	public JTextAreaMaxLength(int length, String text, int rows, int columns) {
		super(new SyPlainDocument(length), text, rows, columns);
	}
}