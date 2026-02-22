# Architecture

## Overview

BitBuddy is a Maven monorepo composed of three Java Spring Boot microservices, a React frontend, and shared infrastructure (Kafka, PostgreSQL). Services are independently deployable and communicate exclusively via Kafka topics; the frontend consumes REST APIs exposed by the backend services.

```
┌─────────────────────────────────────────────────────────────────┐
│                        React Frontend                           │
│                    (Vite, port 5173/3000)                       │
│          polls REST on all three backend services               │
└──────────┬────────────────────┬───────────────────┬────────────┘
           │ GET /api/market-data│GET /api/order-    │GET /api/
           │                    │decision           │order-execution
           ▼                    ▼                   ▼
┌──────────────────┐ ┌──────────────────┐ ┌──────────────────────┐
│ Market Data Svc  │ │Order Decision Svc│ │Order Execution Svc   │
│   (port 8080)    │ │   (port 8081)    │ │     (port 8082)      │
│                  │ │                  │ │                      │
│ Streams prices   │ │ MA Cross strategy│ │ Executes trades via  │
│ from exchanges   │ │ BUY/SELL signals │ │ XChange (KuCoin)     │
└────────┬─────────┘ └────────┬─────────┘ └──────────────────────┘
         │ publish             │ consume            ▲
         │ MarketDataDto       │ MarketDataDto       │ consume
         ▼                     │                    │ MarketOrderDto
   ┌─────────────┐             │ publish             │
   │    Kafka    │◄────────────┘                    │
   │             │──────────────────────────────────┘
   │ KRaft mode  │  market-data-topic → trade-decision-topic
   └─────────────┘
         │ (separate schemas)
         ▼
   ┌─────────────┐
   │  PostgreSQL │  market_data | order_decision | order_execution
   └─────────────┘
```

## Modules

| Module | Package root | Description |
|---|---|---|
| `common` | `ch.ost.clde.dto` | Shared DTOs and enums passed over Kafka |
| `market-data-service` | `ch.ost.clde.mds` | Exchange streaming, normalisation, Kafka producer |
| `order-decision-service` | `ch.ost.clde.ods` | Trading strategy, Kafka consumer + producer |
| `order-execution-service` | `ch.ost.clde.oes` | Trade execution, REST API |
| `frontend` | — | React dashboard |

The root `pom.xml` declares all four Java modules; each service can be built independently with `-pl <module> -am`.

## Kafka messaging

| Topic | Producer | Consumer | Payload |
|---|---|---|---|
| `market-data-topic` | market-data-service | order-decision-service | `MarketDataDto` |
| `trade-decision-topic` | order-decision-service | order-execution-service | `MarketOrderDto` |

Serialization uses Spring Kafka's `JsonSerializer` / `JsonDeserializer` with trusted package `ch.ost.clde.dto`.

## Database

A single PostgreSQL instance is shared locally (Docker Compose) and on AWS (RDS). Each service owns a separate schema and manages its own Flyway migrations:

| Service | Schema | Migrations |
|---|---|---|
| market-data-service | `market_data` | Flyway enabled (v1, v2) |
| order-decision-service | `order_decision` | Flyway disabled |
| order-execution-service | `order_execution` | Flyway enabled (v1, v2) |

## Market Data Service

Streams live trade events from a crypto exchange using the **XChange** library (v5.2.2). The active exchange is selected at startup via the `mds.provider` config key — no code changes needed to switch providers:

```yaml
mds:
  provider: krakenMarketDataStreamingService  # or kucoinMarketDataStreamingService
  tradingPairs:
    - BTC/USD
    - ETH/USD
    - ADA/USD
```

The `MarketDataStreamingServiceFactory` resolves the correct Spring bean by name. Each implementation (Kraken, KuCoin, Bitstamp, Binance) normalises exchange-specific events into `MarketDataDto` before publishing to Kafka.

## Order Decision Service

Implements a **Moving Average Cross** strategy:

- Two sliding windows compute SMA-5 and SMA-7 from incoming price ticks.
- `CrossState` tracks whether SMA-5 is `ABOVE` or `BELOW` SMA-7.
- A signal fires **only on a state transition**:
  - `BELOW → ABOVE` → **BUY** (Golden Cross)
  - `ABOVE → BELOW` → **SELL** (Death Cross)
- No signal is emitted until at least 7 ticks are available.
- Position size is currently fixed at 0.01 (marked `FIXME` in `OrderDecisionService`).

Key classes: `MovingAverageCalculator`, `CrossState`, `OrderDecisionService`.

## Order Execution Service

Consumes `MarketOrderDto` from `trade-decision-topic`, transforms orders into the exchange format expected by XChange, and submits them to KuCoin (or simulates execution when hot trading is disabled). Executions are persisted to the `order_execution` schema and exposed via REST.

Live trading is off by default; enable it in `application.yml`:
```yaml
oes:
  hotTrading: true
```

## Frontend

A React 19 + Vite SPA with three tabs: Overview, Market, and Orders. It polls the three backend REST endpoints on load.

API base URLs are configured at container start via `env-config.js` (generated by the Docker entrypoint), injected into `window.ENV`, and consumed by `frontend/src/services/api.ts`. This allows runtime reconfiguration without a rebuild.

Charts use **Recharts**. No state management library — `App.tsx` owns all state.

## Deployment

### Local development

`docker-compose.yml` provides:
- Kafka in KRaft mode (no Zookeeper) on port 9092 (host) / 9093 (Docker network)
- PostgreSQL on port 5432
- Kafka UI on port 8085

### Kubernetes (Minikube or EKS)

The Helm chart at `helm/` uses a `generic-chart` subchart for all four services. `values.yaml` targets the AWS RDS instance; `values-dev.yaml` overrides for local Minikube use. DB credentials are injected via the `bitbuddy-datasource-secret` Kubernetes Secret.

### AWS (EKS)

CloudFormation templates in `infrastructure/` provision the environment in order:
1. `base-setup.yaml` — VPC, subnets, security groups, IAM roles
2. `eks.yaml` — EKS cluster
3. `rds.yaml` — PostgreSQL RDS instance

After provisioning: `aws eks update-kubeconfig --region us-east-1 --name bitbuddy`, then deploy with `helm install bitbuddy helm/`.

## CI/CD

`.github/workflows/build-and-deploy.yml` is triggered manually. It runs a matrix build across the three Java services (Java 21 Temurin, `mvn -pl $service -am clean verify`) and builds + pushes Docker images tagged `latest` and `<short-sha>` to Docker Hub. The frontend is built via a separate job using `docker/build-push-action`.

`.github/workflows/helm-verify.yml` lints and templates the Helm chart on demand.
