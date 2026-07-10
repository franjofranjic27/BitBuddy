# AGENTS.md

Style and workflow rules for coding agents working in this repository.
Project overview and commands: see [CLAUDE.md](CLAUDE.md); detailed Java style
rules: [.github/instructions/java.instructions.md](.github/instructions/java.instructions.md).

## Java (all `*-service` modules, `common`)

- Java 21, Spring Boot 3.5, multi-module Maven — build from the root
  (`mvn -T 1C clean install`), never introduce inter-service compile
  dependencies (services communicate via Kafka only).
- Follow `.github/instructions/java.instructions.md`: explicit types over
  `var`, `final` parameters/locals, immutability, early returns, no magic
  numbers, comments only for regex/cron/TODO/given-when-then.
- Tests: JUnit 5 + Mockito; integration tests with Testcontainers (Kafka,
  PostgreSQL); mock exchange adapters — never open live WebSockets in tests.

## TypeScript (frontend)

- Strict TypeScript, React 19 + Vite; ESLint config in `frontend/`.

## Domain rules

- Trading strategies are pure functions over price windows — keep them
  deterministic and unit-tested (signal verification).
- Exchange adapters implement `MarketDataStreamingService`; adding an exchange
  means a new implementation + configuration, no factory changes.
- Never commit API keys; exchange credentials come from environment variables.

## Pull Requests

- Repo-wide standards and templates: https://github.com/franjofranjic27/.github (`REPO_STANDARDS.md`).
- Use the matching PR template from that repo (`gh pr create --body-file`):
  `dependency-update.md` for dependency updates, `sonar-fix.md` for Sonar fixes,
  `PULL_REQUEST_TEMPLATE.md` otherwise.
