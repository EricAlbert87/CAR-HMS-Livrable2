# language: fr
Fonctionnalité: Créer une randonnée
  Seul un organisateur qualifié peut publier un événement conforme.

  Scénario: Créer une randonnée admissible
    Étant donné un organisateur authentifié dont le certificat est valide à la date prévue
    Et un sentier CCN où la randonnée est permise, ouvert à la saison choisie
    Et un niveau d'organisateur suffisant pour la difficulté du sentier
    Quand l'organisateur soumet un titre, une description, un horaire, une durée et une capacité valides
    Et la date limite d'inscription se situe dans les 14 jours précédant le départ
    Alors un identifiant unique est attribué à la randonnée
    Et son statut initial est « OUVERTE »
    Et son créateur est enregistré

  Scénario: Rejeter un certificat expiré le jour de la randonnée
    Étant donné un organisateur dont le certificat expire avant la date prévue
    Quand il tente de créer la randonnée
    Alors aucune randonnée n'est créée

  Scénario: Refuser un sentier fermé en saison ou interdit à la randonnée
    Étant donné un sentier CCN non admissible à la date demandée
    Quand un organisateur tente d'y créer une randonnée
    Alors la création est refusée

  Scénario: Refuser une date limite antérieure de plus de 14 jours
    Étant donné un organisateur et un sentier admissibles
    Quand la date limite est fixée à 15 jours avant le départ
    Alors la création est refusée
