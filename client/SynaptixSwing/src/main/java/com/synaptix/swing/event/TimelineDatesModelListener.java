package com.synaptix.swing.event;

import java.util.EventListener;

import javax.swing.event.ChangeEvent;

public interface TimelineDatesModelListener extends EventListener {

    public void datesChanged(ChangeEvent e);

}
