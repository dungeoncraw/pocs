import queue
import threading
import time
import pyarrow.flight as fl

# Configuration to match flight_server.py
LOCATION = "grpc://localhost:8815"
# The server expects this path in the descriptor to find the table
DATA_PATH = "large_table"
QUEUE_SIZE = 4

# Queues for the pipeline stages
q_network_to_compute = queue.Queue(maxsize=QUEUE_SIZE)
q_compute_to_sink = queue.Queue(maxsize=QUEUE_SIZE)


def stage_a_network():
    """Stage A: Fetch batches from the network (I/O Bound)"""
    print("[Stage A] Connecting to Flight server...")
    try:
        with fl.FlightClient(LOCATION) as client:
            # 1. Get FlightInfo to retrieve the Ticket
            descriptor = fl.FlightDescriptor.for_path(DATA_PATH)
            info = client.get_flight_info(descriptor)

            # 2. Use the ticket from the first endpoint
            ticket = info.endpoints[0].ticket
            reader = client.do_get(ticket)

            for i, batch_chunk in enumerate(reader):
                batch = batch_chunk.data
                print(f"[Stage A] Received batch #{i + 1} ({batch.num_rows} rows)")
                q_network_to_compute.put((i + 1, batch))

        print("[Stage A] All batches received.")
    except Exception as e:
        print(f"[Stage A] Error: {e}")
    finally:
        q_network_to_compute.put(None)


def stage_b_compute():
    """Stage B: Process data (CPU Bound)"""
    while True:
        item = q_network_to_compute.get()
        if item is None:
            print("[Stage B] No more data. Stopping.")
            q_compute_to_sink.put(None)
            break

        batch_id, batch = item
        print(f"  [Stage B] Processing batch #{batch_id}...")

        # Simulate heavy processing
        time.sleep(0.6)

        # Example transformation: count nulls (like your previous example)
        null_counts = {batch.schema.names[i]: batch.column(i).null_count for i in range(batch.num_columns)}

        q_compute_to_sink.put((batch_id, null_counts))


def stage_c_sink():
    """Stage C: Save results (I/O Bound)"""
    while True:
        item = q_compute_to_sink.get()
        if item is None:
            print("[Stage C] Task complete.")
            break

        batch_id, results = item
        print(f"    [Stage C] Writing results of batch #{batch_id}: {results}")

        # Simulate writing to a database or logs
        time.sleep(0.4)


def run_pipeline():
    print(f"Starting Pipeline Parallelism (Location: {LOCATION})")
    print("-" * 50)

    threads = [
        threading.Thread(target=stage_a_network),
        threading.Thread(target=stage_b_compute),
        threading.Thread(target=stage_c_sink)
    ]

    start_time = time.time()
    for t in threads: t.start()
    for t in threads: t.join()

    print("-" * 50)
    print(f"Total pipeline time: {time.time() - start_time:.2f} seconds.")


if __name__ == "__main__":
    run_pipeline()