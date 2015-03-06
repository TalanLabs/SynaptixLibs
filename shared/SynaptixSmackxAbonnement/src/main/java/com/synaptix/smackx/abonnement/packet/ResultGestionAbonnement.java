package com.synaptix.smackx.abonnement.packet;

public class ResultGestionAbonnement extends GestionAbonnementIQ {

	public ResultGestionAbonnement() {
		this.setType(Type.RESULT);
	}

	protected String toXMLExtension() {
		return "";
	}

}
