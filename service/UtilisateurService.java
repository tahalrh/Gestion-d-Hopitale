package service;

import java.util.ArrayList;
import java.util.List;

import model.Utilisateur;

public class UtilisateurService {

   
    private List<Utilisateur> utilisateurs = new ArrayList<>();

    public void creerCompte(Utilisateur utilisateur) {

        for (Utilisateur u : utilisateurs) {
            if (u.email.equals(utilisateur.email)) {
                System.out.println("❌ Compte déjà existant !");
                return;
            }
        }

        utilisateurs.add(utilisateur);
        System.out.println("✅ Compte créé avec succès pour : " + utilisateur.nom);
    }

    public void afficherComptes() {
        System.out.println("📋 Liste des utilisateurs :");
        for (Utilisateur u : utilisateurs) {
            System.out.println("- " + u.nom + " (" + u.role + ")");
        }
    }
}
