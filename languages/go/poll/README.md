# Poll Service (Go)

Production-ready, high-throughput poll/voting HTTP service with pluggable storage and first-class load testing.

Simple high-throughput poll/voting HTTP service with pluggable storage (in-memory by default, PostgreSQL when configured) and a k6 load test to ramp up to 10k concurrent virtual users.

Highlights
- High-performance vote ingestion backed by goroutines and a FIFO queue (in-memory or Redis)
- Pluggable storage: in-memory for simplicity or PostgreSQL for persistence
- Clean, versioned API with Swagger UI and OpenAPI spec
- E2E test validates concurrent voting with hundreds of goroutines
- k6 script ramps up to 10k concurrent virtual users and generates an HTML report

Quick links
- API Docs (Swagger UI): http://localhost:8080/swagger
- OpenAPI JSON: http://localhost:8080/swagger.json
- E2E test: internal/api/e2e_test.go
- k6 script: k6/poll_ramp_10k.js

## Table of Contents
- [Quick start](#quick-start)
- [Configuration](#configuration)
- [API overview](#api-overview)
- [Swagger / API docs](#swagger--api-docs)
- [Running the E2E test](#running-the-e2e-test)
- [Load testing with k6](#load-testing-with-k6)
- [Notes and references](#notes-and-references)
- [Examples](#examples)
- [Performance tips](#performance-tips)
- [Contributing](#contributing)
- [License](#license)

## Quick start
1) Run the server (in-memory store with demo data)
   
   ```bash
   go run ./...
   ```

   The server listens on :8080 by default. A demo poll with three options is seeded:
   - poll_id: 11111111-1111-1111-1111-111111111111
   - option_id example: aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa

2) Vote against the seeded poll
   
   ```bash
   curl -s -X POST http://localhost:8080/vote \
     -H 'Content-Type: application/json' \
     -d '{"poll_id":"11111111-1111-1111-1111-111111111111","option_id":"aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa","voter_id":"user-1"}'
   ```

3) Get poll snapshot
   
   ```bash
   curl -s "http://localhost:8080/poll?id=11111111-1111-1111-1111-111111111111" | jq
   ```

## Configuration
- PORT: Port number for the HTTP server (default: 8080).
  
  Example:
  
  ```bash
  PORT=9090 go run ./...
  ```

- STORE_BACKEND: Explicitly select the storage backend. Accepted values: "memory" or "postgres".
  - STORE_BACKEND=memory → force in-memory store (ignores DB_URL) and seed demo data.
  - STORE_BACKEND=postgres → use PostgreSQL; requires DB_URL to be set. If DB_URL is missing or connection fails, the service falls back to memory and logs a warning.
  - Unset/empty → auto mode (default, backward compatible): use PostgreSQL when DB_URL is set; otherwise use in-memory.

- DB_URL: PostgreSQL connection string. Used when STORE_BACKEND=postgres or when STORE_BACKEND is unset and you want auto-detection.
  Example DSN: postgres://user:pass@localhost:5432/poll?sslmode=disable

  On startup, the service auto-creates the required tables and indexes if they do not exist.

- High-throughput tuning (environment variables)
  - VOTE_QUEUE_SIZE: Buffered queue size for incoming votes before workers apply them. Larger buffers absorb traffic spikes.
    Default: 1000000 (1e6). Example: VOTE_QUEUE_SIZE=2000000
  - VOTE_WORKERS: Number of goroutines that apply votes in parallel. If not set, it auto-scales based on CPU.
    Defaults (auto): max(runtime.NumCPU()*32, 128), capped at 4096.
    Example: VOTE_WORKERS=1024

- Queue strategy (FIFO) and Redis optimization
  - The service uses a queue in front of the vote applier workers. Enqueue happens on POST /vote and processing occurs asynchronously, guaranteeing FIFO order per queue.
  - In-memory mode (default): a buffered channel acts as the queue. When the buffer is full, the server responds 503 Service Unavailable.
  - Redis-backed mode (recommended for very high throughput and multi-instance deployments): enable by setting:
    - REDIS_URL: Redis connection URL. Example: redis://localhost:6379/0
    - REDIS_QUEUE_NAME: Redis list key name for the queue (default: votes)
    The server enqueues with RPUSH and workers consume with BLPOP ensuring FIFO semantics.
  - Example Docker Redis for local testing:
    docker run --rm -p 6379:6379 redis:7-alpine

## API overview
- API base paths
  - New organized base: /api/v1
    - Public: /api/v1/public
    - Admin:  /api/v1/admin
  - Legacy (kept for compatibility, deprecated in docs): flat endpoints at root, e.g. /vote, /poll

- Public
  - POST /api/v1/public/vote (legacy: /vote)
    Request JSON: { "poll_id": string, "option_id": string, "voter_id": string }
    Responses:
      202 Accepted on successful enqueue
      404 Not Found if poll/option doesn’t exist
      409 Conflict if poll is closed
      503 Service Unavailable if the queue is full (transient)

    Example:
      
      ```bash
      curl -i -X POST http://localhost:8080/vote \
        -H 'Content-Type: application/json' \
        -d '{"poll_id":"<poll-id>","option_id":"<option-id>","voter_id":"<unique-user>"}'
      ```

  - GET /api/v1/public/option_item?id=<option-id> (legacy: /option_item)
    Response JSON: { "id": string, "label": string, "votes": number }
    Status codes: 200, 404

  - GET /api/v1/public/poll?id=<poll-id> (legacy: /poll)
    Response JSON:
      {
        "id": string,
        "question": string,
        "is_open": bool,
        "options": [{"id": string, "label": string, "votes": number}, ...],
        "voters": [string, ...]
      }
    Status codes: 200, 404

- Admin (CRUD)
  - POST /api/v1/admin/polls (legacy: /polls)
    Create a poll.
    Body: { "id": string, "question": string, "is_open": bool (optional, default true) }
    Status: 201 Created | 409 Conflict (already exists) | 400/404 on errors

    ```bash
    curl -i -X POST http://localhost:8080/polls \
      -H 'Content-Type: application/json' \
      -d '{"id":"poll-1","question":"Your favorite?","is_open":true}'
    ```

  - PUT /api/v1/admin/polls (legacy: /polls)
    Update poll question and open/closed status.
    Body: { "id": string, "question": string, "is_open": bool }
    Status: 204 No Content | 404 Not Found

  - DELETE /api/v1/admin/polls?id=<poll-id> (legacy: /polls)
    Delete a poll (cascades to options and voters in PostgreSQL mode).
    Status: 204 No Content | 404 Not Found

  - GET /api/v1/admin/polls?id=<poll-id> (legacy: /polls)
    Fetch a poll when id is provided; when id is omitted, returns a list of polls.
    Response JSON when listing: [PollResponse, ...]
    Status codes: 200, 404 (for single fetch when not found)

  - POST /api/v1/admin/options (legacy: /options)
    Add an option to a poll.
    Body: { "id": string, "poll_id": string, "label": string }
    Status: 201 Created | 404 Not Found (poll) | 409 Conflict (already exists)

  - PUT /api/v1/admin/options (legacy: /options)
    Update an option label.
    Body: { "id": string, "label": string }
    Status: 204 No Content | 404 Not Found

  - DELETE /api/v1/admin/options?id=<option-id> (legacy: /options)
    Remove an option.
    Status: 204 No Content | 404 Not Found

  - GET /api/v1/admin/options?id=<option-id>&poll_id=<poll-id> (legacy: /options)
    Fetch an option when id is provided.
    When id is omitted, returns a list of options; you may filter by poll_id to list options of a specific poll.
    Response JSON when listing: [OptionItem, ...]
    Status codes: 200, 404 (for single fetch when not found)

  - POST /api/v1/admin/voters (legacy: /voters)
    Pre-authorize a voter for a poll (optional; votes also accept any voter_id unless your workflow requires pre-adding).
    Body: { "poll_id": string, "voter_id": string }
    Status: 201 Created | 404 Not Found (poll) | 409 Conflict (already exists)

  - DELETE /api/v1/admin/voters?poll_id=<poll-id>&voter_id=<voter-id> (legacy: /voters)
    Remove a voter from a poll.
    Status: 204 No Content | 404 Not Found

  - GET /api/v1/admin/voters?poll_id=<poll-id> (legacy: /voters)
    List voters registered/recorded for a poll.
    Response JSON: { "poll_id": string, "voters": [string, ...] }
    Status codes: 200, 404

## Swagger / API docs
- OpenAPI spec (JSON): http://localhost:8080/swagger.json
- Interactive UI: http://localhost:8080/swagger
  The UI uses a CDN-hosted Swagger UI and points to /swagger.json. The spec documents the organized versioned routes under /api/v1 and marks legacy flat routes as deprecated.

## Running the E2E test
The repository includes an end-to-end (E2E) test that spins up the HTTP server in-memory, creates a poll, registers voters, fires hundreds of concurrent vote requests using goroutines, and verifies the results via the public endpoints.

Where it lives
- internal/api/e2e_test.go

How to run
- Run the whole test suite:
  
  ```bash
  go test ./...
  ```

- Run only the E2E test:
  
  ```bash
  go test -run TestE2E_ConcurrentVotes ./internal/api
  ```

- Optional: enable the race detector:
  
  ```bash
  go test -race -run TestE2E_ConcurrentVotes ./internal/api
  ```

Notes
- The test forces STORE_BACKEND=memory for deterministic behavior; no external services or databases are required.
- It uses httptest.Server and an isolated DefaultServeMux, so it does not bind to a real TCP port.
- You can tune throughput-related env vars (VOTE_QUEUE_SIZE, VOTE_WORKERS) if desired, but defaults work.

## Load testing with k6
Prerequisites
- Install k6: https://k6.io/docs/get-started/installation/

Script location
- k6/poll_ramp_10k.js

This script ramps up to 10,000 concurrent virtual users (VUs) sending POST /vote requests. It supports optional setup to create a poll and option before the test.

Environment variables
- BASE_URL: Target base URL (default: http://localhost:8080)
- POLL_ID: Poll ID to vote on (default matches demo seed)
- OPTION_ID: Option ID to vote for (default matches demo seed)
- USE_SETUP: If set to 1 (or any non-empty, non-"0" value), setup() will create a poll and option and use those IDs
- SLEEP_MS: Optional per-iteration sleep to throttle VUs (default 0 for max RPS). Example: SLEEP_MS=0
- TARGET_VUS: Peak concurrent virtual users for the ramping scenario (default: 10000). Increasing this too high on a single machine can cause client-side ephemeral port exhaustion leading to errors like "can't assign requested address". Example: TARGET_VUS=5000

Run examples
- Using seeded demo data:
  
  ```bash
  k6 run k6/poll_ramp_10k.js
  ```

- Let setup create poll and option (idempotent):
  
  ```bash
  USE_SETUP=1 k6 run k6/poll_ramp_10k.js
  ```

- Against a remote service with explicit IDs:
  
  ```bash
  BASE_URL=https://your.host POLL_ID=<poll-id> OPTION_ID=<option-id> k6 run k6/poll_ramp_10k.js
  ```

- Adjusting peak VUs (to avoid local port exhaustion on small machines):
  
  ```bash
  TARGET_VUS=5000 k6 run k6/poll_ramp_10k.js
  ```

### HTML report for k6
- The k6 script is configured to generate a self-contained HTML report after each run using the benc-uk/k6-reporter.
- After the test finishes, two files are created in the current directory:
  - k6-report.html (interactive HTML report)
  - k6-summary.json (raw JSON summary)

How to produce and view the report
1) Run the test (examples):
   
   ```bash
   k6 run k6/poll_ramp_10k.js
   BASE_URL=http://localhost:8080 USE_SETUP=1 k6 run k6/poll_ramp_10k.js
   ```
2) Open the generated report:
   
   ```bash
   # macOS
   open k6-report.html
   # Linux
   xdg-open k6-report.html
   # Windows (PowerShell)
   start k6-report.html
   ```

Notes
- The reporter is imported directly by the script from GitHub (ES module URL). Ensure your environment has internet access during the run, or vendor the file if you need offline execution.
- Import source: https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js (using main branch to avoid 404s on non-existent tags).
- To pin a specific version or run offline, vendor the bundle:
  
  ```bash
  curl -L -o k6/k6-reporter-bundle.js https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js
  # then edit k6/poll_ramp_10k.js
  # import { htmlReport } from './k6-reporter-bundle.js';
  ```

## Notes and references
- Database recommendations and schema notes: see DATABASE.md
- PostgreSQL driver: github.com/lib/pq (auto-migration occurs at startup when DB_URL is set)
- Server source: internal/api/server.go, internal/store, internal/processor
- Redis client: github.com/redis/go-redis/v9 (enabled when REDIS_URL is set)

## Examples
- Force memory store (ignores DB_URL):
  
  ```bash
  STORE_BACKEND=memory go run ./...
  ```

- Force PostgreSQL store:
  
  ```bash
  STORE_BACKEND=postgres DB_URL=postgres://postgres@localhost:5432/poll?sslmode=disable go run ./...
  ```

- Auto mode (use Postgres when DB_URL provided, else memory):
  
  ```bash
  DB_URL=postgres://postgres@localhost:5432/poll?sslmode=disable go run ./...
  ```

## Performance tips
- Server configuration
  - Use the in-memory store for maximum throughput (omit DB_URL) or ensure your DB can handle the write rate.
  - Tune VOTE_QUEUE_SIZE and VOTE_WORKERS via env to match your CPU and expected burst capacity.
  - Prefer enabling Redis queue (set REDIS_URL) for multi-process scaling and backpressure smoothing across instances.
  - Run with GOMAXPROCS set appropriately (defaults to number of CPUs). Example: GOMAXPROCS=$(nproc)

- OS/network tuning (examples; adjust for your OS)
  - Increase file descriptor limits (both soft and hard):
    
    ```bash
    ulimit -n 1048576
    ```
  - Linux sysctls (requires root and persistence via /etc/sysctl.d):
    
    ```bash
    cat <<'SYSCTL' | sudo tee /etc/sysctl.d/99-poll-service.conf
    net.core.somaxconn=65535
    net.ipv4.ip_local_port_range=1024 65535
    net.ipv4.tcp_tw_reuse=1
    net.core.netdev_max_backlog=250000
    net.ipv4.tcp_max_syn_backlog=262144
    net.ipv4.tcp_rmem=4096 87380 33554432
    net.ipv4.tcp_wmem=4096 65536 33554432
    SYSCTL
    sudo sysctl --system
    ```
  - macOS:
    
    ```bash
    sudo sysctl -w kern.ipc.somaxconn=65535
    ```

## Contributing

Contributions are welcome! Please open an issue to discuss your idea or submit a PR with a clear description, tests when applicable, and minimal changes scoped to the feature or fix.

## License

This project is provided as-is for learning and experimentation. Verify license terms in your usage context before deploying to production.

- Client/load generator tips
  - Prefer keep-alive connections and adequate connection pools to avoid ephemeral port exhaustion (errors like "can't assign requested address").
  - Consider running multiple k6 instances or distributed load generators to reach 250k RPS.
  - Use SLEEP_MS=0 in the k6 script for maximum throughput; increase if you want to throttle.
