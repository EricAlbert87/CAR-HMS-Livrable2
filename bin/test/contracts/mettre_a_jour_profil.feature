# language: fr
Fonctionnalité: Mettre à jour le profil d'un randonneur
  Un randonneur peut modifier ses informations personnelles.

  Scénario: Modifier ses propres coordonnées
    Étant donné un randonneur authentifié avec un profil existant
    Quand il soumet un numéro de téléphone et une adresse valides
    Alors ses informations personnelles sont mises à jour

  Scénario: Interdire la modification du profil d'autrui
    Étant donné un randonneur authentifié sans droit d'administration
    Quand il demande de modifier le profil d'un autre randonneur
    Alors l'opération est refusée
