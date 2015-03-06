package simpledaystimeline.common;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import com.synaptix.swing.DayDate;
import com.synaptix.swing.JSimpleDaysTimeline;
import com.synaptix.swing.SimpleDaysTask;
import com.synaptix.swing.simpledaystimeline.AbstractSimpleDaysTimelineTaskRenderer;

public class MyMissionTaskRenderer extends AbstractSimpleDaysTimelineTaskRenderer {

	public double getSelectionHeightPourcent() {
		return 0.8;
	}

	public String getToolTipText(Rectangle rect, JSimpleDaysTimeline simpleDaysTimeline, SimpleDaysTask task, int resource, DayDate dayDate,
			Point point) {
		MyMissionSimpleDaysTask t = (MyMissionSimpleDaysTask) task;
		return t.getNum() + " " + dayDate + " " + point;
	}

	public void paintTask(Graphics g, Rectangle rect, JSimpleDaysTimeline simpleDaysTimeline, SimpleDaysTask task, boolean isSelected,
			boolean hasFocus, int ressource) {
		Graphics2D g2 = (Graphics2D) g.create();

		FontMetrics fm = g2.getFontMetrics();

		if (isSelected) {
			if (task.getOrdre() == 0) {
				g2.setColor(Color.BLUE);
			} else {
				g2.setColor(Color.YELLOW);
			}
		} else {
			if (task.getOrdre() == 0) {
				g2.setColor(new Color(0, 255, 0, 128));
			} else {
				g2.setColor(new Color(0, 128, 128, 128));
			}
		}
		g2.fillRect(rect.x, rect.y + 5, rect.width, rect.height - 10);

		g2.setColor(Color.WHITE);
		g2.drawString(task.toString(), rect.x + 2, rect.y + rect.height / 2 + fm.getDescent());

		g2.setColor(Color.BLACK);
		g2.drawRect(rect.x, rect.y + 5, rect.width - 1, rect.height - 10);

		// g2.drawLine(rect.x, rect.y, rect.x, rect.y + rect.height);
		// g2.drawLine(rect.x + rect.width - 1, rect.y, rect.x + rect.width
		// - 1, rect.y + rect.height);

		g2.dispose();
	}
}