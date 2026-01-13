package main

import (
	"context"
	"fmt"
	"io"
	"log"
	"strings"
	"time"

	"github.com/apache/arrow/go/v15/arrow"
	"github.com/apache/arrow/go/v15/arrow/flight"
	"google.golang.org/grpc"
	"google.golang.org/grpc/credentials/insecure"
)

const (
	location  = "localhost:8815"
	dataPath  = "large_table"
	queueSize = 4
)

type BatchItem struct {
	ID    int
	Batch arrow.Record
}

type ResultItem struct {
	ID      int
	Results map[string]int
}

func main() {
	fmt.Printf("Starting Go Pipeline Parallelism (Location: %s)\n", location)
	fmt.Println(strings.Repeat("-", 50))

	startTime := time.Now()

	qNetworkToCompute := make(chan BatchItem, queueSize)
	qComputeToSink := make(chan ResultItem, queueSize)

	// Stage A: Network
	go func() {
		if err := stageANetwork(qNetworkToCompute); err != nil {
			log.Printf("[Stage A] Error: %v", err)
		}
		close(qNetworkToCompute)
	}()

	// Stage B: Compute
	go func() {
		stageBCompute(qNetworkToCompute, qComputeToSink)
		close(qComputeToSink)
	}()

	// Stage C: Sink (Blocking in main)
	stageCSink(qComputeToSink)

	fmt.Println(strings.Repeat("-", 50))
	fmt.Printf("Total pipeline time: %.2f seconds.\n", time.Since(startTime).Seconds())
}

func stageANetwork(out chan<- BatchItem) error {
	fmt.Println("[Stage A] Connecting to Flight server...")

	conn, err := grpc.Dial(location, grpc.WithTransportCredentials(insecure.NewCredentials()))
	if err != nil {
		return err
	}
	defer conn.Close()

	client := flight.NewFlightClient(conn)

	// 1. Get FlightInfo to retrieve the Ticket
	fd := &flight.FlightDescriptor{
		Type: flight.DescriptorPath,
		Path: []string{dataPath},
	}
	info, err := client.GetFlightInfo(context.Background(), fd)
	if err != nil {
		return err
	}

	// 2. Use the ticket from the first endpoint
	ticket := info.Endpoint[0].Ticket
	stream, err := client.DoGet(context.Background(), ticket)
	if err != nil {
		return err
	}

	reader, err := flight.NewRecordReader(stream)
	if err != nil {
		return err
	}
	defer reader.Release()

	batchID := 1
	for reader.Next() {
		batch := reader.Record()
		batch.Retain() // Retain because it's sent over channel
		fmt.Printf("[Stage A] Received batch #%d (%d rows)\n", batchID, batch.NumRows())
		out <- BatchItem{ID: batchID, Batch: batch}
		batchID++
	}

	if reader.Err() != nil && reader.Err() != io.EOF {
		return reader.Err()
	}

	fmt.Println("[Stage A] All batches received.")
	return nil
}

func stageBCompute(in <-chan BatchItem, out chan<- ResultItem) {
	for item := range in {
		fmt.Printf("  [Stage B] Processing batch #%d...\n", item.ID)

		// Simulate heavy processing
		time.Sleep(600 * time.Millisecond)

		// Example transformation: count nulls
		nullCounts := make(map[string]int)
		schema := item.Batch.Schema()
		for i := 0; i < int(item.Batch.NumCols()); i++ {
			name := schema.Field(i).Name
			nullCount := item.Batch.Column(i).NullN()
			nullCounts[name] = nullCount
		}

		item.Batch.Release() // Release after processing
		out <- ResultItem{ID: item.ID, Results: nullCounts}
	}
	fmt.Println("[Stage B] No more data. Stopping.")
}

func stageCSink(in <-chan ResultItem) {
	for item := range in {
		fmt.Printf("    [Stage C] Writing results of batch #%d: %v\n", item.ID, item.Results)

		// Simulate writing to a database or logs
		time.Sleep(400 * time.Millisecond)
	}
	fmt.Println("[Stage C] Task complete.")
}
