package model;

import java.util.ArrayList;
import java.util.List;
import security.SecurityUtils;

/**
 * Représente un médecin / praticien hospitalier.
 */
public class Medecin extends Utilisateur {

    private String specialite;
    private String serviceHopital;
    private String numeroOrdre;
    private List<Patient> patientsSuivis;

    public Medecin(int id, String nom, String prenom, String specialite, String serviceHopital) {
        super(id, nom, prenom, "MEDECIN");
        this.specialite = specialite;
        this.serviceHopital = serviceHopital;
        this.numeroOrdre = "ORDRE-" + id + "-2026";
        this.patientsSuivis = new ArrayList<>();
    }

    public Medecin(int id, String nom, String prenom, String email, String telephone,
                   String motDePasse, String specialite, String serviceHopital, String numeroOrdre) {
        super(id, nom, prenom, email, telephone, motDePasse, "MEDECIN");
        this.specialite = specialite;
        this.serviceHopital = serviceHopital;
        this.numeroOrdre = numeroOrdre;
        this.patientsSuivis = new ArrayList<>();
    }

    public void consulterPatient(Patient patient, String observations) {
        if (!patientsSuivis.contains(patient)) {
            patientsSuivis.add(patient);
        }
        patient.getDossierMedical().ajouterObservation(observations, "Dr " + getNom());
        SecurityUtils.logAudit("Dr " + getNom(), "CONSULTATION", "PATIENT",
                "Consultation effectuée pour " + patient.getNomComplet() + " en " + specialite);
    }

    public void prescrireTraitement(Patient patient, String medicament, String posologie) {
        patient.getDossierMedical().ajouterObservation("Prescription: " + medicament + " (" + posologie + ")", "Dr " + getNom());
        SecurityUtils.logAudit("Dr " + getNom(), "PRESCRIPTION", "ORDONNANCE",
                "Prescription délivrée à " + patient.getNomComplet() + " : " + medicament);
    }

    // Getters & Setters
    public String getSpecialite() { return specialite; }
    public String getServiceHopital() { return serviceHopital; }
    public String getNumeroOrdre() { return numeroOrdre; }
    public List<Patient> getPatientsSuivis() { return new ArrayList<>(patientsSuivis); }

    public void setSpecialite(String specialite) { this.specialite = specialite; }
    public void setServiceHopital(String serviceHopital) { this.serviceHopital = serviceHopital; }
    public void setNumeroOrdre(String numeroOrdre) { this.numeroOrdre = numeroOrdre; }

    @Override
    public String toString() {
        return String.format("Dr %s (%s - %s) [Ordre: %s]",
                getNomComplet(), specialite, serviceHopital, numeroOrdre);
    }
}
