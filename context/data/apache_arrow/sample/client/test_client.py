import requests
import pyarrow as pa
import pyarrow.ipc as ipc
import io
import datetime

BASE_URL = "http://localhost:8080"

def test_upload():
    print("Testing /upload...")
    # Create sample data
    data = [
        pa.array([10, 11, 12], type=pa.int32()),
        pa.array([100.1, 200.2, 300.3], type=pa.float64()),
        pa.array([datetime.datetime.now() for _ in range(3)], type=pa.timestamp('ns'))
    ]
    schema = pa.schema([
        ('id', pa.int32()),
        ('value', pa.float64()),
        ('timestamp', pa.timestamp('ns'))
    ])
    batch = pa.RecordBatch.from_arrays(data, schema=schema)
    
    sink = io.BytesIO()
    with ipc.new_stream(sink, schema) as writer:
        writer.write_batch(batch)
    
    resp = requests.post(f"{BASE_URL}/upload", data=sink.getvalue(), headers={'Content-Type': 'application/vnd.apache.arrow.stream'})
    print(f"Status: {resp.status_code}, Body: {resp.text}")

def read_arrow_stream(content):
    with ipc.open_stream(content) as reader:
        schema = reader.schema
        print(f"Schema: {schema}")
        for batch in reader:
            print(f"Batch has {batch.num_rows} rows and {batch.num_columns} columns")
            print(batch.to_pydict())

def test_download():
    print("\nTesting /download...")
    resp = requests.get(f"{BASE_URL}/download")
    print(f"Status: {resp.status_code}")
    if resp.status_code == 200:
        read_arrow_stream(resp.content)

def test_query():
    print("\nTesting /query...")
    query = "SELECT * FROM measurements WHERE value > 150"
    resp = requests.post(f"{BASE_URL}/query", data=query, headers={'Content-Type': 'text/plain'})
    print(f"Status: {resp.status_code}")
    if resp.status_code == 200:
        read_arrow_stream(resp.content)

def test_stats():
    print("\nTesting /stats...")
    resp = requests.get(f"{BASE_URL}/stats")
    print(f"Status: {resp.status_code}")
    if resp.status_code == 200:
        read_arrow_stream(resp.content)

def test_echo():
    print("\nTesting /echo...")
    data = [
        pa.array([1, 2], type=pa.int32()),
        pa.array([1.1, 2.2], type=pa.float64()),
        pa.array([datetime.datetime.now() for _ in range(2)], type=pa.timestamp('ns'))
    ]
    schema = pa.schema([
        ('id', pa.int32()),
        ('value', pa.float64()),
        ('timestamp', pa.timestamp('ns'))
    ])
    batch = pa.RecordBatch.from_arrays(data, schema=schema)
    
    sink = io.BytesIO()
    with ipc.new_stream(sink, schema) as writer:
        writer.write_batch(batch)
    
    resp = requests.post(f"{BASE_URL}/echo", data=sink.getvalue(), headers={'Content-Type': 'application/vnd.apache.arrow.stream'})
    print(f"Status: {resp.status_code}")
    if resp.status_code == 200:
        read_arrow_stream(resp.content)

if __name__ == "__main__":
    try:
        test_upload()
        test_download()
        test_query()
        test_stats()
        test_echo()
    except requests.exceptions.ConnectionError:
        print("Error: Could not connect to the server. Make sure main.go is running on http://localhost:8080")
