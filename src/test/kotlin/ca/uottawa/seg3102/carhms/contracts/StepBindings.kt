package ca.uottawa.seg3102.carhms.contracts

import io.cucumber.java.en.Given

/** Correspondances exactes des étapes; générées depuis les 11 contrats. */
class StepBindings {
    private val handler = Steps()

    @Given("^\\Qaucun compte de randonneur n'est créé\\E${'$'}")
    fun etape001() = handler.step("aucun compte de randonneur n'est créé")

    @Given("^\\Qaucun inscrit uniquement en attente ne reçoit ce rappel\\E${'$'}")
    fun etape002() = handler.step("aucun inscrit uniquement en attente ne reçoit ce rappel")

    @Given("^\\Qaucune deuxième inscription active n'est créée\\E${'$'}")
    fun etape003() = handler.step("aucune deuxième inscription active n'est créée")

    @Given("^\\Qaucune nouvelle inscription ni annulation n'est acceptée\\E${'$'}")
    fun etape004() = handler.step("aucune nouvelle inscription ni annulation n'est acceptée")

    @Given("^\\Qaucune randonnée n'est créée\\E${'$'}")
    fun etape005() = handler.step("aucune randonnée n'est créée")

    @Given("^\\Qchaque inscrit confirmé reçoit un rappel avec les détails de la randonnée\\E${'$'}")
    fun etape006() = handler.step("chaque inscrit confirmé reçoit un rappel avec les détails de la randonnée")

    @Given("^\\Qdes randonnées ouvertes et une randonnée annulée\\E${'$'}")
    fun etape007() = handler.step("des randonnées ouvertes et une randonnée annulée")

    @Given("^\\Qdes randonnées sur plusieurs sentiers et à diverses dates\\E${'$'}")
    fun etape008() = handler.step("des randonnées sur plusieurs sentiers et à diverses dates")

    @Given("^\\Qil demande de modifier le profil d'un autre randonneur\\E${'$'}")
    fun etape009() = handler.step("il demande de modifier le profil d'un autre randonneur")

    @Given("^\\Qil indique une expiration antérieure à la délivrance\\E${'$'}")
    fun etape010() = handler.step("il indique une expiration antérieure à la délivrance")

    @Given("^\\Qil rejoint la fin de la liste d'attente horodatée\\E${'$'}")
    fun etape011() = handler.step("il rejoint la fin de la liste d'attente horodatée")

    @Given("^\\Qil soumet un numéro de téléphone et une adresse valides\\E${'$'}")
    fun etape012() = handler.step("il soumet un numéro de téléphone et une adresse valides")

    @Given("^\\Qil tente de créer la randonnée\\E${'$'}")
    fun etape013() = handler.step("il tente de créer la randonnée")

    @Given("^\\Qil tente de modifier un statut d'adhésion\\E${'$'}")
    fun etape014() = handler.step("il tente de modifier un statut d'adhésion")

    @Given("^\\Qil tente de s'y inscrire une deuxième fois\\E${'$'}")
    fun etape015() = handler.step("il tente de s'y inscrire une deuxième fois")

    @Given("^\\Ql'administrateur annule la randonnée avec un motif non vide\\E${'$'}")
    fun etape016() = handler.step("l'administrateur annule la randonnée avec un motif non vide")

    @Given("^\\Ql'administrateur fournit un certificat numéroté, des dates valides et un niveau maximal\\E${'$'}")
    fun etape017() = handler.step("l'administrateur fournit un certificat numéroté, des dates valides et un niveau maximal")

    @Given("^\\Ql'administrateur place son adhésion au statut « SUSPENDUE »\\E${'$'}")
    fun etape018() = handler.step("l'administrateur place son adhésion au statut « SUSPENDUE »")

    @Given("^\\Ql'annulation est refusée\\E${'$'}")
    fun etape019() = handler.step("l'annulation est refusée")

    @Given("^\\Ql'annulation est refusée et l'inscription ne change pas\\E${'$'}")
    fun etape020() = handler.step("l'annulation est refusée et l'inscription ne change pas")

    @Given("^\\Ql'auteur de la transition est enregistré comme « SYSTEM »\\E${'$'}")
    fun etape021() = handler.step("l'auteur de la transition est enregistré comme « SYSTEM »")

