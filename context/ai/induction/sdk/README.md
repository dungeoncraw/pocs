# Induction SDK

This project is a Scala 3 implementation of the **State Induction** concept, specifically focusing on the "Hard part" as described in Diego Pacheco's blog post: [State Induction](https://diegopachecotech.substack.com/p/state-induction).

The Induction SDK allows you to induce specific application states (e.g., error conditions, specific data responses) by instrumenting REST client requests without needing complex E2E setups.

## Prerequisites

- [Scala 3](https://www.scala-lang.org/download/)
- [sbt](https://www.scala-sbt.org/download.html) (Scala Build Tool)

## Project Structure

- `src/main/scala/com/induction/sdk/`: Core SDK implementation.
    - `models.scala`: Profile and Action definitions.
    - `InductionSDK.scala`: The main instrumentation engine.
    - `services.scala`: Profile fetching logic.
- `src/main/scala/main.scala`: A Proof of Concept (PoC) demo.
- `src/test/scala/com/induction/sdk/`: Unit tests.

## How to Run the PoC

The PoC demonstrates a "User Not Paid" scenario where the SDK induces a specific error state.

### Using sbt

To run it locally, use:

```bash
sbt run
```

### Using Docker Compose

To run the full stack (Rust Profile API + Scala SDK App + Bun Server):

```bash
docker-compose up --build
```

This will start:
- Rust API on port 3000.
- Bun Server on port 4000.
- Scala PoC application (which runs once and exits).

### Testing the Bun Server

The Bun server exposes a `/test-induction` endpoint that uses the TypeScript version of the SDK to instrument a call.

#### Using Bruno (Recommended)

A [Bruno](https://www.usebruno.com/) collection is provided in the `bruno-collection` directory. You can import this folder into Bruno to easily test all induction scenarios:
- **Ping**: Health check.
- **Real Call**: Standard request without induction.
- **Mock from Rust API**: Induces `user-not-paid-profile`.
- **Exception from Rust API**: Induces `rust-exception-profile`.
- **Delay from Local JSON**: Induces `slow-api-profile`.
- **Mutation from Local JSON**: Induces `mutation-profile`.

#### Using curl

```bash
# Test with a local profile (Delay)
curl -H "X-Induction-Profile-ID: slow-api-profile" "http://localhost:4000/test-induction?url=https://httpbin.org/json"

# Test with a Rust API profile (Mock)
curl -H "X-Induction-Profile-ID: user-not-paid-profile" "http://localhost:4000/test-induction?url=https://httpbin.org/json"
```

### Using Docker (Standalone SDK PoC)

```bash
# Build the image
docker build -t induction-sdk-poc .

# Run the container
docker run induction-sdk-poc
```

## How to Run Tests

To execute the unit tests and verify the SDK behavior:

```bash
sbt test
```

## Bun Server Integration (TypeScript)

The `bun-server` directory contains a TypeScript implementation of the State Induction SDK logic. This demonstrates how the same concepts and profile definitions can be used across different languages and environments.

### How it works (TypeScript)

1.  **Parity**: The TypeScript SDK (`bun-server/InductionSDK.ts`) mirrors the logic of the Scala SDK:
    *   It checks for the `X-Induction-Profile-ID` header.
    *   It uses a `CompositeProfileFetcher` to find profiles in local JSON files or the remote Rust API.
    *   It supports the same actions: `CallReal`, `MockWhole`, `Mutate`, `Exception`, and `Delay`.
2.  **Instrumentation**: Instead of wrapping `sttp`, the TypeScript SDK instruments the standard `fetch` API.
3.  **Injection**: The SDK is instantiated in `index.ts` and used to wrap outgoing requests within the Bun server's request handler.

### Injecting the SDK into Bun

To use the induction logic in a Bun/TypeScript application:

1.  **Define Fetchers**: Set up the profile sources.
    ```typescript
    const httpFetcher = new HttpProfileFetcher(profileServerUrl);
    const fileFetcher = new FileProfileFetcher("./profiles");
    const fetcher = new CompositeProfileFetcher([fileFetcher, httpFetcher]);
    ```
2.  **Initialize SDK**:
    ```typescript
    const sdk = new InductionSDK(fetcher);
    ```
3.  **Instrument Fetch Calls**: Replace direct `fetch` calls with `sdk.instrumentRequest`.
    ```typescript
    // Inside your Bun.serve fetch handler
    const response = await sdk.instrumentRequest(targetUrl, {
      headers: {
        "X-Induction-Profile-ID": req.headers.get("X-Induction-Profile-ID") || "",
      },
    });
    ```

## How It Works (Core Concept)

1.  **Instrument Request**: The SDK wraps your `sttp` requests.
2.  **Profile ID**: It looks for the `X-Induction-Profile-ID` header.
3.  **Safety First**: It automatically bypasses induction if `APP_ENV` is set to `prod` or `production`.
4.  **Fetch Profile**: If a profile ID is found, it fetches the induction profile from multiple sources (Local JSON files or Remote API).
5.  **Apply Action**: It applies the action defined in the profile:
    *   **CallReal**: Proceeds with the original request.
    *   **MockWhole**: Returns a predefined JSON response.
    *   **Mutate**: Executes the real call and modifies specific fields in the JSON response.
    *   **Exception**: Throws a runtime exception with a custom message.
    *   **Delay**: Introduces a controlled latency before proceeding with the call.

## Variations and JSON Templates

The SDK supports loading profiles from local JSON templates, allowing easy variation of induction states without changing code or remote API state.

Example local profile (`profiles/slow-api-profile.json`):
```json
{
  "id": "slow-api-profile",
  "action": "Delay",
  "delayMs": 2000
}
```

## Usage Example

```scala
import com.induction.sdk.*
import sttp.client3.*

// 1. Define or provide a ProfileFetcher
val fetcher = new HttpProfileFetcher("https://profiles.internal.repo")

// 2. Initialize SDK
val sdk = new InductionSDK(fetcher)

// 3. Instrument your request
val request = basicRequest
  .get(uri"https://api.example.com/user/data")
  .header("X-Induction-Profile-ID", "user-not-paid-profile")

val response = sdk.instrumentRequest(request)

println(s"Response: ${response.body}")
```
