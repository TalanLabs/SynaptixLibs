package simpledaystimeline.common;

public class Hours {

	private int num;

	private String indiceMission;

	private int minuteStart;

	private int minuteEnd;

	public Hours(int num, String indiceMission, int minuteStart, int minuteEnd) {
		super();
		this.num = num;
		this.indiceMission = indiceMission;
		this.minuteStart = minuteStart;
		this.minuteEnd = minuteEnd;
	}

	public int getMinuteStart() {
		return minuteStart;
	}

	public int getMinuteEnd() {
		return minuteEnd;
	}

	public int getNum() {
		return num;
	}

	public String getIndiceMission() {
		return indiceMission;
	}

	public String toString() {
		return "( Hours -> " + num + " " + indiceMission + " " + minuteStart + " " + minuteEnd + " )";
	}
}
