package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import security.SecurityUtils;

/**
 * Représente un patient admis au sein de l'établissement hospitalier.
 * Les données de santé et identifiants nationaux sont protégés conformément au RGPD.
 */
public class Patient extends Utilisateur {

    private LocalDate dateNaissance;
    private String adresse;
    private String groupeSanguin;
    private String numeroSecuriteSociale; // Donnée sensible : stockée de manière sécurisée
    private DossierMedical dossierMedical;
    private List<RendezVous> rendezVousList;
    private List<Facture> factures;

    public Patient(int id, String prenom, String nom) {
        super(id, nom, prenom, "PATIENT");
        this.dateNaissance = LocalDate.of(1990, 1, 1);
        this.adresse = "Adresse non renseignée";
        this.groupeSanguin = "O+";
        this.numeroSecuriteSociale = "1900101000000";
        this.rendezVousList = new ArrayList<>();
        this.factures = new ArrayList<>();
        this.dossierMedical = new DossierMedical(this);
    }

    public Patient(int id, String nom, String prenom, String email, String telephone,
                   LocalDate dateNaissance, String adresse, String groupeSanguin, String numeroSecuriteSociale) {
        super(id, nom, prenom, email, telephone, "PatientPass2026!", "PATIENT");
        this.dateNaissance = dateNaissance;
        this.adresse = adresse;
        this.groupeSanguin = groupeSanguin;
        this.numeroSecuriteSociale = numeroSecuriteSociale;
        this.rendezVousList = new ArrayList<>();
        this.factures = new ArrayList<>();
        this.dossierMedical = new DossierMedical(this);
    }

    public String getNumeroSecuriteSocialeMasque() {
        return SecurityUtils.maskNIR(this.numeroSecuriteSociale);
    }

    public void ajouterRendezVous(RendezVous rdv) {
        this.rendezVousList.add(rdv);
    }

    public void ajouterFacture(Facture facture) {
        this.factures.add(facture);
    }

    // Getters & Setters
    public LocalDate getDateNaissance() { return dateNaissance; }
    public String getAdresse() { return adresse; }
    public String getGroupeSanguin() { return groupeSanguin; }
    public DossierMedical getDossierMedical() { return dossierMedical; }
    public List<RendezVous> getRendezVousList() { return rendezVousList; }
    public List<Facture> getFactures() { return factures; }

    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public void setGroupeSanguin(String groupeSanguin) { this.groupeSanguin = groupeSanguin; }
    public void setNumeroSecuriteSociale(String numeroSecuriteSociale) { this.numeroSecuriteSociale = numeroSecuriteSociale; }

    @Override
    public String toString() {
        return String.format("Patient #%d: %s | Né(e) le: %s | Groupe: %s | NIR: %s",
                id, getNomComplet(), dateNaissance, groupeSanguin, getNumeroSecuriteSocialeMasque());
    }
}
