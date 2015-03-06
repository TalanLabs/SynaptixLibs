package path;

import com.synaptix.swing.path.AbstractPathModel;

public class ExamplePathModel extends AbstractPathModel {

	private String[] lieux;

	private int[] selecteds;

	public ExamplePathModel(String[] lieux, int[] selecteds) {
		super();

		this.selecteds = selecteds;
		this.lieux = lieux;
	}

	public int getNodeCount() {
		return lieux.length;
	}

	public String getNodeName(int index) {
		return lieux[index];
	}

	public String getLineName(int index1, int index2) {
		return index1 % 2 == 0 ? "045634" : "045634\n53min";
	}

	public boolean isSelectedLine(int index1, int index2) {
		boolean res = false;
		int i = 0;
		while (i < selecteds.length - 1 && !res) {
			res = selecteds[i] == index1 && selecteds[i + 1] == index2;
			i++;
		}
		return res;
	}

	public boolean isSelectedNode(int index) {
		boolean res = false;
		int i = 0;
		while (i < selecteds.length && !res) {
			res = selecteds[i] == index;
			i++;
		}
		return res;
	}
}