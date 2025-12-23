# Apache Arrow Go Sample

This project demonstrates a Go application using [Apache Arrow](https://arrow.apache.org/) for high-performance data exchange and [Echo](https://echo.labstack.com/) as a web framework. It uses [DuckDB](https://duckdb.org/) for in-memory data storage and querying.

## Features

- **Upload Arrow Data**: Send Arrow IPC streams to be stored in DuckDB.
- **Download Arrow Data**: Retrieve stored data as Arrow IPC streams.
- **Execute SQL Queries**: Run SQL queries and receive results in Arrow format.
- **Compute Statistics**: Get aggregated statistics (Average, Count) via Arrow records.
- **Echo Service**: Round-trip testing of Arrow IPC streams.

## Prerequisites

- [Go](https://golang.org/doc/install) (1.21 or later recommended)
- Dependencies will be installed automatically via `go mod`.

## Project Structure

- `main.go`: The server application.
- `client/test_client.go`: A Go test client to verify server functionality.
- `client/test_client.py`: A Python test client to verify server functionality using `pyarrow`.

## How to Run

### 1. Start the Server

From the root directory, run:

```bash
go run main.go
```

The server will start on `http://localhost:8080`.

### 2. Run the Test Client (Go)

In a separate terminal, run:

```bash
go run client/test_client.go
```

### 3. Run the Test Client (Python)

The Python client demonstrates how to interact with the Arrow-based Go server using `pyarrow`.

**Prerequisites:**

Ensure you have the required dependencies:

```bash
pip install pyarrow requests
```

**Run the client:**

```bash
python client/test_client.py
```

**What it does:**
- Constructs `pyarrow.RecordBatch` objects.
- Serializes data into Arrow IPC stream format.
- Sends data to the Go server using `requests`.
- Parses Arrow IPC streams received from the server.

Both clients will perform a series of tests against the server's endpoints and print the results to the console.

## API Endpoints

- `POST /upload`: Accepts an Arrow IPC stream and inserts data into the `measurements` table.
- `GET /download`: Returns all data from the `measurements` table as an Arrow IPC stream.
- `POST /query`: Accepts a raw SQL query in the request body and returns the result set as an Arrow IPC stream.
- `GET /stats`: Returns an Arrow record containing the average value and total count of measurements.
- `POST /echo`: Receives an Arrow IPC stream and sends it back immediately.

## How to Test Manually

You can also use `curl` to test some endpoints (though interpreting the binary Arrow format in the terminal might be difficult):

```bash
# Test the Stats endpoint
curl http://localhost:8080/stats --output stats.arrow

# Send a query
curl -X POST -d "SELECT * FROM measurements" http://localhost:8080/query --output query_results.arrow
```
