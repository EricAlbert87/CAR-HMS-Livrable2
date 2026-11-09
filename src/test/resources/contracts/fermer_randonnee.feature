# language: fr
Fonctionnalité: Fermer une randonnée
  Le système bloque les mouvements d'inscription dès la date limite.

  Scénario: Fermer automatiquement une randonnée à la date limite
    Étant donné une randonnée ouverte ou complète
    Et la date limite d'inscription est atteinte
    Quand le système exécute la fermeture automatique
    Alors la randonnée passe au statut « FERMÉE »
    Et l'auteur de la transition est enregistré comme « SYSTEM »
    Et aucune nouvelle inscription ni annulation n'est acceptée

  Scénario: Conserver le statut annulé
    Étant donné une randonnée annulée avant la date limite
    Quand le système vérifie les fermetures à effectuer
    Alors le statut de la randonnée reste « ANNULÉE »
