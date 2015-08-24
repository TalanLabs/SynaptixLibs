package com.synaptix.client.common.message;

import java.util.Map;

import javax.swing.Icon;

import com.synaptix.constants.shared.ConstantsWithLookingBundle;
import com.synaptix.constants.shared.DefaultDescription;

@DefaultDescription("Commun")
public interface CommonConstantsBundle extends ConstantsWithLookingBundle {

	@DefaultStringValue("Fichier")
	public String file();

	@DefaultStringValue("Est-ce que vous voulez écraser le fichier sélectionné ?")
	public String doYouWantToOverwriteTheSelectedFile();

	@DefaultStringValue("Choisissez un dossier")
	public String chooseFolder();

	@DefaultStringValue("Fichier texte (txt)")
	public String txtFile();

	@DefaultStringValue("Fichier Adobe Pdf (pdf)")
	public String pdfFile();

	@DefaultStringValue("Effacer")
	public String clear();

	@DefaultStringValue("Aucun")
	public String none();

	@DefaultStringValue("Informations")
	public String information();

	@DefaultStringValue("Informations")
	public String informations();

	@DefaultStringValue("Langue")
	public String language();

	@DefaultStringValue("Choisissez un fichier ...")
	public String chooseAFileEllipsis();

	@DefaultStringValue("Annuler")
	@Description("Cancel action in the dialog box")
	public String cancel();

	@DefaultStringValue("Ok")
	@Description("Accept action in the dialog box")
	public String ok();

	@DefaultStringValue("Description")
	public String description();

	@DefaultStringValue("Libellé")
	public String meaning();

	@DefaultStringValue("Rafraîchir")
	public String refresh();

	@DefaultStringValue("Voir les tâches de l'ordonnanceur")
	public String showTasks();

	@DefaultStringValue("Travail en cours")
	public String workInProgress();

	@DefaultStringValue("Fait")
	public String done();

	@DefaultStringValue("Sauvegarde en cours")
	public String saving();

	@DefaultStringValue("Chargement en cours")
	public String loading();

	@DefaultStringValue("Fini")
	public String finished();

	@DefaultStringValue("Fermer")
	public String close();

	@DefaultStringValue("Administration")
	public String administration();

	@DefaultStringValue("Ajouter ...")
	@Description("Function who open dialog for add object")
	public String addEllipsis();

	@DefaultStringValue("Général")
	@Description("General title for extension dialog")
	public String general();

	@DefaultStringValue("Aucune opération n'a été effectuée. Votre objet n'est pas à jour par rapport à la base de données.")
	public String noOperationWasPerformedYourObjectIsNotUpdatedFromTheDatabase();

	@DefaultStringValue("Aucune opération n'a été effectuée. Votre objet n'a pas été inséré en base de données.")
	public String noOperationWasPerformedYourObjectIsNotInserted();

	@DefaultStringValue("OMS")
	public String psc();

	@DefaultStringValue("OMS - {0}")
	public String pscUser(String user);

	@DefaultStringValue("OMS - {0} - [v{1}]")
	public String pscUserVersion(String user, String version);

	@DefaultStringValue("OMS - {0} - [{1}]")
	public String pscUserDatabase(String user, String databaseName);

	@DefaultStringValue("OMS - {0} - [{1} v{2}]")
	public String pscUserDatabaseVersion(String user, String databaseName, String version);

	@DefaultStringValue("Editer")
	public String edit();

	@DefaultStringValue("Supprimer")
	public String delete();

	@DefaultStringValue("Entrer un préfixe de recherche ou un patron (? = n'importe quel caractère, * = n'importe quelle chaîne de caractères)")
	public String enterSearchText();

	@DefaultStringValue("Merci de sélectionner un élément")
	public String pleaseSelectItem();

	@DefaultStringValue("Il n'y a aucune opération à effectuer")
	public String noOperationToPerform();

