package com.synaptix.widget.view.swing;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * A plain document with a maximum length. If a text that is too long is pasted in this document, it will be cropped.
 * 
 * @author E407780
 * @deprecated user SyPlainDocument
 */
@Deprecated
public class PlainDocumentMaxLength extends PlainDocument {

	private static final long serialVersionUID = -1756821629879111654L;
	private int maxLength;

	/**
	 * Constructs a PlainDocumentMaxLength
	 * 
	 * @param maxLength
	 *            The maximum length of the text
	 */
	public PlainDocumentMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	@Override
	public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
		if (getLength() + str.length() > maxLength) {
			str = str.substring(0, maxLength - getLength());
		}
		super.insertString(offset, str, a);
	}
}