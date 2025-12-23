package main

import (
	"bytes"
	"fmt"
	"io"
	"log"
	"net/http"
	"time"

	"github.com/apache/arrow/go/v14/arrow"
	"github.com/apache/arrow/go/v14/arrow/array"
	"github.com/apache/arrow/go/v14/arrow/ipc"
	"github.com/apache/arrow/go/v14/arrow/memory"
)

var pool = memory.NewGoAllocator()

func main() {
	time.Sleep(2 * time.Second) // Wait for server to start if running concurrently
	
	// Create sample data
	schema := arrow.NewSchema(
		[]arrow.Field{
			{Name: "id", Type: arrow.PrimitiveTypes.Int32},
			{Name: "value", Type: arrow.PrimitiveTypes.Float64},
			{Name: "timestamp", Type: arrow.FixedWidthTypes.Timestamp_ns},
		},
		nil,
	)

	b := array.NewRecordBuilder(pool, schema)
	defer b.Release()

	b.Field(0).(*array.Int32Builder).AppendValues([]int32{1, 2, 3}, nil)
	b.Field(1).(*array.Float64Builder).AppendValues([]float64{10.5, 20.7, 30.2}, nil)
	b.Field(2).(*array.TimestampBuilder).AppendValues([]arrow.Timestamp{
		arrow.Timestamp(time.Now().UnixNano()),
		arrow.Timestamp(time.Now().UnixNano()),
		arrow.Timestamp(time.Now().UnixNano()),
	}, nil)

	rec := b.NewRecord()
	defer rec.Release()

	var buf bytes.Buffer
	writer := ipc.NewWriter(&buf, ipc.WithSchema(schema))
	if err := writer.Write(rec); err != nil {
		log.Fatal(err)
	}
	writer.Close()

	// 1. Test /upload
	fmt.Println("Testing /upload...")
	resp, err := http.Post("http://localhost:8080/upload", "application/vnd.apache.arrow.stream", &buf)
	if err != nil {
		log.Fatal(err)
	}
	body, _ := io.ReadAll(resp.Body)
	fmt.Printf("Status: %s, Body: %s\n", resp.Status, body)

	// 2. Test /download
	fmt.Println("\nTesting /download...")
	resp, err = http.Get("http://localhost:8080/download")
	if err != nil {
		log.Fatal(err)
	}
	fmt.Printf("Status: %s\n", resp.Status)
	readArrow(resp.Body)

	// 3. Test /query
	fmt.Println("\nTesting /query...")
	resp, err = http.Post("http://localhost:8080/query", "text/plain", bytes.NewBufferString("SELECT * FROM measurements WHERE value > 20"))
	if err != nil {
		log.Fatal(err)
	}
	fmt.Printf("Status: %s\n", resp.Status)
	readArrow(resp.Body)

	// 4. Test /stats
	fmt.Println("\nTesting /stats...")
	resp, err = http.Get("http://localhost:8080/stats")
	if err != nil {
		log.Fatal(err)
	}
	fmt.Printf("Status: %s\n", resp.Status)
	readArrow(resp.Body)

	// 5. Test /echo
	fmt.Println("\nTesting /echo...")
	buf.Reset()
	writer = ipc.NewWriter(&buf, ipc.WithSchema(schema))
	writer.Write(rec)
	writer.Close()
	resp, err = http.Post("http://localhost:8080/echo", "application/vnd.apache.arrow.stream", &buf)
	if err != nil {
		log.Fatal(err)
	}
	fmt.Printf("Status: %s\n", resp.Status)
	readArrow(resp.Body)
}

func readArrow(r io.Reader) {
	reader, err := ipc.NewReader(r, ipc.WithAllocator(pool))
	if err != nil {
		fmt.Printf("Error creating reader: %v\n", err)
		return
	}
	defer reader.Release()

	for reader.Next() {
		rec := reader.Record()
		fmt.Printf("Record has %d rows and %d columns\n", rec.NumRows(), rec.NumCols())
		fmt.Printf("Schema: %v\n", rec.Schema())
		// Print first row as example
		for i := 0; i < int(rec.NumCols()); i++ {
			fmt.Printf("Col %d (%s): %v\n", i, rec.Schema().Field(i).Name, rec.Column(i))
		}
	}
}
