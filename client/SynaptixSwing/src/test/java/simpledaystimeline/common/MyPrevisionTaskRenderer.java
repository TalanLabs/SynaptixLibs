package simpledaystimeline.common;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import com.synaptix.swing.JSimpleDaysTimeline;
import com.synaptix.swing.SimpleDaysTask;
import com.synaptix.swing.simpledaystimeline.AbstractSimpleDaysTimelineTaskRenderer;

public class MyPrevisionTaskRenderer extends AbstractSimpleDaysTimelineTaskRenderer {

	public void paintTask(Graphics g, Rectangle rect, JSimpleDaysTimeline simpleDaysTimeline, SimpleDaysTask task, boolean isSelected,
			boolean hasFocus, int ressource) {
		Graphics2D g2 = (Graphics2D) g.create();

		FontMetrics fm = g2.getFontMetrics();

		if (isSelected) {
			g2.setColor(Color.BLUE);
			g2.fillRect(rect.x, rect.y + 5, rect.width, rect.height - 10);
		}

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