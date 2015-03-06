package com.synaptix.swing;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;
import javax.swing.TransferHandler;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.swing.utils.GUIWindow;
import com.synaptix.swing.utils.Toolkit;
import com.synaptix.view.swing.error.ErrorViewManager;

public class JImageCropping extends JPanel {

	private static final long serialVersionUID = -5084766218584073919L;

	private CardLayout cardLayout;

	private JPanel labelPanel;

	private JImage imageComponent;

	private JWaitGlassPane waitGlassPane;

	private JLabel noImageLabel;

	private Action cropAction;

	private JButton cropButton;

	private Image imageCurrent;

	private Image imageCrop;

	private int startX;

	private int startY;

	private int endX;

	private int endY;

	private JPopupMenu popupMenu;

	private Action pasteAction;

	public JImageCropping() {
		this(null);
	}

	public JImageCropping(Image image) {
		super(new BorderLayout());

		this.imageCrop = null;
		this.imageCurrent = null;
		this.startX = 0;
		this.startY = 0;
		this.endX = 0;
		this.endY = 0;

		initActions();
		initComponents();
		this.add(buildPanel(), BorderLayout.CENTER);

		if (image != null) {
			imageCurrent = image;
		}

		refreshImage();
	}

	private void initActions() {
		cropAction = new CropAction();
		cropAction.setEnabled(false);

		pasteAction = new PasteAction();
		pasteAction.setEnabled(false);
	}

