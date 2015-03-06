package com.synaptix.swing.roulement;

import java.util.Enumeration;

import javax.swing.ListSelectionModel;

import com.synaptix.swing.roulement.event.RoulementTimelineResourcesModelListener;

public interface RoulementTimelineResourcesModel {

	public void addResource(RoulementTimelineResource aResource);

	public void removeResource(RoulementTimelineResource resource);

	public void moveResource(int resourceIndex, int newIndex);

	public void setResourceMargin(int newMargin);

	public int getResourceCount();

	public Enumeration<RoulementTimelineResource> getResources();

	public int getResourceIndex(Object resourceIdentifier);

	public RoulementTimelineResource getResource(int resourceIndex);

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
			RoulementTimelineResourcesModelListener x);

	public void removeResourcesModelListener(
			RoulementTimelineResourcesModelListener x);
}
