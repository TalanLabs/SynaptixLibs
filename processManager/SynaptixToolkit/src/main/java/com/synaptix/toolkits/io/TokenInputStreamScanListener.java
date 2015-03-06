/*
 * Fichier : TokenInputStreamListener.java
 * Projet  : stxToolkit
 * Date    : 26 juil. 2004
 * Auteur  : sps
 *
 */
package com.synaptix.toolkits.io;

/**
 * Projet GPSTrains
 * 
 * @author sps
 */
public class TokenInputStreamScanListener implements InputStreamScanListener {
	public static String SEP_CR = "\r";
	public static String SEP_NL = "\n";
	public static String SEP_LINES = SEP_CR + SEP_NL;
	public static String SEP_SPACE = " ";
	public static String SEP_TAB = " ";
	public static String SEP_SPACES = SEP_TAB + SEP_SPACE;
	public static String SEP_SPACES_AND_LINES = SEP_SPACES + SEP_LINES;

	String separators;
	StringBuffer buffer;
	byte[] bytebuffer;
	int buffer_offset;
	int buffer_len;
	TokenListener tokenListener;

	/**
	 * 
	 */
	public TokenInputStreamScanListener(TokenListener tokenListener, String separators) {
		this.separators = separators;
		this.buffer = new StringBuffer();
		this.bytebuffer = new byte[1024];
		this.buffer_offset = 0;
		this.buffer_len = 0;
		this.tokenListener = tokenListener;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.toolkits.io.InputStreamListener#data(byte[], int, int)
	 */
	public void data(byte[] data, int offset, int len) {
		int istart = offset;
		int iend = offset + len;
		for (int i = istart; i < iend; i++) {
			byte b = data[i];
			if (separators.indexOf(b) >= 0) {
				// this is a separator.
				buffer.append(new String(bytebuffer, 0, buffer_len));
				buffer_offset = 0;
				buffer_len = 0;
				tokenListener.token(buffer.toString());
				buffer = new StringBuffer();
				tokenListener.separator(b);
			} else {
				bytebuffer[buffer_offset] = b;
				buffer_offset++;
				buffer_len++;
				if (buffer_len == 1024) {
					buffer.append(new String(bytebuffer, 0, buffer_len));
					buffer_offset = 0;
					buffer_len = 0;
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.toolkits.io.InputStreamListener#EOD(byte[], int, int)
	 */
	public void EOD(byte[] data, int offset, int len) {
		int istart = offset;
		int iend = offset + len;
		for (int i = istart; i < iend; i++) {
			byte b = data[i];
			if (separators.indexOf(b) >= 0) {
				// this is a separator.
				buffer.append(new String(bytebuffer, 0, buffer_len));
				buffer_offset = 0;
				buffer_len = 0;
				tokenListener.token(buffer.toString());
				buffer = new StringBuffer();
				tokenListener.separator(b);
			} else {
				bytebuffer[buffer_offset] = b;
				buffer_offset++;
				buffer_len++;
				if (buffer_len == 1024) {
					buffer.append(new String(bytebuffer, 0, buffer_len));
					buffer_offset = 0;
					buffer_len = 0;
				}
			}
		}
		if (buffer.length() > 0)
			tokenListener.token(buffer.toString());
		tokenListener.end();
	}

	public static interface TokenListener {
		public void token(String token);

		public void separator(byte sep);

		public void end();
	}
}
