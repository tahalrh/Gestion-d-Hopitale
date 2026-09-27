# ==============================================================================
# Multi-stage Dockerfile - Système d'Information Hospitalier (Java 21)
# Optimisé pour la sécurité (utilisateur non-root, image Alpine minimale)
# ==============================================================================

# --- Étape 1 : Compilation & Packaging ---
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /build

# Copie de l'arborescence des sources
COPY . .

# Compilation modulaire des sources Java
RUN mkdir -p bin && \
    javac -d bin \
    module-info.java \
    app/Main.java \
    config/*.java \
    model/*.java \
    security/*.java \
    service/*.java

# Création du JAR exécutable avec le manifeste
RUN jar --create --file /build/gestion-hopital.jar \
    --main-class app.Main \
    -C bin .

# --- Étape 2 : Image d'Exécution Minimale & Sécurisée ---
FROM eclipse-temurin:21-jre-alpine AS runtime

# Métadonnées de l'image
LABEL maintainer="Taha El Rhayyate"
LABEL description="Système d'Information Hospitalier (SIH) - Backend Haute Concurrence"
LABEL version="1.0.0"

# Sécurité : Création d'un utilisateur et groupe système non-root
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# Copie du binaire compilé depuis l'étape précédente
COPY --from=builder --chown=appuser:appgroup /build/gestion-hopital.jar /app/gestion-hopital.jar

# Configuration des variables d'environnement par défaut
ENV APP_ENV=production \
    JAVA_OPTS="-Xms128m -Xmx512m -XX:+UseG1GC"

# Exécution sous l'utilisateur restreint
USER appuser

# Exposition du port applicatif
EXPOSE 8080

# Commande de démarrage
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar gestion-hopital.jar"]
