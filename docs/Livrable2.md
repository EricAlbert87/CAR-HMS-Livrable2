# SEG 3502 — Projet CAR-HMS — Livrable 2 : Domaine

**Équipe :** [NOM DE L'ÉQUIPE]  
**Produit :** Système de gestion des événements de randonnée du Cairn (CAR-HMS)  
**Cours :** SEG 3502 — Automne 2026  
**Professeur :** [NOM DU PROFESSEUR]  
**Date de remise :** [DATE CONFIRMÉE DANS BRIGHTSPACE]  
**Numéro du livrable :** 2

| Membre | Numéro étudiant | Identifiant GitHub |
|---|---|---|
| [Membre 1] | [Numéro] | [GitHub] |
| [Membre 2] | [Numéro] | [GitHub] |
| [Membre 3] | [Numéro] | [GitHub] |
| [Membre 4] | [Numéro] | [GitHub] |

> **Attention — intégrité académique.** La consigne interdit l'IA générative pour les
> décisions de conception, les modèles UML du domaine et les réalisations de cas
> d'utilisation. Les sections 3, 4 et 5 ci-dessous sont donc des espaces réservés :
> elles doivent être rédigées et illustrées par les membres de l'équipe.

## 1. Sommaire exécutif

Ce document présente l'évolution du système CAR-HMS depuis les contrats initiaux
vers l'implémentation du domaine et les tests de comportement. Destiné au client,
à l'équipe de développement et à l'équipe enseignante, il rassemble les choix
établis par l'équipe, le modèle UML du domaine, les réalisations des cas
d'utilisation, les méthodes de vérification du code et la déclaration de
l'utilisation des outils d'IA générative.

## 2. Description de l'équipe et des rôles

| Membre | Travail réellement réalisé | Heures | Contribution |
|---|---|---|---|
| [Membre 1] | [Tâches] | [h] | [%] |
| [Membre 2] | [Tâches] | [h] | [%] |
| [Membre 3] | [Tâches] | [h] | [%] |
| [Membre 4] | [Tâches] | [h] | [%] |

[Décrire ici toute répartition inégale, si elle a eu lieu. Ne pas attribuer de
contributions fictives.]

## 3. Hypothèses et décisions de conception

**À produire par l'équipe sans IA générative.** Compléter en s'appuyant sur la
solution de référence du livrable 1, sur les contrats et sur les décisions
réellement prises pendant la conception tactique.

| ID | Exigence ou contrat à clarifier | Hypothèse ou décision prise par l'équipe | Justification | Conséquence UML ou code |
|---|---|---|---|---|
| D-01 | [Référence] | [Décision] | [Justification] | [Conséquence] |
| D-02 | [Référence] | [Décision] | [Justification] | [Conséquence] |
| D-03 | [Référence] | [Décision] | [Justification] | [Conséquence] |

Vérifier que **toute** décision qui ne découle pas explicitement des contrats
ou des exigences figure dans ce tableau. Les hypothèses du livrable 1 ne
doivent être reprises que si l'équipe les maintient après la revue de référence.

## 4. Modèle du domaine

**À produire par l'équipe sans IA générative.** Un diagramme UML pour chacun
des sept sous-domaines conservés ou corrigés par l'équipe :

1. Gestion des randonnées : [INSÉRER LE DIAGRAMME MERMAID/IMAGE DE L'ÉQUIPE].
2. Gestion des inscriptions : [INSÉRER LE DIAGRAMME].
3. Gestion des membres : [INSÉRER LE DIAGRAMME].
4. Catalogue des sentiers : [INSÉRER LE DIAGRAMME].
5. Notifications : [INSÉRER LE DIAGRAMME].
6. Identité et accès : [INSÉRER LE DIAGRAMME].
7. Rapports : [INSÉRER LE DIAGRAMME].

Chaque diagramme doit identifier explicitement **entités / objets-valeurs**,
les agrégats et leurs racines, les agrégations UML **internes** aux agrégats,
les références par ID **entre** les agrégats et toutes les multiplicités.
Conserver les diagrammes de l'équipe directement dans ce document sous forme
de blocs Mermaid, ou stocker les images dans `docs/diagrammes/` et utiliser
`![Titre](diagrammes/fichier.png)`.

## 5. Réalisations des cas d'utilisation

**À produire par l'équipe sans IA générative.** Pour chacun des neuf cas
ci-dessous, ajouter **deux diagrammes de séquence**, un niveau 1 et un niveau 2.

| Cas d'utilisation | Niveau 1 | Niveau 2 |
|---|---|---|
| Parcourir les randonnées | [DIAGRAMME] | [DIAGRAMME] |
| Enregistrer un randonneur | [DIAGRAMME] | [DIAGRAMME] |
| S'inscrire à une randonnée | [DIAGRAMME] | [DIAGRAMME] |
| Annuler une inscription | [DIAGRAMME] | [DIAGRAMME] |
| Mettre à jour un randonneur (trois commandes) | [DIAGRAMME] | [DIAGRAMME] |
| Fermer une randonnée | [DIAGRAMME] | [DIAGRAMME] |
| Envoyer un rappel | [DIAGRAMME] | [DIAGRAMME] |
| Créer une randonnée | [DIAGRAMME] | [DIAGRAMME] |
| Annuler une randonnée | [DIAGRAMME] | [DIAGRAMME] |

Les diagrammes doivent refléter les collaborations effectivement choisies par
l'équipe : patron façade, fabriques, dépôts et événements du domaine
correspondant aux modifications d'état. Les cas Se connecter, Se déconnecter et
Générer le rapport d'utilisation des sentiers sont exclus des réalisations UML
demandées, conformément à l'énoncé.

## 6. Implémentation et vérification du code

### 6.1 Organisation

La version de travail du code accompagne le rapport dans le même dépôt GitHub.
Les interfaces de commandes et les DTO du livrable 1 sont conservés. Les
fichiers ajoutés pour l'implémentation sont répartis dans les dossiers suivants :

- `domain/common` : événements du domaine, publication et horloge abstraite ;
- `domain/*` : entités, objets-valeurs, interfaces de dépôts et de fabriques, façades ;
- `application/usecases/implementation` : implémentations des neuf interfaces ;
- `application/services` : ports des services de courriel, météo et identifiants ;
- `src/test/kotlin/.../contracts` : doubles des services, définitions des étapes ;
- `src/test/resources/contracts` : onze contrats `.feature` du livrable 1.

Le code fourni est une base technique pour la section « Code » de l'énoncé.
L'équipe doit vérifier que toute son organisation est conforme aux diagrammes
qu'elle a elle-même conçus, puis adapter le code en conséquence.

### 6.2 Contrats et données de test

Les 11 fichiers de contrats comprennent 30 scénarios : validation des parcours,
enregistrement, inscriptions, listes d'attente, annulations, mises à jour,
certification, fermeture, rappels et création. Des doubles de test implémentent
les interfaces des dépôts, fabriques et services externes sans base de données
ni service réseau réel. Les tests examinent les résultats, les états stockés,
les événements publiés et les courriels préparés.

### 6.3 Résultat des vérifications

Une compilation locale des classes métier Kotlin et des sources de test
(hors classes nécessitant des bibliothèques externes), a été exécutée avec un
compilateur Kotlin 1.9.0 et une annotation de test de substitution. Une
vérification hors ligne a ensuite fait exécuter les étapes des **30 scénarios** :
**30 succès, 0 échec**. Cette vérification n'est PAS l'exécution officielle du
moteur Cucumber avec Gradle.

**À remplir après exécution sur l'ordinateur de l'équipe :**

| Vérification finale | Résultat réel | Preuve |
|---|---|---|
| `./gradlew compileKotlin` | [À EXÉCUTER] | [Capture/journal] |
| `./gradlew test` | [À EXÉCUTER] | [Capture/journal] |
| Nombre de scénarios exécutés par Cucumber | [À CONFIRMER] | [Rapport de tests] |
| Dernier commit GitHub | [LIEN] | [Commit] |

Ne pas présenter la vérification hors ligne comme une exécution de Cucumber.

### 6.4 Bonus : intégration continue (facultatif)

Une configuration manuelle est proposée dans
`.github/workflows/contracts-manuel.yml`. Le déclenchement est
`workflow_dispatch`, un délai maximal de dix minutes est défini et la tâche
exécute les tests, et pas uniquement la compilation.

Les conditions du bonus sont historiques : le workflow doit être mis en place
**avant** les implémentations, conserver une première exécution où les contrats
échouent, et conserver une exécution finale où ils réussissent. Le fichier de
workflow seul ne prouve pas cette progression.

**Première exécution en échec :** [URL RÉELLE À AJOUTER]  
**Exécution finale réussie :** [URL RÉELLE À AJOUTER]

## 7. Déclaration d'utilisation de l'intelligence artificielle générative

Dans le cadre de la section 2 « Code » du livrable 2, nous avons utilisé
**ChatGPT (OpenAI, GPT-6)** pour obtenir une proposition d'implémentation Kotlin
à partir des interfaces, DTO et contrats du livrable 1 fournis à l'outil.
Les demandes concernaient principalement les entités et services Kotlin,
les façades et interfaces de dépôt/fabrique, les implémentations des commandes,
les doubles de test, les étapes Cucumber, la configuration de test et un
exemple de workflow GitHub Actions.

Les résultats proposés ont été soumis à une compilation locale des sources
métier et à un parcours hors ligne des 30 scénarios. Cette démarche ne remplace
pas la vérification finale de la suite Cucumber avec `./gradlew test` ni la
revue de conception que les membres de l'équipe doivent réaliser. Les
**diagrammes de classes UML, les diagrammes de séquence UML et les décisions de
conception du rapport ne sont pas générés par cet outil** et doivent être
réalisés indépendamment par l'équipe.

**À compléter par l'équipe :** [Indiquer les autres outils utilisés, leurs
versions, les prompts effectivement donnés, ainsi que les vérifications et
modifications réelles effectuées par les membres.]

## 8. Références

[1] Kalala, K. (2026). *Système de gestion des événements de randonnée du
Cairn (CAR-HMS) — Exigences initiales*, CAR-Req-01.

[2] Chen, B. (2026). *Livrable 1 : domaine initial*, SEG 3102, Université
d'Ottawa.

[3] *Livrable 2 : Domaine*, SEG 3502, Projet de l'automne 2026, document fourni
par l'enseignant, quatre pages.

[4] Documents et solution de référence du livrable 1 : [Référence exacte et lien
si l'équipe y a accès].
