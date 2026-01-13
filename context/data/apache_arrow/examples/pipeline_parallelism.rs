use arrow::array::Array;
use arrow_flight::decode::FlightRecordBatchStream;
use arrow_flight::flight_service_client::FlightServiceClient;
use arrow_flight::{FlightDescriptor, Ticket};
use futures::StreamExt;
use std::collections::HashMap;
use std::time::{Duration, Instant};
use tokio::sync::mpsc;

const LOCATION: &str = "http://localhost:8815";
const DATA_PATH: &str = "large_table";
const QUEUE_SIZE: usize = 4;

#[tokio::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    println!("Starting Rust Pipeline Parallelism (Location: {})", LOCATION);
    println!("{}", "-".repeat(50));

    let start_time = Instant::now();

    // Queues for the pipeline stages
    let (tx_network_to_compute, rx_network_to_compute) = mpsc::channel(QUEUE_SIZE);
    let (tx_compute_to_sink, rx_compute_to_sink) = mpsc::channel(QUEUE_SIZE);

    // Stage A: Network
    let stage_a = tokio::spawn(async move {
        if let Err(e) = stage_a_network(tx_network_to_compute).await {
            eprintln!("[Stage A] Error: {:?}", e);
        }
    });

    // Stage B: Compute
    let stage_b = tokio::spawn(async move {
        stage_b_compute(rx_network_to_compute, tx_compute_to_sink).await;
    });

    // Stage C: Sink
    let stage_c = tokio::spawn(async move {
        stage_c_sink(rx_compute_to_sink).await;
    });

    // Wait for all stages to complete
    let _ = tokio::join!(stage_a, stage_b, stage_c);

    println!("{}", "-".repeat(50));
    println!(
        "Total pipeline time: {:.2} seconds.",
        start_time.elapsed().as_secs_f64()
    );

    Ok(())
}

async fn stage_a_network(
    tx: mpsc::Sender<(usize, arrow::record_batch::RecordBatch)>,
) -> Result<(), Box<dyn std::error::Error>> {
    println!("[Stage A] Connecting to Flight server...");

    let mut client = FlightServiceClient::connect(LOCATION).await?;

    // 1. Get FlightInfo to retrieve the Ticket
    let descriptor = FlightDescriptor::new_path(vec![DATA_PATH.to_string()]);
    let info = client.get_flight_info(descriptor).await?.into_inner();

    // 2. Use the ticket from the first endpoint
    if let Some(endpoint) = info.endpoint.first() {
        if let Some(ticket) = &endpoint.ticket {
            let stream = client.do_get(ticket.clone()).await?.into_inner();
            let mut reader = FlightRecordBatchStream::new_from_flight_data(stream);

            let mut batch_id = 1;
            while let Some(batch) = reader.next().await {
                let batch = batch?;
                println!(
                    "[Stage A] Received batch #{} ({} rows)",
                    batch_id,
                    batch.num_rows()
                );
                tx.send((batch_id, batch)).await?;
                batch_id += 1;
            }
        }
    }

    println!("[Stage A] All batches received.");
    Ok(())
}

async fn stage_b_compute(
    mut rx: mpsc::Receiver<(usize, arrow::record_batch::RecordBatch)>,
    tx: mpsc::Sender<(usize, HashMap<String, usize>)>,
) {
    while let Some((batch_id, batch)) = rx.recv().await {
        println!("  [Stage B] Processing batch #{}...", batch_id);

        // Simulate heavy processing
        tokio::time::sleep(Duration::from_millis(600)).await;

        // Example transformation: count nulls
        let mut null_counts = HashMap::new();
        let schema = batch.schema();
        for i in 0..batch.num_columns() {
            let name = schema.field(i).name().clone();
            let null_count = batch.column(i).null_count();
            null_counts.insert(name, null_count);
        }

        if let Err(e) = tx.send((batch_id, null_counts)).await {
            eprintln!("[Stage B] Send error: {:?}", e);
            break;
        }
    }
    println!("[Stage B] No more data. Stopping.");
}

async fn stage_c_sink(mut rx: mpsc::Receiver<(usize, HashMap<String, usize>)>) {
    while let Some((batch_id, results)) = rx.recv().await {
        println!(
            "    [Stage C] Writing results of batch #{}: {:?}",
            batch_id, results
        );

        // Simulate writing to a database or logs
        tokio::time::sleep(Duration::from_millis(400)).await;
    }
    println!("[Stage C] Task complete.");
}
