# AuroraPress — Scala 3 Final Project (1-Week Challenge)

**Domain:** Static site generator + dev server for blogs/knowledge bases (non-financial).  
**Goal:** Deliver a single **native executable** built with **GraalVM Native Image** that supports `init`, `build`, `serve`, and an extensible plugin system, using the Scala 3 features covered in your 30-day plan.

---

## Overview

You will build **AuroraPress**, a static site generator and development server that:
- converts Markdown content (with front matter) into HTML,
- renders pages through templates,
- builds an in-browser searchable index,
- supports a **plugin API**,
- serves the generated site locally with **live reload**,
- ships as a **single native binary** compiled via **GraalVM Native Image** (no sbt required).

> **Timebox:** 1 week (7 days).  
> **Out of scope:** Anything related to finance or financial markets.

---

## Deliverables

1. **Native executable**: `./aurorapress` produced with **GraalVM Native Image** (via Scala CLI or direct `native-image`).
2. **CLI** runner usage documented (`init`, `new post`, `build`, `serve`).
3. **Project source** with clear modules (`core`, `cli`, `server`, `plugins`, `examples`).
4. **Example site** in `examples/blog-minimal` to verify the flow.
5. **This README** with setup, usage and architecture notes.
6. **Automated tests** (unit/integration + golden tests).
7. **Build report** artifact (`public/build-report.json`).

---

## Suggested 7-Day Plan (optional)

- **Day 1:** CLI skeleton (`init`, `new post`, config loader), data model & slugs (opaque types).
- **Day 2:** Markdown parser + front matter validation + template engine sketch.
- **Day 3:** Build pipeline (discovery → normalize → render → write), incremental cache.
- **Day 4:** Dev server (`serve`, `--watch`, SSE/WebSocket), live reload & health/stats routes.
- **Day 5:** Plugin system (Shortcodes, Sitemap, SearchIndex), search-index.json.
- **Day 6:** Errors/diagnostics, concurrency tuning, tests (unit/property/golden).
- **Day 7:** Observability (macros), **GraalVM native build**, docs & polish.

---

## Functional Requirements

### CLI & Project Structure
- Commands:
    - `aurorapress init`: scaffolds `content/`, `templates/`, `assets/`, `public/`, `config.json`.
    - `aurorapress new post "Title"`: creates a draft Markdown with front matter.
    - `aurorapress build [--config path] [--out dir] [--drafts] [--clean] [--concurrency N]`
    - `aurorapress serve [--port 8080] [--watch] [--drafts]`
- Default layout:  
  `content/`, `templates/`, `assets/`, `public/`, `config.json`
- Clear usage errors and non-zero exit codes on failures.

### Content Parsing & Front Matter
- Input: `.md` files with **front matter** (JSON or YAML): `title`, `date` (ISO), `tags`, `author`, `draft` (bool), `description`, optional `template`/`layout`.
- Validations: required fields, date parsing, descriptive diagnostics with filename/line references.
- Slug generation from title via **extension method**.
- Normalize to a strongly-typed `Meta`.

### Rendering & Templates
- Type class `Render[A]` → `Html`/`String`. Instances for `Post`, `Page`, `IndexPage`, `TagPage`.
- Layout resolution: per front matter or default.
- Partials/helpers for reusable blocks.
- **Shortcodes** like `{{ gallery: path=... }}` expanded through plugins.

### Build Pipeline (Lazy & Incremental)
- Scan `content/` and `assets/` via streaming (`Iterator`/`LazyList`).
- Steps: discover → normalize → render → write → index.
- Incremental: avoid reprocessing if `mtime`/hash unchanged; cache under `.aurorapress/cache.json`.
- Assets: copy static, allow simple generation (e.g., CSS minification).
- Produce timing & count stats.

### Dev Server
- Serve `public/` with routing for `/`, post pages, tag pages, 404.
- `--drafts` flag to preview drafts.
- Live reload via SSE or WebSocket when rebuild happens.
- `/__health` and `/__stats` (timings, counts, approximate memory).
- Graceful shutdown on SIGINT/SIGTERM.

### Search (Local, Client-Side)
- Generate `public/search-index.json` with `title`, `slug`, `summary`, `tags`, `contentSnippet`.
- Tokenization/normalization with basic stopword removal.
- `/search` template that queries the index via simple JS.
- Accessibility basics (ARIA attributes).