	@DefaultStringValue("Edition des perspectives")
	public String editPerspective();

	@DefaultStringValue("Nom")
	public String name();

	@DefaultStringValue("Date")
	public String date();

	@DefaultStringValue("Commentaires")
	public String comments();

	@DefaultStringValue("Ce champ est obligatoire")
	public String mandatoryField();

	@DefaultStringValue("Ce champ est invalide")
	public String invalidField();

	@DefaultStringValue("Saisie requise")
	public String inputNeeded();

	@DefaultStringValue("Confirmation")
	public String confirmation();

	@DefaultStringValue("Utilisateur")
	public String user();

	@DefaultStringValue("Ce fichier doit être lisible")
	public String notReadable();

	@DefaultStringValue("Merci de sélectionner un élément")
	public String selectAnItem();

	@DefaultStringValue("Voulez-vous supprimer l'élément sélectionné ?")
	public String doYouWantToDeleteTheSelectedItem();

	@DefaultStringValue("Supprimer ...")
	public String deleteEllipsis();

	@DefaultStringValue("Options")
	public String options();

	@DefaultStringValue("Afficher le nom des lieux")
	public String showLocationName();

	@DefaultStringValue("Voir la mini carte")
	public String showMiniMap();

	@DefaultStringValue("Afficher le viseur")
	public String showCrosshair();

	@DefaultStringValue("Afficher la barre d'informations")
	public String showInfoBar();

	@DefaultStringValue("Afficher l'échelle")
	public String showScalable();

	@DefaultStringValue("Afficer les contrôles de vue")
	public String showViewControls();

	@DefaultStringValue("Afficher les points de passage")
	public String showWaypoints();

	@DefaultStringValue("Afficher les flèches")
	public String showArrows();

	@DefaultStringValue("Camionneur")
	public String trucker();

	@DefaultStringValue("Expéditeur")
	public String sender();

	@DefaultStringValue("Destinataire")
	public String consignee();

	@DefaultStringValue("Donneur d'ordre")
	public String princIsDo();

	@DefaultStringValue("Direct régulier")
	public String tt();

	@DefaultStringValue("Traction")
	public String pp();

	@DefaultStringValue("Semi-direct aval")
	public String tp();

	@DefaultStringValue("Semi-direct amont")
	public String pt();

	@DefaultStringValue("Spot direct")
	public String sp();

	@DefaultStringValue("Aucune sélection")
	public String noneSelected();

	@DefaultStringValue("Double clic pour supprimer")
	public String doubleClickToClear();

	@DefaultStringValue("Rechercher ...")
	public Object searchEllipsis();

	@DefaultStringValue("Erreur")
	public String error();

	@DefaultStringValue("Min")
	public String min();

	@DefaultStringValue("Max")
	public String max();

	@DefaultStringValue("Aujourd'hui")
	public Icon today();

	@DefaultStringValue("Actions")
	public String actions();

	@DefaultStringValue("Autres")
	public String others();

	@DefaultStringValue("Libellé")
	public String label();

	@DefaultStringValue("Filtre")
	public String filter();

	@DefaultStringValue("Pays")
	public String country();

	@DefaultStringValue("Etat")
	public String state();

	@DefaultStringValue("Ville")
	public String city();

	@DefaultStringValue("Plateforme")
	public String platform();

	@DefaultStringValue("Zip")
	public String zip();

	@DefaultStringValue("Dénombrement...")
	public String counting();

	@DefaultStringValue("Récupération de {0} résultats sur {1} ...")
	public String fetchingXResultsOverY(int i, int count);

	@DefaultStringValue("Tpop")
	public String tpop();

	@DefaultStringValue("Légende")
	public String legend();

	@DefaultStringValue("Afficher la légende")
	public String showLegend();

	@DefaultStringValue("Tout sélectionner")
	public String selectAll();

	@DefaultStringValue("Tout désélectionner")
	public String unselectAll();

