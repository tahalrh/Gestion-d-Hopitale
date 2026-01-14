package model;

import java.time.LocalDateTime;

public class RendezVous implements Runnable {

    private int id;
    private LocalDateTime dateHeure;
    private String statut;
    private Patient patient;
    private Utilisateur medecin;

    public RendezVous(int id, LocalDateTime dateHeure,
                      Patient patient, Utilisateur medecin) {
        this.id = id;
        this.dateHeure = dateHeure;
        this.patient = patient;
        this.medecin = medecin;
        this.statut = "PLANIFIE";
    }

    @Override
    public void run() {
        System.out.println("Traitement RDV pour " + patient.getNom());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        statut = "CONFIRME";
        System.out.println("RDV confirmé pour " + patient.getNom());
    }
}
