import pyarrow as pa
import pyarrow.flight as fl
import pyarrow.feather as feather
import os


class FeatherFlightServer(fl.FlightServerBase):
    def __init__(self, location, file_path, **kwargs):
        super(FeatherFlightServer, self).__init__(location, **kwargs)
        self._location = location
        self._file_path = file_path
        self._table = feather.read_table(self._file_path)

    def _make_flight_info(self, key, descriptor):
        if key == "large_table":
            # The endpoint must use the same location as the server
            endpoints = [fl.FlightEndpoint("large_table_ticket", [self._location])]
            return fl.FlightInfo(self._table.schema, descriptor, endpoints, self._table.num_rows, self._table.nbytes)
        raise fl.FlightError(f"Unknown flight {key}")

    def list_flights(self, context, criteria):
        yield self._make_flight_info("large_table", fl.FlightDescriptor.for_path("large_table"))

    def get_flight_info(self, context, descriptor):
        key = descriptor.path[0].decode('utf-8')
        return self._make_flight_info(key, descriptor)

    def do_get(self, context, ticket):
        key = ticket.ticket.decode('utf-8')
        if key == "large_table_ticket":
            return fl.RecordBatchStream(self._table)
        raise fl.FlightError(f"Unknown ticket {key}")


def main():
    USE_HTTP = False

    if USE_HTTP:
        location = "http://localhost:8815"
    else:
        location = "grpc://localhost:8815"

    file_path = "sql_data.feather"

    if not os.path.exists(file_path):
        print(f"Error: {file_path} not found.")
        return

    # Create the Location object explicitly to ensure compatibility
    fl_location = fl.Location.from_uri(location)

    server = FeatherFlightServer(fl_location, file_path)
    print(f"Flight server started at {location}")
    server.serve()


if __name__ == "__main__":
    main()