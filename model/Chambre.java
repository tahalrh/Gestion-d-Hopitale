package model;

import java.util.concurrent.Semaphore;
import security.SecurityUtils;

/**
 * Gestion concurrente des admissions en chambre avec contrôle par Sémaphore.
 * Régule l'accès physique et prévient la surcapacité hospitalière.
 */
public class Chambre {

    private int numero;
    private String type;
    private int capacite;
    private final Semaphore semaphore;

    public Chambre(int numero, String type, int capacite) {
        this.numero = numero;
        this.type = type;
        this.capacite = capacite;
        this.semaphore = new Semaphore(capacite, true); // Semaphore équitable (FIFO)
    }

    public Chambre(int numero, String type) {
        this(numero, type, 2); // Capacité par défaut de 2 places
    }

    /**
     * Admission d'un patient avec acquisition sécurisée du sémaphore.
     */
    public void occuper(String patient) {
        System.out.println("⏳ [CHAMBRE " + numero + "] Demande d'admission pour " + patient + " (Lits libres : " + semaphore.availablePermits() + ")");
        try {
            semaphore.acquire();
            System.out.println("🛏️ [CHAMBRE " + numero + "] ✅ " + patient + " est installé(e) dans la chambre (" + type + ")");
            SecurityUtils.logAudit(patient, "ADMISSION", "CHAMBRE_" + numero, "Entrée en chambre");
            
            // Simulation de la durée de séjour/consultation
            Thread.sleep(2000);
            
            System.out.println("🚪 [CHAMBRE " + numero + "] ⬅️ " + patient + " quitte la chambre");
            SecurityUtils.logAudit(patient, "DECHARGE", "CHAMBRE_" + numero, "Sortie de chambre");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("❌ Interruption lors de l'occupation de la chambre : " + e.getMessage());
        } finally {
            semaphore.release();
            System.out.println("✨ [CHAMBRE " + numero + "] Place libérée (Disponibles : " + semaphore.availablePermits() + "/" + capacite + ")");
        }
    }

    public int getNumero() { return numero; }
    public String getType() { return type; }
    public int getCapacite() { return capacite; }
    public int getPlacesDisponibles() { return semaphore.availablePermits(); }
}
