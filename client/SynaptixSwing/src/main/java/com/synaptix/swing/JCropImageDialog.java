package com.synaptix.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class JCropImageDialog extends JPanel {

	private static final long serialVersionUID = -7012949187601705859L;

	public final static int ACCEPT_OPTION = 0;

	public final static int CANCEL_OPTION = 1;

	private static final int WIDTH_IMAGE = 256;

	private static final int HEIGHT_IMAGE = 256;

	private JDialogModel dialog;

	private JViewCropImage viewCropImage;

	private Image image;

	private JTextField widthField;

	private JTextField heightField;

	private JSpinner startXField;

	private JSpinner startYField;

	private JSpinner endXField;

	private JSpinner endYField;

	private int returnValue;

	public JCropImageDialog() {
		super(new BorderLayout());

		initComponents();

		dialog = null;
		this.add(buildContents(), BorderLayout.CENTER);
	}

	private void initComponents() {
		viewCropImage = new JViewCropImage();
		viewCropImage.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		viewCropImage.setPreferredSize(new Dimension(WIDTH_IMAGE, HEIGHT_IMAGE));

		widthField = new JTextField();
		widthField.setHorizontalAlignment(JTextField.RIGHT);
		widthField.setEditable(false);

		heightField = new JTextField();
		heightField.setHorizontalAlignment(JTextField.RIGHT);
		heightField.setEditable(false);

		startXField = new JSpinner(new SpinnerNumberModel());
		startXField.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				viewCropImage.setStartX(getStartX());
			}
		});
		startXField.setEnabled(false);

		startYField = new JSpinner(new SpinnerNumberModel());
		startYField.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				viewCropImage.setStartY(getStartY());
			}
		});
		startYField.setEnabled(false);

		endXField = new JSpinner(new SpinnerNumberModel());
		endXField.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				viewCropImage.setEndX(getEndX());
			}
		});
		endXField.setEnabled(false);

		endYField = new JSpinner(new SpinnerNumberModel());
		endYField.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				viewCropImage.setEndY(getEndY());
			}
		});
		endYField.setEnabled(false);
	}

	private JPanel buildNorthPanel() {
		JPanel panel = new JPanel(new BorderLayout());

		FormLayout layoutWest = new FormLayout("40dlu,40dlu", // columns //$NON-NLS-1$
				"pref"); // rows //$NON-NLS-1$
		PanelBuilder builderWest = new PanelBuilder(layoutWest);
		CellConstraints ccWest = new CellConstraints();
		builderWest.add(startYField, ccWest.xy(2, 1));
		panel.add(builderWest.getPanel(), BorderLayout.WEST);

		FormLayout layoutEast = new FormLayout("40dlu", // columns //$NON-NLS-1$
				"pref"); // rows //$NON-NLS-1$
		PanelBuilder builderEast = new PanelBuilder(layoutEast);
		CellConstraints ccEast = new CellConstraints();
		builderEast.add(widthField, ccEast.xy(1, 1));
		panel.add(builderEast.getPanel(), BorderLayout.EAST);

		return panel;
	}

	private JPanel buildSouthPanel() {
		JPanel panel = new JPanel(new BorderLayout());

		FormLayout layoutEast = new FormLayout("40dlu,40dlu", // columns //$NON-NLS-1$
				"pref"); // rows //$NON-NLS-1$
		PanelBuilder builderEast = new PanelBuilder(layoutEast);
		CellConstraints ccEast = new CellConstraints();
		builderEast.add(endYField, ccEast.xy(1, 1));
		panel.add(builderEast.getPanel(), BorderLayout.EAST);

		FormLayout layoutWest = new FormLayout("40dlu", // columns //$NON-NLS-1$
				"pref"); // rows //$NON-NLS-1$
		PanelBuilder builderWest = new PanelBuilder(layoutWest);
		CellConstraints ccWest = new CellConstraints();
		builderWest.add(heightField, ccWest.xy(1, 1));
		panel.add(builderWest.getPanel(), BorderLayout.WEST);

		return panel;
	}

	private JPanel buildWestPanel() {
		FormLayout layout = new FormLayout("40dlu", // columns //$NON-NLS-1$
				"pref"); // rows //$NON-NLS-1$

		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();

		builder.add(startXField, cc.xy(1, 1));

		return builder.getPanel();
	}

	private JPanel buildEastPanel() {
		JPanel panel = new JPanel(new BorderLayout());

		FormLayout layout = new FormLayout("40dlu", // columns //$NON-NLS-1$
				"pref"); // rows //$NON-NLS-1$

		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();

		builder.add(endXField, cc.xy(1, 1));

		panel.add(builder.getPanel(), BorderLayout.SOUTH);
		return panel;
	}

	private JPanel buildContents() {
		JPanel panel = new JPanel(new BorderLayout());

		panel.add(buildNorthPanel(), BorderLayout.NORTH);
		panel.add(buildWestPanel(), BorderLayout.WEST);
		panel.add(buildSouthPanel(), BorderLayout.SOUTH);
		panel.add(buildEastPanel(), BorderLayout.EAST);
		panel.add(viewCropImage, BorderLayout.CENTER);

		return panel;
	}

	private void updateFields(Image image, int startX, int startY, int endX, int endY) {
		if (image != null) {
			startXField.setEnabled(true);
			startYField.setEnabled(true);
			endXField.setEnabled(true);
			endYField.setEnabled(true);

			widthField.setText(String.valueOf(image.getWidth(null)));
			heightField.setText(String.valueOf(image.getHeight(null)));

			startXField.setModel(new SpinnerNumberModel(startX, 0, image.getWidth(null) / 2, 8));
			startYField.setModel(new SpinnerNumberModel(startY, 0, image.getHeight(null) / 2, 8));
			endXField.setModel(new SpinnerNumberModel(endX, 0, image.getWidth(null) / 2, 8));
			endYField.setModel(new SpinnerNumberModel(endY, 0, image.getHeight(null) / 2, 8));

			viewCropImage.setImage(image);

			viewCropImage.setStartX(startX);
			viewCropImage.setStartY(startY);
			viewCropImage.setEndX(endX);
			viewCropImage.setEndY(endY);
		} else {
			viewCropImage.setImage(null);
			startXField.setEnabled(false);
			startYField.setEnabled(false);
			endXField.setEnabled(false);
			endYField.setEnabled(false);

			widthField.setText(""); //$NON-NLS-1$
			heightField.setText(""); //$NON-NLS-1$
		}
	}

	public int showDialog(Component parent, String imagePath, int startX, int startY, int endX, int endY) {
		returnValue = CANCEL_OPTION;

		dialog = new JDialogModel(parent, SwingMessages.getString("JCropImageDialog.14"), this, new Action[] { //$NON-NLS-1$
				new AcceptAction(), new CancelAction() }, new CancelAction());
		dialog.setResizable(true);

		new ImageSwingWorker(imagePath, startX, startY, endX, endY).execute();

		dialog.showDialog();
		dialog.dispose();

		return returnValue;
	}

	public int showDialog(Component parent, Image imageCurrent, int startX, int startY, int endX, int endY) {
		returnValue = CANCEL_OPTION;

		dialog = new JDialogModel(parent, SwingMessages.getString("JCropImageDialog.14"), this, new Action[] { //$NON-NLS-1$
				new AcceptAction(), new CancelAction() }, new CancelAction());
		dialog.setResizable(true);

		updateFields(imageCurrent, startX, startY, endX, endY);

		dialog.showDialog();
		dialog.dispose();

		return returnValue;
	}

	public int getEndX() {
		SpinnerNumberModel model = (SpinnerNumberModel) endXField.getModel();
		return model.getNumber().intValue();
	}

	public int getEndY() {
		SpinnerNumberModel model = (SpinnerNumberModel) endYField.getModel();
		return model.getNumber().intValue();
	}

	public int getStartX() {
		SpinnerNumberModel model = (SpinnerNumberModel) startXField.getModel();
		return model.getNumber().intValue();
	}

	public int getStartY() {
		SpinnerNumberModel model = (SpinnerNumberModel) startYField.getModel();
		return model.getNumber().intValue();
	}

	private final class AcceptAction extends AbstractAction {

		private static final long serialVersionUID = 9184100540105793420L;

		public AcceptAction() {
			super(SwingMessages.getString("JCropImageDialog.16")); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e) {
			returnValue = ACCEPT_OPTION;
			dialog.closeDialog();
		}
	}

	private final class CancelAction extends AbstractAction {

		private static final long serialVersionUID = -1165636071113981103L;

		public CancelAction() {
			super(SwingMessages.getString("JCropImageDialog.17")); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e) {
			returnValue = CANCEL_OPTION;
			dialog.closeDialog();
		}
	}

	private final class ImageSwingWorker extends SwingWorker<Image, String> {

		private JWaitGlassPane wait;

		private String imagePath;

		private int startX;

		private int startY;

		private int endX;

		private int endY;

		public ImageSwingWorker(String imagePath, int startX, int startY, int endX, int endY) {
			super();
			this.imagePath = imagePath;
			this.startX = startX;
			this.startY = startY;
			this.endX = endX;
			this.endY = endY;

			wait = new JWaitGlassPane();
			dialog.setGlassPane(wait);
			wait.start();
		}

		@Override
		protected Image doInBackground() throws Exception {
			Image res = null;
			publish(SwingMessages.getString("JCropImageDialog.18")); //$NON-NLS-1$
			if (imagePath != null) {
				try {
					res = ImageIO.read(new File(imagePath));
				} catch (IOException e) {
					res = null;
				}
			}
			return res;
		}

		@Override
		protected void process(List<String> chunks) {
			if (chunks.size() > 0) {
				wait.setText(chunks.get(0));
			} else {
				wait.setText(""); //$NON-NLS-1$
			}
		}

		@Override
		protected void done() {
			try {
				image = get();
				updateFields(image, startX, startY, endX, endY);
			} catch (InterruptedException e) {
				image = null;
				updateFields(image, startX, startY, endX, endY);
			} catch (ExecutionException e) {
				image = null;
				updateFields(image, startX, startY, endX, endY);
			}
			wait.stop();
		}
	}
}
