package com.synaptix.deployer.client.view;

import java.util.List;

import com.synaptix.client.view.IView;
import com.synaptix.deployer.model.IFileSystemSpace;

public interface IFileSystemSpaceView extends IView {

	/**
	 * Set components
	 * 
	 * @param fileSystemSpaceList
	 */
	public void setComponents(List<IFileSystemSpace> fileSystemSpaceList);

}
