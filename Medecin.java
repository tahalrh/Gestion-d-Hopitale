package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Medecin extends Utilisateur {
    private String specialite;
    private String service;
    private List<Patient> patientsSuivis;
    private List<RendezVous> rendezVous;
    
    public Medecin(int id, String nom, String prenom, String specialite) {
        super(id, nom, prenom, ROLE_MEDECIN);
        this.specialite = specialite;
        this.patientsSuivis = new ArrayList<>();
        this.rendezVous = new ArrayList<>();
    }
    
    // Getters
    public String getSpecialite() { return specialite; }
    public String getService() { return service; }
    public List<Patient> getPatientsSuivis() { return new ArrayList<>(patientsSuivis); }
    
    // Setters
    public void setService(String service) { this.service = service; }
    
    // Méthodes métier
    public Consultation consulterPatient(Patient patient, String symptomes) {
        Consultation consultation = new Consultation(patient, this, new Date(), symptomes);
        patient.getDossierMedical().ajouterConsultation(consultation);
        patientsSuivis.add(patient);
        return consultation;
    }
    
    public Prescription prescrireTraitement(Consultation consultation, String medicament, String posologie) {
        return consultation.creerPrescription(medicament, posologie);
    }
    
    public RendezVous programmerRendezVous(Patient patient, Date date, String motif) {
        RendezVous rdv = new RendezVous(patient, this, date);
        rdv.setMotif(motif);
        rendezVous.add(rdv);
        patient.prendreRendezVous(this, date);
        return rdv;
    }
    
    public List<RendezVous> voirPlanning() {
        return new ArrayList<>(rendezVous);
    }
    
    public void mettreAJourDossier(Patient patient, String notes) {
        if (patientsSuivis.contains(patient)) {
            patient.getDossierMedical().ajouterNotes(notes);
        }
    }
    
    // Implémentation des méthodes abstraites
    @Override
    public boolean seConnecter(String login, String password) {
        return this.login.equals(login) && this.password.equals(password);
    }
    
    @Override
    public void seDeconnecter() {
        System.out.println("Médecin " + getNomComplet() + " déconnecté");
    }
}