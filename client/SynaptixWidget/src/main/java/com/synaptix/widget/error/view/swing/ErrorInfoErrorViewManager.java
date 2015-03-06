package com.synaptix.widget.error.view.swing;

import java.awt.Component;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.error.ErrorReporter;

import com.google.common.base.Preconditions;
import com.synaptix.view.swing.error.ErrorViewManager;
import com.synaptix.widget.error.view.swing.filter.ExecutionExceptionFilter;

public class ErrorInfoErrorViewManager extends ErrorViewManager {

	private static final Log LOG = LogFactory.getLog(ErrorInfoErrorViewManager.class);

	private List<IErrorInfoBuilder> map;

	private IErrorInfoBuilder defaultBuilder;

	private IThrowableFilter throwableFilter;

	private ErrorReporter errorReporter;

	private IDefaultStateErrorInfo defaultStateErrorInfo;

	public ErrorInfoErrorViewManager() {
		super();

		this.map = new ArrayList<IErrorInfoBuilder>();
		this.defaultBuilder = new DefaultErrorInfoBuilder();
		this.throwableFilter = new ExecutionExceptionFilter();
		this.errorReporter = null;
	}

	public void setDefaultErrorInfoBuilder(IErrorInfoBuilder builder) {
		Preconditions.checkNotNull(builder);

		this.defaultBuilder = builder;
	}

	public void setThrowableFilter(IThrowableFilter throwableFilter) {
		this.throwableFilter = throwableFilter;
	}

	public void setErrorReporter(ErrorReporter errorReporter) {
		this.errorReporter = errorReporter;
	}

	public void setDefaultStateErrorInfo(IDefaultStateErrorInfo defaultStateErrorInfo) {
		this.defaultStateErrorInfo = defaultStateErrorInfo;
	}

	public void addErrorInfoBuilder(IErrorInfoBuilder builder) {
		Preconditions.checkNotNull(builder);

		map.add(builder);
	}

	@Override
	public void showErrorDialog(Component parent, Throwable t) {
		if (throwableFilter != null && t != null) {
			t = throwableFilter.filter(t);
		}

		LOG.error(t.getMessage(), t);

		IErrorInfoBuilder builder = null;
		if (t != null) {
			Iterator<IErrorInfoBuilder> it = map.iterator();
			while (it.hasNext() && builder == null) {
				IErrorInfoBuilder b = it.next();
				if (b.acceptThrowable(t)) {
					builder = b;
				}
			}
		}
		if (builder == null) {
			builder = defaultBuilder;
		}
		ErrorInfo errorInfo = builder.buildErrorInfo(t);
		JXErrorPane errorPane = new JXErrorPane();
		if (builder.isActiveReport()) {
			if (defaultStateErrorInfo != null) {
				Map<String, String> state = new HashMap<String, String>();
				state.putAll(errorInfo.getState());
				Map<String, String> s = defaultStateErrorInfo.getDefaultState();
				if (s != null) {
					state.putAll(s);
				}
				errorInfo = new ErrorInfo(errorInfo.getTitle(), errorInfo.getBasicErrorMessage(), errorInfo.getDetailedErrorMessage(), errorInfo.getCategory(), errorInfo.getErrorException(),
						errorInfo.getErrorLevel(), state);
			}

			errorPane.setErrorReporter(errorReporter);
		}
		errorPane.setErrorInfo(errorInfo);
		showDialog(parent, errorPane);
	}

	private void showDialog(final Component owner, final JXErrorPane pane) {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				JDialog dlg = JXErrorPane.createDialog(owner, pane);
				dlg.setResizable(false);
				dlg.setVisible(true);
			}
		};

		if (!SwingUtilities.isEventDispatchThread()) {
			try {
				SwingUtilities.invokeAndWait(r);
			} catch (InvocationTargetException ex) {
				ex.printStackTrace();
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		} else {
			r.run();
		}
	}
}
