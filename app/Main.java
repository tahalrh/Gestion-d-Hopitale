package app;

import java.time.LocalDateTime;

import model.*;

public class Main {

    public static void main(String[] args) {

        Patient p1 = new Patient(1, "Ali", "Ben Ali");
        Patient p2 = new Patient(2, "Sara", "El Amrani");

        Utilisateur medecin = new Administrateur(
                1, "Dr", "Hassan",
                "doc@hopital.ma", "0600", "1234", null
        );

        // Thread RDV
        RendezVous rdv1 = new RendezVous(1, LocalDateTime.now(), p1, medecin);
        RendezVous rdv2 = new RendezVous(2, LocalDateTime.now(), p2, medecin);

        new Thread(rdv1).start();
        new Thread(rdv2).start();

        // Semaphore Chambre
        Chambre chambre = new Chambre(101, "Simple");
        new Thread(() -> chambre.occuper("Ali")).start();
        new Thread(() -> chambre.occuper("Sara")).start();
        new Thread(() -> chambre.occuper("Omar")).start();

        // Lock Facture
        Facture facture = new Facture(1, 600);
        new Thread(() -> facture.payer()).start();
        new Thread(() -> facture.payer()).start();
    }
}
