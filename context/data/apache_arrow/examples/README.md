# Apache Arrow Flight Examples

This project demonstrates Apache Arrow Flight server and client implementations using Python. It includes scripts to generate a Feather file, serve it over gRPC using Arrow Flight, and consume the data using different client strategies.

## Project Structure

- `file/`: Contains file-based Arrow Flight examples.
    - `generate_feather.py`: Generates a sample `sql_data.feather` file in the root directory.
    - `flight_server.py`: A gRPC Flight server that loads the Feather file and serves it.
    - `consumes_file.py`: A standard Flight client that connects to the server and processes data sequentially.
    - `pipeline_parallelism.py`: An advanced Flight client demonstrating pipeline parallelism in Python.
    - `pipeline_parallelism.go`: Pipeline parallelism implementation in Go.
    - `pipeline_parallelism.rs`: Pipeline parallelism implementation in Rust.
- `sql/`: Contains SQL-based Arrow Flight examples.
    - `flight_sql_server.py`: A Flight server that supports SQL queries using DuckDB over the Feather data.
    - `flight_sql_client.py`: A Flight client that sends SQL queries to the server.
    - `flight_sql_consumer.py`: A more advanced consumer for Flight SQL that executes multiple queries.
- `pyproject.toml`: Project configuration and dependencies.

## Prerequisites

Ensure you have Python installed. This project uses `pyarrow`, `pandas`, and `duckdb`. You can install the dependencies using `pip`:

```bash
pip install pyarrow pandas duckdb
```

Note: This project was tested with `pyarrow>=22.0.0`.

## How to Run

Follow these steps in order:

### 1. Generate the Data File

First, create the Feather file that the server will use:

```bash
python3 file/generate_feather.py
```
This will create a file named `sql_data.feather` in the current directory.

### 2. Start the Flight Server

Start the Arrow Flight server:

```bash
python3 file/flight_server.py
```
The server will start listening on `grpc://localhost:8815`. Keep this terminal window open.

### 3. Run a Client

You can run either the standard client or the pipeline parallelism client in a new terminal window.

#### Option A: Standard Sequential Client

The client will connect to the server, retrieve the schema, and process the data batches. It includes a simple flow control mechanism that pauses after every 2 batches.

```bash
python3 file/consumes_file.py
```

#### Option B: Pipeline Parallelism Client

This client demonstrates a 3-stage pipeline to maximize throughput:
1. **Stage A (Network)**: Fetches batches from the Flight server (I/O bound).
2. **Stage B (Compute)**: Processes data, e.g., counting nulls (CPU bound).
3. **Stage C (Sink)**: Handles results, e.g., logging (I/O bound).

```bash
python3 file/pipeline_parallelism.py
```

#### Option C: Flight SQL Client

This client executes a SQL query against the Feather file through a Flight SQL server powered by DuckDB.

1. Start the Flight SQL server:
```bash
python3 sql/flight_sql_server.py
```

2. Run the client in another terminal:
```bash
python3 sql/flight_sql_client.py
```

#### Option D: Flight SQL Consumer

This consumer demonstrates executing multiple types of queries (filtering, aggregation) against the Flight SQL server.

1. Ensure the Flight SQL server is running (`python3 sql/flight_sql_server.py`).

2. Run the consumer:
```bash
python3 sql/flight_sql_consumer.py
```

### 4. Run Rust and Go Clients

New clients in Rust and Go have been added to demonstrate the same pipeline parallelism.

#### Rust Client

The Rust client uses `tokio` for async orchestration and `arrow-flight` crate.

**Prerequisites:**
- Rust installed (`rustc`, `cargo`)

**Run:**
```bash
# Assuming you are in the file/ directory or have the necessary setup
# For a quick run, assuming arrow and tokio are available:
cargo run --example pipeline_parallelism # if part of a cargo project
```

#### Go Client

The Go client uses goroutines and channels for the pipeline stages.

**Prerequisites:**
- Go installed

**Run:**
```bash
go run file/pipeline_parallelism.go
```

## Flow Control and Parallelism

- **Sequential Client (`file/consumes_file.py`)**: Demonstrates manual flow control by pausing processing to simulate backpressure management.
- **Pipeline Client (`file/pipeline_parallelism.py`)**: Uses Python `queue.Queue` to decouple I/O and CPU-bound tasks, allowing the next batch to be fetched while the current one is being processed.
