-- Brasil Burger - Script SQL COMPLET pour tous les projets (Java + C# + Symfony)
-- À exécuter sur https://console.neon.tech (SQL Editor)
-- Date: 11 décembre 2025
-- Base de données partagée entre Java, C# et Symfony

-- ============================================
-- ÉTAPE 1: Nettoyer les objets existants
-- ============================================
DROP TABLE IF EXISTS paiements CASCADE;
DROP TABLE IF EXISTS commande_lignes CASCADE;
DROP TABLE IF EXISTS commandes CASCADE;
DROP TABLE IF EXISTS clients CASCADE;
DROP TABLE IF EXISTS livreur_zones CASCADE;
DROP TABLE IF EXISTS livreurs CASCADE;
DROP TABLE IF EXISTS quartiers CASCADE;
DROP TABLE IF EXISTS produits CASCADE;
DROP TABLE IF EXISTS zones CASCADE;
DROP TYPE IF EXISTS type_produit_enum CASCADE;
DROP TYPE IF EXISTS type_complement_enum CASCADE;
DROP TYPE IF EXISTS statut_commande_enum CASCADE;
DROP TYPE IF EXISTS type_livraison_enum CASCADE;
DROP TYPE IF EXISTS mode_paiement_enum CASCADE;

-- ============================================
-- ÉTAPE 2: Créer les types ENUM
-- ============================================
CREATE TYPE type_produit_enum AS ENUM ('BURGER', 'COMPLEMENT', 'MENU');
CREATE TYPE type_complement_enum AS ENUM ('BOISSON', 'FRITES', 'SAUCE', 'DESSERT');
CREATE TYPE statut_commande_enum AS ENUM ('EN_ATTENTE', 'VALIDEE', 'EN_PREPARATION', 'PRETE', 'EN_LIVRAISON', 'TERMINEE', 'ANNULEE');
CREATE TYPE type_livraison_enum AS ENUM ('SUR_PLACE', 'A_EMPORTER', 'LIVRAISON');
CREATE TYPE mode_paiement_enum AS ENUM ('WAVE', 'ORANGE_MONEY', 'ESPECES');

-- ============================================
-- ÉTAPE 3: Tables JAVA (Gestion des Ressources)
-- ============================================

CREATE TABLE zones (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL UNIQUE,
    prix_livraison DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE quartiers (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    zone_id INT NOT NULL REFERENCES zones(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE livreurs (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    telephone VARCHAR(20) NOT NULL UNIQUE,
    disponible BOOLEAN DEFAULT TRUE,
    actif BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE livreur_zones (
    livreur_id INT NOT NULL REFERENCES livreurs(id) ON DELETE CASCADE,
    zone_id INT NOT NULL REFERENCES zones(id) ON DELETE CASCADE,
    PRIMARY KEY (livreur_id, zone_id)
);

CREATE TABLE produits (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(150) NOT NULL,
    prix DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    image VARCHAR(500) NULL,
    type_produit type_produit_enum NOT NULL,
    description TEXT NULL,
    ingredients TEXT NULL,
    type_complement type_complement_enum NULL,
    burger_id INT NULL REFERENCES produits(id) ON DELETE SET NULL,
    boisson_id INT NULL REFERENCES produits(id) ON DELETE SET NULL,
    frites_id INT NULL REFERENCES produits(id) ON DELETE SET NULL,
    archived BOOLEAN DEFAULT FALSE,
    disponible BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- ÉTAPE 4: Tables C# (Gestion Client/Authentification)
-- ============================================

CREATE TABLE clients (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    telephone VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(150) UNIQUE,
    mot_de_passe VARCHAR(255) NOT NULL,
    adresse TEXT NULL,
    quartier_id INT NULL REFERENCES quartiers(id) ON DELETE SET NULL,
    actif BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- ÉTAPE 5: Tables SYMFONY (Commandes/Paiements/Stats)
-- ============================================

CREATE TABLE commandes (
    id SERIAL PRIMARY KEY,
    client_id INT NOT NULL REFERENCES clients(id) ON DELETE CASCADE,
    date_commande TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    statut statut_commande_enum DEFAULT 'EN_ATTENTE',
    type_livraison type_livraison_enum NOT NULL,
    quartier_id INT NULL REFERENCES quartiers(id) ON DELETE SET NULL,
    zone_id INT NULL REFERENCES zones(id) ON DELETE SET NULL,
    livreur_id INT NULL REFERENCES livreurs(id) ON DELETE SET NULL,
    montant_total DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    frais_livraison DECIMAL(10, 2) DEFAULT 0.00,
    notes TEXT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE commande_lignes (
    id SERIAL PRIMARY KEY,
    commande_id INT NOT NULL REFERENCES commandes(id) ON DELETE CASCADE,
    produit_id INT NOT NULL REFERENCES produits(id) ON DELETE RESTRICT,
    quantite INT NOT NULL DEFAULT 1,
    prix_unitaire DECIMAL(10, 2) NOT NULL,
    sous_total DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE paiements (
    id SERIAL PRIMARY KEY,
    commande_id INT NOT NULL UNIQUE REFERENCES commandes(id) ON DELETE CASCADE,
    date_paiement TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    montant DECIMAL(10, 2) NOT NULL,
    mode_paiement mode_paiement_enum NOT NULL,
    reference_transaction VARCHAR(100) UNIQUE,
    statut VARCHAR(50) DEFAULT 'VALIDE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- ÉTAPE 6: Index pour performance
-- ============================================
CREATE INDEX idx_produits_type ON produits(type_produit);
CREATE INDEX idx_produits_complement_type ON produits(type_complement);
CREATE INDEX idx_produits_archived ON produits(archived);
CREATE INDEX idx_quartiers_zone ON quartiers(zone_id);
CREATE INDEX idx_livreurs_disponible ON livreurs(disponible, actif);
CREATE INDEX idx_clients_telephone ON clients(telephone);
CREATE INDEX idx_clients_email ON clients(email);
CREATE INDEX idx_commandes_client ON commandes(client_id);
CREATE INDEX idx_commandes_statut ON commandes(statut);
CREATE INDEX idx_commandes_date ON commandes(date_commande);
CREATE INDEX idx_commandes_livreur ON commandes(livreur_id);
CREATE INDEX idx_commande_lignes_commande ON commande_lignes(commande_id);
CREATE INDEX idx_paiements_commande ON paiements(commande_id);

-- ============================================
-- ÉTAPE 7: Données de test
-- ============================================
INSERT INTO zones (nom, prix_livraison) VALUES
('Plateau', 500.00),
('Parcelles Assainies', 1000.00),
('Almadies', 1500.00),
('Mermoz', 800.00),
('Ouakam', 1200.00);

-- Quartiers
INSERT INTO quartiers (nom, zone_id) VALUES
('Plateau Centre', 1),
('Plateau Rue Moussé Diop', 1),
('Parcelles U15', 2),
('Parcelles U10', 2),
('Almadies Plage', 3),
('Mermoz Pyrotechnie', 4),
('Ouakam Village', 5);

-- Livreurs
INSERT INTO livreurs (nom, prenom, telephone, disponible, actif) VALUES
('Diop', 'Ibrahima', '771234567', TRUE, TRUE),
('Fall', 'Moussa', '772345678', TRUE, TRUE),
('Ndiaye', 'Abdou', '773456789', TRUE, TRUE),
('Sow', 'Cheikh', '774567890', FALSE, TRUE);

-- Affectation livreurs-zones
INSERT INTO livreur_zones (livreur_id, zone_id) VALUES
(1, 1), (1, 2),
(2, 2), (2, 3),
(3, 3), (3, 4), (3, 5),
(4, 1), (4, 4);

-- Produits: BURGERS
INSERT INTO produits (nom, prix, image, type_produit, description, ingredients) VALUES
('Classic Burger', 3500.00, 'classic_burger.jpg', 'BURGER', 'Notre burger classique avec steak haché', 'Pain, Steak haché, Salade, Tomate, Oignon, Sauce'),
('Cheese Burger', 4000.00, 'cheese_burger.jpg', 'BURGER', 'Burger avec fromage cheddar fondu', 'Pain, Steak haché, Cheddar, Salade, Tomate, Sauce'),
('Big Brasil', 5500.00, 'big_brasil.jpg', 'BURGER', 'Le burger signature de Brasil Burger', 'Pain, Double Steak, Cheddar, Bacon, Salade, Tomate, Oignon, Sauce spéciale'),
('Chicken Burger', 4500.00, 'chicken_burger.jpg', 'BURGER', 'Burger au poulet croustillant', 'Pain, Poulet pané, Salade, Tomate, Sauce mayo');

-- Produits: COMPLEMENTS (Boissons)
INSERT INTO produits (nom, prix, image, type_produit, type_complement) VALUES
('Coca Cola', 1000.00, 'coca.jpg', 'COMPLEMENT', 'BOISSON'),
('Fanta Orange', 1000.00, 'fanta.jpg', 'COMPLEMENT', 'BOISSON'),
('Sprite', 1000.00, 'sprite.jpg', 'COMPLEMENT', 'BOISSON'),
('Jus de Bissap', 1500.00, 'bissap.jpg', 'COMPLEMENT', 'BOISSON'),
('Eau Minérale', 500.00, 'eau.jpg', 'COMPLEMENT', 'BOISSON');

-- Produits: COMPLEMENTS (Frites)
INSERT INTO produits (nom, prix, image, type_produit, type_complement) VALUES
('Frites Classiques', 1500.00, 'frites_classiques.jpg', 'COMPLEMENT', 'FRITES'),
('Frites Maison', 2000.00, 'frites_maison.jpg', 'COMPLEMENT', 'FRITES'),
('Frites Épicées', 2500.00, 'frites_epicees.jpg', 'COMPLEMENT', 'FRITES');

-- Produits: COMPLEMENTS (Sauces)
INSERT INTO produits (nom, prix, image, type_produit, type_complement) VALUES
('Sauce Ketchup', 200.00, 'ketchup.jpg', 'COMPLEMENT', 'SAUCE'),
('Sauce Mayonnaise', 200.00, 'mayo.jpg', 'COMPLEMENT', 'SAUCE'),
('Sauce Piment', 300.00, 'piment.jpg', 'COMPLEMENT', 'SAUCE'),
('Sauce Brasil', 500.00, 'brasil.jpg', 'COMPLEMENT', 'SAUCE');

-- Produits: COMPLEMENTS (Desserts)
INSERT INTO produits (nom, prix, image, type_produit, type_complement) VALUES
('Brownie Chocolat', 2000.00, 'brownie.jpg', 'COMPLEMENT', 'DESSERT'),
('Glace Vanille', 1500.00, 'glace.jpg', 'COMPLEMENT', 'DESSERT');

-- ============================================
-- ÉTAPE 8: Vérification
-- ============================================
SELECT 
    'Base de donnees Brasil Burger creee avec succes sur Neon!' AS message,
    (SELECT COUNT(*) FROM zones) AS zones,
    (SELECT COUNT(*) FROM quartiers) AS quartiers,
    (SELECT COUNT(*) FROM livreurs) AS livreurs,
    (SELECT COUNT(*) FROM produits WHERE type_produit = 'BURGER') AS burgers,
    (SELECT COUNT(*) FROM produits WHERE type_produit = 'COMPLEMENT') AS complements,
    (SELECT COUNT(*) FROM clients) AS clients,
    (SELECT COUNT(*) FROM commandes) AS commandes;
