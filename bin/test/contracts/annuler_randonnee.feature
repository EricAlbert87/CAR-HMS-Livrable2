# language: fr
Fonctionnalité: Annuler une randonnée
  L'organisateur responsable ou un administrateur peut annuler avant le départ.

  Scénario: Annuler une randonnée et prévenir les inscrits
    Étant donné une randonnée future avec des inscrits confirmés et en attente
    Et un administrateur authentifié
    Quand l'administrateur annule la randonnée avec un motif non vide
    Alors le statut de la randonnée devient « ANNULÉE »
    Et toutes les inscriptions confirmées et en attente deviennent « ANNULÉE »
    Et un courriel contenant le motif est demandé pour chaque inscrit
    Et l'identité de l'administrateur est enregistrée

  Scénario: Refuser l'annulation par un organisateur non responsable
    Étant donné une randonnée future créée par un autre organisateur
    Quand un organisateur non administrateur tente de l'annuler
    Alors l'annulation est refusée

  Scénario: Refuser l'annulation après le début
    Étant donné une randonnée dont la date de début est passée
    Quand son organisateur demande son annulation
    Alors l'annulation est refusée
