package app;

import config.DatabaseConnection;
import config.EnvConfig;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import model.*;
import security.SecurityUtils;
import service.PatientService;
import service.UtilisateurService;

/**
 * Point d'entrée principal du Système d'Information Hospitalier (SIH).
 * Démonstration des modules métiers, de la conformité RGPD et de la concurrence multi-thread.
 */
public class Main {

    public static void main(String[] args) {
        afficherBanniere();

        // 1. Initialisation des Services Métiers et Configuration
        System.out.println("🔧 [PHASE 1] Initialisation Système & Sécurité RGPD...");
        System.out.println("   ➤ Environnement : " + EnvConfig.get("APP_ENV", "production"));
        System.out.println("   ➤ Base de données : " + DatabaseConnection.getSafeStatus());
        DatabaseConnection.getConnection(); // Détecte la connexion SQL ou bascule en mode sécurisé local

        UtilisateurService utilisateurService = new UtilisateurService();
        PatientService patientService = new PatientService();

        // 2. Gestion des Acteurs et Patients (Conformité RGPD)
        System.out.println("\n👥 [PHASE 2] Création et Enregistrement des Entités...");
        Patient p1 = new Patient(1, "Ali", "Ben Ali");
        p1.setEmail("ali.benali@email.ma");
        p1.setNumeroSecuriteSociale("1850412345678");

        Patient p2 = new Patient(2, "Sara", "El Amrani");
        p2.setEmail("sara.elamrani@email.ma");
        p2.setNumeroSecuriteSociale("2920928765432");

        Utilisateur medecin = new Medecin(1, "Hassan", "Dr", "Cardiologie", "Soins Intensifs");
        medecin.setEmail("doc.hassan@hopital.ma");

        Utilisateur admin = new Administrateur(
                99, "Alami", "Karim",
                "admin@hopital.ma", "0600112233", "AdminRootSecure2026!", null
        );

        utilisateurService.creerCompte(medecin);
        utilisateurService.creerCompte(admin);
        patientService.enregistrerPatient(p1);
        patientService.enregistrerPatient(p2);

        // Consultation et dossier médical
        if (medecin instanceof Medecin) {
            ((Medecin) medecin).consulterPatient(p1, "Pouls régulier, tension artérielle 12/8.");
            ((Medecin) medecin).prescrireTraitement(p1, "Bêta-bloquant 50mg", "1 comprimé chaque matin");
        }

        utilisateurService.afficherComptes();
        patientService.afficherRegistrePatients();

        // 3. Traitements Concurrents & Multi-Threading
        System.out.println("\n⚡ [PHASE 3] Exécution des Processus Concurrents & Verrous...");
        List<Thread> threads = new ArrayList<>();

        // A. Traitement Asynchrone des Rendez-Vous (Runnable)
        System.out.println("\n--- [A] File Asynchrone des Rendez-Vous (Threads Runnable) ---");
        RendezVous rdv1 = new RendezVous(1, LocalDateTime.now().plusDays(1), p1, medecin);
        RendezVous rdv2 = new RendezVous(2, LocalDateTime.now().plusDays(2), p2, medecin);

        Thread tRdv1 = new Thread(rdv1, "Thread-RDV-1");
        Thread tRdv2 = new Thread(rdv2, "Thread-RDV-2");
        threads.add(tRdv1);
        threads.add(tRdv2);
        tRdv1.start();
        tRdv2.start();

        // B. Contrôle d'Accès aux Chambres avec Sémaphore (Capacité limitée)
        System.out.println("\n--- [B] Régulation d'Admission en Chambre (Semaphore Capacité 2) ---");
        Chambre chambre = new Chambre(101, "Simple", 2);
        Thread tChambre1 = new Thread(() -> chambre.occuper("Ali"), "Thread-Chambre-Ali");
        Thread tChambre2 = new Thread(() -> chambre.occuper("Sara"), "Thread-Chambre-Sara");
        Thread tChambre3 = new Thread(() -> chambre.occuper("Omar"), "Thread-Chambre-Omar");
        threads.add(tChambre1);
        threads.add(tChambre2);
        threads.add(tChambre3);
        tChambre1.start();
        tChambre2.start();
        tChambre3.start();

        // C. Règlement Financier Sécurisé avec ReentrantLock (Protection double-débit)
        System.out.println("\n--- [C] Transaction Financière Sécurisée (ReentrantLock) ---");
        Facture facture = new Facture(1, 600.0);
        Thread tFacture1 = new Thread(() -> facture.payer(), "Thread-Paiement-1");
        Thread tFacture2 = new Thread(() -> facture.payer(), "Thread-Paiement-2");
        threads.add(tFacture1);
        threads.add(tFacture2);
        tFacture1.start();
        tFacture2.start();

        // Attente de la fin des traitements
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("\n================================================================================");
        System.out.println("✅ [TERMINÉ] Tous les processus hospitaliers ont été exécutés avec succès !");
        System.out.println("🔒 Données sensibles protégées | Concurrence régulée | Piste d'audit à jour.");
        System.out.println("================================================================================\n");
    }

    private static void afficherBanniere() {
        System.out.println("================================================================================");
        System.out.println("       🏥 GESTION-D-HOPITALE - SYSTÈME D'INFORMATION HOSPITALIER (SIH)          ");
        System.out.println("         Architecture Java Concurrente • Sécurisée RGPD • Déploiement Docker    ");
        System.out.println("================================================================================");
    }
}
