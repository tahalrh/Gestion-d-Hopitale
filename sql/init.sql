-- ==============================================================================
-- SYSTÈME D'INFORMATION HOSPITALIER (SIH) - GESTION-D-HOPITALE
-- Schéma Relationnel & Initialisation de la Base de Données
-- ==============================================================================

CREATE DATABASE IF NOT EXISTS hopital_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE hopital_db;

-- ------------------------------------------------------------------------------
-- 1. Table: UTILISATEURS (Table de base pour l'authentification et les rôles)
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS utilisateurs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    telephone VARCHAR(20),
    mot_de_passe_hash VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'MEDECIN', 'PATIENT', 'INFIRMIER') NOT NULL,
    actif BOOLEAN DEFAULT TRUE,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_utilisateur_email (email),
    INDEX idx_utilisateur_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------------------------
-- 2. Table: PATIENTS (Informations patient & RGPD - données de santé pseudonymisées)
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS patients (
    id INT AUTO_INCREMENT PRIMARY KEY,
    utilisateur_id INT NOT NULL UNIQUE,
    date_naissance DATE,
    adresse VARCHAR(255),
    groupe_sanguin ENUM('A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-'),
    numero_securite_sociale_hash VARCHAR(255) NOT NULL COMMENT 'Empreinte chiffrée selon normes RGPD',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (utilisateur_id) REFERENCES utilisateurs(id) ON DELETE CASCADE,
    INDEX idx_patient_user (utilisateur_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------------------------
-- 3. Table: MEDECINS (Praticiens et spécialités médicales)
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS medecins (
    id INT AUTO_INCREMENT PRIMARY KEY,
    utilisateur_id INT NOT NULL UNIQUE,
    specialite VARCHAR(100) NOT NULL,
    service_hopital VARCHAR(100) NOT NULL,
    numero_ordre VARCHAR(50),
    FOREIGN KEY (utilisateur_id) REFERENCES utilisateurs(id) ON DELETE CASCADE,
    INDEX idx_medecin_specialite (specialite)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------------------------
-- 4. Table: DOSSIERS_MEDICAUX (Données cliniques protégées)
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS dossiers_medicaux (
    id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL UNIQUE,
    allergies TEXT,
    antecedents TEXT,
    observations TEXT,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    derniere_mise_a_jour TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------------------------
-- 5. Table: CHAMBRES (Gestion des lits & occupation avec Semaphore)
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS chambres (
    id INT AUTO_INCREMENT PRIMARY KEY,
    numero INT NOT NULL UNIQUE,
    type ENUM('Simple', 'Double', 'Soins Intensifs', 'Maternité') NOT NULL,
    capacite INT NOT NULL DEFAULT 1,
    lits_occupes INT NOT NULL DEFAULT 0,
    statut ENUM('DISPONIBLE', 'OCCUPEE', 'MAINTENANCE') DEFAULT 'DISPONIBLE',
    INDEX idx_chambre_statut (statut)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------------------------
-- 6. Table: RENDEZ_VOUS (Planification et gestion concurrente)
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS rendez_vous (
    id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    medecin_id INT NOT NULL,
    date_heure DATETIME NOT NULL,
    motif VARCHAR(255) NOT NULL,
    statut ENUM('PLANIFIE', 'CONFIRME', 'EN_COURS', 'TERMINE', 'ANNULE') DEFAULT 'PLANIFIE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    FOREIGN KEY (medecin_id) REFERENCES medecins(id) ON DELETE CASCADE,
    INDEX idx_rdv_date (date_heure),
    INDEX idx_rdv_patient (patient_id),
    INDEX idx_rdv_medecin (medecin_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------------------------
-- 7. Table: FACTURES (Transactions financières avec verrouillage ReentrantLock)
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS factures (
    id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    montant DECIMAL(10, 2) NOT NULL,
    statut ENUM('NON_PAYEE', 'EN_COURS', 'PAYEE', 'ANNULEE') DEFAULT 'NON_PAYEE',
    date_emission DATE NOT NULL,
    date_paiement DATETIME NULL,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    INDEX idx_facture_statut (statut)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------------------------
-- 8. Table: LOGS_AUDIT (Piste d'audit RGPD pour traçabilité de tout accès médical)
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS logs_audit (
    id INT AUTO_INCREMENT PRIMARY KEY,
    utilisateur_id INT NULL,
    action VARCHAR(50) NOT NULL,
    ressource VARCHAR(100) NOT NULL,
    details TEXT,
    ip_source VARCHAR(45),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (utilisateur_id) REFERENCES utilisateurs(id) ON DELETE SET NULL,
    INDEX idx_audit_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==============================================================================
-- DONNÉES DE DÉMONSTRATION SÉCURISÉES (SEED DATA)
-- ==============================================================================

-- 1. Utilisateurs (Mots de passe hachés en SHA-256)
INSERT INTO utilisateurs (id, nom, prenom, email, telephone, mot_de_passe_hash, role) VALUES
(1, 'Hassan', 'Dr', 'dr.hassan@hopital.ma', '0600112233', '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', 'MEDECIN'),
(2, 'Ben Ali', 'Ali', 'ali.benali@email.ma', '0611223344', '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', 'PATIENT'),
(3, 'El Amrani', 'Sara', 'sara.elamrani@email.ma', '0622334455', '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', 'PATIENT'),
(4, 'Alami', 'Directeur', 'admin@hopital.ma', '0633445566', '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', 'ADMIN');

-- 2. Médecin
INSERT INTO medecins (id, utilisateur_id, specialite, service_hopital, numero_ordre) VALUES
(1, 1, 'Cardiologie', 'Service des Soins Cardiovasculaires', 'MED-CAS-2024-042');

-- 3. Patients (NIR pseudonymisé pour respect RGPD)
INSERT INTO patients (id, utilisateur_id, date_naissance, adresse, groupe_sanguin, numero_securite_sociale_hash) VALUES
(1, 2, '1985-04-12', '12 Boulevard Zerktouni, Casablanca', 'O+', 'hash_sec_a7f920c812d4'),
(2, 3, '1992-09-28', '45 Avenue Hassan II, Rabat', 'A+', 'hash_sec_b8e193f734a1');

-- 4. Dossiers médicaux
INSERT INTO dossiers_medicaux (id, patient_id, allergies, antecedents, observations) VALUES
(1, 1, 'Pénicilline', 'Hypertension artérielle légère', 'Suivi cardiologique régulier semestriel.'),
(2, 2, 'Aucune allergie connue', 'Appendicectomie en 2015', 'Examen général satisfaisant.');

-- 5. Chambres
INSERT INTO chambres (id, numero, type, capacite, lits_occupes, statut) VALUES
(1, 101, 'Simple', 1, 0, 'DISPONIBLE'),
(2, 102, 'Double', 2, 0, 'DISPONIBLE'),
(3, 201, 'Soins Intensifs', 1, 0, 'DISPONIBLE');

-- 6. Rendez-vous
INSERT INTO rendez_vous (id, patient_id, medecin_id, date_heure, motif, statut) VALUES
(1, 1, 1, '2026-10-05 10:00:00', 'Consultation de suivi cardiologique', 'CONFIRME'),
(2, 2, 1, '2026-10-05 11:30:00', 'Bilan électrocardiogramme', 'PLANIFIE');

-- 7. Factures
INSERT INTO factures (id, patient_id, montant, statut, date_emission) VALUES
(1, 1, 600.00, 'NON_PAYEE', '2026-09-25'),
(2, 2, 450.00, 'PAYEE', '2026-09-20');

-- 8. Piste d'audit RGPD
INSERT INTO logs_audit (utilisateur_id, action, ressource, details, ip_source) VALUES
(4, 'SYSTEM_INIT', 'DATABASE', 'Initialisation du schéma de données sécurisé et seeding', '127.0.0.1');
