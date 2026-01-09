package model;

public abstract class Utilisateur {
    protected int id;
    protected String login;
    protected String password;
    protected String role;
    protected String nom;
    protected String prenom;
    protected String email;
    protected boolean actif;
    
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_MEDECIN = "MEDECIN";
    public static final String ROLE_PATIENT = "PATIENT";
    
    public Utilisateur(int id, String nom, String prenom, String role) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.role = role;
        this.actif = true;
    }
    
    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getNomComplet() { return prenom + " " + nom; }
    public String getRole() { return role; }
    public boolean isActif() { return actif; }
    
    public void setActif(boolean actif) { this.actif = actif; }
    public void setLogin(String login) { this.login = login; }
    public void setPassword(String password) { this.password = password; }
    
    public abstract boolean seConnecter(String login, String password);
    public abstract void seDeconnecter();
}