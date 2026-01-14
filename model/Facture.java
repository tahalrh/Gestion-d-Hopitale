package model;

import java.time.LocalDate;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Facture {

    private int id;
    private double montant;
    private String statut;
    private LocalDate date;

    private Lock lock = new ReentrantLock();

    public Facture(int id, double montant) {
        this.id = id;
        this.montant = montant;
        this.statut = "NON_PAYEE";
        this.date = LocalDate.now();
    }

    public void payer() {
        lock.lock();
        try {
            System.out.println("Paiement en cours...");
            Thread.sleep(1500);
            statut = "PAYEE";
            System.out.println("Facture payée : " + montant + " DH");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
