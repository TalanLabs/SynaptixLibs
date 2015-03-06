package com.synaptix.abonnement;

public class AbonnementDescriptor {

	private String name;

	private IAbonnement<?> abonnement;

	private String description;

	public AbonnementDescriptor(String name, IAbonnement<?> abonnement,
			String description) {
		super();
		this.name = name;
		this.abonnement = abonnement;
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public IAbonnement<?> getAbonnement() {
		return abonnement;
	}

	public String getDescription() {
		return description;
	}
}
