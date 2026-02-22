# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**BitBuddy** is a modular cryptocurrency trading bot — a Maven monorepo with 3 Java Spring Boot microservices and 1 React frontend. Services communicate via Apache Kafka, persist to PostgreSQL (schema-per-service), and are deployed via Helm/Kubernetes.

## Commands

### Backend (Maven — run from repo root)

```bash
# Build all modules
mvn -T 1C clean install

# Build a specific service (with dependencies from common/)
mvn -pl market-data-service -am clean verify
mvn -pl order-decision-service -am clean verify
mvn -pl order-execution-service -am clean verify

# Run tests (includes integration tests via Failsafe + TestContainers)
mvn verify

# Run a single service locally
cd market-data-service && mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Frontend (npm — run from `frontend/`)

```bash
npm run dev      # Vite dev server with HMR (port 5173)
npm run build    # Production build
npm run lint     # ESLint
npm run preview  # Preview production build
```

### Local Infrastructure (Docker Compose)

```bash
docker-compose up -d    # Start Kafka (KRaft) + PostgreSQL + Kafka UI
docker-compose down     # Stop all
```

Kafka UI is available at `http://localhost:8085`.

### Helm / Kubernetes

```bash
helm lint helm/
helm template bitbuddy helm/
helm install bitbuddy helm/ -n bitbuddy --create-namespace -f helm/values.yaml -f helm/values-dev.yaml
```

## Architecture

### Data Flow

```
Market Data Service (port 8080)
  → streams prices from crypto exchanges via XChange library (default: Kraken)
  → persists to PostgreSQL schema: market_data
  → publishes MarketDataDto to Kafka topic: market-data-topic

Order Decision Service (port 8081)
  → consumes market-data-topic
  → applies Moving Average Cross strategy (MA5 vs MA7)
  → persists decisions to PostgreSQL schema: order_decision
  → publishes MarketOrderDto to Kafka topic: trade-decision-topic

Order Execution Service (port 8082)
  → consumes trade-decision-topic
  → executes trades via XChange (KuCoin; live trading disabled by default)
  → persists to PostgreSQL schema: order_execution
  → exposes REST API consumed by frontend

React Frontend (port 3000/5173)
  → polls REST endpoints on all 3 services
  → displays market data, decisions, and executions
```

### Module Layout

| Module | Path | Purpose |
|---|---|---|
| `common` | `common/` | Shared DTOs: `MarketDataDto`, `MarketOrderDto`, `OrderType` enum |
| `market-data-service` | `market-data-service/` | Exchange streaming, Kafka producer |
| `order-decision-service` | `order-decision-service/` | MA cross strategy, Kafka consumer+producer |
| `order-execution-service` | `order-execution-service/` | Trade execution, REST API |
| `frontend` | `frontend/` | React 19 + Vite + Recharts dashboard |

### Trading Strategy

Moving Average Cross in `order-decision-service/src/main/java/ch/ost/clde/ods/domain/`:
- `MovingAverageCalculator.java` — SMA over sliding windows (MA5, MA7)
- `CrossState.java` — enum: `ABOVE` (Golden Cross → BUY), `BELOW` (Death Cross → SELL), `NONE`
- Signal fires only on state change; position size fixed at 0.01 (marked `FIXME`)

### Exchange Adapters

Factory pattern in `market-data-service`: implementations of `MarketDataStreamingService` (Kraken, Bitstamp, Binance, KuCoin). Selected by name via `mds.provider` in `application.yml` — no code change required to switch providers.

### Kafka

| Topic | Producer | Consumer | DTO |
|---|---|---|---|
| `market-data-topic` | market-data-service | order-decision-service | `MarketDataDto` |
| `trade-decision-topic` | order-decision-service | order-execution-service | `MarketOrderDto` |

Serialization: Spring Kafka JSON serializer; trusted package `ch.ost.clde.dto`.

### Database

Single PostgreSQL instance in dev (shared via Docker Compose); schema-per-service isolation. Flyway manages migrations for `market-data-service` and `order-execution-service`. Flyway is disabled in `order-decision-service`.

### Frontend API Configuration

Endpoints are configurable at runtime via `window.ENV` (injected by `env-config.js` in Docker entrypoint) or Vite env vars:
- `MARKET_DATA_API_URL`
- `TRADE_DECISIONS_API_URL`
- `ORDER_EXECUTIONS_API_URL`

See `frontend/src/services/api.ts`.

## Key Configuration Files

- `docker-compose.yml` — local Kafka (KRaft, no Zookeeper) + PostgreSQL + Kafka UI
- `*/src/main/resources/application.yml` — per-service Spring config (ports, DB schemas, Kafka topics, CORS)
- `helm/values.yaml` + `helm/values-dev.yaml` — Kubernetes deployment configuration
- `.github/workflows/build-and-deploy.yml` — CI/CD matrix build for all 3 Java services + frontend

## Testing

Integration tests use **TestContainers** (real Postgres + Kafka containers). They live under `src/integration-test/` in each service and run via Maven Failsafe (`mvn verify`). Unit tests use MockMvc and run via Surefire (`mvn test`).
