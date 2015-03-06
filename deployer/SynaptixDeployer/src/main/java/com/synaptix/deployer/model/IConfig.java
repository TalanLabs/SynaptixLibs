package com.synaptix.deployer.model;

import com.synaptix.component.IComponent;
import com.synaptix.component.annotation.SynaptixComponent;

/**
 * A database configuration
 * 
 * @author Nicolas P
 * 
 */
@SynaptixComponent
public interface IConfig extends IComponent {

	@EqualsKey
	public String getEnvironment();

	public void setEnvironment(String environment);

	@EqualsKey
	public String getUser();

	public void setUser(String user);

	@EqualsKey
	public char[] getPassword();

	public void setPassword(char[] password);

	// public SynaptixConfiguration getSynaptixConfiguration();
	//
	// public void setSynaptixConfiguration(SynaptixConfiguration synaptixConfiguration);

}
