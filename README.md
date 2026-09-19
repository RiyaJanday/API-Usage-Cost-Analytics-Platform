# API Usage & Cost Analytics Platform

A multi-provider API observability and cost analytics MVP for tracking request volume, latency, errors, and spend across providers such as OpenAI, Anthropic, Gemini, Stripe, Twilio, AWS, and internal APIs.

## What is included

- Spring Boot REST API with JPA entities for organizations, users, providers, APIs, endpoints, pricing plans, and alerts.
- JDBC batch ingestion for high-volume `api_usage` writes.
- SQL-driven analytics for summary metrics, provider cost breakdowns, endpoint costs, daily trends, error rates, and latency.
- React operational dashboard with Dashboard, Providers, Usage, Analytics, Pricing, and Alerts views.
- H2 in-memory database for first-run development, with Postgres driver included for migration.
- Seeded demo dataset so the dashboard is useful immediately.

## Run locally

Backend:

```bash
cd backend
mvn spring-boot:run
```

Frontend:

```bash
cd frontend
npm install
npm run dev
```

Open `http://localhost:5173`.

On Windows, this workspace path contains `&`, which can confuse npm script execution. If `npm run dev`
or `npm run build` fails with a path error, use Vite directly:

```bash
node .\node_modules\vite\bin\vite.js --host 0.0.0.0
node .\node_modules\vite\bin\vite.js build
```

## API surface

- `GET /api/dashboard`
- `GET /api/providers`
- `GET /api/pricing`
- `GET /api/alerts`
- `GET /api/usage?limit=50`
- `POST /api/usage`
- `GET /api/analytics/summary`
- `GET /api/analytics/cost`
- `GET /api/analytics/requests`
- `GET /api/analytics/errors`
- `GET /api/analytics/latency`

## Ingestion example

```json
[
  {
    "apiId": 1,
    "endpointId": 1,
    "userId": 1,
    "provider": "OpenAI",
    "model": "gpt-5",
    "endpoint": "/v1/chat/completions",
    "statusCode": 200,
    "latencyMs": 842,
    "inputUnits": 1240,
    "outputUnits": 530,
    "timestamp": "2026-09-13T05:30:00Z"
  }
]
```

The backend calculates cost from the pricing table, then inserts the events using JDBC batch writes.

## Postgres profile notes

The default configuration uses H2 for convenience. To move to Postgres, change `spring.datasource.url`, `username`, `password`, and `driver-class-name` in `backend/src/main/resources/application.yml`, then set `spring.jpa.hibernate.ddl-auto` to `validate` or use migrations.

## Next build steps

- Add JWT authentication and Spring Security RBAC.
- Add Redis-backed dashboard caching and rate limiting.
- Add CSV export and scheduled alert evaluation.
- Replace seeded data with Flyway migrations plus test fixtures.
