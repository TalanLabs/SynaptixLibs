package com.synaptix.deployer.client.message;

import com.synaptix.constants.shared.ConstantsWithLookingBundle;

public interface DeployerManagementConstantsBundle extends ConstantsWithLookingBundle {

	@DefaultStringValue("Deployeur")
	public String deployer();

	@DefaultStringValue("Etes-vous sûr(e) de vouloir déployer sur l''environnement de {0} ?")
	public String areYouSureYouWantToDeploy(String name);

	@DefaultStringValue("Un déploiement est en cours. Confirmer la fermeture ?")
	public String aDeploymentIsInProgressConfirm();

	@DefaultStringValue("Numéro de build Jenkins")
	public String jenkinsBuildNumber();

	@DefaultStringValue("Environnement")
	public String environment();

	@DefaultStringValue("Instructions")
	public String instructions();

	@DefaultStringValue("Compilation")
	public String build();

	@DefaultStringValue("Vérifier l'état")
	public String checkState();

	@DefaultStringValue("Etat : {0}")
	public String state(String state);

	@DefaultStringValue("Lancé ({0} {0,choice,1#instance|1<instances})")
	public String launched(int instances);

	@DefaultStringValue("Arrêté")
	public String stopped();

	@DefaultStringValue("Arrêter les instances")
	public String stopInstances();

	@DefaultStringValue("Renommer les anciens .war")
	public String renameOld();

	@DefaultStringValue("Télécharger les nouveaux .war")
	public String downloadNew();

	@DefaultStringValue("Lancer les instances")
	public String launch();

	@DefaultStringValue("Prochaine étape")
	public String nextStep();

	@DefaultStringValue("Tout stopper")
	public String stopAll();

	@DefaultStringValue("Tout renommer")
	public String renameAll();

	@DefaultStringValue("Tout télécharger")
	public String downloadAll();

	@DefaultStringValue("Tout relancer")
	public String launchAll();

	@DefaultStringValue("Confirmation")
	public String confirmation();

	@DefaultStringValue("Numéro de compilation")
	public String buildNumber();

	@DefaultStringValue("Nombre invalide")
	public String invalidNumber();

	@DefaultStringValue("Aucun job sélectionné")
	public String noSelectedJob();

	@DefaultStringValue("Lancer")
	public String run();

	@DefaultStringValue("Arrêt")
	public String stopTitle();

	@DefaultStringValue("Renommage")
	public String renameTitle();

	@DefaultStringValue("Mise à jour")
	public String downloadTitle();

	@DefaultStringValue("Lancement")
	public String launchTitle();

	@DefaultStringValue("Envoyer un mail")
	public String sendMail();

	@DefaultStringValue("Configuration du mail")
	public String mailConfiguration();

	@DefaultStringValue("Editer")
	public String edit();

	@DefaultStringValue("Hôte SMTP")
	public String smtpHost();

	@DefaultStringValue("Port SMTP")
	public String smtpPort();

	@DefaultStringValue("Login")
	public String login();

	@DefaultStringValue("Mot de passe")
	public String password();

	@DefaultStringValue("Expéditeur")
	public String sender();

	@DefaultStringValue("Destinataires")
	public String receivers();

	@DefaultStringValue("Mail")
	public String mail();

	@DefaultStringValue("Livraison en {0}")
	public String deploy(String environment);

	@DefaultStringValue("Une livraison sur l''environnement de {0} vient d''être effectuée.\n{2} {1,choice,1#a été compilé|1<ont été compilés} avec le build {3}")
	public String deployDetails(String environment, int count, String wars, String build);

	@DefaultStringValue("SSL")
	public String ssl();

	@DefaultStringValue("Jouer des scripts")
	public String playScripts();

	@DefaultStringValue("Parcourir")
	public String browse();

	@DefaultStringValue("Retirer la sélection")
	public String removeSelection();

	@DefaultStringValue("Schéma")
	public String databaseSchema();

	@DefaultStringValue("Vous avez une erreur, merci de la corriger avant de continuer")
	public String youHaveAnErrorPleaseCorrectItFirst();

	@DefaultStringValue("Scripts")
	public String scripts();

	@DefaultStringValue("Voir le log")
	public String showLog();

}
