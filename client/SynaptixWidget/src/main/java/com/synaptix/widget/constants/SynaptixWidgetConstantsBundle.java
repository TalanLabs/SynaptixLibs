package com.synaptix.widget.constants;

import javax.swing.Icon;

import com.synaptix.constants.shared.ConstantsWithLookingBundle;
import com.synaptix.constants.shared.DefaultDescription;

@DefaultDescription("Framework : widgets")
public interface SynaptixWidgetConstantsBundle extends ConstantsWithLookingBundle {

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

	@DefaultStringValue("Tout effacer")
	public String clearAll();

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

	@DefaultStringValue("Première")
	@Description("First page")
	public String firstPage();

	@DefaultStringValue("Dernière")
	@Description("Last page")
	public String lastPage();

	@DefaultStringValue("Suivante")
	@Description("Next page")
	public String nextPage();

	@DefaultStringValue("Précédente")
	@Description("Previous page")
	public String previousPage();

	@DefaultStringValue("{0} {0,choice,0#page|1#page|1<pages} ({1} {1,choice,0#ligne|1#ligne|1<lignes})")
	public String pagesNumberTotalRow(int pagesNumber, int count);

	@DefaultStringValue("Page {0}")
	public String numOfPage(int currentPage);

	@DefaultStringValue("Importer des données ...")
	public String importDatasHellip();

	@DefaultStringValue("Charger")
	public String uploadHellip();

	@DefaultStringValue("Télécharger")
	public String downloadHellip();

	@DefaultStringValue("Fichier Csv (csv)")
	public String csvFile();

	@DefaultStringValue("Dupliquer...")
	public String cloneEllipsis();

	@DefaultStringValue("Merci de sélectionner un élément")
	public String selectAnItem();

	@DefaultStringValue("Taille maximum : {0} {0,choice,0#caractère|1#caractère|1<caractères}")
	public String maximumLengthCharacters(int i);

	@DefaultStringValue("Supprimer tout")
	public String deleteAll();

	@DefaultStringValue("Ajouter un favoris ...")
	public String addFavoriteHellip();

	@DefaultStringValue("Ajouter un favoris")
	public String addFavorite();

	@DefaultStringValue("Supprimer un favoris ...")
	public String deleteFavoriteHellip();

	@DefaultStringValue("Supprimer un favoris")
	public String deleteFavorite();

	@DefaultStringValue("Supprimer les filtres")
	public String clearsFilters();

	@DefaultStringValue("Filtres")
	public String filters();

	@DefaultStringValue("Coller depuis le presse-papier")
	public String pasteClipboard();

	@DefaultStringValue("{1} {1,choice,0#élément sélectionné|1#élément sélectionné|1<éléments sélectionnés} ({0})")
	public String selectedItems(int size, int s);

	@DefaultStringValue("Voulez-vous supprimer l'élément sélectionné ?")
	public String doYouWantToDeleteTheSelectedItem();

	@DefaultStringValue("Voulez-vous supprimer l(es) élément(s) sélectionné(s) ?")
	public String doYouWantToDeleteTheSelectedItemS();

	@DefaultStringValue("Supprimer ...")
	public String deleteEllipsis();

	@DefaultStringValue("Ouvrir la carte ...")
	public String openMapEllipsis();

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

	@DefaultStringValue("Aucune sélection")
	public String noneSelected();

	@DefaultStringValue("Double clic pour supprimer")
	public String doubleClickToClear();

	@DefaultStringValue("Rechercher ...")
	public String searchEllipsis();

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

	@DefaultStringValue("Chargement de la page {0}...")
	public String fetchingPageX(int page);

	@DefaultStringValue("Chargement de {0} ligne(s)...")
	public String fetchingXLines(int lines);

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

	@DefaultStringValue("Demo")
	public String demo();

	@DefaultStringValue("Bac à sable")
	public String sandbox();

	@DefaultStringValue("LOCAL")
	public String local();

	@DefaultStringValue("PRODUCTION")
	public String prod();

	@DefaultStringValue("PRE-PRODUCTION")
	public String preprod();

	@DefaultStringValue("FORMATION")
	public String formation();

	@DefaultStringValue("INTEGRATION")
	public String integration();

	@DefaultStringValue("Déconnecté")
	public String disconnected();

	@DefaultStringValue("Reconnexion dans {0} {0,choice,0#seconde|1#seconde|1<secondes}")
	public String reconnectingInXSeconds(int seconds);

	@DefaultStringValue("Vous avez été déconnecté\nL'application va se fermer.")
	public String youHaveBeenDisconnectedTheApplicationWillNowStop();

