import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.synaptix.swing.AbstractTriageModel;
import com.synaptix.swing.JTableTriage;
import com.synaptix.swing.triage.LotDraw;
import com.synaptix.swing.triage.Side;
import com.synaptix.swing.triage.TriageInfoLotDrawRenderer;
import com.synaptix.swing.triage.TriageVoyageDrawRenderer;
import com.synaptix.swing.triage.VoyageDraw;
import com.synaptix.swing.utils.Manager;

public class TestTableTriage {

	private static Random random = new Random(10);

	private static MyInfoLotDrawRenderer infoLotDrawRenderer = new MyInfoLotDrawRenderer();

	private static MyVoyageDrawRenderer voyageDrawRenderer = new MyVoyageDrawRenderer();

	private final static class MyLot extends LotDraw {

		private static int seqIdLot = 0;

		private int idLot;

		public MyLot() {
			idLot = seqIdLot++;
		}

		public Date getDate() {
			return null;
		}

		public Date getDateMax() {
			return null;
		}

		public Date getDateMin() {
			return null;
		}

		public boolean isSelected() {
			return true;
		}

		public TriageInfoLotDrawRenderer getRenderer() {
			return infoLotDrawRenderer;
		}

		public String toString() {
			return String.valueOf(idLot);
		}
	}

	private static class MyInfoLotDrawRenderer implements
			TriageInfoLotDrawRenderer {

		private Font font = new Font("Tahoma", Font.BOLD, 12);

		private FontRenderContext frc = new FontRenderContext(
				new AffineTransform(), false, false);

		public MyInfoLotDrawRenderer() {
		}

		public Dimension getMinimumSize(Object source, Side side,
				LotDraw lotDraw) {
			Rectangle2D rect = font.getStringBounds(lotDraw.toString(), frc);
			return new Dimension((int) rect.getBounds().getWidth(), (int) rect
					.getBounds().getHeight() * 2);
		}

		public void paintInfoLotDraw(Graphics g, Rectangle rect, Object source,
				Side side, LotDraw lotDraw, boolean isSelected) {
			Graphics2D g2 = (Graphics2D) g;

			g2.setColor(Color.BLACK);
			g2.setFont(font);
			Rectangle2D rect2 = font.getStringBounds(lotDraw.toString(), frc);
			g2.drawString(lotDraw.toString(), rect.x + 2, rect.y
					- (int) rect2.getY());

			g2.drawString(lotDraw.toString(), rect.x + 2, rect.y
					+ (int) rect2.getHeight() - (int) rect2.getY());
		}
	}

	private final static class MyVoyage extends VoyageDraw {

		private int type;

		public MyVoyage(int type) {
			this.type = type;
		}

		public int getType() {
			return type;
		}

		public boolean isSelected() {
			return true;
		}

		public TriageVoyageDrawRenderer getRenderer() {
			return voyageDrawRenderer;
		}
	}

	private static class MyVoyageDrawRenderer implements
			TriageVoyageDrawRenderer {

		public void paintVoyageDraw(Graphics g, Rectangle rect, Object source,
				Side side, LotDraw lotDraw, VoyageDraw voyageDraw,
				boolean isSelected) {
			Graphics2D g2 = (Graphics2D) g;

			if (isSelected) {
				g2.setColor(Color.YELLOW);
			} else {
				g2.setColor(Color.RED);
			}
			g2.fillRect(rect.x, rect.y, rect.width - 1, rect.height - 1);

			g2.setColor(Color.BLACK);
			g2.drawRect(rect.x, rect.y, rect.width - 1, rect.height - 1);

			if (isSelected) {
				g2.setColor(Color.YELLOW);
			} else {
				g2.setColor(Color.BLUE);
			}
			g2
					.fillRect(rect.x + 2, rect.y + 2, rect.width - 5,
							rect.height - 5);

			g2.setColor(Color.BLACK);
			g2
					.drawRect(rect.x + 2, rect.y + 2, rect.width - 5,
							rect.height - 5);
		}
	}

	private final static class TestTriageModel extends AbstractTriageModel {

		private List<LotDraw> leftList;

		private Map<LotDraw, List<VoyageDraw>> leftMap;

		public TestTriageModel() {
			leftList = new ArrayList<LotDraw>();
			leftMap = new HashMap<LotDraw, List<VoyageDraw>>();

			List<VoyageDraw> voyageAlls = new ArrayList<VoyageDraw>();

			Calendar c = Calendar.getInstance();
			for (int j = 0; j < 3; j++) {
				List<VoyageDraw> voyages = new ArrayList<VoyageDraw>();
				for (int i = 0; i < (int) (random.nextInt(50)); i++) {
					VoyageDraw v = new MyVoyage(random.nextInt(3));
					voyages.add(v);
					voyageAlls.add(v);
				}
				LotDraw lot = new MyLot();
				leftList.add(lot);
				leftMap.put(lot, voyages);

				if (random.nextInt(100) >= 20) {
					c.add(Calendar.MINUTE, random.nextInt(300));
				}
			}
		}

		public List<LotDraw> getLotDraws(Side side) {
			if (leftList != null) {
				return leftList;
			}
			return null;
		}

		public List<VoyageDraw> getVoyageDraws(Side side, LotDraw lotDraw) {
			if (leftMap != null) {
				return leftMap.get(lotDraw);
			}
			return null;
		}
	}

	public static void main(String[] args) {
		Manager.installLookAndFeelWindows();

		final JFrame frame = new JFrame("Tests JTriage");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());

		JTableTriage triage = new JTableTriage(Side.RIGHT,
				new TestTriageModel());
		triage.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				// System.out.println("ici");
			}
		});
		frame.getContentPane().add(new JTextField(), BorderLayout.NORTH);
		frame.getContentPane()
				.add(new JScrollPane(triage), BorderLayout.CENTER);

		//frame.pack();
		frame.setSize(100, 512);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.setVisible(true);
			}
		});
	}

}
