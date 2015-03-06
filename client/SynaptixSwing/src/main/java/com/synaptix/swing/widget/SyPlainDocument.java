package com.synaptix.swing.widget;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * A custom plain document. Can be set to have a maximum length: if a text that is too long is pasted in this document, it will be cropped. Also has a "CAPS LOCK" mode where all text entered in the
 * document is converted to upper case.
 * 
 * @author Nicolas Sauvage
 */
public class SyPlainDocument extends PlainDocument {

	private static final long serialVersionUID = 3014349125742962922L;
	private int maxLength;
	private boolean capsLock;

	/**
	 * Constructs a SyPlainDocument with a maximum length.
	 * 
	 * @param maxLength
	 *            The maximum length of the text. 0 means that there is no maximum.
	 */
	public SyPlainDocument(int maxLength) {
		this.maxLength = maxLength;
		capsLock = false;
	}

	/**
	 * Constructs a SyPlainDocument with a maximum length.
	 * 
	 * @param maxLength
	 *            The maximum length of the text. 0 means that there is no maximum.
	 * @param capsLock
	 *            If set to true all text will be forced to uppercase.
	 */
	public SyPlainDocument(int maxLength, boolean capsLock) {
		this.maxLength = maxLength;
		this.capsLock = capsLock;
	}

	@Override
	public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
		if (maxLength > 0 && getLength() + str.length() > maxLength) {
			str = str.substring(0, maxLength - getLength());
		}
		if (capsLock) {
			str = str.toUpperCase();
		}
		super.insertString(offset, str, a);
	}
}