# BitBuddy - Your Crypto Trading Bot

Ein modularer Krypto-Trading-Bot auf Basis von **Spring Boot (Java 21)** in einer **Microservice-Monorepo**-Architektur.
Jeder Service besitzt seine eigene Datenbank und ist klar abgegrenzt.

---

## Inhaltsverzeichnis

1. Überblick & Architektur
2. Services
3. Technologie-Stack & Entscheidungen
4. Schnelles Loslegen (Quick Start)
5. Lokale Entwicklung (Details)
6. IDE Setup (IntelliJ) ✅
7. Austausch der Exchange (Adapter-Konzept)
8. Konfiguration
9. Tests
10. Strategie: Gleitender Durchschnitt (MA5/MA7 Kreuz) ⚙️
11. Deployment: Kubernetes (Minikube)
12. Deployment: AWS (CloudFormation: Reihenfolge Base → EKS → RDS)
13. Sicherheit / Secrets
14. Entscheidungslog / Trade-offs
15. Roadmap & Nächste Schritte
16. Haftungsausschluss
17. Anhang: Kommandoreferenz

---

## 1. Überblick & Architektur

BitBuddy empfängt Markt-Daten, trifft automatisiert Handelsentscheidungen und führt Orders bei einer Exchange aus. Die
Kommunikation zwischen den Services erfolgt ereignisgetrieben über Kafka.

Vereinfachtes Datenflussmodell:

```
[Exchange WebSocket] --> Market Data Service --(Kafka: market-data-topic)--> Trade Decision Service 
   --> (Kafka: trade-decision-topic) --> Order Execution Service --> [Exchange REST/WebSocket]
                                   \--> (Persistenz + Monitoring)
```

---

## 2. Services

### Market Data Service

- Streamt Trades / Preise von einer Exchange (aktuell Kraken)
- Normalisiert Daten & persistiert relevante Informationen (PostgreSQL)
- Publiziert Ereignisse auf Kafka (`market-data-topic`)

### Trade Decision Service

- Konsumiert Markt-Daten von Kafka
- Wendet Strategien (z. B. MA Cross), Regeln oder Schwellenwerte an
- Publiziert Handelsentscheidungen (`trade-decision-topic`)

### Order Execution Service

- Konsumiert Handelsentscheidungen
- Transformiert Entscheidung in ausführbare Orders
- Sendet Order an die Exchange (via Adapter-Interface) oder simuliert
- Persistiert Executions in eigener PostgreSQL Instanz

---

## 3. Technologie-Stack & Entscheidungen

| Komponente     | Technologie             | Grund der Wahl                                          |
|----------------|-------------------------|---------------------------------------------------------|
| Backend        | Spring Boot (Java 21)   | Stabil, Ökosystem, moderne Sprachfeatures               |
| Messaging      | Kafka                   | Event-Streaming, Replays, Skalierung                    |
| Datenbanken    | PostgreSQL pro Service  | Lose Kopplung, Ownership, Isolation                     |
| Container      | Docker / Docker Compose | Einfaches lokales Multi-Service Setup                   |
| Orchestrierung | Helm + Kubernetes       | Portabilität, Cloud Deployment                          |
| Exchange       | Kraken (aktuell)        | Binance blockiert Region (HTTP 451) – Kraken erreichbar |
| Monorepo       | Maven Multimodule       | Shared `common/`, konsistente Versionierung             |

---

## 4. Schnelles Loslegen (Quick Start)

```bash
# Infrastruktur (Kafka, Postgres)
docker-compose up -d

# (Optional) Gesamt-Build
mvn -T 1C clean package

# Beispiel: Market Data Service starten
cd market-data-service
mvn spring-boot:run
```

Logs prüfen:

```bash
docker compose ps
docker logs <container-name> --tail=100 -f
```

Stoppen:

```bash
docker-compose down
```

---

## 5. Lokale Entwicklung (Details)

### 5.1 Repository klonen

```bash
git clone <repo-url>
cd BitBuddy
```

### 5.2 Infrastruktur starten

```bash
docker-compose up -d
```

### 5.3 Module bauen

```bash
mvn -T 1C clean install
```

### 5.4 Service mit Profil

