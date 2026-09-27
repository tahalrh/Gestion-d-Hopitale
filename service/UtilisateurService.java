package service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import model.Utilisateur;
import security.SecurityUtils;

/**
 * Service de gestion des comptes utilisateurs (Médecins, Patients, Administrateurs).
 * Thread-safe et conforme aux règles de protection des identifiants.
 */
public class UtilisateurService {

    private final List<Utilisateur> utilisateurs = Collections.synchronizedList(new ArrayList<>());

    public synchronized boolean creerCompte(Utilisateur utilisateur) {
        if (utilisateur == null || utilisateur.getEmail() == null) {
            System.err.println("❌ Utilisateur invalide.");
            return false;
        }

        for (Utilisateur u : utilisateurs) {
            if (u.getEmail().equalsIgnoreCase(utilisateur.getEmail())) {
                System.out.println("❌ Compte déjà existant pour l'email : " + SecurityUtils.maskEmail(utilisateur.getEmail()));
                return false;
            }
        }

        utilisateurs.add(utilisateur);
        System.out.println("✅ Compte créé avec succès pour : " + utilisateur.getNomComplet() + " [" + utilisateur.getRole() + "]");
        SecurityUtils.logAudit("ADMIN", "CREATE_USER", "UTILISATEUR",
                "Création utilisateur ID #" + utilisateur.getId() + " - " + SecurityUtils.maskEmail(utilisateur.getEmail()));
        return true;
    }

    public Optional<Utilisateur> rechercherParEmail(String email) {
        synchronized (utilisateurs) {
            return utilisateurs.stream()
                    .filter(u -> u.getEmail().equalsIgnoreCase(email))
                    .findFirst();
        }
    }

    public Optional<Utilisateur> rechercherParId(int id) {
        synchronized (utilisateurs) {
            return utilisateurs.stream()
                    .filter(u -> u.getId() == id)
                    .findFirst();
        }
    }

    public boolean authentifier(String email, String motDePasse) {
        Optional<Utilisateur> userOpt = rechercherParEmail(email);
        if (userOpt.isPresent()) {
            return userOpt.get().seConnecter(email, motDePasse);
        }
        SecurityUtils.logAudit(email, "AUTH_FAILED", "UTILISATEUR", "Identifiant inconnu");
        return false;
    }

    public void afficherComptes() {
        System.out.println("\n📋 [ANNUAIRE SÉCURISÉ] Liste des utilisateurs actifs :");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("%-5s | %-20s | %-12s | %-25s | %-10s%n", "ID", "NOM COMPLET", "RÔLE", "EMAIL (MASQUÉ)", "STATUT");
        System.out.println("--------------------------------------------------------------------------------");
        synchronized (utilisateurs) {
            for (Utilisateur u : utilisateurs) {
                System.out.printf("%-5d | %-20s | %-12s | %-25s | %-10s%n",
                        u.getId(),
                        u.getNomComplet(),
                        u.getRole(),
                        SecurityUtils.maskEmail(u.getEmail()),
                        u.isActif() ? "ACTIF" : "INACTIF");
            }
        }
        System.out.println("--------------------------------------------------------------------------------");
    }

    public List<Utilisateur> getTousLesUtilisateurs() {
        synchronized (utilisateurs) {
            return new ArrayList<>(utilisateurs);
        }
    }
}
