# Vérification locale effectuée pendant la préparation du code

- Sources métier Kotlin compilées à l'aide du compilateur local Kotlin 1.9.0, hormis le lanceur Spring Boot qui requiert des dépendances externes.
- Définition des classes de test vérifiée avec une annotation factice `Given` fournie uniquement au compilateur de vérification.
- Les 30 scénarios des 11 fichiers `.feature` ont été parcourus avec un banc d'essai local qui appelle les véritables assertions de `Steps.kt`.
- Résultat : **30 scénarios, 0 échec**.

## Limites

Ce n'est pas une exécution du moteur Cucumber. L'environnement de préparation
n'avait ni Gradle, ni accès aux dépôts de dépendances. Il faut impérativement
générer le wrapper et lancer `./gradlew test` sur la machine de l'équipe, puis
corriger toute erreur découverte et ajouter les résultats réels au rapport.
