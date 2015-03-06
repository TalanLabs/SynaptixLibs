package com.synaptix.widget.perimeter.view.swing;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DropMode;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.TransferHandler;

import com.synaptix.widget.util.StaticWidgetHelper;
import com.synaptix.widget.view.swing.helper.StaticImage;

public abstract class AbstractStringPasteListPerimeterWidget extends AbstractListPerimeterWidget<String> {

	private static final long serialVersionUID = 6179619240167076798L;

	private static final Icon PASTE_ICON = StaticImage.getImageScale(
			new ImageIcon(AbstractStringPasteListPerimeterWidget.class.getResource("/com/synaptix/widget/actions/view/swing/iconPaste.png")), 10); //$NON-NLS-1$

	protected Action pasteAction;

	public AbstractStringPasteListPerimeterWidget(String title) {
		this(title, false);
	}

	public AbstractStringPasteListPerimeterWidget(String title, boolean editable) {
		super(title, editable);

		list.setDropMode(DropMode.INSERT);
		list.setTransferHandler(new MyTransferHandler());
	}

	@Override
	protected Action[] buildActions() {
		pasteAction = new PasteAction();
		return new Action[] { addAction, pasteAction, null, deleteAction, clearAction };
	}

	protected abstract List<String> createObjectsToString(String s);

	@Override
	protected String toObjectString(String o) {
		return o;
	}

	private final class PasteAction extends AbstractAction {

		private static final long serialVersionUID = 8433301832170365910L;

		public PasteAction() {
			super("", PASTE_ICON); //$NON-NLS-1$

			this.putValue(Action.SHORT_DESCRIPTION, StaticWidgetHelper.getSynaptixWidgetConstantsBundle().pasteClipboard());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			TransferHandler.getPasteAction().actionPerformed(new ActionEvent(list, ActionEvent.ACTION_PERFORMED, "paste"));
		}
	}

	private final class MyTransferHandler extends TransferHandler {

		private static final long serialVersionUID = 9040679391852229329L;

		@Override
		public boolean canImport(TransferSupport support) {
			boolean res = false;
			Transferable t = support.getTransferable();
			if (t != null && support.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				res = true;
			}
			return res;
		}

		@Override
		public boolean importData(TransferSupport support) {
			boolean res = false;
			if (canImport(support)) {
				try {
					Transferable t = support.getTransferable();
					String s = (String) t.getTransferData(DataFlavor.stringFlavor);

					addObjects(createObjectsToString(s));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return res;
		}
	}
}
