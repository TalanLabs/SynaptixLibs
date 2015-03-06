package com.synaptix.swing.utils;

import java.util.Stack;

import com.synaptix.swing.SwingMessages;

public class DefaultErrorMessageBuilder implements ErrorMessageBuilder {

	private final static String TEXT_ERROR = "<html><body><p>" //$NON-NLS-1$
			+ SwingMessages.getString("DefaultErrorMessageBuilder.1") + "</p></body></html>"; //$NON-NLS-1$ //$NON-NLS-2$

	public boolean isShowErrorMessage(Throwable e) {
		return true;
	}

	public String buildShortMessage(Throwable e) {
		return TEXT_ERROR;
	}

	public String buildLongMessage(Throwable e) {
		Stack<Throwable> stack = new Stack<Throwable>();
		Throwable t = e;
		while (t != null) {
			stack.push(t);
			t = t.getCause();
		}

		return createExceptionHtml(stack);
	}

	private String createExceptionHtml(Stack<Throwable> stack) {
		StringBuilder sb = new StringBuilder();

		sb.append("<h1 align=\"center\"><font face=\"Times New Roman\"><b><u>" //$NON-NLS-1$
				+ SwingMessages.getString("DefaultErrorMessageBuilder.4") + "</u></b></font></h1>"); //$NON-NLS-1$ //$NON-NLS-2$

		sb.append(createTagHtml("p", //$NON-NLS-1$
				"align='justify'", //$NON-NLS-1$
				SwingMessages.getString("DefaultErrorMessageBuilder.8"))); //$NON-NLS-1$

		while (!stack.isEmpty()) {
			Throwable t = stack.pop();
			sb.append("<br/>"); //$NON-NLS-1$
			sb.append("<hr/>"); //$NON-NLS-1$
			sb.append("<table border='0' width='100%' cellpadding='0' cellspacing='0'>"); //$NON-NLS-1$

			sb.append("<tr>"); //$NON-NLS-1$
			sb.append(createTagHtml("td", "width='100'", SwingMessages.getString("DefaultErrorMessageBuilder.15") + " :")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			sb.append(createTagHtml("td", createTagHtml("p", t //$NON-NLS-1$ //$NON-NLS-2$
					.getLocalizedMessage())));
			sb.append("</tr>"); //$NON-NLS-1$

			sb.append("<tr>"); //$NON-NLS-1$
			sb.append(createTagHtml("td", "width='100'", SwingMessages.getString("DefaultErrorMessageBuilder.23") + " :")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			sb.append(createTagHtml("td", createTagHtml("p", SwingMessages.getString("DefaultErrorMessageBuilder.27")))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			sb.append("</tr>"); //$NON-NLS-1$

			sb.append("<tr>"); //$NON-NLS-1$
			sb.append(createTagHtml("td", "width='100'", SwingMessages.getString("DefaultErrorMessageBuilder.32") + " :")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			sb.append(createTagHtml("td", createTagHtml("p", t.getClass() //$NON-NLS-1$ //$NON-NLS-2$
					.getCanonicalName())));
			sb.append("</tr>"); //$NON-NLS-1$

			sb.append("<tr><td colspan='2'><table border='1' width='100%' cellpadding='0' cellspacing='0'>"); //$NON-NLS-1$
			sb.append("<tr><th>Class</th><th>Method</th><th>File</th><th>Line</th></tr>"); //$NON-NLS-1$
			for (StackTraceElement ste : t.getStackTrace()) {
				sb.append("<tr>"); //$NON-NLS-1$
				sb.append(createTagHtml("td", ste.getClassName())); //$NON-NLS-1$
				sb.append(createTagHtml("td", ste.getMethodName())); //$NON-NLS-1$
				sb.append(createTagHtml("td", ste.getFileName())); //$NON-NLS-1$
				sb.append(createTagHtml("td", String.valueOf(ste //$NON-NLS-1$
						.getLineNumber())));
				sb.append("</tr>"); //$NON-NLS-1$
			}

			sb.append("</table></td></tr>"); //$NON-NLS-1$
			sb.append("</table>"); //$NON-NLS-1$
		}

		String body = createTagHtml("body", sb.toString()); //$NON-NLS-1$
		String html = createTagHtml("html", body); //$NON-NLS-1$

		return html;
	}

	private String createTagHtml(String tag, String attributes, String text) {
		StringBuilder sb = new StringBuilder();
		sb.append("<"); //$NON-NLS-1$
		sb.append(tag);
		sb.append(" "); //$NON-NLS-1$
		sb.append(attributes);
		sb.append(">"); //$NON-NLS-1$

		sb.append(text);

		sb.append("</"); //$NON-NLS-1$
		sb.append(tag);
		sb.append(">"); //$NON-NLS-1$

		return sb.toString();
	}

	private String createTagHtml(String tag, String text) {
		StringBuilder sb = new StringBuilder();
		sb.append("<"); //$NON-NLS-1$
		sb.append(tag);
		sb.append(">"); //$NON-NLS-1$

		sb.append(text);

		sb.append("</"); //$NON-NLS-1$
		sb.append(tag);
		sb.append(">"); //$NON-NLS-1$

		return sb.toString();
	}
}