	private void initComponents() {
		cardLayout = new CardLayout();
		labelPanel = new JPanel(cardLayout);
		labelPanel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		labelPanel.setFocusable(true);
		labelPanel.addFocusListener(new MyFocusListener());
		labelPanel.setTransferHandler(new MyTransferHandler());
		labelPanel.addMouseListener(new MyMouseListener());

		labelPanel.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK), "doCopy"); //$NON-NLS-1$
		labelPanel.getActionMap().put("doCopy", TransferHandler.getCopyAction()); //$NON-NLS-1$

		labelPanel.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK), "doPaste"); //$NON-NLS-1$
		labelPanel.getActionMap().put("doPaste", //$NON-NLS-1$
				TransferHandler.getPasteAction());

		imageComponent = new JImage();
		labelPanel.add(imageComponent, "image"); //$NON-NLS-1$

		waitGlassPane = new JWaitGlassPane();
		labelPanel.add(waitGlassPane, "wait"); //$NON-NLS-1$

		noImageLabel = new JLabel(SwingMessages.getString("JImageCropping.6"), JLabel.CENTER); //$NON-NLS-1$
		labelPanel.add(noImageLabel, "noImage"); //$NON-NLS-1$

		cardLayout.show(labelPanel, "noImage"); //$NON-NLS-1$

		popupMenu = new JPopupMenu();
		popupMenu.add(new PasteAction());

		cropButton = new JButton(cropAction);
	}

	private JPanel buildPanel() {
		FormLayout layout = new FormLayout("fill:d:grow", // columns //$NON-NLS-1$
				"fill:d:grow,fill:d:none"); // rows //$NON-NLS-1$

		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();

		builder.add(labelPanel, cc.xy(1, 1));
		builder.add(cropButton, cc.xy(1, 2));

		return builder.getPanel();
	}

	public void setEnabled(boolean enabled) {
		labelPanel.setFocusable(enabled);
		cropAction.setEnabled(false);

		if (enabled) {
			noImageLabel.setText(SwingMessages.getString("JImageCropping.6")); //$NON-NLS-1$
		} else {
			noImageLabel.setText(SwingMessages.getString("JImageCropping.12")); //$NON-NLS-1$
		}

		super.setEnabled(enabled);
	}

	public boolean isShowCropping() {
		return cropButton.isVisible();
	}

	public void setShowCropping(boolean show) {
		cropButton.setVisible(show);
	}

	public void setImage(Image image) {
		this.imageCurrent = image;
		this.imageCrop = image;
		this.startX = 0;
		this.startY = 0;
		this.endX = 0;
		this.endY = 0;

		refreshImage();
	}

	public Image getImage() {
		return imageCrop;
	}

	public int getEndX() {
		return endX;
	}

	public void setEndX(int endX) {
		this.endX = endX;
		refreshImage();
	}

	public int getEndY() {
		return endY;
	}

	public void setEndY(int endY) {
		this.endY = endY;
		refreshImage();
	}

	public int getStartX() {
		return startX;
	}

	public void setStartX(int startX) {
		this.startX = startX;
		refreshImage();
	}

	public int getStartY() {
		return startY;
	}

	public void setStartY(int startY) {
		this.startY = startY;
		refreshImage();
	}

	private void refreshImage() {
		new ImageSwingWorker().execute();
	}

	public void addChangeListener(ChangeListener l) {
		listenerList.add(ChangeListener.class, l);
	}

	public void removeChangeListener(ChangeListener l) {
		listenerList.remove(ChangeListener.class, l);
	}

	private void fireStateChanged() {
		ChangeListener[] list = listenerList.getListeners(ChangeListener.class);
		ChangeEvent e = new ChangeEvent(this);
		for (ChangeListener l : list) {
			l.stateChanged(e);
		}
	}

	private final class MyFocusListener implements FocusListener {

		public void focusGained(FocusEvent e) {
			labelPanel.setBorder(BorderFactory.createEtchedBorder(Color.ORANGE, Color.YELLOW));
		}

		public void focusLost(FocusEvent e) {
			labelPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		}
	}

	private final class CropAction extends AbstractAction {

		private static final long serialVersionUID = 1371799295461340235L;

		public CropAction() {
			super(SwingMessages.getString("JImageCropping.13")); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e) {
			JCropImageDialog dialog = new JCropImageDialog();
			if (dialog.showDialog(JImageCropping.this, imageCurrent, startX, startY, endX, endY) == JCropImageDialog.ACCEPT_OPTION) {
				startX = dialog.getStartX();
				startY = dialog.getStartY();
				endX = dialog.getEndX();
				endY = dialog.getEndY();

				refreshImage();
			}
		}
	}

	private final class ImageSwingWorker extends SwingWorker<Boolean, String> {

		public ImageSwingWorker() {
			super();
			cardLayout.show(labelPanel, "wait"); //$NON-NLS-1$
			cropAction.setEnabled(false);
			waitGlassPane.start();
		}

		@Override
		protected Boolean doInBackground() throws Exception {
			imageCrop = imageCurrent;
			if (imageCurrent != null && (startX != 0 || startY != 0 || endX != 0 || endY != 0)) {
				publish(SwingMessages.getString("JImageCropping.15")); //$NON-NLS-1$
				BufferedImage b = new BufferedImage(imageCurrent.getWidth(null) - startX - endX, imageCurrent.getHeight(null) - startY - endY, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2d = b.createGraphics();
				g2d.drawImage(imageCurrent, -startX, -startY, imageCurrent.getWidth(null), imageCurrent.getHeight(null), null);
				g2d.dispose();
				imageCrop = b;
			}
			return true;
		}

		@Override
		protected void process(List<String> chunks) {
			if (chunks.size() > 0) {
				waitGlassPane.setText(chunks.get(0));
			} else {
				waitGlassPane.setText(""); //$NON-NLS-1$
			}
		}

		@Override
		protected void done() {
			try {
				get();

				cropAction.setEnabled(false);

				if (imageCrop != null) {
					imageComponent.setImage(imageCrop);
					cropAction.setEnabled(true);

					cardLayout.show(labelPanel, "image"); //$NON-NLS-1$
				} else {
					imageComponent.setImage(null);

					cardLayout.show(labelPanel, "noImage"); //$NON-NLS-1$
				}
			} catch (Exception e) {
				imageCrop = null;
				imageComponent.setImage(null);
			}
			waitGlassPane.stop();

			fireStateChanged();
		}
	}

	private class JImage extends JComponent {

		private static final long serialVersionUID = 7924058591534802955L;

		private Image image;

		public JImage() {
			super();
			image = null;
		}

		public void setImage(Image image) {
			this.image = image;
			revalidate();
			repaint();
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(96, 96);
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (image != null) {
				Rectangle2D rect = Toolkit.getImageScaleForComponent(image, this, false);

				g.drawImage(image, (int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(), (int) rect.getHeight(), null);
			}
		}
	}

	private final class MyTransferHandler extends TransferHandler {

		private static final long serialVersionUID = -3565111047322168878L;

		@Override
		public int getSourceActions(JComponent c) {
			return TransferHandler.COPY;
		}

		public boolean canImport(TransferSupport support) {
			boolean res = false;
			if (JImageCropping.this.isEnabled()) {
				Transferable transferable = support.getTransferable();
				if (transferable != null && (support.isDataFlavorSupported(DataFlavor.javaFileListFlavor) || support.isDataFlavorSupported(DataFlavor.imageFlavor))) {
					res = true;
				}
			}
			return res;
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean importData(TransferSupport support) {
			boolean res = false;

			if (canImport(support)) {
				try {
					Transferable transferable = support.getTransferable();
					if (transferable != null) {
						if (support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
							List<File> files = (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
							if (files != null && files.size() == 1) {
								File file = files.get(0);
								try {
									Image image = ImageIO.read(file);
									if (image != null) {
										res = true;
										setImage(image);
									}
								} catch (IOException e) {
								}
							}
						} else if (support.isDataFlavorSupported(DataFlavor.imageFlavor)) {
							Image image = (Image) transferable.getTransferData(DataFlavor.imageFlavor);
							if (image != null) {
								res = true;
								setImage(image);
							}
						}
					}
				} catch (Exception e) {
					ErrorViewManager.getInstance().showErrorDialog(JImageCropping.this, e);
				}
			}

			if (!res) {
				JOptionPane.showMessageDialog(GUIWindow.getActiveWindow(), SwingMessages.getString("JImageCropping.19"), //$NON-NLS-1$
						SwingMessages.getString("JImageCropping.20"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
			}

			return res;
		}
	}

	private final class MyMouseListener extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent e) {
			labelPanel.requestFocus();
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (e.isPopupTrigger() && JImageCropping.this.isEnabled()) {
				popupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (e.isPopupTrigger() && JImageCropping.this.isEnabled()) {
				popupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	private final class PasteAction extends AbstractAction {

		private static final long serialVersionUID = -8162380875897366573L;

		public PasteAction() {
			super(SwingMessages.getString("JImageCropping.21")); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e) {
			TransferHandler.getPasteAction().actionPerformed(new ActionEvent(labelPanel, ActionEvent.ACTION_PERFORMED, "paste")); //$NON-NLS-1$
		}
	}
}
