# Notea - Application de Prise de Notes & To Do List avec priorités et dates d'échéances pour Android

![Notea](https://img.shields.io/badge/platform-Android-brightgreen.svg)
![Kotlin](https://img.shields.io/badge/language-Kotlin-blue.svg)
![API](https://img.shields.io/badge/API-24%2B-blue.svg)

Notea est une application Android native, simple et efficace, qui combine un carnet de notes et un gestionnaire de tâches (to-do list). Elle est conçue pour aider à organiser les idées et les obligations du quotidien de manière intuitive.

## 🏛️ Architecture du Projet

Le projet est construit sur une architecture moderne inspirée du modèle **MVVM (Modèle-Vue-VueModèle)**, qui sépare clairement les responsabilités pour une meilleure maintenance et testabilité :

*   **Modèle (Model)** : La couche de données, entièrement gérée par la bibliothèque **Room**.
    *   `Note.kt` : L'**entité** qui définit la structure d'une note.
    *   `NoteDao.kt` : L'interface qui contient toutes les opérations possibles sur la base de données (lecture, écriture, suppression).
    *   `AppDatabase.kt` : La classe qui représente la base de données physique sur le téléphone.
*   **Vue (View)** : L'interface utilisateur, définie par les fichiers **XML** (`activity_main.xml`, etc.) et gérée par les `Activity`.
*   **Logique (Activités)** : Les `Activity` (`MainActivity`, `NoteDetailActivity`) agissent comme le chef d'orchestre. Elles utilisent les **Coroutines Kotlin** pour exécuter les opérations de base de données en arrière-plan, garantissant que l'interface utilisateur reste toujours fluide.

## 🗃️ Base de Données

Pour la persistance des données, le projet utilise **Room**, la bibliothèque officielle de Google pour la gestion de bases de données locales. C'est une surcouche puissante au-dessus de **SQLite** qui simplifie énormément l'accès aux données.

Des `TypeConverters` ont été mis en place pour permettre à Room de stocker des types de données complexes comme les `Date`s.

## ✨ Fonctionnalités

*   **Système Hybride Notes / To-Do List** : Chaque note est aussi une tâche potentielle. Une case à cocher permet de marquer une tâche comme terminée, ce qui **barre automatiquement le texte** pour un suivi visuel immédiat.
*   **Organisation par Priorités** : Chaque note peut être classée selon quatre niveaux : `Urgent`, `Haute`, `Normale` ou `Basse`, avec un code couleur distinctif.
*   **Dates d'Échéance** : Possibilité d'associer une date limite à n'importe quelle note, affichée en toutes lettres (ex: "1 janvier 2024").
*   **Recherche et Tri** : Une **barre de recherche en temps réel** et des **options de tri** (par date ou par priorité) permettent de retrouver et d'organiser facilement les notes.
*   **Gestion Complète et Sécurisée** : L'application gère toutes les opérations CRUD (Créer, Lire, Mettre à jour, Supprimer), avec une confirmation avant chaque suppression.

## 🧪 Tests Unitaires

Pour garantir la fiabilité de l'application, des tests unitaires instrumentés ont été écrits pour le `NoteDao`.

*   **Environnement de Test Isolé** : Chaque test s'exécute sur une **base de données temporaire en mémoire**, ce qui garantit des tests rapides, fiables et sans effets secondaires.
*   **Tests Critiques** : Les tests vérifient les opérations les plus importantes :
    1.  **Insertion et Lecture** : Vérifie qu'une note insérée peut être relue correctement.
    2.  **Suppression** : Vérifie qu'une note supprimée n'existe plus dans la base de données.

Ces tests assurent que le cœur de l'application – la sauvegarde des données – est et restera toujours fiable.

## 🚀 Installation

1.  Clonez ce dépôt.
2.  Ouvrez le projet dans Android Studio.
3.  Lancez la compilation et exécutez l'application sur un émulateur ou un appareil physique.