```bash
cd market-data-service
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 5.6 Kafka Debug

```bash
# Topics
docker exec -it kafka kafka-topics.sh --bootstrap-server kafka:9092 --list
# Nachrichten lesen
docker exec -it kafka kafka-console-consumer.sh --bootstrap-server kafka:9092 --topic market-data-topic --from-beginning --timeout-ms 5000
```

---

## 6. IDE Setup (IntelliJ)

Schritt-für-Schritt Einrichtung für produktives Arbeiten.

1. Projekt öffnen: "Open" und Root-Ordner `BitBuddy` wählen.
   ![Screenshot: Projekt öffnen – TODO](docs/images/placeholder-open-project.png)
2. Maven Sync prüfen (rechts Maven Tool Window → Reload All Maven Projects).
   ![Screenshot: Maven Reload – TODO](docs/images/placeholder-maven-reload.png)
3. JDK 21 setzen: File > Project Structure > SDK = 21.
   ![Screenshot: SDK Auswahl – TODO](docs/images/placeholder-set-jdk.png)
4. Run-Konfiguration anlegen: „Add New Configuration“ → Spring Boot → `market-data-service` Main-Class.
   ![Screenshot: Run Config – TODO](docs/images/placeholder-run-config.png)
5. Aktivieren eines Profils: In Run Config `Active Profiles=dev`.
   ![Screenshot: Profile – TODO](docs/images/placeholder-active-profiles.png)
6. Tests starten: Maven Lifecycle `test` oder über Kontext-Menü.
   ![Screenshot: Tests – TODO](docs/images/placeholder-run-tests.png)
7. Optional: Remote Docker Compose Service Logs über IntelliJ Services Tab.
   ![Screenshot: Services Tab – TODO](docs/images/placeholder-services-tab.png)

> Ersetze alle Platzhalter-Bilder durch echte Screenshots in `docs/images/`.

---

## 7. Austausch der Exchange (Adapter-Konzept)

Interface: `MarketDataStreamingService` mit Implementierungen (`KrakenMarketDataStreamingService`,
`KucoinMarketDataStreamingService`, ...).

Konfigurationsbasierte Auswahl:

```yaml
marketdata:
  provider: krakenMarketDataStreamingService
```

Factory löst Bean dynamisch:

```java
public class MarketDataStreamingServiceFactory {
    @Value("${marketdata.provider}")
    String providerBeanName;
    @Autowired
    ApplicationContext ctx;

    public MarketDataStreamingService resolve() {
        return (MarketDataStreamingService) ctx.getBean(providerBeanName);
    }
}
```

`MarketDataStream` ruft beim Start `factory.resolve()`.

Vorteile: Kein Code-Refactor beim Wechsel – reine Property-Änderung.

---

## 8. Konfiguration

Beispiel `application.yml` Ausschnitt:

```yaml
marketdata:
  provider: krakenMarketDataStreamingService
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

Achte auf korrektes Pair-Format (Exchange-spezifisch).

---

## 9. Tests

```bash
mvn verify
```

Empfehlungen:

- Exchange-Adapter mocken (kein Live-WebSocket im Unit-Test)
- Integrationstests: Testcontainers (Kafka, PostgreSQL) – später einführen
- Verifikation von Strategie-Signalen (siehe Abschnitt Strategie)

Mock-Beispiel:

```java

@Mock
MarketOrderProducer producer;

@BeforeEach
void setUp() {
    service = new OrderDecisionService(props, producer);
}
```

---

## 10. Strategie: Gleitender Durchschnitt (MA5/MA7 Kreuz) ⚙️

Aktuelle Beispiel-Strategie berechnet zwei einfache gleitende Durchschnitte (SMA) – Fenstergrösse 5 und 7. Beim Kreuz (
Short SMA überschreitet Long SMA nach oben) wird ein BUY-Signal erzeugt; beim Unterschreiten ein SELL-Signal.

Formel Einfacher SMA:

```
SMA_n = (Summe der letzten n Preise) / n
```

Logik (Pseudo-Code):

```text
price_t -> Buffer
if Buffer.size >= 7:
    sma5 = avg(last 5)
    sma7 = avg(last 7)
    prevCrossState = (sma5_prev > sma7_prev)
    currentCrossState = (sma5 > sma7)
    if !prevCrossState && currentCrossState:
        emit BUY
    if prevCrossState && !currentCrossState:
        emit SELL
```

Edge Cases:

- Weniger als 7 Preise: kein Signal
- Gleichheit (sma5 == sma7): kein Richtungswechsel
- Hohe Volatilität: optional Debounce (z. B. Mindestabstand zwischen Signalen)

Erweiterungen (später):

- EMA statt SMA
- Filter durch Volumen / Spread
- Bestätigung durch mehrere Kerzen

---

## 11. Deployment: Kubernetes (Minikube)

### 11.1 Start & Helm Deployment

```bash
minikube start
cd helm
helm install bitbuddy . -n bitbuddy-dev --create-namespace -f values.yaml
```

### 11.2 Logs & Status

```bash
kubectl get pods -n bitbuddy-dev
kubectl logs <pod> -n bitbuddy-dev --tail=200
```

### 11.3 Entfernen

```bash
helm uninstall bitbuddy -n bitbuddy-dev
```

### 11.4 Optional: Kafka Debug

```bash
kubectl exec -n bitbuddy-dev -it <kafka-pod> -- kafka-topics.sh --bootstrap-server localhost:9092 --list
```