---

## Technical Requirements

### Scala 3 Language Features (apply purposefully)
- ADTs with `enum` and `sealed`.
- **Opaque types**: `Slug`, `UrlPath`, `NonEmptyString`, etc.
- **Union** (`A | B`) and **intersection** (`A & B`) types where appropriate.
- **Match types** for compile-time transformations (e.g., output extension).
- **Type classes** using `given/using`, `summon`, and (optional) **derives**.
- **Extension methods** and **given Conversion** (use sparingly).
- **Context functions** (`?=>`) to pass `BuildContext` implicitly.
- **inline** utilities with `scala.compiletime` helpers.
- **Macros** (quotes/splices): debug/logging and literal-validation helpers.

### Concurrency & Performance
- Parallel build steps with `Future`s; control pool via `--concurrency`.
- Implement/Use combinators (`sequence/traverse`); `Promise` for live-reload signals.
- Benchmark simple scenario (e.g., 1000 pages) and record timings.
- Prefer streaming (`View`/`LazyList`) to control memory.

### Error Handling & Diagnostics
- Use `Either[Diagnostic, A]` for recoverable errors; convert from `Try`/`Option` as needed.
- Aggregate per-file diagnostics and write a summary to `public/build-report.json`.
- Continue build on non-fatal errors and present a final status (non-zero if errors).

### Configuration
- `config.json`: `siteName`, `baseUrl`, `theme`, `plugins`, `nav`, `dev.port`, `perf.concurrency`.
- `--profile=dev|prod` toggling minification/sourcemaps/etc.
- Validate required keys & types; inline feature flags where helpful.
- Environment override: `AURORAPRESS_CONFIG` (path to config).

### Plugin System
- Define a minimal plugin API, e.g.:
  ```scala
  trait Plugin:
    def name: String
    def init(using BuildContext): Unit = ()
    def transform(doc: Document)(using BuildContext): Either[Diagnostic, Document]
  ```
- `BuildContext` provided as a **context function**.
- Registration via `config.json` and/or `plugins.scl` (Scala snippets).
- **Required example plugins**:
    - `ShortcodesPlugin` – expands `{{ ... }}` into HTML.
    - `SitemapPlugin` – writes `sitemap.xml` (combine routes with `Semigroup/Monoid`).
    - `SearchIndexPlugin` – writes `public/search-index.json`.

### Non-Functional
- Immutability by default; justify any `var`.
- Consistent style: `scalafmt`, helpful compiler flags for dev/ci.
- Clean module boundaries (`core`, `cli`, `server`, `plugins`, `examples`).
- Clear logging with levels (`Debug`, `Info`, `Warn`, `Error`).

---

## Building & Packaging with **GraalVM Native Image** (no sbt)

### Prerequisites
- **GraalVM JDK** (prefer LTS, e.g., 21).
- Install Native Image:
  ```bash
  gu install native-image
  ```
- **Scala CLI** (recommended for this project).

### Option A — Scala CLI one-step native build (recommended)
Produce a self-contained native binary directly with Scala CLI (GraalVM must be the active JDK):
```bash
# run from the project root
scala-cli package .   --native   -o aurorapress   --force
```
Then run:
```bash
./aurorapress init
./aurorapress new post "Hello, Aurora"
./aurorapress build --drafts --concurrency 4
./aurorapress serve --watch --port 8080
```

### Option B — Using `native-image` explicitly
If you prefer to control `native-image` flags yourself:

1) First, produce class files or a (thin) assembly using Scala CLI (still no sbt):
```bash
scala-cli package . -o target/aurorapress.jar --assembly --force
```
2) Then call `native-image`:
```bash
native-image   --no-fallback   --initialize-at-build-time   -H:Name=aurorapress   -cp target/aurorapress.jar
```

> **Notes on reflection & resources:** if your code or libraries use reflection (e.g., some JSON/YAML parsers, service loaders), provide configuration under `graal/` and pass:  
> `-H:ConfigurationFileDirectories=graal/`  
> Also include resource/config files as needed:  
> `-H:IncludeResources='.*(\.html|\.txt|\.json)$'`

