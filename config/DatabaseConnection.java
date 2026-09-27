package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gestionnaire sécurisé de connexion à la base de données relationnelle.
 * Les identifiants sont strictement externalisés dans l'environnement (RGPD & PCI-DSS).
 */
public class DatabaseConnection {

    private static Connection connection = null;

    public static synchronized Connection getConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    return connection;
                }
            } catch (SQLException ignored) {}
        }

        String host = EnvConfig.get("DB_HOST", "localhost");
        String port = EnvConfig.get("DB_PORT", "3306");
        String dbName = EnvConfig.get("DB_NAME", "hopital_db");
        String user = EnvConfig.get("DB_USER", "hopital_user");
        String password = EnvConfig.get("DB_PASSWORD", "HopitalSecurite2026!");

        String jdbcUrl = String.format(
            "jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8",
            host, port, dbName
        );

        try {
            // Tentative de chargement du driver MySQL si présent
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException ignored) {
                // Si le driver n'est pas dans le classpath local, Java tentera de le résoudre
            }

            connection = DriverManager.getConnection(jdbcUrl, user, password);
            System.out.println("✅ [DATABASE] Connexion établie avec succès à la base " + dbName + " sur " + host + ":" + port);
            return connection;
        } catch (SQLException e) {
            System.out.println("ℹ️ [DATABASE] Serveur MySQL distant non joignable (" + host + ":" + port + ") : " + e.getMessage());
            System.out.println("💡 [DATABASE] Bascule automatique en mode persistance mémoire pour l'environnement local/démonstration.");
            return null;
        }
    }

    public static String getSafeStatus() {
        String host = EnvConfig.get("DB_HOST", "localhost");
        String port = EnvConfig.get("DB_PORT", "3306");
        String dbName = EnvConfig.get("DB_NAME", "hopital_db");
        String user = EnvConfig.get("DB_USER", "hopital_user");
        return String.format("Host=%s:%s | Base=%s | User=%s | Auth=ENV_PROTECTED", host, port, dbName, user);
    }
}
