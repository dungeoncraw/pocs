package main

import (
	"database/sql"
	"io"
	"log"
	"net/http"
	"time"

	"github.com/apache/arrow/go/v14/arrow"
	"github.com/apache/arrow/go/v14/arrow/array"
	"github.com/apache/arrow/go/v14/arrow/ipc"
	"github.com/apache/arrow/go/v14/arrow/memory"
	"github.com/labstack/echo/v4"
	_ "github.com/marcboeker/go-duckdb"
)

var db *sql.DB
var pool = memory.NewGoAllocator()

func initDB() {
	var err error
	db, err = sql.Open("duckdb", "arrow_sample.db")
	if err != nil {
		log.Fatal(err)
	}

	_, err = db.Exec(`CREATE TABLE IF NOT EXISTS measurements (
		id INTEGER,
		value DOUBLE,
		timestamp TIMESTAMP
	)`)
	if err != nil {
		log.Fatal(err)
	}
}

func main() {
	initDB()
	defer db.Close()

	e := echo.New()

	e.POST("/upload", handleUpload)
	e.GET("/download", handleDownload)
	e.POST("/query", handleQuery)
	e.GET("/stats", handleStats)
	e.POST("/echo", handleEcho)

	e.Logger.Fatal(e.Start(":8080"))
}

func handleUpload(c echo.Context) error {
	reader, err := ipc.NewReader(c.Request().Body, ipc.WithAllocator(pool))
	if err != nil {
		return c.String(http.StatusBadRequest, "Invalid Arrow IPC: "+err.Error())
	}
	defer reader.Release()

	for reader.Next() {
		record := reader.Record()
		ids := record.Column(0).(*array.Int32)
		values := record.Column(1).(*array.Float64)
		timestamps := record.Column(2).(*array.Timestamp)

		for i := 0; i < int(record.NumRows()); i++ {
			_, err := db.Exec("INSERT INTO measurements VALUES (?, ?, ?)",
				ids.Value(i), values.Value(i), timestamps.Value(i).ToTime(arrow.Nanosecond))
			if err != nil {
				return c.String(http.StatusInternalServerError, "DB Insert Error: "+err.Error())
			}
		}
	}

	return c.String(http.StatusOK, "Data uploaded successfully")
}

func handleDownload(c echo.Context) error {
	rows, err := db.Query("SELECT id, value, timestamp FROM measurements")
	if err != nil {
		return err
	}
	defer rows.Close()

	c.Response().Header().Set(echo.HeaderContentType, "application/vnd.apache.arrow.stream")
	writer := ipc.NewWriter(c.Response().Writer, ipc.WithSchema(getMeasurementSchema()))
	defer writer.Close()

	rb := rowsToRecordBatch(rows)
	if rb != nil {
		defer rb.Release()
		if err := writer.Write(rb); err != nil {
			return err
		}
	}

	return nil
}

func handleQuery(c echo.Context) error {
	body, err := io.ReadAll(c.Request().Body)
	if err != nil {
		return err
	}
	query := string(body)

	rows, err := db.Query(query)
	if err != nil {
		return c.String(http.StatusBadRequest, "Query Error: "+err.Error())
	}
	defer rows.Close()

	c.Response().Header().Set(echo.HeaderContentType, "application/vnd.apache.arrow.stream")

	rb := rowsToRecordBatch(rows)
	if rb != nil {
		defer rb.Release()
		writer := ipc.NewWriter(c.Response().Writer, ipc.WithSchema(rb.Schema()))
		defer writer.Close()
		return writer.Write(rb)
	}

	return nil
}

func handleStats(c echo.Context) error {
	var avg float64
	var count int
	err := db.QueryRow("SELECT AVG(value), COUNT(*) FROM measurements").Scan(&avg, &count)
	if err != nil {
		return err
	}

	schema := arrow.NewSchema(
		[]arrow.Field{
			{Name: "avg_value", Type: arrow.PrimitiveTypes.Float64},
			{Name: "total_count", Type: arrow.PrimitiveTypes.Int64},
		},
		nil,
	)

	b := array.NewRecordBuilder(pool, schema)
	defer b.Release()

	b.Field(0).(*array.Float64Builder).Append(avg)
	b.Field(1).(*array.Int64Builder).Append(int64(count))

	rec := b.NewRecord()
	defer rec.Release()

	c.Response().Header().Set(echo.HeaderContentType, "application/vnd.apache.arrow.stream")
	writer := ipc.NewWriter(c.Response().Writer, ipc.WithSchema(schema))
	defer writer.Close()

	return writer.Write(rec)
}

func handleEcho(c echo.Context) error {
	reader, err := ipc.NewReader(c.Request().Body, ipc.WithAllocator(pool))
	if err != nil {
		return err
	}
	defer reader.Release()

	c.Response().Header().Set(echo.HeaderContentType, "application/vnd.apache.arrow.stream")

	var writer *ipc.Writer
	var newSchema *arrow.Schema
	defer func() {
		if writer != nil {
			writer.Close()
		}
	}()

	for reader.Next() {
		rec := reader.Record()

		if writer == nil {
			fields := rec.Schema().Fields()
			fields = append(fields, arrow.Field{Name: "processed", Type: arrow.FixedWidthTypes.Boolean})
			newSchema = arrow.NewSchema(fields, nil)
			writer = ipc.NewWriter(c.Response().Writer, ipc.WithSchema(newSchema))
		}

		b := array.NewBooleanBuilder(pool)
		for i := 0; i < int(rec.NumRows()); i++ {
			b.Append(true)
		}
		processedCol := b.NewArray()
		defer processedCol.Release()
		defer b.Release()

		cols := make([]arrow.Array, rec.NumCols())
		for i := 0; i < int(rec.NumCols()); i++ {
			cols[i] = rec.Column(i)
		}
		cols = append(cols, processedCol)

		newRec := array.NewRecord(newSchema, cols, rec.NumRows())
		defer newRec.Release()

		if err := writer.Write(newRec); err != nil {
			return err
		}
	}

	return nil
}

func getMeasurementSchema() *arrow.Schema {
	return arrow.NewSchema(
		[]arrow.Field{
			{Name: "id", Type: arrow.PrimitiveTypes.Int32},
			{Name: "value", Type: arrow.PrimitiveTypes.Float64},
			{Name: "timestamp", Type: arrow.FixedWidthTypes.Timestamp_ns},
		},
		nil,
	)
}

func rowsToRecordBatch(rows *sql.Rows) arrow.Record {
	schema := getMeasurementSchema()
	b := array.NewRecordBuilder(pool, schema)
	defer b.Release()

	for rows.Next() {
		var id int32
		var val float64
		var t time.Time
		if err := rows.Scan(&id, &val, &t); err != nil {
			log.Println("Scan error:", err)
			continue
		}

		b.Field(0).(*array.Int32Builder).Append(id)
		b.Field(1).(*array.Float64Builder).Append(val)
		b.Field(2).(*array.TimestampBuilder).Append(arrow.Timestamp(t.UnixNano()))
	}

	return b.NewRecord()
}
