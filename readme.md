# CYBOOK: Système de Gestion de Bibliothèque

## Table des Matières
- [Description](#description)
- [Fonctionnalités](#fonctionnalités)
- [Technologie](#technologie)
- [Installation](#installation)
  - [Prérequis](#prérequis)
  - [Base de Données](#base-de-données)
  - [Configuration](#configuration)
  - [Lancement](#lancement)
- [Démarrage de l'Application](#démarrage-de-lapplication)
- [Interface Utilisateur Principale](#interface-utilisateur-principale)
- [Navigation](#navigation)
- [Clôture de l'Application](#clôture-de-lapplication)
- [Structure de la Base de Données](#structure-de-la-base-de-données)
  - [Table `books`](#table-books)
  - [Table `users`](#table-users)
  - [Table `loans`](#table-loans)
- [Contribuer](#contribuer)
- [Contact](#contact)
- [Auteurs](#auteurs)

## Description
CYBOOK est un système de gestion de bibliothèque conçu pour aider les bibliothécaires à gérer efficacement les livres, les utilisateurs et les emprunts. Ce projet a été développé par le Groupe 7.

## Fonctionnalités
- **Gestion des livres** : Ajout de nouveaux livres, recherche et listing des livres disponibles.
- **Gestion des utilisateurs** : Ajout de nouveaux utilisateurs, modification des informations utilisateur, et recherche d'utilisateurs.
- **Gestion des emprunts** : Enregistrement des emprunts, gestion des retours de livres, et suivi des emprunts en retard.

## Technologie
CYBOOK utilise une architecture MVC avec JavaFX pour l'interface utilisateur et JDBC pour l'accès à la base de données MySQL. Composants principaux :
- Contrôleurs pour les vues principales
- Classes d'accès aux données (`dao`)
- Modèles pour les entités (`model`)
- Classes utilitaires (`util`)
- Intégration avec `BNFApiClient` (`api`)

## Installation

### Prérequis
- JDK 8 ou supérieur
- MySQL Server
- JavaFX (assurez-vous que JavaFX est correctement configuré avec votre environnement de développement)

### Base de Données
1. Créez une base de données nommée `cybook` dans MySQL.
2. Importez le script SQL fourni pour configurer les tables nécessaires.

### Configuration
1. Clonez le repository du projet.
2. Modifiez les informations de connexion à la base de données dans la classe `util.databaseManager`.

### Lancement
Exécutez la classe `Main.java` pour démarrer l'application JavaFX.

## Démarrage de l'Application
Pour démarrer CYBOOK, exécutez la classe `Main.java` située dans le dossier `application`. Cela lancera l'interface utilisateur principale de CYBOOK.

## Interface Utilisateur Principale
L'interface principale de CYBOOK se présente avec trois options principales permettant de gérer les différents aspects de la bibliothèque :
1. **Gérer les emprunts** : Permet de gérer les emprunts de livres, y compris l'enregistrement des nouveaux emprunts et des retours, ainsi que la visualisation des emprunts en retard.
2. **Gérer les utilisateurs** : Offre les outils pour ajouter, rechercher, et modifier les informations des utilisateurs.
3. **Gérer les livres** : Permet d'ajouter, rechercher et gérer les informations des livres.

## Navigation
Utilisez les boutons disponibles sur l'écran principal pour naviguer entre les différentes fonctionnalités de gestion de la bibliothèque.

## Clôture de l'Application
Fermez l'application via l'option "Fichier" dans la barre de menu pour terminer votre session de manière sécurisée.

## Structure de la Base de Données
### Table `books`
- `isbn` : Numéro ISBN standardisé du livre.
- `stock` : Nombre de livres en stock.

### Table `users`
- `id` : Identifiant unique pour chaque utilisateur.
- `firstName` : Prénom de l'utilisateur.
- `lastName` : Nom de famille de l'utilisateur.
- `email` : Adresse email de l'utilisateur.
- `address` : Adresse physique.

### Table `loans`
- `id` : Identifiant unique de l'emprunt.
- `user_id` : Identifiant de l'utilisateur.
- `book_isbn` : ISBN du livre emprunté.
- `loan_date` : Date d'emprunt.
- `due_date` : Date de retour.
- `returned` : Statut de retour (oui/non).

## Contribuer
Les contributions externes sont les bienvenues. Créez une branche, effectuez vos modifications et soumettez une pull request.

## Contact
Pour toute question, n'hésitez pas à nous contacter.

## Auteurs
- Alexandre BOUHARIRA-THELLIEZ
- Ryan ALI MLADJAO
- Abdelazim RAMADAN
- Lyna BEN TAHAR
- Yanis ZIDI

Merci d'utiliser CYBOOK. Nous espérons que ce système facilitera grandement la gestion de votre bibliothèque.