	@DefaultStringValue("Editer ...")
	public String editEllipsis();

	@DefaultStringValue("Gestion des zips invalides")
	public String zipsErrManagement();

	@DefaultStringValue("Sélectionnez un point")
	public String selectPoint();

	@DefaultStringValue("Dupliquer ...")
	public String duplicateEllipsis();

	@DefaultStringValue("Score")
	public String score();

	@DefaultStringValue("Total")
	public String total();

	@DefaultStringValue("Changer le nombre de lignes affichées")
	public String changeTheNumberOfDisplayedLines();

	@DefaultStringValue("{0} lignes")
	public String lines(int value);

	@DefaultStringValue("Edition de la taille des pages")
	public String pageSizeEdition();

	@DefaultStringValue("Sélectionner un nombre de ligne")
	public String selectANumberOfLines();

	@DefaultStringValue("{0} lignes affichées")
	public String displayingLines(int currentPageSize);

	@DefaultStringValue("Inverser la sélection")
	public String reverseSelection();

	@DefaultStringValue("Recherche par login")
	public String searchByLogin();

	@DefaultStringValue("Recherche par id tpop")
	public String searchByTpopId();

	@DefaultStringValue("Recherche par id centre")
	public String searchByIdCenter();

	@DefaultStringValue("Recherche par code plateforme")
	public String searchByPlatformId();

	@DefaultStringValue("Recherche par id type de camionneur")
	public String searchByTruckTypedId();

	@DefaultStringValue("Recherche par nom")
	public String searchByName();

	@DefaultStringValue("Recherche par pays")
	public String searchByCountry();

	@DefaultStringValue("Recherche par zip")
	public String searchByZip();

	@DefaultStringValue("Recherche par ville")
	public String searchByCity();

	@DefaultStringValue("Groupe")
	public String group();

	@DefaultStringValue("Exporter sous excel ...")
	public String exportExcelEllipsis();

	@DefaultStringValue("Fichier excel")
	public String excelFile();

	@DefaultStringValue("Sauvegarder")
	public String save();

	@DefaultStringValue("Défaire")
	public String undo();

	@DefaultStringValue("Refaire")
	public String redo();

	@DefaultStringValue("Annuler ...")
	public String cancelEllipsis();

	@DefaultStringValue("Km")
	public String kmSymbole();

	@DefaultStringValue("%")
	public String pourcentageSymbole();

	@DefaultStringValue("H")
	public String hourSymbole();

	@DefaultStringValue("E-mail invalide")
	public String invalidEmail();

	@DefaultStringValue("Tous")
	public String all();

	@DefaultStringValue("Supprimer")
	public String remove();

	@DefaultStringValue("Outils")
	public String tools();

	@DefaultStringValue("Tous les segments doivent être complétés")
	public String allSegmentsMustBeFilled();

	@DefaultStringValue("Au moins un segment est nécessaire")
	public String youNeedAtLeastOneSegment();

	@DefaultStringValue("Chargement des noms des lieux ...")
	public String loadingNameplacesEllipsis();

	@DefaultStringValue("Afficher le nom des lieux")
	public String showNamePlaces();

	@DefaultStringValue("Attention")
	public String warning();

	@DefaultStringValue("Une erreur s'est produite")
	public String anErrorHasOccured();

	@DefaultStringValue("Succès")
	public String success();

	@DefaultStringValue("Sélectionner au moins un")
	public String selectOne();

	@DefaultStringValue("Commentaire")
	public String comment();

	@DefaultStringValue("Lire le commentaire")
	public String readComment();

	@DefaultStringValue("Editer le commentaire")
	public String editComment();

	@DefaultStringValue("Annulé")
	public String cancelled();

	@DefaultStringValue("Modifié")
	public String modified();

	@DefaultStringValue("Requête de validation")
	public String validationRequest();

	@DefaultStringValue("Envoi de mails...")
	public String sendingMails();

