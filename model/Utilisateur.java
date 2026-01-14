package model;

import java.time.LocalDateTime;

public abstract class Utilisateur {

    protected int id;
    protected String nom;
    protected String prenom;
    protected String email;
    protected String telephone;
    protected String motDePasse;
    protected String role;
    protected LocalDateTime dateCreation;
    protected boolean seConnecter;

    public Utilisateur(int id, String nom, String prenom, String email,
                       String telephone, String motDePasse, String role) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.motDePasse = motDePasse;
        this.role = role;
        this.dateCreation = LocalDateTime.now();
        this.seConnecter = false;
    }

    public void seConnecter() {
        this.seConnecter = true;
        System.out.println(nom + " connecté");
    }

    public void seDeconnecter() {
        this.seConnecter = false;
        System.out.println(nom + " déconnecté");
    }

    public synchronized void changerMotDePasse(String nouveauMotDePasse) {
        this.motDePasse = nouveauMotDePasse;
        System.out.println("Mot de passe modifié pour " + nom);
    }

    }
}
