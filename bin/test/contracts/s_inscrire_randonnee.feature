# language: fr
Fonctionnalité: S'inscrire à une randonnée
  Les membres actifs admissibles prennent une place ou attendent leur tour.

  Scénario: Confirmer une inscription lorsqu'une place existe
    Étant donné un randonneur connecté dont l'adhésion est active
    Et une randonnée admissible ouverte avec une place restante
    Et la date limite d'inscription n'est pas atteinte
    Quand le randonneur s'inscrit à la randonnée
    Alors son inscription est confirmée et horodatée
    Et la randonnée devient complète
    Et un courriel de confirmation est demandé

  Scénario: Ajouter à la liste d'attente dans l'ordre d'arrivée
    Étant donné une randonnée complète avant sa date limite
    Et un randonneur connecté admissible sans inscription à cette randonnée
    Quand le randonneur demande son inscription
    Alors il rejoint la fin de la liste d'attente horodatée
    Et sa position lui est communiquée

  Scénario: Rejeter une difficulté excessive ou une adhésion inactive
    Étant donné un randonneur dont l'adhésion est suspendue
    Quand le randonneur tente de s'inscrire à une randonnée
    Alors l'inscription est refusée

  Scénario: Empêcher les inscriptions en double
    Étant donné un randonneur déjà inscrit à une randonnée
    Quand il tente de s'y inscrire une deuxième fois
    Alors aucune deuxième inscription active n'est créée