    @Given("^\\Ql'identité de l'administrateur est enregistrée\\E${'$'}")
    fun etape022() = handler.step("l'identité de l'administrateur est enregistrée")

    @Given("^\\Ql'inscription du titulaire passe à « ANNULÉE »\\E${'$'}")
    fun etape023() = handler.step("l'inscription du titulaire passe à « ANNULÉE »")

    @Given("^\\Ql'inscription est refusée\\E${'$'}")
    fun etape024() = handler.step("l'inscription est refusée")

    @Given("^\\Ql'opération est refusée\\E${'$'}")
    fun etape025() = handler.step("l'opération est refusée")

    @Given("^\\Ql'organisateur soumet un titre, une description, un horaire, une durée et une capacité valides\\E${'$'}")
    fun etape026() = handler.step("l'organisateur soumet un titre, une description, un horaire, une durée et une capacité valides")

    @Given("^\\Qla certification n'est pas enregistrée\\E${'$'}")
    fun etape027() = handler.step("la certification n'est pas enregistrée")

    @Given("^\\Qla création du compte est refusée\\E${'$'}")
    fun etape028() = handler.step("la création du compte est refusée")

    @Given("^\\Qla création est refusée\\E${'$'}")
    fun etape029() = handler.step("la création est refusée")

    @Given("^\\Qla date limite d'inscription est atteinte\\E${'$'}")
    fun etape030() = handler.step("la date limite d'inscription est atteinte")

    @Given("^\\Qla date limite d'inscription n'est pas atteinte\\E${'$'}")
    fun etape031() = handler.step("la date limite d'inscription n'est pas atteinte")

    @Given("^\\Qla date limite d'inscription se situe dans les 14 jours précédant le départ\\E${'$'}")
    fun etape032() = handler.step("la date limite d'inscription se situe dans les 14 jours précédant le départ")

    @Given("^\\Qla date limite est fixée à 15 jours avant le départ\\E${'$'}")
    fun etape033() = handler.step("la date limite est fixée à 15 jours avant le départ")

    @Given("^\\Qla première inscription en attente passe à « CONFIRMÉE »\\E${'$'}")
    fun etape034() = handler.step("la première inscription en attente passe à « CONFIRMÉE »")

    @Given("^\\Qla randonnée annulée n'est pas présentée\\E${'$'}")
    fun etape035() = handler.step("la randonnée annulée n'est pas présentée")

    @Given("^\\Qla randonnée complète est affichée avec zéro place disponible\\E${'$'}")
    fun etape036() = handler.step("la randonnée complète est affichée avec zéro place disponible")

    @Given("^\\Qla randonnée devient complète\\E${'$'}")
    fun etape037() = handler.step("la randonnée devient complète")

    @Given("^\\Qla randonnée passe au statut « FERMÉE »\\E${'$'}")
    fun etape038() = handler.step("la randonnée passe au statut « FERMÉE »")

    @Given("^\\Qla randonnée redevient ouverte\\E${'$'}")
    fun etape039() = handler.step("la randonnée redevient ouverte")

    @Given("^\\Qla seconde inscription conserve sa place dans la file\\E${'$'}")
    fun etape040() = handler.step("la seconde inscription conserve sa place dans la file")

    @Given("^\\Qle code d'invitation ne peut plus servir à une autre inscription\\E${'$'}")
    fun etape041() = handler.step("le code d'invitation ne peut plus servir à une autre inscription")

    @Given("^\\Qle membre soumet ses données sans consentement du tuteur légal\\E${'$'}")
    fun etape042() = handler.step("le membre soumet ses données sans consentement du tuteur légal")

    @Given("^\\Qle message contient un avertissement météorologique\\E${'$'}")
    fun etape043() = handler.step("le message contient un avertissement météorologique")

    @Given("^\\Qle nom du sentier, l'organisateur et les places disponibles sont indiqués\\E${'$'}")
    fun etape044() = handler.step("le nom du sentier, l'organisateur et les places disponibles sont indiqués")

    @Given("^\\Qle nouveau statut est enregistré\\E${'$'}")
    fun etape045() = handler.step("le nouveau statut est enregistré")

    @Given("^\\Qle randonneur demande son inscription\\E${'$'}")
    fun etape046() = handler.step("le randonneur demande son inscription")

    @Given("^\\Qle randonneur devient organisateur de randonnée\\E${'$'}")
    fun etape047() = handler.step("le randonneur devient organisateur de randonnée")

