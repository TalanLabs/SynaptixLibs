package com.synaptix.deployer.model;

public enum SqlObject {

	 /**
	 *
	 */
	 TABLE("TABLE"),
	 /**
	 *
	 */
	 SEQUENCE("SEQUENCE"),
	 /**
	 *
	 */
	 INDEX("INDEX"),
	 /*
	 *
	 */
	 PROCEDURE("PROCEDURE"),
	 /**
	 *
	 */
	 PACKAGE("PACKAGE"),
	 /**
	 *
	 */
	 FONCTION("FONCTION"),
	 /**
	 *
	 */
	 VIEW("VIEW"),
	 /**
	 *
	 */
	 SYNONYM("SYNONYM");
//	/**
//	 * 
//	 */
//	CONSTRAINT("CONSTRAINT");

	private final String objectType;

	private SqlObject(String objectType) {
		this.objectType = objectType;
	}

	public String getObjectType() {
		return objectType;
	}
}
