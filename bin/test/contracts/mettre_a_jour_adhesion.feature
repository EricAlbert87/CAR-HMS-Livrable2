# language: fr
Fonctionnalité: Mettre à jour le statut d'adhésion
  Un administrateur peut modifier l'admissibilité d'un membre.

  Scénario: Suspendre une adhésion
    Étant donné un administrateur authentifié et un randonneur actif
    Quand l'administrateur place son adhésion au statut « SUSPENDUE »
    Alors le nouveau statut est enregistré
    Et le randonneur ne peut plus effectuer de nouvelle inscription

  Scénario: Refuser un changement par un randonneur ordinaire
    Étant donné un randonneur authentifié sans rôle d'administrateur
    Quand il tente de modifier un statut d'adhésion
    Alors l'opération est refusée
