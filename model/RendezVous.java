package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import security.SecurityUtils;

/**
 * Gestion asynchrone des consultations et rendez-vous (implements Runnable).
 * Permet le traitement concurrent des confirmations et notifications patients.
 */
public class RendezVous implements Runnable {

    private int id;
    private LocalDateTime dateHeure;
    private String statut;
    private String motif;
    private Patient patient;
    private Utilisateur medecin;

    public RendezVous(int id, LocalDateTime dateHeure, Patient patient, Utilisateur medecin, String motif) {
        this.id = id;
        this.dateHeure = dateHeure;
        this.patient = patient;
        this.medecin = medecin;
        this.motif = motif;
        this.statut = "PLANIFIE";
        if (patient != null) {
            patient.ajouterRendezVous(this);
        }
    }

    public RendezVous(int id, LocalDateTime dateHeure, Patient patient, Utilisateur medecin) {
        this(id, dateHeure, patient, medecin, "Consultation générale");
    }

    @Override
    public void run() {
        String patientNom = (patient != null) ? patient.getNomComplet() : "Patient Inconnu";
        String medecinNom = (medecin != null) ? medecin.getNomComplet() : "Médecin Inconnu";
        
        System.out.println("📅 [RDV #" + id + "] ⚙️ Traitement asynchrone du rendez-vous pour " + patientNom + " avec " + medecinNom + "...");
        try {
            Thread.sleep(1500); // Simulation vérification disponibilité & envoi SMS/Email
            this.statut = "CONFIRME";
            System.out.println("📅 [RDV #" + id + "] ✅ Statut mis à jour : CONFIRMÉ pour " + patientNom + " à " + 
                    dateHeure.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            SecurityUtils.logAudit("SYSTEM_NOTIF", "CONFIRMATION_RDV", "RDV_" + id,
                    "Notification envoyée au patient " + patientNom);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            this.statut = "ERREUR";
            System.err.println("❌ Interruption du traitement du RDV #" + id + " : " + e.getMessage());
        }
    }

    // Getters & Setters
    public int getId() { return id; }
    public LocalDateTime getDateHeure() { return dateHeure; }
    public String getStatut() { return statut; }
    public String getMotif() { return motif; }
    public Patient getPatient() { return patient; }
    public Utilisateur getMedecin() { return medecin; }

    public void setStatut(String statut) { this.statut = statut; }
    public void setMotif(String motif) { this.motif = motif; }

    @Override
    public String toString() {
        return String.format("RDV #%d | Date: %s | Patient: %s | Médecin: %s | Statut: %s",
                id, dateHeure.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                patient != null ? patient.getNomComplet() : "N/A",
                medecin != null ? medecin.getNomComplet() : "N/A",
                statut);
    }
}