---

## 12. Deployment: AWS (CloudFormation: Reihenfolge Base → EKS → RDS)

### 12.1 Reihenfolge & Übersicht

1. CloudFormation Stack `base-setup.yaml` (VPC, Subnets, Security Groups, evtl. IAM Grundrollen)
2. CloudFormation Stack `eks.yaml` (EKS Cluster) – im Prozess passende IAM Rollen auswählen und deren ARN kopieren
3. CloudFormation Stack `rds.yaml` (PostgreSQL Datenbank)

### 12.2 Vorbereitung

```bash
aws configure  # Zugangsdaten eintragen
aws sts get-caller-identity
```

### 12.3 EKS kubeconfig aktualisieren

```bash
aws eks update-kubeconfig --region us-east-1 --name bitbuddy-cluster
```

### 12.4 Helm Deployment auf EKS

```bash
kubectl create namespace bitbuddy-prod
helm install bitbuddy helm -n bitbuddy-prod -f helm/values.yaml
```

### 12.5 Prüfung

```bash
kubectl get nodes
kubectl get pods -n bitbuddy-prod
kubectl logs <pod> -n bitbuddy-prod --tail=200
```

### 12.6 Hinweis Binance Blockade

HTTP 451 Antwort → Wechsel auf Kraken bestätigt.

---

## 13. Sicherheit / Secrets

Da es sich um ein Schulungs-/Lehrprojekt handelt, wird kein umfangreiches Secrets Management umgesetzt. Minimale
Massnahmen:

- Keine API Keys im Git Repository (Umgebungsvariablen oder lokale `.env` Dateien)
- Keine sensiblen Zugangsdaten in Klartext in `application.yml`
- Für produktive Szenarien: Einsatz von Kubernetes Secrets, SOPS, Vault, AWS KMS empfohlen.

---

## 14. Entscheidungslog / Trade-offs

| Thema           | Entscheidung       | Alternative     | Grund                                       |
|-----------------|--------------------|-----------------|---------------------------------------------|
| Exchange        | Kraken             | Binance, Kucoin | Region-Blockade Binance / Stabilität Kraken |
| Architektur     | Microservices      | Monolith        | Skalierbarkeit / Verantwortlichkeiten       |
| Messaging       | Kafka              | REST, gRPC      | Event-Replay, Entkopplung                   |
| Persistenz      | Pro Service DB     | Gemeinsame DB   | Isolation, klare Ownership                  |
| Strategie       | SMA Cross (5/7)    | EMA, RSI, MACD  | Einfach, demonstrativ                       |
| Adapter Pattern | Exchange Interface | Harte Kopplung  | Schneller Wechsel                           |
| Monorepo        | Ja                 | Multi-Repo      | Einheitliche Versionierung                  |

---

## 15. Roadmap & Nächste Schritte

- Testcontainers für Kafka/Postgres Integration
- Erweiterte Strategien (EMA, RSI, Volumenfilter)
- Observability (Prometheus, Grafana, Jaeger) – Charts prüfen
- Risk Management (Positionsgrösse, Stop-Loss, Max Drawdown)
- CI/CD (GitHub Actions / ArgoCD Pipeline)
- Performance-Tuning (Batch-Verarbeitung, Async, Backpressure)
- Optionale Multi-Exchange Aggregation (Spread-Analysen)

---

## 16. Haftungsausschluss

Bildungs- und Experimentierprojekt. Kein Anspruch auf Profitabilität. Einsatz auf eigene Verantwortung.

---

## 17. Anhang: Kommandoreferenz

```bash
# Infrastruktur lokal starten
docker-compose up -d
# Build aller Module
mvn -T 1C clean package
# Einzelservice (Market Data)
(cd market-data-service && mvn spring-boot:run)
# Kafka Topics
docker exec -it kafka kafka-topics.sh --bootstrap-server kafka:9092 --list
# Kubernetes (Minikube)
minikube start && helm install bitbuddy helm -n bitbuddy-dev --create-namespace -f helm/values.yaml
# AWS EKS Kubeconfig
open ~/.aws/credentials
aws eks update-kubeconfig --region us-east-1 --name bitbuddy
kubectl create namespace bitbuddy
kubectl config set-context --current --namespace=bitbuddy


kubectl port-forward pod/XXX 5005:5005

 helm repo add eks https://aws.github.io/eks-charts                                                                    ✔  bitbuddy/bitbuddy 󱃾  21:30:22 

helm repo update


helm upgrade --install aws-load-balancer-controller eks/aws-load-balancer-controller \
  -n kube-system \
  --set clusterName=bitbuddy \
  --set region=us-east-1 \
  --set vpcId=vpc-0db7329069f03664d \
  --set serviceAccount.create=true \
  --set serviceAccount.name=aws-load-balancer-controller
```