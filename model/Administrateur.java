package model;

import security.SecurityUtils;

/**
 * Représente un administrateur du système hospitalier.
 */
public class Administrateur extends Utilisateur {

    public Administrateur(int id, String nom, String prenom, String email,
                          String telephone, String motDePasse) {
        super(id, nom, prenom, email, telephone, motDePasse, "ADMIN");
    }

    public Administrateur(int id, String nom, String prenom, String email,
                          String telephone, String motDePasse, Object unused) {
        this(id, nom, prenom, email, telephone, motDePasse);
    }

    public void creerCompte(Utilisateur utilisateur) {
        SecurityUtils.logAudit(getNomComplet(), "USER_CREATE", "COMPTE",
                "Compte créé pour : " + utilisateur.getNomComplet() + " (" + utilisateur.getRole() + ")");
    }

    public void supprimerCompte(Utilisateur utilisateur) {
        utilisateur.setActif(false);
        SecurityUtils.logAudit(getNomComplet(), "USER_DEACTIVATE", "COMPTE",
                "Compte désactivé pour : " + utilisateur.getNomComplet());
    }

    public void genererRapport() {
        SecurityUtils.logAudit(getNomComplet(), "REPORT_GENERATE", "STATS",
                "Rapport d'activité hospitalière généré");
        System.out.println("📊 Rapport d'activité hospitalière généré avec succès.");
    }

    public void configurerSysteme() {
        SecurityUtils.logAudit(getNomComplet(), "SYSTEM_CONFIG", "GLOBAL",
                "Configuration système actualisée");
        System.out.println("⚙️ Configuration du système hospitalier terminée.");
    }
}
