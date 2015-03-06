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
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.synaptix.swing.AbstractTriageModel;
import com.synaptix.swing.JTriage;
import com.synaptix.swing.triage.LotDraw;
import com.synaptix.swing.triage.Side;
import com.synaptix.swing.triage.TriageInfoLotDrawRenderer;
import com.synaptix.swing.triage.TriageVoyageDrawRenderer;
import com.synaptix.swing.triage.VoyageDraw;
import com.synaptix.swing.utils.Manager;

public class TestTriage {

	private static Random random = new Random(10);

	private static MyInfoLotDrawRenderer infoLotDrawRenderer = new MyInfoLotDrawRenderer();

	private static MyVoyageDrawRenderer voyageDrawRenderer = new MyVoyageDrawRenderer();

	private static Date createDateAndHour(Date date, int minutes) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.MINUTE, minutes);
		return c.getTime();
	}

	private final static class MyLot extends LotDraw {

		private static int seqIdLot = 0;

		private int idLot;

		private Date date;

		private Date dateMin;

		private Date dateMax;

		private Side side;

		public MyLot(Side side, Date date) {
			this.side = side;
			this.date = date;

			idLot = seqIdLot++;

			dateMin = createDateAndHour(date, random.nextInt(900) - 450);
			dateMax = createDateAndHour(date, random.nextInt(300) - 150);
			
			toolTipText = side + " " + idLot;
		}

		public Date getDate() {
			return date;
		}

		public Date getDateMax() {
			return dateMax;
		}

		public Date getDateMin() {
			return dateMin;
		}

		public boolean isSelected() {
			return true;
		}

		public TriageInfoLotDrawRenderer getRenderer() {
			return infoLotDrawRenderer;
		}

		public String toString() {
			return side + " " + idLot;
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

		private static int id = 0;

		private int type;

		private int idVoyage;

		public MyVoyage(int type) {
			this.type = type;
			this.idVoyage = id++;
			
			toolTipText = type + " " + idVoyage;
		}

		public int getType() {
			return type;
		}

		public int getIdVoyage() {
			return idVoyage;
		}

		public boolean isSelected() {
			return true;
		}

		public TriageVoyageDrawRenderer getRenderer() {
			return voyageDrawRenderer;
		}

		public boolean equals(Object obj) {
			if (obj != null && obj instanceof MyVoyage) {
				MyVoyage v = (MyVoyage) obj;
				return getIdVoyage() == v.getIdVoyage();
			}
			return super.equals(obj);
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

		private List<LotDraw> rightList;

		private Map<LotDraw, List<VoyageDraw>> leftMap;

		private Map<LotDraw, List<VoyageDraw>> rightMap;

		public TestTriageModel() {
			leftList = new ArrayList<LotDraw>();
			leftMap = new HashMap<LotDraw, List<VoyageDraw>>();
			rightList = new ArrayList<LotDraw>();
			rightMap = new HashMap<LotDraw, List<VoyageDraw>>();

			List<VoyageDraw> voyageAlls = new ArrayList<VoyageDraw>();

			Calendar c = Calendar.getInstance();
			for (int j = 0; j < 30; j++) {
				List<VoyageDraw> voyages = new ArrayList<VoyageDraw>();
				for (int i = 0; i < (int) (random.nextInt(50)); i++) {
					VoyageDraw v = new MyVoyage(random.nextInt(3));
					voyages.add(v);
					voyageAlls.add(v);
				}
				LotDraw lot = new MyLot(Side.LEFT, c.getTime());
				leftList.add(lot);
				leftMap.put(lot, voyages);

				if (random.nextInt(100) >= 20) {
					c.add(Calendar.MINUTE, random.nextInt(300));
				}
			}

			c.setTime(new Date());
			for (int j = 0; j < 42; j++) {
				List<VoyageDraw> voyages = new ArrayList<VoyageDraw>();

				for (int i = 0; i < random.nextInt(50); i++) {
					if (voyageAlls.size() > 0) {
						int k = random.nextInt(voyageAlls.size());
						voyages.add(voyageAlls.get(k));

						voyageAlls.remove(k);
					}
				}
				LotDraw lot = new MyLot(Side.RIGHT, c.getTime());
				rightList.add(lot);
				rightMap.put(lot, voyages);

				if (random.nextInt(100) >= 30) {
					c.add(Calendar.MINUTE, random.nextInt(300));
				}
			}
		}

		public List<LotDraw> getLotDraws(Side side) {
			switch (side) {
			case RIGHT:
				if (rightList != null) {
					return rightList;
				}
				break;
			case LEFT:
				if (leftList != null) {
					return leftList;
				}
				break;
			}
			return null;
		}

		public List<VoyageDraw> getVoyageDraws(Side side, LotDraw lotDraw) {
			switch (side) {
			case RIGHT:
				if (rightMap != null) {
					return rightMap.get(lotDraw);
				}
				break;
			case LEFT:
				if (leftMap != null) {
					return leftMap.get(lotDraw);
				}
				break;
			}
			return null;
		}
	}

	public static void main(String[] args) {
		Manager.installLookAndFeelWindows();

		final JFrame frame = new JFrame("Tests JTriage");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());

		final JTriage triage = new JTriage(new TestTriageModel());
		triage.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				//triage.print();
			}
		});
		frame.getContentPane().add(new JTextField(), BorderLayout.NORTH);
		frame.getContentPane().add(triage, BorderLayout.CENTER);

		frame.pack();
		// frame.setSize(512, 512);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.setVisible(true);
				
				
			}
		});
	}

}
