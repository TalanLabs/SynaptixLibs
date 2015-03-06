package com.synaptix.swing;

import java.util.List;

import com.synaptix.swing.event.TriageModelListener;
import com.synaptix.swing.triage.LotDraw;
import com.synaptix.swing.triage.Side;
import com.synaptix.swing.triage.VoyageDraw;


public interface TriageModel {

	public List<LotDraw> getLotDraws(Side side);

	public List<VoyageDraw> getVoyageDraws(Side side, LotDraw lotDraw);

	public void addTriageModelListener(TriageModelListener listener);

	public void removeTriageModelListener(TriageModelListener listener);

}
