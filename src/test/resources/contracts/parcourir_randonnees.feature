# language: fr
Fonctionnalité: Parcourir les randonnées
  Les visiteurs et les membres recherchent les randonnées accessibles.

  Scénario: Consulter les randonnées classées par date
    Étant donné des randonnées ouvertes et une randonnée annulée
    Quand un visiteur parcourt les randonnées sans filtre
    Alors les randonnées ouvertes sont présentées par date croissante
    Et la randonnée annulée n'est pas présentée
    Et le nom du sentier, l'organisateur et les places disponibles sont indiqués

  Scénario: Filtrer les randonnées par sentier, date et difficulté
    Étant donné des randonnées sur plusieurs sentiers et à diverses dates
    Quand un visiteur demande le sentier « Sentier A », une date et la difficulté « FACILE »
    Alors seules les randonnées correspondant aux trois filtres sont présentées

  Scénario: Visualiser une randonnée complète pour rejoindre la liste d'attente
    Étant donné une randonnée complète dont la date limite d'inscription n'est pas atteinte
    Quand un visiteur parcourt les randonnées avec l'option « inclure les randonnées complètes »
    Alors la randonnée complète est affichée avec zéro place disponible
