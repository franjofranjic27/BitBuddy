# BitBuddy - Your Crypto Trading Bot

This project is a **crypto trading Bot** built with **Spring Boot (Java 21)**.  
It follows a microservice architecture and is managed as a **monorepo**.  
Each service has its own database and responsibilities.

## 🚀 Services

### 1. Market Data Service

- Fetches market data (e.g., ticker prices) from external APIs
- Stores market data in Postgres
- Publishes data to Kafka (`market-data-topic`)

### 2. Trade Decision Service

- Consumes market data from Kafka
- Applies decision logic (strategies, thresholds, rules)
- Produces trading decisions (e.g., BUY/SELL signals)

### 3. Order Execution Service

- Consumes trading decisions
- Sends executable orders to an exchange (simulated or real)
- Persists executed orders into its own Postgres DB

---

## Prerequisites

- [Docker](https://docs.docker.com/get-docker/) & Docker Compose
- JDK 21
- Maven 3.9+

## ▶️ Getting Started

### Run Infrastructure (DBs + Kafka + RabbitMQ)

```bash
docker-compose up -d
```

### Run a Service locally

```bash
cd market-data-service
mvn spring-boot:run
```

### Alternatively, run all services with Docker:

```bash
docker-compose up --build
```

```bash
helm install market-data-service . -n bitbuddy-dev -f values.yaml   
```

```bash
helm uninstall market-data-service -n bitbuddy-dev
```

## Working with AWS

1. go to https://awsacademy.instructure.com/courses/136791/modules/items/13159531
2. press on "Start Lab"
3. under "AWS Details" you get the credentials you need to paste into your aws credentials file
3. make sure you have aws cli installed
4. on linux sudo nano ...
5. on mac ... to paste your credentials
