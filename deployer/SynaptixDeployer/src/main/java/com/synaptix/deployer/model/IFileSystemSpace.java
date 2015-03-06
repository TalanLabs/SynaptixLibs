package com.synaptix.deployer.model;

import com.synaptix.component.IComponent;
import com.synaptix.component.annotation.SynaptixComponent;

@SynaptixComponent
public interface IFileSystemSpace extends IComponent {

	public String getName();

	public void setName(String name);

	public String getTotalSpace();

	public void setTotalSpace(String totalSpace);

	public String getUsedSpace();

	public void setUsedSpace(String usedSpace);

	public String getAvailableSpace();

	public void setAvailableSpace(String availableSpace);

	public Integer getPercentage();

	public void setPercentage(Integer percentage);

	public String getMountedOn();

	public void setMountedOn(String mountedOn);

}
