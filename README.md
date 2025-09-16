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

## ▶️ Getting Started

### Prerequisites
- [Docker](https://docs.docker.com/get-docker/) & Docker Compose
- JDK 21
- Maven 3.9+

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

