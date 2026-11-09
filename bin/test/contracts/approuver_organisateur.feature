# language: fr
Fonctionnalité: Approuver un organisateur
  Un administrateur saisit la certification de chef de randonnée.

  Scénario: Accorder le rôle d'organisateur à un membre certifié
    Étant donné un administrateur authentifié
    Et un randonneur avec une adhésion active
    Quand l'administrateur fournit un certificat numéroté, des dates valides et un niveau maximal
    Alors les détails de certification sont enregistrés
    Et le randonneur devient organisateur de randonnée

  Scénario: Refuser une certification dont les dates sont incohérentes
    Étant donné un administrateur authentifié
    Quand il indique une expiration antérieure à la délivrance
    Alors la certification n'est pas enregistrée