	@DefaultStringValue("Message d'un admin")
	public String messageFromAdmin();

	@DefaultStringValue("Contrainte d''unicité sur le champ :\n {0}")
	public String unicityConstraintExceptionOnField(String description);

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

	@DefaultStringValue("Copier")
	public String copy();

	@DefaultStringValue("Corbeille")
	public String bin();

	@DefaultStringValue("Exporter en pdf...")
	public String exportToPdfEllipsis();

	@DefaultStringValue("Exporter en txt..")
	public String exportToTxtEllipsis();

	@DefaultStringValue("Terminé")
	public String finish();

	@DefaultStringValue("Couper")
	public String cut();

	@DefaultStringValue("Descendre")
	public String down();

	@DefaultStringValue("Coller")
	public String paste();

	@DefaultStringValue("Imprimer...")
	public String printEllipsis();

	@DefaultStringValue("Envoyer")
	public String send();

	@DefaultStringValue("Paramètres...")
	public String settingsEllipsis();

	@DefaultStringValue("Monter")
	public String up();

	@DefaultStringValue("Choisir")
	public String choose();

	@DefaultStringValue("Vous devez ouvrir le lien dans votre navigateur web")
	public String youHaveToOpenTheLinkInYourWebBrowser();

	@DefaultStringValue("Donner un libellé")
	public String enterAMeaning();

	@DefaultStringValue("Vous ne pouvez pas exporter une table avec plus de {0} lignes")
	public String maxExcelExportLine(int count);

	@DefaultStringValue("Création du fichier excel")
	public String createExcelFile();

	@DefaultStringValue("Exporter...")
	public String exportEllipsis();

	@DefaultStringValue("Exporter la page courante en excel...")
	public String exportCurrentPageExcelEllipsis();

	@DefaultStringValue("Exporter toutes les pages en excel...")
	public String exportAllPagesExcelEllipsis();

	@DefaultStringValue("Exporter en excel")
	public String exportExcel();

	@DefaultStringValue("Ajouter plusieurs valeurs")
	public String addMany();

	@DefaultStringValue("Filtrer")
	public String doFilter();

	@DefaultStringValue("Fuseau horaire")
	public String timezone();

	@DefaultStringValue("Veuillez vérifier votre saisie.")
	public String pleaseVerifyYourInput();

	@DefaultStringValue("Cet élément existe déjà dans la base de données")
	public String unicityConstraintException();

	@DefaultStringValue("Validation")
	public String validation();

	@DefaultStringValue("Tout étendre")
	public String expandAll();

	@DefaultStringValue("Tout réduire")
	public String collapseAll();

	@DefaultStringValue("Erreur : fenêtre invalide")
	public String errorInvalidWindow();

	@DefaultStringValue("Vous avez probablement fermé l'application avec une fenêtre qui n'existe plus")
	public String youMightHaveClosedTheApplicationWithAWindowThatDoesntExistAnymore();

	@DefaultStringValue("Fenêtre invalide")
	public String invalidWindow();

	@DefaultStringValue("Ok et saisir à nouveau")
	public String okAndReopen();

	@DefaultStringValue("Nombre de lignes ?")
	public String numberOfLines();

	@DefaultStringValue("Axe de recherche")
	public String searchAxis();

	@DefaultStringValue("Fichier Zip (zip)")
	public String zipFile();

	@DefaultStringValue("Somme")
	public String sum();

	@DefaultStringValue("Moyenne")
	public String mean();

	@DefaultStringValue("Une erreur inattendue s'est produite")
	public String basicErrorMessage();

	@DefaultStringValue("Message")
	public String message();

	@DefaultStringValue("Au moins une colonne doit être sélectionnée")
	public String aColumnAtLeastMustBeSelected();

	@DefaultStringValue("Vrai")
	public String printTrue();

	@DefaultStringValue("Faux")
	public String printFalse();

	@DefaultStringValue("Tableau hiérarchique")
	public String hierarchicalTable();

	@DefaultStringValue("{0} (Verrouillé)")
	public String columnLocked(String name);

	@DefaultStringValue("Colonnes par défaut")
	public String defaultColumns();

	@DefaultStringValue("Affichage des colonnes")
	public String columnDisplay();

	@DefaultStringValue("Etape {0}")
	public String step(int step);

	@DefaultStringValue("Afficher toute la recherche...")
	public String showAllItems();

	@DefaultStringValue("Envoyer un rapport d'erreur")
	public String reportError();

	@DefaultStringValue("Aucun résultat")
	public String noResult();

	@DefaultStringValue("Lancer une recherche")
	public String launchSearch();

}
