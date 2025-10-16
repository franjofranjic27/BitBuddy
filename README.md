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
- Helm
- Kubectl
- Minikube

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

## Deploy to Kubernetes (minikube)

### Install

```bash
cd helm
minikube start
helm install bitbuddy . -n bitbuddy-dev -f values.yaml   
```

then you can check logs:

```bash
kubectl get pods -n bitbuddy-dev
kubectl logs <pod-name> -n bitbuddy-dev 
```

### Uninstall

```bash
helm uninstall bitbuddy -n bitbuddy-dev
```

## Deploying to AWS

1. go to https://awsacademy.instructure.com/courses/136791/modules/items/13159531
2. press on "Start Lab"
3. go to CloudFormation
4. Create new Stack
5. Upload template
6. Create Stack
7. Go to EC2
8. Create new instance
9. Go to RDS
10. Create new instance
11. Go to SQS
12. Create new queue
13. Go to SNS

aws cli authentication
open ~/.aws/credentials
copy the access key and secret key
aws eks update-kubeconfig --region us-east-1 --name bitbuddy-cluster

-> Market Data Service
Wir können nicht binance verwenden, da er die anfrage von der us region blockt
/ # curl -I https://api.binance.com/api/v3/exchangeInfo
HTTP/2 451
server: CloudFront
date: Thu, 16 Oct 2025 12:15:07 GMT
content-length: 224
content-type: application/json
x-cache: Error from cloudfront
via: 1.1 3200e279ff99ad1800a0dd3b3c8e2d10.cloudfront.net (CloudFront)
x-amz-cf-pop: IAD61-P2
x-amz-cf-id: tlu0zK6dpGog-pwATI4ppDQMxPrCtj-taYkKYfgbf8kuu4bJzEFK2Q==

so after testing via a websocket image on kuberntes
kubectl run netshoot --rm -it --image=nicolaka/netshoot -- bash

i checked that i can connect to kraken exchange via
websocat wss://ws.kraken.com

so there kraken will be the go to exchange for now