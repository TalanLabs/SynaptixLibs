package com.synaptix.gwt.shared.field;

public interface IFieldDto {

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