    @Given("^\\Qle randonneur ne peut plus effectuer de nouvelle inscription\\E${'$'}")
    fun etape048() = handler.step("le randonneur ne peut plus effectuer de nouvelle inscription")

    @Given("^\\Qle randonneur s'inscrit à la randonnée\\E${'$'}")
    fun etape049() = handler.step("le randonneur s'inscrit à la randonnée")

    @Given("^\\Qle randonneur tente de s'inscrire à une randonnée\\E${'$'}")
    fun etape050() = handler.step("le randonneur tente de s'inscrire à une randonnée")

    @Given("^\\Qle rappel est envoyé sans avertissement météorologique\\E${'$'}")
    fun etape051() = handler.step("le rappel est envoyé sans avertissement météorologique")

    @Given("^\\Qle service météorologique annonce de la pluie au point de départ\\E${'$'}")
    fun etape052() = handler.step("le service météorologique annonce de la pluie au point de départ")

    @Given("^\\Qle service météorologique est indisponible\\E${'$'}")
    fun etape053() = handler.step("le service météorologique est indisponible")

    @Given("^\\Qle statut de la randonnée devient « ANNULÉE »\\E${'$'}")
    fun etape054() = handler.step("le statut de la randonnée devient « ANNULÉE »")

    @Given("^\\Qle statut de la randonnée reste « ANNULÉE »\\E${'$'}")
    fun etape055() = handler.step("le statut de la randonnée reste « ANNULÉE »")

    @Given("^\\Qle système demande le nom du tuteur et son acceptation de la décharge\\E${'$'}")
    fun etape056() = handler.step("le système demande le nom du tuteur et son acceptation de la décharge")

    @Given("^\\Qle système exécute la fermeture automatique\\E${'$'}")
    fun etape057() = handler.step("le système exécute la fermeture automatique")

    @Given("^\\Qle système prépare le rappel\\E${'$'}")
    fun etape058() = handler.step("le système prépare le rappel")

    @Given("^\\Qle système prépare les rappels\\E${'$'}")
    fun etape059() = handler.step("le système prépare les rappels")

    @Given("^\\Qle système vérifie les fermetures à effectuer\\E${'$'}")
    fun etape060() = handler.step("le système vérifie les fermetures à effectuer")

    @Given("^\\Qle titulaire de la place confirmée annule son inscription\\E${'$'}")
    fun etape061() = handler.step("le titulaire de la place confirmée annule son inscription")

    @Given("^\\Qles détails de certification sont enregistrés\\E${'$'}")
    fun etape062() = handler.step("les détails de certification sont enregistrés")

    @Given("^\\Qles randonnées ouvertes sont présentées par date croissante\\E${'$'}")
    fun etape063() = handler.step("les randonnées ouvertes sont présentées par date croissante")

    @Given("^\\Qsa position lui est communiquée\\E${'$'}")
    fun etape064() = handler.step("sa position lui est communiquée")

    @Given("^\\Qses informations personnelles sont mises à jour\\E${'$'}")
    fun etape065() = handler.step("ses informations personnelles sont mises à jour")

    @Given("^\\Qseules les randonnées correspondant aux trois filtres sont présentées\\E${'$'}")
    fun etape066() = handler.step("seules les randonnées correspondant aux trois filtres sont présentées")

    @Given("^\\Qson créateur est enregistré\\E${'$'}")
    fun etape067() = handler.step("son créateur est enregistré")

    @Given("^\\Qson inscription est confirmée et horodatée\\E${'$'}")
    fun etape068() = handler.step("son inscription est confirmée et horodatée")

    @Given("^\\Qson organisateur demande son annulation\\E${'$'}")
    fun etape069() = handler.step("son organisateur demande son annulation")

    @Given("^\\Qson statut initial est « OUVERTE »\\E${'$'}")
    fun etape070() = handler.step("son statut initial est « OUVERTE »")

    @Given("^\\Qtoutes les inscriptions confirmées et en attente deviennent « ANNULÉE »\\E${'$'}")
    fun etape071() = handler.step("toutes les inscriptions confirmées et en attente deviennent « ANNULÉE »")

    @Given("^\\Qun administrateur authentifié\\E${'$'}")
    fun etape072() = handler.step("un administrateur authentifié")

