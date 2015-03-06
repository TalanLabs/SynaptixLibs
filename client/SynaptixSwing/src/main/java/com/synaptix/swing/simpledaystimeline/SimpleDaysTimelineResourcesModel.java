package com.synaptix.swing.simpledaystimeline;

import java.util.Enumeration;

import javax.swing.ListSelectionModel;

import com.synaptix.swing.event.SimpleDaysTimelineResourcesModelListener;

public interface SimpleDaysTimelineResourcesModel {

	public void addResource(SimpleDaysTimelineResource aResource);

	public void removeResource(SimpleDaysTimelineResource resource);

	public void moveResource(int resourceIndex, int newIndex);

	public void setResourceMargin(int newMargin);

	public int getResourceCount();

	public Enumeration<SimpleDaysTimelineResource> getResources();

	public int getResourceIndex(Object resourceIdentifier);

	public SimpleDaysTimelineResource getResource(int resourceIndex);

	public int getDefaultHeight();

	public int getHeight(int resourceIndex);

	public int getResourceMargin();

	public int getResourceIndexAtY(int yPosition);

	public int getTotalResourceHeight();

	public void setResourceSelectionAllowed(boolean flag);

	public boolean getResourceSelectionAllowed();

	public int[] getSelectedResources();

	public int getSelectedResourceCount();

	public void setSelectionModel(ListSelectionModel newModel);

	public ListSelectionModel getSelectionModel();

	public void addResourcesModelListener(
			SimpleDaysTimelineResourcesModelListener x);

	public void removeResourcesModelListener(
			SimpleDaysTimelineResourcesModelListener x);
}
