# Brasil Burger - Application Java avec Maven

## Description
Système de gestion de restaurant Brasil Burger avec architecture Clean Architecture.

## Prérequis
- Java JDK 11 ou supérieur
- Maven 3.6+
- PostgreSQL 12+

## Configuration de la base de données

### 1. Créer la base de données PostgreSQL
```sql
-- Se connecter à PostgreSQL et exécuter le script
psql -U postgres -f database/brasil_burger.sql
```

### 2. Configuration de l'application
Le fichier `src/main/resources/application.properties` contient la configuration de connexion :
```properties
db.url=jdbc:postgresql://localhost:5432/brasil_burger
db.username=postgres
db.password=amina2003@
```

## Structure du projet
```
brasil-burger-java/
├── pom.xml                         # Configuration Maven
├── src/
│   └── main/
│       ├── java/                   # Code source Java
│       │   ├── Application.java    # Point d'entrée
│       │   ├── core/               # Configuration et factories
│       │   ├── entity/             # Entités métier
│       │   ├── repository/         # Repositories (DB et List)
│       │   ├── services/           # Services métier
│       │   └── view/               # Interface utilisateur
│       └── resources/              # Ressources
│           └── application.properties
├── database/
│   └── brasil_burger.sql           # Script PostgreSQL
└── README.md
```

## Compilation et exécution

### 1. Installer les dépendances
```bash
mvn clean install
```

### 2. Compiler le projet
```bash
mvn compile
```

### 3. Exécuter l'application
```bash
mvn exec:java -Dexec.mainClass="Application"
```

### 4. Créer un package JAR
```bash
mvn package
```

Le fichier JAR sera créé dans `target/brasil-burger-1.0-SNAPSHOT.jar`

### 5. Exécuter le JAR
```bash
java -jar target/brasil-burger-1.0-SNAPSHOT.jar
```

## Architecture

### Couches de l'application
- **Entity** : Modèles de données (Livreur, Produit, Zone, Quartier)
- **Repository** : Accès aux données (DB et List implementations)
- **Service** : Logique métier
- **View** : Interface utilisateur console
- **Core** : Configuration et factories

### Patterns utilisés
- **Repository Pattern** : Abstraction de l'accès aux données
- **Factory Pattern** : Création des repositories et services
- **Dependency Injection** : Injection des dépendances

## Base de données PostgreSQL

### Principales tables
- `clients` : Clients du restaurant
- `gestionnaires` : Utilisateurs administratifs
- `zones` : Zones de livraison
- `quartiers` : Quartiers dans les zones
- `livreurs` : Livreurs
- `produits` : Burgers, menus, compléments
- `commandes` : Commandes clients
- `ligne_commandes` : Détails des commandes
- `paiements` : Paiements des commandes

### Fonctionnalités PostgreSQL
- **ENUM Types** : Types énumérés pour les statuts
- **Triggers** : Calculs automatiques (totaux, numéros de commande)
- **Stored Procedures** : Statistiques et rapports
- **Views** : Vues pour les requêtes complexes

## Commandes Maven utiles

```bash
# Nettoyer le projet
mvn clean

# Compiler
mvn compile

# Exécuter les tests
mvn test

# Créer le package
mvn package

# Installer dans le dépôt local
mvn install

# Afficher les dépendances
mvn dependency:tree

# Mettre à jour les dépendances
mvn versions:display-dependency-updates
```

## Dépendances principales
- **PostgreSQL JDBC Driver** (42.7.1) : Driver pour PostgreSQL
- **JUnit** (4.13.2) : Tests unitaires

## Auteur
Brasil Burger Team

## Licence
Propriétaire