    @Given("^\\Qun administrateur authentifié et un randonneur actif\\E${'$'}")
    fun etape073() = handler.step("un administrateur authentifié et un randonneur actif")

    @Given("^\\Qun code d'invitation inconnu ou déjà utilisé\\E${'$'}")
    fun etape074() = handler.step("un code d'invitation inconnu ou déjà utilisé")

    @Given("^\\Qun code d'invitation valide et non utilisé\\E${'$'}")
    fun etape075() = handler.step("un code d'invitation valide et non utilisé")

    @Given("^\\Qun compte de randonneur est créé avec une adhésion active\\E${'$'}")
    fun etape076() = handler.step("un compte de randonneur est créé avec une adhésion active")

    @Given("^\\Qun courriel contenant le motif est demandé pour chaque inscrit\\E${'$'}")
    fun etape077() = handler.step("un courriel contenant le motif est demandé pour chaque inscrit")

    @Given("^\\Qun courriel de confirmation est demandé\\E${'$'}")
    fun etape078() = handler.step("un courriel de confirmation est demandé")

    @Given("^\\Qun courriel de promotion est demandé\\E${'$'}")
    fun etape079() = handler.step("un courriel de promotion est demandé")

    @Given("^\\Qun identifiant unique est attribué à la randonnée\\E${'$'}")
    fun etape080() = handler.step("un identifiant unique est attribué à la randonnée")

    @Given("^\\Qun membre adulte soumet ses données obligatoires et accepte la décharge\\E${'$'}")
    fun etape081() = handler.step("un membre adulte soumet ses données obligatoires et accepte la décharge")

    @Given("^\\Qun membre tente de s'enregistrer\\E${'$'}")
    fun etape082() = handler.step("un membre tente de s'enregistrer")

    @Given("^\\Qun membre âgé de moins de 18 ans\\E${'$'}")
    fun etape083() = handler.step("un membre âgé de moins de 18 ans")

    @Given("^\\Qun niveau d'organisateur suffisant pour la difficulté du sentier\\E${'$'}")
    fun etape084() = handler.step("un niveau d'organisateur suffisant pour la difficulté du sentier")

    @Given("^\\Qun organisateur authentifié dont le certificat est valide à la date prévue\\E${'$'}")
    fun etape085() = handler.step("un organisateur authentifié dont le certificat est valide à la date prévue")

    @Given("^\\Qun organisateur dont le certificat expire avant la date prévue\\E${'$'}")
    fun etape086() = handler.step("un organisateur dont le certificat expire avant la date prévue")

    @Given("^\\Qun organisateur et un sentier admissibles\\E${'$'}")
    fun etape087() = handler.step("un organisateur et un sentier admissibles")

    @Given("^\\Qun organisateur non administrateur tente de l'annuler\\E${'$'}")
    fun etape088() = handler.step("un organisateur non administrateur tente de l'annuler")

    @Given("^\\Qun organisateur tente d'y créer une randonnée\\E${'$'}")
    fun etape089() = handler.step("un organisateur tente d'y créer une randonnée")

    @Given("^\\Qun randonneur annule son inscription confirmée avant la limite\\E${'$'}")
    fun etape090() = handler.step("un randonneur annule son inscription confirmée avant la limite")

    @Given("^\\Qun randonneur authentifié avec un profil existant\\E${'$'}")
    fun etape091() = handler.step("un randonneur authentifié avec un profil existant")

    @Given("^\\Qun randonneur authentifié sans droit d'administration\\E${'$'}")
    fun etape092() = handler.step("un randonneur authentifié sans droit d'administration")

    @Given("^\\Qun randonneur authentifié sans rôle d'administrateur\\E${'$'}")
    fun etape093() = handler.step("un randonneur authentifié sans rôle d'administrateur")

    @Given("^\\Qun randonneur avec une adhésion active\\E${'$'}")
    fun etape094() = handler.step("un randonneur avec une adhésion active")

    @Given("^\\Qun randonneur connecté admissible sans inscription à cette randonnée\\E${'$'}")
    fun etape095() = handler.step("un randonneur connecté admissible sans inscription à cette randonnée")

    @Given("^\\Qun randonneur connecté dont l'adhésion est active\\E${'$'}")
    fun etape096() = handler.step("un randonneur connecté dont l'adhésion est active")

