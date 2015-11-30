import java.awt.Color;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.internal.utils.SubstanceTextUtilities;

import com.synaptix.widget.xytable.DefaultXYTableModel;
import com.synaptix.widget.xytable.JXYTable;
import com.synaptix.widget.xytable.JXYTableColumnFooter;
import com.synaptix.widget.xytable.JXYTableRowFooter;
import com.synaptix.widget.xytable.JXYTableRowHeader;
import com.synaptix.widget.xytable.JXYTableScrollPane;

import helper.MainHelper;

public class MainXYTable {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				MainHelper.init();

				JFrame frame = MainHelper.createFrame();

				JXYTable xyTable = new JXYTable(new DefaultXYTableModel(10, 20));
				xyTable.setXYTableRowHeader(new JXYTableRowHeader(xyTable));
				xyTable.setRowHeaderWidth(90);
				xyTable.setXYTableColumnFooter(new JXYTableColumnFooter(xyTable));
				xyTable.setXYTableRowFooter(new JXYTableRowFooter(xyTable));

				JXYTableScrollPane sp = new JXYTableScrollPane(xyTable);
				sp.setCorner(JXYTableScrollPane.LOWER_LEFT_0_CORNER, create("Total"));
				sp.setCorner(JXYTableScrollPane.UPPER_RIGHT_0_CORNER, create("Total"));
				sp.setCorner(JXYTableScrollPane.LOWER_RIGHT_0_CORNER, create("10000"));
				frame.getContentPane().add(sp);

				frame.pack();
				frame.setVisible(true);
			}
		});
	}

	private static JLabel create(String name) {
		JLabel label = new JLabel(name);
		label.putClientProperty(SubstanceTextUtilities.ENFORCE_FG_COLOR, true);
		label.putClientProperty(SubstanceLookAndFeel.COLORIZATION_FACTOR, 1.0);
		label.setBackground(Color.GRAY);
		label.setForeground(Color.WHITE);
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setOpaque(true);
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		return label;
	}
}
