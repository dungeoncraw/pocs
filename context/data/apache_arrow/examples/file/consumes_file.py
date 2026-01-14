import pyarrow.flight as fl
import time

def process_flight_data():
    USE_HTTP = False

    if USE_HTTP:
        location = "http://localhost:8815"
    else:
        location = "grpc://localhost:8815"

    print(f"Attempting to connect to: {location}")

    # Use a context manager to ensure the client connection is closed properly
    with fl.FlightClient(location) as client:
        descriptor = fl.FlightDescriptor.for_path("large_table")

        try:
            # Retrieve flight info which contains the schema and endpoints
            info = client.get_flight_info(descriptor)
            print(f"Connected. Data schema:\n{info.schema}")

            batches_processed = 0
            MAX_IN_FLIGHT = 2

            # Iterate through all endpoints
            for endpoint in info.endpoints:
                reader = client.do_get(endpoint.ticket)

                for batch in reader:
                    do_some_heavy_processing(batch)
                    batches_processed += 1

                    print(f"Processed batch {batches_processed} ({batch.data.num_rows} rows)")

                    if batches_processed % MAX_IN_FLIGHT == 0:
                        print("Flow control: Pausing to clear downstream resources...")
                        slow_down_a_bit()

        except fl.FlightError as e:
            print(f"Flight RPC error: {e}")
        except Exception as e:
            print(f"Unexpected error: {e}")

def do_some_heavy_processing(chunk):
    batch = chunk.data
    null_counts = {batch.schema.names[i]: batch.column(i).null_count for i in range(batch.num_columns)}
    print(f"   [Processing] Null counts: {null_counts}")

def slow_down_a_bit():
    time.sleep(1)

if __name__ == "__main__":
    process_flight_data()