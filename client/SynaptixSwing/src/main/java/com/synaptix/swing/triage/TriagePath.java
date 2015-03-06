package com.synaptix.swing.triage;


public class TriagePath {

	private Side side;

	private LotDraw lotDraw;

	private VoyageDraw voyageDraw;

	public TriagePath(Side side, LotDraw lotDraw) {
		this(side, lotDraw, null);
	}

	public TriagePath(Side side, LotDraw lotDraw, VoyageDraw voyageDraw) {
		super();
		this.side = side;
		this.lotDraw = lotDraw;
		this.voyageDraw = voyageDraw;
	}

	public Side getSide() {
		return side;
	}

	public LotDraw getLotDraw() {
		return lotDraw;
	}

	public VoyageDraw getVoyageDraw() {
		return voyageDraw;
	}

	public boolean equals(Object obj) {
		if (obj != null && obj instanceof TriagePath) {
			TriagePath t = (TriagePath) obj;
			if (voyageDraw != null) {
				return side.equals(t.side) && lotDraw.equals(t.lotDraw)
						&& voyageDraw.equals(t.voyageDraw);
			} else {
				return side.equals(t.side) && lotDraw.equals(t.lotDraw)
						&& t.voyageDraw == null;
			}
		}
		return super.equals(obj);
	}
}
