package service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import model.Patient;
import security.SecurityUtils;

/**
 * Service métier dédié à la gestion des Patients (CRUD, Dossiers Médicaux et RGPD).
 */
public class PatientService {

    private final List<Patient> patients = Collections.synchronizedList(new ArrayList<>());

    public synchronized void enregistrerPatient(Patient patient) {
        if (patient == null) return;
        patients.add(patient);
        SecurityUtils.logAudit("SECRETARIAT", "CREATE_PATIENT", "PATIENT",
                "Admission patient #" + patient.getId() + " - " + patient.getNomComplet());
    }

    public Optional<Patient> trouverParId(int id) {
        synchronized (patients) {
            return patients.stream().filter(p -> p.getId() == id).findFirst();
        }
    }

    public List<Patient> rechercherParNom(String query) {
        if (query == null || query.trim().isEmpty()) return Collections.emptyList();
        String lower = query.toLowerCase();
        synchronized (patients) {
            return patients.stream()
                    .filter(p -> p.getNom().toLowerCase().contains(lower) || p.getPrenom().toLowerCase().contains(lower))
                    .collect(Collectors.toList());
        }
    }

    public void afficherRegistrePatients() {
        System.out.println("\n🏥 [REGISTRE DES PATIENTS - CONFORMITÉ RGPD]");
        System.out.println("--------------------------------------------------------------------------------------------------");
        System.out.printf("%-5s | %-20s | %-12s | %-8s | %-20s | %-20s%n", 
                "ID", "NOM COMPLET", "NAISSANCE", "GROUPE", "NIR (SÉCURISÉ)", "CONTACT MASQUÉ");
        System.out.println("--------------------------------------------------------------------------------------------------");
        synchronized (patients) {
            for (Patient p : patients) {
                System.out.printf("%-5d | %-20s | %-12s | %-8s | %-20s | %-20s%n",
                        p.getId(),
                        p.getNomComplet(),
                        p.getDateNaissance(),
                        p.getGroupeSanguin(),
                        p.getNumeroSecuriteSocialeMasque(),
                        SecurityUtils.maskEmail(p.getEmail()));
            }
        }
        System.out.println("--------------------------------------------------------------------------------------------------");
    }

    public List<Patient> getTousLesPatients() {
        synchronized (patients) {
            return new ArrayList<>(patients);
        }
    }
}
