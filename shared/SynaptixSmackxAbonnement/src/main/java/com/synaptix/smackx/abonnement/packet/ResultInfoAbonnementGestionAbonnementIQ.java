package com.synaptix.smackx.abonnement.packet;

public class ResultInfoAbonnementGestionAbonnementIQ extends
		GestionAbonnementIQ {

	private String name;

	private String[] adherants;

	public ResultInfoAbonnementGestionAbonnementIQ(String name,
			String[] adherants) {
		super();
		this.setType(Type.RESULT);

		this.name = name;
		this.adherants = adherants;
	}

	public String getName() {
		return name;
	}

	public String[] getAdherants() {
		return adherants;
	}

	protected String toXMLExtension() {
		StringBuilder sb = new StringBuilder();
		sb.append("<abonnement name=\"");
		sb.append(name);
		sb.append("\">");
		if (adherants != null) {
			for (String user : adherants) {
				sb.append("<user name=\"");
				sb.append(user);
				sb.append("\"/>");
			}
		}
		sb.append("</abonnement>");
		return sb.toString();
	}
}
