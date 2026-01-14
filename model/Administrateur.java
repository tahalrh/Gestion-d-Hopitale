package model;

public class Administrateur extends Utilisateur {

    public Administrateur(int id, String nom, String prenom, String email,
                           String telephone, String motDePasse) {
        super(id, nom, prenom, email, telephone, motDePasse, "ADMIN");
    }

    public void creerCompte(Utilisateur utilisateur) {
        System.out.println("Compte créé pour : " + utilisateur.nom);
    }

    public void supprimerCompte(Utilisateur utilisateur) {
        System.out.println("Compte supprimé pour : " + utilisateur.nom);
    }

    public void genererRapport() {
        System.out.println("Rapport généré");
    }

    public void configurerSysteme() {
        System.out.println("Configuration du système terminée");
    }
}
