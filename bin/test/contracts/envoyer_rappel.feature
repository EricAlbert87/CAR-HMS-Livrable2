# language: fr
Fonctionnalité: Envoyer un rappel de randonnée
  Les inscrits confirmés reçoivent un rappel la veille.

  Scénario: Inclure un avertissement pour pluie ou neige
    Étant donné une randonnée prévue le lendemain avec des inscriptions confirmées
    Et le service météorologique annonce de la pluie au point de départ
    Quand le système prépare les rappels
    Alors chaque inscrit confirmé reçoit un rappel avec les détails de la randonnée
    Et le message contient un avertissement météorologique
    Et aucun inscrit uniquement en attente ne reçoit ce rappel

  Scénario: Envoyer les rappels si la météo est indisponible
    Étant donné une randonnée prévue le lendemain avec une inscription confirmée
    Et le service météorologique est indisponible
    Quand le système prépare le rappel
    Alors le rappel est envoyé sans avertissement météorologique
