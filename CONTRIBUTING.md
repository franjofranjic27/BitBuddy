# Contributing

## Prerequisites

| Tool | Version |
|---|---|
| Java (Temurin) | 21 |
| Maven | 3.9+ |
| Node.js | 20 |
| Docker | any recent |
| Helm | 3.17+ |

## First-time setup

```bash
git clone https://github.com/franjofranjic27/BitBuddy.git
cd BitBuddy

# Activate the commit-msg hook (enforces commit convention)
git config core.hooksPath .githooks

# Start local infrastructure (Kafka + PostgreSQL)
docker-compose up -d

# Build all Maven modules
mvn -T 1C clean install

# Install frontend dependencies
cd frontend && npm install
```

## Commit convention

Follow the format defined in [docs/COMMIT_CONVENTION.md](docs/COMMIT_CONVENTION.md).

Use these scopes for BitBuddy:

| Scope | Path |
|---|---|
| `mds` | `market-data-service/` |
| `ods` | `order-decision-service/` |
| `oes` | `order-execution-service/` |
| `common` | `common/` |
| `frontend` | `frontend/` |
| `helm` | `helm/` |
| `infra` | `docker-compose.yml`, `docker/`, `infrastructure/` |
| `ci` | `.github/workflows/` |
| `docs` | `docs/` |

Omit the scope for cross-cutting changes (e.g. root `pom.xml` updates).

## Branching and PRs

- Branch off `main` for all changes.
- Open a PR against `main`; at least one review is required (see `CODEOWNERS`).
- The CI workflow runs on pull requests and must pass before merging.

## CI/CD

The `build-and-deploy` workflow is triggered manually (`workflow_dispatch`). It requires these repository secrets:

| Secret | Purpose |
|---|---|
| `DOCKERHUB_USERNAME` | Docker Hub login |
| `DOCKERHUB_TOKEN` | Docker Hub push token |

The workflow builds and pushes Docker images tagged `latest` and `<short-sha>` for each Java service and the frontend.

## Running tests

```bash
# All tests (unit + integration via TestContainers — requires Docker)
mvn verify

# Unit tests only
mvn test

# Single service
mvn -pl order-decision-service -am verify
```

Integration tests (`*IT.java`) use TestContainers and spin up real Postgres and Kafka containers automatically — no manual setup required beyond having Docker running.
