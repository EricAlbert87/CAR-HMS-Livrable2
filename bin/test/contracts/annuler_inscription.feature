# language: fr
Fonctionnalité: Annuler une inscription
  Une annulation confirme la désinscription et peut libérer une place.

  Scénario: Promouvoir la première personne en attente
    Étant donné une randonnée complète avant la date limite d'inscription
    Et une inscription confirmée et deux inscriptions en attente ordonnées
    Quand le titulaire de la place confirmée annule son inscription
    Alors l'inscription du titulaire passe à « ANNULÉE »
    Et la première inscription en attente passe à « CONFIRMÉE »
    Et un courriel de promotion est demandé
    Et la seconde inscription conserve sa place dans la file

  Scénario: Redevenir ouverte lorsqu'aucune personne n'attend
    Étant donné une randonnée complète sans liste d'attente
    Quand un randonneur annule son inscription confirmée avant la limite
    Alors la randonnée redevient ouverte

  Scénario: Refuser l'annulation après la date limite
    Étant donné une randonnée dont la date limite d'inscription est passée
    Quand un randonneur demande l'annulation de son inscription
    Alors l'annulation est refusée et l'inscription ne change pas
