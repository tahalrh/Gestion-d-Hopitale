package model;

import java.time.LocalDate;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import security.SecurityUtils;

/**
 * Gestion transactionnelle des factures hospitalières avec ReentrantLock.
 * Empêche les conditions de concurrence (double débit, paiements simultanés).
 */
public class Facture {

    private int id;
    private double montant;
    private String statut;
    private LocalDate date;
    private final Lock lock = new ReentrantLock();

    public Facture(int id, double montant) {
        this.id = id;
        this.montant = montant;
        this.statut = "NON_PAYEE";
        this.date = LocalDate.now();
    }

    public boolean payer() {
        lock.lock();
        try {
            if ("PAYEE".equals(statut)) {
                System.out.println("⚠️ [FACTURE #" + id + "] Rejet transaction : Facture de " + montant + " DH déjà acquittée !");
                return false;
            }

            System.out.println("💳 [FACTURE #" + id + "] Traitement du paiement de " + montant + " DH en cours...");
            Thread.sleep(1200);
            statut = "PAYEE";
            System.out.println("✅ [FACTURE #" + id + "] Règlement validé avec succès : " + montant + " DH.");
            SecurityUtils.logAudit("SYSTEM_PAIEMENT", "TRANSACTION", "FACTURE_" + id,
                    "Facture acquittée pour un montant de " + montant + " DH");
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("❌ Erreur transactionnelle lors du paiement : " + e.getMessage());
            return false;
        } finally {
            lock.unlock();
        }
    }

    // Getters
    public int getId() { return id; }
    public double getMontant() { return montant; }
    public String getStatut() { return statut; }
    public LocalDate getDate() { return date; }

    @Override
    public String toString() {
        return String.format("Facture #%d | Montant: %.2f DH | Statut: %s | Date: %s",
                id, montant, statut, date);
    }
}
