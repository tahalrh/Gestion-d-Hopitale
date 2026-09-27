package model;

import java.time.LocalDateTime;
import security.SecurityUtils;

/**
 * Classe abstraite de base représentant tout acteur du Système d'Information Hospitalier.
 */
public abstract class Utilisateur {

    protected int id;
    protected String nom;
    protected String prenom;
    protected String email;
    protected String telephone;
    protected String motDePasseHash;
    protected String role;
    protected LocalDateTime dateCreation;
    protected boolean connecte;
    protected boolean actif;

    public Utilisateur(int id, String nom, String prenom, String email,
                       String telephone, String motDePasse, String role) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.motDePasseHash = (motDePasse != null && !motDePasse.isEmpty()) 
            ? SecurityUtils.hashPassword(motDePasse) : "";
        this.role = role;
        this.dateCreation = LocalDateTime.now();
        this.connecte = false;
        this.actif = true;
    }

    public Utilisateur(int id, String nom, String prenom, String role) {
        this(id, nom, prenom, nom.toLowerCase() + "." + prenom.toLowerCase() + "@hopital.ma", "0600000000", "DefaultPass2026!", role);
    }

    public boolean seConnecter(String email, String rawPassword) {
        String testHash = SecurityUtils.hashPassword(rawPassword);
        if (this.email.equalsIgnoreCase(email) && this.motDePasseHash.equals(testHash) && this.actif) {
            this.connecte = true;
            SecurityUtils.logAudit(getNomComplet(), "LOGIN_SUCCESS", "AUTH", "Connexion réussie");
            return true;
        }
        SecurityUtils.logAudit(email, "LOGIN_FAILED", "AUTH", "Échec d'authentification");
        return false;
    }

    public void seDeconnecter() {
        this.connecte = false;
        SecurityUtils.logAudit(getNomComplet(), "LOGOUT", "AUTH", "Déconnexion de session");
    }

    public synchronized void changerMotDePasse(String nouveauMotDePasse) {
        this.motDePasseHash = SecurityUtils.hashPassword(nouveauMotDePasse);
        SecurityUtils.logAudit(getNomComplet(), "PASSWORD_CHANGE", "SECURITE", "Mot de passe mis à jour");
    }

    // Getters & Setters
    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getNomComplet() { return prenom + " " + nom; }
    public String getEmail() { return email; }
    public String getTelephone() { return telephone; }
    public String getRole() { return role; }
    public boolean isConnecte() { return connecte; }
    public boolean isActif() { return actif; }
    public LocalDateTime getDateCreation() { return dateCreation; }

    public void setNom(String nom) { this.nom = nom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public void setEmail(String email) { this.email = email; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public void setActif(boolean actif) { this.actif = actif; }
}
