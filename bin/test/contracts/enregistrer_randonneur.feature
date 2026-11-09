# language: fr
Fonctionnalité: Enregistrer un randonneur
  Un membre du club utilise un code d'invitation pour ouvrir un compte.

  Scénario: Inscription valide d'un membre adulte
    Étant donné un code d'invitation valide et non utilisé
    Et une adresse courriel non enregistrée
    Quand un membre adulte soumet ses données obligatoires et accepte la décharge
    Alors un compte de randonneur est créé avec une adhésion active
    Et le code d'invitation ne peut plus servir à une autre inscription

  Scénario: Décharge d'un membre mineur
    Étant donné un membre âgé de moins de 18 ans
    Quand le membre soumet ses données sans consentement du tuteur légal
    Alors la création du compte est refusée
    Et le système demande le nom du tuteur et son acceptation de la décharge

  Scénario: Refuser une invitation non valide
    Étant donné un code d'invitation inconnu ou déjà utilisé
    Quand un membre tente de s'enregistrer
    Alors aucun compte de randonneur n'est créé
