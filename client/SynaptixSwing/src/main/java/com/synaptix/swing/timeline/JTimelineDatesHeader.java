package com.synaptix.swing.timeline;

import java.util.Calendar;
import java.util.Date;

import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;

import com.synaptix.swing.JTimeline;
import com.synaptix.swing.event.TimelineDatesModelListener;
import com.synaptix.swing.plaf.TimelineDatesHeaderUI;

public class JTimelineDatesHeader extends JComponent implements TimelineDatesModelListener {

	private static final long serialVersionUID = -7863759892137733142L;
	
	private static final String uiClassID = "TimelineDatesHeaderUI"; //$NON-NLS-1$
	
	protected JTimeline timeline;
	
	protected TimelineDatesModel datesModel;

	private boolean isDayFormat = false;

	static {
		UIManager.getDefaults().put(uiClassID,
				"com.synaptix.swing.plaf.basic.BasicTimelineDatesHeaderUI"); //$NON-NLS-1$
	}
	
	public JTimelineDatesHeader() {
		this(null);
	}
	
	public JTimelineDatesHeader(TimelineDatesModel dm) {
		if (dm == null)
			dm = createDefaultDatesModel();
		setDatesModel(dm);

		initializeLocalVars();

		updateUI();
		isDayFormat = false;
	}
	
	public boolean isDayFormat(){
		return this.isDayFormat;
	}
	
	public void setIsDayFormat(boolean b){
		this.isDayFormat = b;
	}
	
	protected void initializeLocalVars() {
		setOpaque(true);
	}
	
	public TimelineDatesHeaderUI getUI() {
		return (TimelineDatesHeaderUI) ui;
	}

	public void setUI(TimelineDatesHeaderUI ui) {
		if (this.ui != ui) {
			super.setUI(ui);
			repaint();
		}
	}

	public void updateUI() {
		setUI((TimelineDatesHeaderUI) UIManager.getUI(this));
	}

	public String getUIClassID() {
		return uiClassID;
	}

	public void setTimeline(JTimeline timeline) {
		JTimeline old = this.timeline;
		this.timeline = timeline;
		firePropertyChange("timeline", old, timeline); //$NON-NLS-1$
	}

	public JTimeline getTimeline() {
		return timeline;
	}
	
	public void setDatesModel(TimelineDatesModel datesModel) {
		if (datesModel == null) {
			throw new IllegalArgumentException(
					"Cannot set a null TimelineDatesModel"); //$NON-NLS-1$
		}
		TimelineDatesModel old = this.datesModel;
		if (datesModel != old) {
			if (old != null) {
				old.removeDatesModelListener(this);
			}
			this.datesModel = datesModel;
			datesModel.addDatesModelListener(this);

			firePropertyChange("datesModel", old, datesModel); //$NON-NLS-1$
			resizeAndRepaint();
		}
	}
	
	public TimelineDatesModel getDatesModel() {
		return datesModel;
	}

	protected TimelineDatesModel createDefaultDatesModel() {
		Calendar cMin = Calendar.getInstance();
		cMin.setTime(new Date());
		cMin.add(Calendar.MONTH, -6);
		Calendar cMax = (Calendar)cMin.clone();
		cMax.add(Calendar.MONTH, 6);
		return new TimelineDatesModel(cMin.getTime(),cMax.getTime());
	}
	
	public void resizeAndRepaint() {
		revalidate();
		repaint();
	}
	
	public void datesChanged(ChangeEvent e) {
		resizeAndRepaint();
	}
}
