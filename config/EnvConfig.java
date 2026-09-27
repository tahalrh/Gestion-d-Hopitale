package config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Chargeur de configuration d'environnement (.env et variables système).
 * Conforme aux principes de sécurité 12-Factor App :
 * Aucune information d'identification n'est stockée en dur dans le code source.
 */
public class EnvConfig {

    private static final Map<String, String> envMap = new HashMap<>();
    private static boolean initialized = false;

    static {
        loadEnvironment();
    }

    private static synchronized void loadEnvironment() {
        if (initialized) return;

        // 1. Recherche du fichier .env à la racine ou dans le répertoire de travail
        File[] candidateFiles = new File[] {
            new File(".env"),
            new File("../.env"),
            new File(System.getProperty("user.dir"), ".env")
        };

        for (File file : candidateFiles) {
            if (file.exists() && file.isFile()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (line.isEmpty() || line.startsWith("#")) {
                            continue;
                        }
                        int separatorIndex = line.indexOf('=');
                        if (separatorIndex > 0) {
                            String key = line.substring(0, separatorIndex).trim();
                            String value = line.substring(separatorIndex + 1).trim();
                            // Nettoyage des guillemets optionnels
                            if ((value.startsWith("\"") && value.endsWith("\"")) ||
                                (value.startsWith("'") && value.endsWith("'"))) {
                                value = value.substring(1, value.length() - 1);
                            }
                            envMap.put(key, value);
                        }
                    }
                    System.out.println("🔒 [SECURITE] Fichier .env chargé avec succès : " + file.getPath());
                    break;
                } catch (IOException e) {
                    System.err.println("⚠️ Impossible de lire le fichier .env : " + e.getMessage());
                }
            }
        }
        initialized = true;
    }

    public static String get(String key, String defaultValue) {
        // Priorité 1 : Variable d'environnement système (Docker / CI / Kubernetes)
        String sysEnv = System.getenv(key);
        if (sysEnv != null && !sysEnv.isEmpty()) {
            return sysEnv;
        }

        // Priorité 2 : Fichier .env local
        String fileEnv = envMap.get(key);
        if (fileEnv != null && !fileEnv.isEmpty()) {
            return fileEnv;
        }

        // Priorité 3 : Valeur par défaut fournie
        return defaultValue;
    }

    public static String get(String key) {
        return get(key, "");
    }

    public static int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(get(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        return Boolean.parseBoolean(get(key, String.valueOf(defaultValue)));
    }
}
