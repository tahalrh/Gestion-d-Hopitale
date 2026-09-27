package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import security.SecurityUtils;

/**
 * Dossier Médical Partagé (DMP) d'un patient.
 * Données sensibles soumises aux exigences strictes de confidentialité RGPD.
 */
public class DossierMedical {

    private Patient patient;
    private LocalDateTime dateCreation;
    private LocalDateTime derniereMiseAJour;
    private List<String> allergies;
    private List<String> antecedents;
    private List<String> observations;

    public DossierMedical(Patient patient) {
        this.patient = patient;
        this.dateCreation = LocalDateTime.now();
        this.derniereMiseAJour = LocalDateTime.now();
        this.allergies = new ArrayList<>();
        this.antecedents = new ArrayList<>();
        this.observations = new ArrayList<>();
    }

    public void ajouterAllergie(String allergie, String praticien) {
        allergies.add(allergie);
        this.derniereMiseAJour = LocalDateTime.now();
        SecurityUtils.logAudit(praticien, "UPDATE_DMP", "ALLERGIE", 
            "Ajout allergie pour patient #" + patient.getId() + " (" + allergie + ")");
    }

    public void ajouterAntecedent(String antecedent, String praticien) {
        antecedents.add(antecedents.size() + ". " + antecedent);
        this.derniereMiseAJour = LocalDateTime.now();
        SecurityUtils.logAudit(praticien, "UPDATE_DMP", "ANTECEDENT", 
            "Ajout antécédent pour patient #" + patient.getId());
    }

    public void ajouterObservation(String observation, String praticien) {
        observations.add("[" + LocalDateTime.now() + " par " + praticien + "] " + observation);
        this.derniereMiseAJour = LocalDateTime.now();
        SecurityUtils.logAudit(praticien, "UPDATE_DMP", "OBSERVATION", 
            "Ajout observation clinique patient #" + patient.getId());
    }

    public List<String> consulterDossier(String praticien) {
        SecurityUtils.logAudit(praticien, "READ_DMP", "DOSSIER_COMPLET", 
            "Consultation dossier patient #" + patient.getId());
        return new ArrayList<>(observations);
    }

    // Getters
    public Patient getPatient() { return patient; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public LocalDateTime getDerniereMiseAJour() { return derniereMiseAJour; }
    public List<String> getAllergies() { return new ArrayList<>(allergies); }
    public List<String> getAntecedents() { return new ArrayList<>(antecedents); }
    public List<String> getObservations() { return new ArrayList<>(observations); }
}
