package com.synaptix.component.field;

public interface IField {

	/**
	 * Get name of field
	 * 
	 * @return
	 */
	public String name();

	/**
	 * Not use toString for get a name, use name()
	 * 
	 * @return
	 */
	@Override
	public String toString();

}
