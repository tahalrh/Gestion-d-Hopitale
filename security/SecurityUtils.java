package security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utilitaires de sécurité et conformité RGPD (Règlement Général sur la Protection des Données).
 * - Article 32 du RGPD : Sécurité du traitement (pseudonymisation, chiffrement, intégrité)
 * - Article 30 du RGPD : Registre des activités de traitement (piste d'audit des accès aux données de santé)
 */
public class SecurityUtils {

    private static final String SALT = "HOPITAL_SECURITY_SALT_2026_";

    /**
     * Hachage sécurisé des mots de passe en SHA-256 avec salage.
     */
    public static String hashPassword(String password) {
        if (password == null || password.isEmpty()) {
            return "";
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest((SALT + password).getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur d'initialisation de l'algorithme SHA-256", e);
        }
    }

    /**
     * Masquage du numéro de sécurité sociale / NIR pour protéger la vie privée des patients.
     * Exemple : 1850412345678 -> 185******5678
     */
    public static String maskNIR(String nir) {
        if (nir == null || nir.length() < 7) {
            return "***MASKED***";
        }
        int len = nir.length();
        String prefix = nir.substring(0, 3);
        String suffix = nir.substring(len - 4);
        return prefix + "*".repeat(len - 7) + suffix;
    }

    /**
     * Masquage d'une adresse email pour la conformité d'affichage public / logs.
     * Exemple : patient@hospital.ma -> p*****t@hospital.ma
     */
    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "***@***";
        }
        String[] parts = email.split("@");
        String name = parts[0];
        String domain = parts[1];
        if (name.length() <= 2) {
            return name.charAt(0) + "*@" + domain;
        }
        return name.charAt(0) + "*".repeat(name.length() - 2) + name.charAt(name.length() - 1) + "@" + domain;
    }

    /**
     * Journalisation d'audit RGPD pour toute manipulation de données de santé sensibles.
     */
    public static void logAudit(String user, String action, String resource, String details) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.printf("📋 [AUDIT RGPD] %s | Utilisateur: %-15s | Action: %-10s | Ressource: %-15s | %s%n",
                timestamp, user, action, resource, details);
    }
}
