package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Patient extends Utilisateur {
    private Date dateNaissance;
    private String adresse;
    private String telephone;
    private String groupeSanguin;
    private String numeroSecurite;
    private DossierMedical dossierMedical;
    private List<RendezVous> rendezVous;
    private List<Facture> factures;
    
    public Patient(int id, String nom, String prenom, Date dateNaissance) {
        super(id, nom, prenom, ROLE_PATIENT);
        this.dateNaissance = dateNaissance;
        this.rendezVous = new ArrayList<>();
        this.factures = new ArrayList<>();
        this.dossierMedical = new DossierMedical(this);
    }
    
    public Date getDateNaissance() { return dateNaissance; }
    public String getAdresse() { return adresse; }
    public String getTelephone() { return telephone; }
    public String getGroupeSanguin() { return groupeSanguin; }
    public DossierMedical getDossierMedical() { return dossierMedical; }
    
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public void setGroupeSanguin(String groupeSanguin) { this.groupeSanguin = groupeSanguin; }
    
    public RendezVous prendreRendezVous(Medecin medecin, Date date) {
        RendezVous rdv = new RendezVous(this, medecin, date);
        rendezVous.add(rdv);
        return rdv;
    }
    
    public boolean payerFacture(Facture facture, double montant) {
        return facture.appliquerPaiement(montant);
    }
    
    public List<Consultation> voirHistorique() {
        return dossierMedical.getConsultations();
    }
    
    public void ajouterFacture(Facture facture) {
        factures.add(facture);
    }
    
    @Override
    public boolean seConnecter(String login, String password) {
        return this.login.equals(login) && this.password.equals(password);
    }
    
    @Override
    public void seDeconnecter() {
        System.out.println("Patient " + getNomComplet() + " déconnecté");
    }
}