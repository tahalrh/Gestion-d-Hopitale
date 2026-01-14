package model;

import java.util.concurrent.Semaphore;

public class Chambre {

    private int numero;
    private String type;

    private static Semaphore semaphore = new Semaphore(2);

    public Chambre(int numero, String type) {
        this.numero = numero;
        this.type = type;
    }

    public void occuper(String patient) {
        try {
            semaphore.acquire();
            System.out.println(patient + " entrer dans la chambre " + numero);
            Thread.sleep(3000);
            System.out.println(patient + " sortie dans la chambre " + numero);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaphore.release();
        }
    }
}

