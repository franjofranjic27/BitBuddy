# BitBuddy - Your Crypto Trading Bot

Ein modularer Krypto-Trading-Bot auf Basis von **Spring Boot (Java 21)** in einer **Microservice-Monorepo**-Architektur.
Jeder Service besitzt seine eigene Datenbank und ist klar abgegrenzt.

---

## 📋 Überblick

**BitBuddy** ist ein experimenteller Trading-Bot für Kryptowährungen mit modularer Architektur:

- **Market Data Service** – Streamt Preise von Exchanges (Kraken, KuCoin, ...) und publiziert auf Kafka
- **Trade Decision Service** – Wendet Handelsstrategien an (z.B. MA-Cross) und erzeugt Signale
- **Order Execution Service** – Führt Orders aus oder simuliert sie

---

## 🏗️ Services

### Market Data Service

Streamt Trades/Preise von Exchanges (Kraken, KuCoin), normalisiert und persistiert diese in PostgreSQL, publiziert
Events auf Kafka (`market-data-topic`).

### Trade Decision Service

Konsumiert Marktdaten von Kafka, wendet Trading-Strategien an (z.B. MA-Cross) und publiziert Handelsentscheidungen (
`trade-decision-topic`).

### Order Execution Service

Konsumiert Handelsentscheidungen, transformiert diese in ausführbare Orders und sendet sie an die Exchange oder
simuliert die Ausführung. Persistiert Executions in PostgreSQL.

---

## 🚀 Quick Start

```bash
# Infrastruktur starten (Kafka, PostgreSQL)
docker-compose up -d

# Module bauen
mvn -T 1C clean install

# Service starten (z.B. Market Data Service)
cd market-data-service
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Stoppen
docker-compose down
```

**Kafka Debug:**

```bash
# Topics anzeigen
docker exec -it kafka kafka-topics.sh --bootstrap-server kafka:9092 --list

# Nachrichten konsumieren
docker exec -it kafka kafka-console-consumer.sh --bootstrap-server kafka:9092 \
  --topic market-data-topic --from-beginning --timeout-ms 5000
```

---

## ⚙️ Konfiguration

### Exchange Adapter

Interface `MarketDataStreamingService` mit Implementierungen für verschiedene Exchanges (Kraken, KuCoin). Austausch
erfolgt via Konfiguration:

```yaml
marketdata:
  provider: krakenMarketDataStreamingService  # oder kucoinMarketDataStreamingService
  tradingPairs:
    - BTC/USD
    - ETH/USD

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/marketdata
    username: market
    password: secret
  kafka:
    bootstrap-servers: localhost:9092
```

Die Factory löst den Bean dynamisch auf – kein Code-Refactor beim Exchange-Wechsel nötig.

---

## 🧪 Tests

```bash
mvn verify
```

**Empfehlungen:**

- Exchange-Adapter mocken (kein Live-WebSocket)
- Integrationstests mit Testcontainers (Kafka, PostgreSQL)
- Strategie-Signale verifizieren

---

## 📈 Trading-Strategie: MA Cross (MA5/MA7)

Berechnet zwei einfache gleitende Durchschnitte (SMA) mit Fenstergröße 5 und 7:

- **BUY**: Short SMA kreuzt Long SMA nach oben
- **SELL**: Short SMA kreuzt Long SMA nach unten

**Formel:** `SMA_n = (Summe der letzten n Preise) / n`

**Edge Cases:**

- Weniger als 7 Preise → kein Signal
- SMA-Gleichheit → kein Richtungswechsel
- Optional: Debounce bei hoher Volatilität

---

## ☸️ Deployment: Kubernetes (Minikube)

```bash
# Minikube starten
minikube start

# Helm Deployment
cd helm
helm install bitbuddy . -n bitbuddy --create-namespace -f values.yaml -f values-dev.yaml

# Status prüfen
kubectl get pods -n bitbuddy
kubectl logs <pod> -n bitbuddy

# Optional: Kafka Debug
kubectl exec -n bitbuddy -it <kafka-pod> -- \
  kafka-topics.sh --bootstrap-server localhost:9092 --list
```

---

## ☁️ Deployment: AWS (EKS)

### CloudFormation Reihenfolge

1. **Base Setup**: `base-setup.yaml` (VPC, Subnets, Security Groups, IAM Rollen)
2. **EKS Cluster**: `eks.yaml` (IAM Rollen-ARN kopieren)
3. **RDS**: `rds.yaml` (PostgreSQL)

### Vorbereitung

```bash
open ~/.aws/credentials
aws eks update-kubeconfig --region us-east-1 --name bitbuddy
kubectl create namespace bitbuddy
kubectl config set-context --current --namespace=bitbuddy
```

### Helm Deployment

```bash
helm install bitbuddy helm
```

### Nginx Ingress Controller (LoadBalancer)

```bash
# Repository hinzufügen
helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
helm repo update

# Ingress Controller installieren
helm install ingress-nginx ingress-nginx/ingress-nginx \
  --set controller.service.type=LoadBalancer \
  --namespace ingress-nginx \
  --create-namespace

# Warten bis bereit
kubectl wait --namespace ingress-nginx \
  --for=condition=ready pod \
  --selector=app.kubernetes.io/component=controller \
  --timeout=120s

# Status prüfen
kubectl get svc -n ingress-nginx ingress-nginx-controller
```

### Prüfung

```bash
kubectl get nodes
kubectl get pods
kubectl logs <pod>
```

---

## 🔒 Sicherheit & Secrets

Da es sich um ein Schulungs-/Lehrprojekt handelt, wird kein umfangreiches Secrets Management umgesetzt. Minimale
Massnahmen:

- Keine API Keys im Git Repository (Umgebungsvariablen oder lokale `.env` Dateien)
- Keine sensiblen Zugangsdaten in Klartext in `application.yml`
- Für produktive Szenarien: Einsatz von Kubernetes Secrets, SOPS, Vault, AWS KMS empfohlen.

---

## ⚠️ Haftungsausschluss

Bildungs- und Experimentierprojekt. Kein Anspruch auf Profitabilität. Einsatz auf eigene Verantwortung.