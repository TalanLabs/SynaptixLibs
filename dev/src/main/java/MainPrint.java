import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import com.synaptix.swing.JDialogModel;
import com.synaptix.widget.actions.view.swing.AbstractAddAction;
import com.synaptix.widget.actions.view.swing.AbstractEditAction;

import helper.MainHelper;

public class MainPrint {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				MainHelper.init();

				JFrame frame = MainHelper.createFrame();
				frame.getContentPane().setLayout(new BorderLayout());
				frame.getContentPane().add(new JScrollPane(new JTable(new DefaultTableModel(100, 10))), BorderLayout.CENTER);

				frame.setVisible(true);

				JPanel panel = new JPanel();
				panel.add(new JButton(new AbstractEditAction("edit") {

					@Override
					public void actionPerformed(ActionEvent e) {
						int maxWidth = 0;
						int maxHeight = 0;

						List<BufferedImage> bis = new ArrayList<BufferedImage>();

						for (Window window : Window.getWindows()) {
							BufferedImage bi = new BufferedImage(window.getWidth(), window.getHeight(), BufferedImage.TYPE_INT_ARGB);
							Graphics g = bi.createGraphics();
							window.paint(g);
							g.dispose();

							maxWidth = Math.max(maxWidth, bi.getWidth());
							maxHeight += bi.getHeight();

							bis.add(bi);
						}

						BufferedImage res = new BufferedImage(maxWidth, maxHeight, BufferedImage.TYPE_INT_ARGB);
						Graphics g = res.createGraphics();
						int y = 0;
						for (BufferedImage bi : bis) {
							g.drawImage(bi, 0, y, null);
							y += bi.getHeight();
						}
						g.dispose();

						File file = new File("D:/test.png");
						if (file.exists()) {
							file.delete();
						}
						try {
							file.createNewFile();
							ImageIO.write(res, "png", file);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}));
				new JDialogModel(frame, "Test", panel, new Action[] { new AbstractAddAction("test") {
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub

					}
				} }).showDialog();
			}
		});
	}

}