    @Given("^\\Qun randonneur demande l'annulation de son inscription\\E${'$'}")
    fun etape097() = handler.step("un randonneur demande l'annulation de son inscription")

    @Given("^\\Qun randonneur dont l'adhésion est suspendue\\E${'$'}")
    fun etape098() = handler.step("un randonneur dont l'adhésion est suspendue")

    @Given("^\\Qun randonneur déjà inscrit à une randonnée\\E${'$'}")
    fun etape099() = handler.step("un randonneur déjà inscrit à une randonnée")

    @Given("^\\Qun sentier CCN non admissible à la date demandée\\E${'$'}")
    fun etape100() = handler.step("un sentier CCN non admissible à la date demandée")

    @Given("^\\Qun sentier CCN où la randonnée est permise, ouvert à la saison choisie\\E${'$'}")
    fun etape101() = handler.step("un sentier CCN où la randonnée est permise, ouvert à la saison choisie")

    @Given("^\\Qun visiteur demande le sentier « Sentier A », une date et la difficulté « FACILE »\\E${'$'}")
    fun etape102() = handler.step("un visiteur demande le sentier « Sentier A », une date et la difficulté « FACILE »")

    @Given("^\\Qun visiteur parcourt les randonnées avec l'option « inclure les randonnées complètes »\\E${'$'}")
    fun etape103() = handler.step("un visiteur parcourt les randonnées avec l'option « inclure les randonnées complètes »")

    @Given("^\\Qun visiteur parcourt les randonnées sans filtre\\E${'$'}")
    fun etape104() = handler.step("un visiteur parcourt les randonnées sans filtre")

    @Given("^\\Qune adresse courriel non enregistrée\\E${'$'}")
    fun etape105() = handler.step("une adresse courriel non enregistrée")

    @Given("^\\Qune inscription confirmée et deux inscriptions en attente ordonnées\\E${'$'}")
    fun etape106() = handler.step("une inscription confirmée et deux inscriptions en attente ordonnées")

    @Given("^\\Qune randonnée admissible ouverte avec une place restante\\E${'$'}")
    fun etape107() = handler.step("une randonnée admissible ouverte avec une place restante")

    @Given("^\\Qune randonnée annulée avant la date limite\\E${'$'}")
    fun etape108() = handler.step("une randonnée annulée avant la date limite")

    @Given("^\\Qune randonnée complète avant la date limite d'inscription\\E${'$'}")
    fun etape109() = handler.step("une randonnée complète avant la date limite d'inscription")

    @Given("^\\Qune randonnée complète avant sa date limite\\E${'$'}")
    fun etape110() = handler.step("une randonnée complète avant sa date limite")

    @Given("^\\Qune randonnée complète dont la date limite d'inscription n'est pas atteinte\\E${'$'}")
    fun etape111() = handler.step("une randonnée complète dont la date limite d'inscription n'est pas atteinte")

    @Given("^\\Qune randonnée complète sans liste d'attente\\E${'$'}")
    fun etape112() = handler.step("une randonnée complète sans liste d'attente")

    @Given("^\\Qune randonnée dont la date de début est passée\\E${'$'}")
    fun etape113() = handler.step("une randonnée dont la date de début est passée")

    @Given("^\\Qune randonnée dont la date limite d'inscription est passée\\E${'$'}")
    fun etape114() = handler.step("une randonnée dont la date limite d'inscription est passée")

    @Given("^\\Qune randonnée future avec des inscrits confirmés et en attente\\E${'$'}")
    fun etape115() = handler.step("une randonnée future avec des inscrits confirmés et en attente")

    @Given("^\\Qune randonnée future créée par un autre organisateur\\E${'$'}")
    fun etape116() = handler.step("une randonnée future créée par un autre organisateur")

    @Given("^\\Qune randonnée ouverte ou complète\\E${'$'}")
    fun etape117() = handler.step("une randonnée ouverte ou complète")

    @Given("^\\Qune randonnée prévue le lendemain avec des inscriptions confirmées\\E${'$'}")
    fun etape118() = handler.step("une randonnée prévue le lendemain avec des inscriptions confirmées")

    @Given("^\\Qune randonnée prévue le lendemain avec une inscription confirmée\\E${'$'}")
    fun etape119() = handler.step("une randonnée prévue le lendemain avec une inscription confirmée")

}
