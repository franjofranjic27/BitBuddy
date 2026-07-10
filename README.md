# BitBuddy

[![CI](https://img.shields.io/github/actions/workflow/status/franjofranjic27/BitBuddy/ci.yml?branch=main&style=for-the-badge&label=CI)](https://github.com/franjofranjic27/BitBuddy/actions/workflows/ci.yml)
[![Quality Gate](https://img.shields.io/sonar/quality_gate/franjofranjic27_BitBuddy?server=https%3A%2F%2Fsonarcloud.io&style=for-the-badge)](https://sonarcloud.io/summary/overall?id=franjofranjic27_BitBuddy)
[![Coverage](https://img.shields.io/sonar/coverage/franjofranjic27_BitBuddy?server=https%3A%2F%2Fsonarcloud.io&style=for-the-badge)](https://sonarcloud.io/summary/overall?id=franjofranjic27_BitBuddy)
[![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge)](#tech-stack)
[![MIT License](https://img.shields.io/badge/License-MIT-blue.svg?style=for-the-badge)](LICENSE)

BitBuddy 🌕 is an experimental crypto trading bot: a Spring Boot microservice
monorepo that streams market data from exchanges (Kraken, KuCoin), applies
trading strategies (e.g. MA cross) and executes or simulates orders — services
communicate via Kafka, each owning its own PostgreSQL database.

## Project Status

Educational/experimental project — no claim to profitability, use at your own
risk. Deployable to Kubernetes (Minikube or AWS EKS) via the included Helm
chart; images are pushed to Docker Hub by the release workflow.

## Services

| Service | Role |
|---|---|
| **market-data-service** | Streams trades/prices from exchanges (Kraken, KuCoin), normalizes and persists them, publishes events to Kafka (`market-data-topic`) |
| **order-decision-service** | Consumes market data, applies trading strategies (e.g. MA cross) and publishes decisions (`trade-decision-topic`) |
| **order-execution-service** | Consumes decisions, transforms them into executable orders, sends them to the exchange or simulates execution, persists executions |
| **common** | Shared library |
| **frontend** | React + Vite dashboard |

## Quick Start

**Prerequisites:** Java 21, Maven, Docker with Compose

```bash
# 1. Start infrastructure (Kafka, PostgreSQL)
docker-compose up -d

# 2. Build all modules
mvn -T 1C clean install

# 3. Start a service (e.g. market data)
cd market-data-service
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

**Kafka debug:**

```bash
docker exec -it kafka kafka-topics.sh --bootstrap-server kafka:9092 --list
docker exec -it kafka kafka-console-consumer.sh --bootstrap-server kafka:9092 \
  --topic market-data-topic --from-beginning --timeout-ms 5000
```

## Configuration

Exchange adapters implement `MarketDataStreamingService` (Kraken, KuCoin);
switching exchanges is pure configuration — the factory resolves the bean
dynamically:

```yaml
marketdata:
  provider: krakenMarketDataStreamingService  # or kucoinMarketDataStreamingService
  tradingPairs:
    - BTC/USD
    - ETH/USD
```

## Trading Strategy: MA Cross (MA5/MA7)

Two simple moving averages with window sizes 5 and 7
(`SMA_n = (sum of last n prices) / n`):

- **BUY** — short SMA crosses above the long SMA
- **SELL** — short SMA crosses below the long SMA

Edge cases: fewer than 7 prices → no signal; equal SMAs → no direction change;
optional debounce under high volatility.

## Deployment

### Kubernetes (Minikube)

```bash
minikube start
cd helm
helm install bitbuddy . -n bitbuddy --create-namespace -f values.yaml -f values-dev.yaml
kubectl get pods -n bitbuddy
```

### AWS (EKS)

CloudFormation order: `base-setup.yaml` (VPC, subnets, IAM) → `eks.yaml` →
`rds.yaml` (PostgreSQL), then:

```bash
aws eks update-kubeconfig --region us-east-1 --name bitbuddy
kubectl create namespace bitbuddy
kubectl config set-context --current --namespace=bitbuddy
helm install bitbuddy helm

# Nginx ingress controller (LoadBalancer)
helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
helm install ingress-nginx ingress-nginx/ingress-nginx \
  --set controller.service.type=LoadBalancer \
  --namespace ingress-nginx --create-namespace
```

## Tech Stack

| Technology | Version | Purpose |
|---|---|---|
| Java | 21 | Language |
| Spring Boot | 3.5 | Service framework |
| Apache Kafka | — | Inter-service messaging |
| PostgreSQL | — | Per-service persistence |
| React + Vite | 19 / 7 | Frontend dashboard |
| Helm / Kubernetes | — | Deployment (Minikube, AWS EKS) |
| Testcontainers | — | Integration tests (Kafka, PostgreSQL) |

## Security & Secrets

Educational project — no full secrets management. Minimum measures: no API
keys in the repository (environment variables or local `.env` files), no
plaintext credentials in `application.yml`. For production scenarios use
Kubernetes Secrets, SOPS, Vault or AWS KMS.

## Documentation

| Document | Description |
|---|---|
| [Docs site](https://franjofranjic27.github.io/BitBuddy/) | Rendered documentation (GitHub Pages) |
| [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) | System design, module structure, tech stack |
| [docs/COMMIT_CONVENTION.md](docs/COMMIT_CONVENTION.md) | Commit message format and rules |
| [docs/TESTING.md](docs/TESTING.md) | How to run and write tests |
| [docs/WORKFLOWS.md](docs/WORKFLOWS.md) | GitHub Actions CI/CD workflows |
| [CONTRIBUTING.md](CONTRIBUTING.md) | How to contribute to this project |

Repo-wide conventions (README/badge standard, PR and issue templates) live in
[franjofranjic27/.github](https://github.com/franjofranjic27/.github).

## Disclaimer

Educational and experimental project. No claim to profitability. Use at your
own risk.

## License

[MIT](LICENSE)
