# CAR-HMS — Livrable 2 — SEG 3502

Point de départ : contenu du ZIP du Livrable 1 fourni par l'équipe.

## Contenu

- `docs/Livrable2.md` : rapport Markdown à personnaliser, avec sections UML **réservées au travail de l'équipe** conformément à la restriction de l'énoncé.
- `src/main/kotlin/.../domain` : entités, objets-valeurs, événements, façades, contrats de fabriques et dépôts.
- `src/main/kotlin/.../application/usecases/implementation` : neuf implémentations de cas d'utilisation.
- `src/main/kotlin/.../application/services` : interfaces des services externes.
- `src/test/resources/contracts` : onze fonctionnalités Cucumber, trente scénarios.
- `src/test/kotlin/.../contracts` : doubles de test, étapes et suite Cucumber.

## Exécuter les tests sur votre ordinateur

1. Installer **Java 21** et **Gradle 8.14.3** si nécessaire.
2. Dans le répertoire du projet :

```bash
gradle wrapper --gradle-version 8.14.3
./gradlew compileKotlin
./gradlew test
```

> Le ZIP original du Livrable 1 ne contenait pas de wrapper Gradle. Les fichiers
> `gradlew`, `gradlew.bat` et `gradle/wrapper/*` doivent donc être générés et
> conservés dans le dépôt pour répondre exactement à la commande demandée.

Les dépendances téléchargées via Gradle sont nécessaires à l'exécution réelle
avec le moteur Cucumber/JUnit. Cette exécution n'a pas pu être réalisée dans
l'environnement de génération, où Gradle et l'accès aux dépôts de dépendances
ne sont pas disponibles. Les sources Kotlin non dépendantes de bibliothèques
externes ont été compilées et les 30 scénarios parcourus avec un banc d'essai
local : 30 succès.

## Modification des scénarios

Après toute modification d'un fichier `.feature`, synchroniser les définitions
d'étapes exactes à l'aide de :

```bash
python3 scripts/generate_step_bindings.py
```

Ajouter ensuite la logique et les assertions de la nouvelle étape dans
`src/test/kotlin/.../contracts/Steps.kt`.

## Bonus GitHub Actions

Workflow facultatif `workflow_dispatch` dans `.github/workflows/`.
Son historique de tests échoués **doit précéder** celui des tests réussis,
conformément à l'énoncé. Ne pas inventer de liens d'exécution.