### Common flags you may consider
- `--static` (if you need a fully static binary; platform-dependent).
- `--enable-url-protocols=http,https` (if templates/plugins fetch remote assets).
- `-H:+ReportExceptionStackTraces` during development.

---

## Detailed Architecture (Guidance)

- **Data Model (ADTs & Opaque)**:  
  `Content = Post(meta, body) | Page(meta, body) | Draft(meta, body)`  
  `Asset = Static(path) | Generated(path, rule)`  
  `Diagnostic = Info(msg) | Warn(msg) | Error(code, msg, cause?)`  
  Opaque: `Slug`, `UrlPath`, `NonEmptyString`, etc. Constructors validate invariants.

- **Template Engine**:  
  Minimal engine or adapter to a tiny templating DSL; type class `Render[A]` for page types; partials (recent posts, tag cloud).

- **Build Graph**:  
  Streaming discovery, hashing/mtime checks, deterministic output to `public/`, cache file for incremental rebuilds.

- **Server**:  
  http4s **or** sttp for HTTP; SSE/WebSocket endpoint; watch FS changes and trigger rebuild + notify clients.

- **Observability**:  
  Macros:
    - `dbg(expr)` prints expression and value in dev.
    - `constAsset("literal")` proves literal at compile time; runtime verifies existence.
    - `inline logTime[A](stage)(block)` captures timings into `BuildStats` and `/__stats`.

---

## Acceptance Criteria (Definition of Done)

- **CLI Works End-to-End**
    - `init` creates a ready-to-use skeleton.
    - `new post "Title"` creates a draft with valid front matter and slug.
    - `build` turns content into a navigable site in `public/`, including index, post and tag pages.
    - `serve --watch` rebuilds on change and triggers live reload in the browser.
- **Plugins**
    - At least 3 plugins implemented and executed during build: Shortcodes, Sitemap, SearchIndex.
    - Plugins can be enabled/disabled via `config.json`.
- **Search**
    - `public/search-index.json` is generated and `/search` page can query it client-side.
- **Diagnostics & Reports**
    - Per-file warnings/errors shown in console and aggregated into `public/build-report.json`.
    - Non-zero exit code when build has **errors**; warnings do **not** fail the build.
- **Observability**
    - Macro `dbg` available in dev; can be silenced in prod.
    - `/__health` returns 200; `/__stats` exposes timing counters.
- **Quality & Tests**
    - Unit tests for opaque types (Slug/UrlPath), front-matter parser, renderer, shortcodes.
    - Property tests (e.g., slug idempotence & non-emptiness).
    - Golden tests compare generated HTML against fixtures (timestamp normalization allowed).
    - Basic integration test that hits the dev server routes.
- **Packaging**
    - **Native binary** compiled by GraalVM available; README explains how to build and run.
- **Non-Financial Domain**
    - No financial/market functionality is present.

---

## Evaluation Rubric

- **Correctness & Completeness (40%)** — Meets all acceptance criteria; CLI/Server/Plugins/Search work end-to-end.
- **Code Quality (25%)** — Idiomatic Scala 3 (opaque types, enums, type classes), modular design, immutability.
- **Robustness (15%)** — Clear diagnostics, partial failure tolerance, tests (unit/property/golden).
- **Performance (10%)** — Sensible use of concurrency/streaming, measured timings and a native binary.
- **DX & Docs (10%)** — Developer experience: clear README, examples, helpful logs and flags.

---

## Getting Started (Example Workflow)

```bash
# build native binary (Option A)
scala-cli package . --native -o aurorapress --force

# initialize, build and serve
./aurorapress init
./aurorapress new post "Hello, Aurora"
./aurorapress build --drafts --concurrency 4
./aurorapress serve --watch --port 8080
```

> Tip: Keep using `scala-cli` during development (`scala-cli run .`) for fast iterations, then package to a native image for distribution.

---

## Out of Scope (Explicit)

- Any feature that deals with **financial** data, trading, pricing, or market integrations.
- External search services (only local JSON index is required).
- Database persistence (the project is file-based).

---

## Notes

- Favor **explicit** error handling (`Either/Try/Using`) over unchecked exceptions.
- Keep module boundaries crisp. Avoid leaking CLI/server concerns into `core`.
- Use **derives** and **given** thoughtfully — avoid magic; prefer clarity.