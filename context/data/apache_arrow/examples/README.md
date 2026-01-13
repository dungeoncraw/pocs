# Apache Arrow Flight Examples

This project demonstrates Apache Arrow Flight server and client implementations using Python. It includes scripts to generate a Feather file, serve it over gRPC using Arrow Flight, and consume the data using different client strategies.

## Project Structure

- `generate_feather.py`: Generates a sample `sql_data.feather` file.
- `flight_server.py`: A gRPC Flight server that loads the Feather file and serves it.
- `consumes_file.py`: A standard Flight client that connects to the server and processes data sequentially with basic flow control.
- `pipeline_parallelism.py`: An advanced Flight client demonstrating pipeline parallelism with multiple stages (Network, Compute, Sink) using threads and queues.
- `pyproject.toml`: Project configuration and dependencies.

## Prerequisites

Ensure you have Python installed. This project uses `pyarrow` and `pandas`. You can install the dependencies using `pip`:

```bash
pip install pyarrow pandas
```

Note: This project was tested with `pyarrow>=22.0.0`.

## How to Run

Follow these steps in order:

### 1. Generate the Data File

First, create the Feather file that the server will use:

```bash
python3 generate_feather.py
```
This will create a file named `sql_data.feather` in the current directory.

### 2. Start the Flight Server

Start the Arrow Flight server:

```bash
python3 flight_server.py
```
The server will start listening on `grpc://localhost:8815`. Keep this terminal window open.

### 3. Run a Client

You can run either the standard client or the pipeline parallelism client in a new terminal window.

#### Option A: Standard Sequential Client

The client will connect to the server, retrieve the schema, and process the data batches. It includes a simple flow control mechanism that pauses after every 2 batches.

```bash
python3 consumes_file.py
```

#### Option B: Pipeline Parallelism Client

This client demonstrates a 3-stage pipeline to maximize throughput:
1. **Stage A (Network)**: Fetches batches from the Flight server (I/O bound).
2. **Stage B (Compute)**: Processes data, e.g., counting nulls (CPU bound).
3. **Stage C (Sink)**: Handles results, e.g., logging (I/O bound).

```bash
python3 pipeline_parallelism.py
```

## Flow Control and Parallelism

- **Sequential Client (`consumes_file.py`)**: Demonstrates manual flow control by pausing processing to simulate backpressure management.
- **Pipeline Client (`pipeline_parallelism.py`)**: Uses Python `queue.Queue` to decouple I/O and CPU-bound tasks, allowing the next batch to be fetched while the current one is being processed.
