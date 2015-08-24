package com.synaptix.client.common.swing.view;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import com.synaptix.client.common.util.StaticCommonHelper;
import com.synaptix.taskmanager.controller.helper.INlsMessageExtendedContext;
import com.synaptix.widget.view.swing.helper.IToolBarActionsBuilder;


public class NlsMessageExtendedPanelDescriptor {

	private final INlsMessageExtendedContext nlsMessageExtendedContext;

	private Action exportNlsMeaningsAction;

	private Action importNlsMeaningsAction;

	public NlsMessageExtendedPanelDescriptor(INlsMessageExtendedContext nlsMessageExtendedContext) {
		super();

		this.nlsMessageExtendedContext = nlsMessageExtendedContext;

		initialize();
	}

	private void initialize() {
		exportNlsMeaningsAction = new ExportNlsMeaningsAction();
		importNlsMeaningsAction = new ImportNlsMeaningsAction();
	}

	public void installToolBar(IToolBarActionsBuilder builder) {
		if (nlsMessageExtendedContext.hasAuthChangeNlsMeanings()) {
			builder.addSeparator();
			builder.addAction(exportNlsMeaningsAction);
			builder.addAction(importNlsMeaningsAction);
		}
	}

	private final class ExportNlsMeaningsAction extends AbstractAction {

		private static final long serialVersionUID = -568506073501766843L;

		public ExportNlsMeaningsAction() {
			super(StaticCommonHelper.getCommonConstantsBundle().exportNlsMeaningsEllipsis());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			nlsMessageExtendedContext.exportNlsMeanings();
		}
	}

	private final class ImportNlsMeaningsAction extends AbstractAction {

		private static final long serialVersionUID = 7884353791524034606L;

		public ImportNlsMeaningsAction() {
			super(StaticCommonHelper.getCommonConstantsBundle().importNlsMeaningsEllipsis());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			nlsMessageExtendedContext.importNlsMeanings();
		}
	}

}
