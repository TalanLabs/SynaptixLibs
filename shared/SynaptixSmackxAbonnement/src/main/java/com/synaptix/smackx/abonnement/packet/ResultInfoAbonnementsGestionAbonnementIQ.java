package com.synaptix.smackx.abonnement.packet;

import com.synaptix.abonnement.AbonnementDescriptor;

public class ResultInfoAbonnementsGestionAbonnementIQ extends
		GestionAbonnementIQ {

	private AbonnementDescriptor[] abonnementDescriptors;

	public ResultInfoAbonnementsGestionAbonnementIQ(
			AbonnementDescriptor[] abonnementDescriptors) {
		super();
		this.setType(Type.RESULT);

		this.abonnementDescriptors = abonnementDescriptors;
	}

	public AbonnementDescriptor[] getAbonnementDescriptors() {
		return abonnementDescriptors;
	}

	protected String toXMLExtension() {
		StringBuilder sb = new StringBuilder();
		sb.append("<abonnements>");
		for (AbonnementDescriptor desc : abonnementDescriptors) {
			sb.append("<abonnement name=\"");
			sb.append(desc.getName());
			sb.append("\">");
			if (desc.getDescription() != null) {
				sb.append("<description>");
				sb.append(desc.getDescription());
				sb.append("</description>");
			}
			sb.append("</abonnement>");
		}
		sb.append("</abonnements>");
		return sb.toString();
	}
}