	@DefaultStringValue("Précédent")
	public String previous();

	@DefaultStringValue("Suivant")
	public String next();

	@DefaultStringValue("Mois précédent")
	public String previousMonth();

	@DefaultStringValue("Mois suivant")
	public String nextMonth();

	@DefaultStringValue("Le champ doit être entre {0} et {1}")
	public String fieldMustbeBetween(int i, int j);

	@DefaultStringValue("Infini")
	public String infinity();

	@DefaultStringValue("Vous ne pouvez pas éditer l'utilisateur root")
	public String youCannotEditTheRootUser();

	@DefaultStringValue("Valider")
	public String validate();

	@DefaultStringValue("Sauvegarder en tant que nouveau")
	public String saveAsNew();

	@DefaultStringValue("Déconnecté")
	public String disconnected();

	@DefaultStringValue("Reconnexion dans {0} {0,choice,0#seconde|1#seconde|1<secondes}")
	public String reconnectingInXSeconds(int seconds);

	@DefaultStringValue("Vous avez été déconnecté\nL'application va se fermer.")
	public String youHaveBeenDisconnectedTheApplicationWillNowStop();

	@DefaultStringValue("Message d'un admin")
	public String messageFromAdmin();

	@DefaultStringValue("Ce code existe déjà")
	public String codeAlreadyExists();

	@DefaultStringValue("Le login ne doit pas contenir le symbole arobase \"@\"")
	public String loginMustNotContainAtSign();

	@DefaultStringValue("Oui")
	public String yes();

	@DefaultStringValue("Non")
	public String no();

	@DefaultStringValue("Date de création")
	public String createdDate();

	@DefaultStringValue("Actif")
	public String active();

	@DefaultStringValue("Les deux données doivent être renseignées")
	public String bothDataHaveToBeFilled();

	@DefaultStringValue("Le maximum doit être supérieur au minimum")
	public String theMaximumHasToBeSuperiorToTheMinimum();

	@DefaultStringValue("La date de début doit précéder la date de fin")
	public String theStartDateHasToBePriorToTheEndDate();

	@DefaultStringValue("Recherche précédente")
	public String previousSearch();

	@DefaultStringValue("Recherche suivante")
	public String nextSearch();

	@DefaultStringValue("Recherche {0}")
	public String search(int index);

	@DefaultStringValue("Vous n'avez pas les droits requis pour réaliser cette action.")
	public String youDoNotHaveTheRights();

	@DefaultStringValue("File is not image")
	public Object notImageFile();

	@DefaultStringValue("Inconnu")
	public String unknown();

	@DefaultStringValue("Choisir")
	public String choose();

	@DefaultStringValue("Dupliquer...")
	public String cloneEllipsis();

	@DefaultStringValue("Vrai")
	public String trueValue();

	@DefaultStringValue("Faux")
	public String falseValue();

	@DefaultStringValue("Début")
	public String start();

	@DefaultStringValue("Fin")
	public String end();

	@DefaultStringValue("Global")
	public String global();

	@DefaultStringValue("Exporter les libellés...")
	public String exportNlsMeaningsEllipsis();

	@DefaultStringValue("Importer les libellés...")
	public String importNlsMeaningsEllipsis();

	@DefaultStringValue("Fichier de traduction")
	public String translationFile();

	@DefaultStringValue("Importer un fichier de traduction")
	public String importATranslationFile();

	@DefaultStringValue("Exporter un fichier de traduction")
	public String exportATranslationFile();

	@DefaultStringValue("Traduction")
	public String translation();

	@DefaultStringValue("Le fichier est vide")
	public String theFileIsEmpty();

	@DefaultStringValue("Feuillets de données")
	public String dataSheets();

	@DefaultStringValue("Ligne de début")
	public String startingLine();

	@DefaultStringValue("Colonne des noms de bundles")
	public String columnOfBundlesNames();

