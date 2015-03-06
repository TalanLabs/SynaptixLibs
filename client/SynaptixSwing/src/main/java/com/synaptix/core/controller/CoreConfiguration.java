package com.synaptix.core.controller;

import java.awt.Image;

import com.synaptix.core.event.CoreConfigurationListener;

public interface CoreConfiguration {

	public abstract String getName();

	public abstract String getVersion();

	public abstract String getOtherTitle();

	public abstract String getSave();
	
	public abstract Image getLogo();

	public abstract void addCoreConfigurationListener(
			CoreConfigurationListener l);

	public abstract void removeCoreConfigurationListener(
			CoreConfigurationListener l);

}
