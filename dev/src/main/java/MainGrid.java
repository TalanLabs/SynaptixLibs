import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jdesktop.swingx.painter.PinstripePainter;

import com.synaptix.widget.grid.GridCellModelEvent;
import com.synaptix.widget.grid.GridCellModelListener;
import com.synaptix.widget.grid.GridCellTransferable;
import com.synaptix.widget.grid.JGridCell;

import helper.MainHelper;

public class MainGrid {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				MainHelper.init();

				JFrame frame = MainHelper.createFrame();

				JLabel label = new JLabel("DRAG ME", JLabel.CENTER);
				label.setOpaque(true);
				label.setBackground(Color.LIGHT_GRAY);
				label.setPreferredSize(new Dimension(100, 50));
				label.setTransferHandler(new MyDragMeTransferHandler());
				label.addMouseListener(new MyDragMeMouseAdapter());
				frame.getContentPane().add(label, BorderLayout.NORTH);

				final JGridCell gridCell = new JGridCell(50, 12, 12);
				PinstripePainter pp = new PinstripePainter(new Color(0, 0, 0, 8), 45, 4, 4);
				pp.setCacheable(true);
				gridCell.setBackgroundPainter(pp);
				gridCell.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

				// gridCell.setEnabled(false);

				gridCell.addCell("Text1", new Rectangle(0, 0, 1, 1));
				gridCell.addCell("Text2", new Rectangle(5, 5, 3, 2));

				gridCell.getInputMap(JGridCell.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "deleteCell");
				gridCell.getActionMap().put("deleteCell", new AbstractAction() {

					private static final long serialVersionUID = 6607103870016842438L;

					@Override
					public void actionPerformed(ActionEvent e) {
						if (gridCell.getSelectionModel() != null && !gridCell.getSelectionModel().isSelectionEmpty()) {
							for (Object value : gridCell.getSelectionModel().getSelectionCells()) {
								gridCell.removeCell(value);
							}
						}
					}
				});

				gridCell.getInputMap(JGridCell.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_G, 0), "showGrid");
				gridCell.getActionMap().put("showGrid", new AbstractAction() {

					private static final long serialVersionUID = 2051574269719586254L;

					@Override
					public void actionPerformed(ActionEvent e) {
						gridCell.setShowGrid(!gridCell.isShowGrid());
					}
				});

				gridCell.getInputMap(JGridCell.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_P, 0), "showPage");
				gridCell.getActionMap().put("showPage", new AbstractAction() {

					private static final long serialVersionUID = -3495598694943451209L;

					@Override
					public void actionPerformed(ActionEvent e) {
						gridCell.setShowPage(!gridCell.isShowPage());
					}
				});

				gridCell.addGridCellModelListener(new GridCellModelListener() {
					@Override
					public void valueChanged(GridCellModelEvent e) {
						System.out.println(e.getType());
					}
				});

				frame.getContentPane().add(new JScrollPane(gridCell), BorderLayout.CENTER);

				final JSlider cellSizeSlider = new JSlider(10, 150, 90);
				cellSizeSlider.addChangeListener(new ChangeListener() {
					@Override
					public void stateChanged(ChangeEvent e) {
						gridCell.setCellSize(cellSizeSlider.getValue());
					}
				});
				frame.getContentPane().add(cellSizeSlider, BorderLayout.SOUTH);

				final JSlider numGridXSlider = new JSlider(JSlider.VERTICAL, 12, 32, 12);
				numGridXSlider.addChangeListener(new ChangeListener() {
					@Override
					public void stateChanged(ChangeEvent e) {
						gridCell.setNumGridX(numGridXSlider.getValue());
					}
				});
				frame.getContentPane().add(numGridXSlider, BorderLayout.WEST);

				final JSlider numGridYSlider = new JSlider(JSlider.VERTICAL, 12, 32, 12);
				numGridYSlider.addChangeListener(new ChangeListener() {
					@Override
					public void stateChanged(ChangeEvent e) {
						gridCell.setNumGridY(numGridYSlider.getValue());
					}
				});
				frame.getContentPane().add(numGridYSlider, BorderLayout.EAST);

				frame.setSize(800, 800);
				frame.setVisible(true);
			}
		});
	}

	public static class MyDragMeMouseAdapter extends MouseAdapter {

		public void mousePressed(MouseEvent e) {
			JComponent c = (JComponent) e.getSource();
			TransferHandler handler = c.getTransferHandler();
			handler.exportAsDrag(c, e, TransferHandler.COPY);
		}
	}

	private static class MyDragMeTransferHandler extends TransferHandler {

		private static final long serialVersionUID = 5111950707141828591L;

		private Random rand = new Random(0);

		private int v = 0;

		@Override
		public int getSourceActions(JComponent c) {
			return TransferHandler.COPY;
		}

		@Override
		protected Transferable createTransferable(JComponent c) {
			Dimension dimension = new Dimension(rand.nextInt(6) + 1, rand.nextInt(6) + 1);
			Point anchor = new Point(dimension.width / 2, dimension.height / 2);
			return new GridCellTransferable("DragMe" + String.valueOf(v++), dimension, anchor);
		}
	}
}