	@DefaultStringValue("Colonne des noms de clés")
	public String columnOfKeys();

	@DefaultStringValue("Colonne des traductions")
	public String columnTranslations();

	@DefaultStringValue("Vous êtes sur le point d'importer ces traductions. Voulez-vous continuer ?")
	public String youAreAboutToImportTheseTranslationsWouldYouContinue();

	@DefaultStringValue("Saisir un libellé")
	public String enterALabel();

	@DefaultStringValue("Formatter le contenu")
	public String formatContent();

	@DefaultStringValue("Echec")
	public String failure();

	@DefaultStringValue("Paramètres")
	public String parameters();

	@DefaultStringValue("Edition")
	public String edition();

	@DefaultStringValue("Veuillez vérifier votre saisie")
	public String pleaseVerifyYourInput();

	@DefaultStringValue("Raccourcis")
	public String shortcuts();

	@DefaultStringValue("Ouvrir le document")
	public String openDocument();

	@DefaultStringValue("Voir l'ordre client")
	public String openCustomerOrder();

	@DefaultStringValue("Fermer")
	public String exit();

	@DefaultStringValue("Echec de la sauvegarde")
	public String saveFailed();

	@DefaultStringValue("Télécharger tous les documents")
	public String downloadAllDocuments();

	@DefaultStringValue("Veuillez renseigner au moins une référence.")
	public String atLeastOneReference();

	@DefaultStringValue("Veuillez renseigner au moins un critère de date.")
	public String atLeastOneDate();

	@DefaultStringValue("La période saisie doit être inférieure à {0} mois.")
	public String dateRangeShouldNotExceedNMonths(final int maxMonthRange);

	@DefaultStringValue("La date de début doit être antérieure à la date de fin")
	public String dateRangeIsInvalid();

	@DefaultStringValue("Validation echouée")
	public String validateFailed();

	@DefaultStringValue("Critères")
	public String criteria();

	@DefaultStringValue("Résultat")
	public String result();

	@DefaultStringValue("Dimensions")
	public String dimensions();

	@DefaultStringValue("Veuillez affiner votre recherche")
	public String pleaseDetailYourSearch();

	@DefaultStringValue("Le numéro de téléphone est invalide")
	public String invalidPhone();

	@DefaultStringMapValue({ "FIXED_LINE", "Fixe", "MOBILE", "Mobile", "FIXED_LINE_OR_MOBILE", "Fixe ou mobile", "TOLL_FREE", "Gratuit", "PREMIUM_RATE", "Payant", "SHARED_COST", "Coût partagé",
			"VOIP", "Voip", "PERSONAL_NUMBER", "Numéro personnel", "PAGER", "Pager", "UAN", "Universal Access Numbers", "VOICEMAIL", "Voice Mail Access Numbers", "UNKNOWN", "Inconnue" })
	public Map<String, String> phoneNumberTypes();

	@DefaultStringValue("%")
	String percentSymbol();

	@DefaultStringValue("Code")
	public String serviceCode();

	@DefaultStringValue("Date de début")
	public String startDate();

	@DefaultStringValue("Date de fin")
	public String endDate();

	@DefaultStringValue("Message d'erreur")
	public String errorMessage();

	@DefaultStringValue("Voir les erreurs")
	public String showErrors();

	@DefaultStringValue("Les deux données doivent être différentes")
	public String bothDataHaveToBeDifferent();

	@DefaultStringValue("Le nombre de saisie doit être inférieur à 40 références")
	public String lessThan40References();

	@DefaultStringValue("Durée")
	public String duration();

	@DefaultStringValue("Relancer l'ordonnanceur")
	public String startTaskManger();

	@DefaultStringValue("Jour(s)")
	public String calculDurationDays();

	@DefaultStringValue("Rôles")
	public String memberRole();

	@DefaultStringValue("Enlèvement/Livraison")
	public String pkpDlv();

}
