package simpledaystimeline.common;

import com.synaptix.swing.AbstractSimpleDaysTask;
import com.synaptix.swing.DayDate;
import com.synaptix.swing.simpledaystimeline.SimpleDaysTimelineTaskRenderer;

public class MyMissionSimpleDaysTask extends AbstractSimpleDaysTask {

	private static final SimpleDaysTimelineTaskRenderer RENDERER = new MyMissionTaskRenderer();

	private static final int ORDRE = 5;

	private int jour;

	private DayDate min, max;

	private int num;

	private String indiceMission;

	static int id = 0;

	private static int nextId() {
		return id++;
	}

	public MyMissionSimpleDaysTask(String indiceMission, int jour, DayDate min, DayDate max) {
		super();

		this.jour = jour;
		this.min = min;
		this.max = max;
		this.indiceMission = indiceMission;

		num = nextId();
	}

	public int getJour() {
		return jour;
	}

	public String getIndiceMission() {
		return indiceMission;
	}

	public DayDate getDayDateMax() {
		return max;
	}

	public DayDate getDayDateMin() {
		return min;
	}

	public SimpleDaysTimelineTaskRenderer getTaskRenderer() {
		return RENDERER;
	}

	public boolean isNoClipping() {
		return false;
	}

	public boolean isSelectable() {
		return true;
	}

	public int getOrdre() {
		return ORDRE;
	}

	public int hashCode() {
		return num;
	}
	
	public boolean equals(Object o) {
		if (o != null && o instanceof MyMissionSimpleDaysTask) {
			MyMissionSimpleDaysTask o2 = (MyMissionSimpleDaysTask) o;
			return num == o2.num;
		}
		return super.equals(o);
	}

	public int getNum() {
		return num;
	}

	public String toString() {
		return "( M " + num + " " + indiceMission + " )";
	}
}