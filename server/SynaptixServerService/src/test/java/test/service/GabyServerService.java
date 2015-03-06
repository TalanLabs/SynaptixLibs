package test.service;

public class GabyServerService implements IGabyService {

	private String rien;

	public String whatYourName() {
		return "Gaby" + rien;
	}

	public void longTraitement(IGabyServiceCallback gabyServiceCallback) {
		gabyServiceCallback.publish("Debut");

		gabyServiceCallback.publish("Au milieu");

		gabyServiceCallback.publish("Terminé");
	}

	public void toto() {
		rien = "Toto";
	}
}
