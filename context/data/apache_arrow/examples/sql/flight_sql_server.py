import pyarrow as pa
import pyarrow.flight as fl
import pyarrow.feather as feather
import duckdb
import os
import json

class FeatherFlightSQLServer(fl.FlightServerBase):
    def __init__(self, location, file_path, **kwargs):
        super(FeatherFlightSQLServer, self).__init__(location, **kwargs)
        self._location = location
        self._file_path = file_path
        # Load the feather file into a table
        self._table = feather.read_table(self._file_path)
        # Initialize DuckDB and register the table
        self._db = duckdb.connect(':memory:')
        self._db.register('feather_table', self._table)

    def get_flight_info(self, context, descriptor):
        # Command descriptor.
        if descriptor.descriptor_type == fl.DescriptorType.CMD:
            command = descriptor.command.decode('utf-8')
            # Expecting a JSON command or just the SQL string
            try:
                cmd_obj = json.loads(command)
                sql = cmd_obj.get("query")
            except:
                sql = command
            
            result_set = self._db.sql(sql)
            schema = result_set.arrow().schema
            
            # The ticket will be the SQL query itself (simplified)
            ticket = fl.Ticket(sql.encode('utf-8'))
            endpoints = [fl.FlightEndpoint(ticket, [self._location])]
            
            return fl.FlightInfo(schema, descriptor, endpoints, -1, -1)
        
        raise fl.FlightError("Invalid descriptor")

    def do_get(self, context, ticket):
        sql = ticket.ticket.decode('utf-8')
        result_set = self._db.sql(sql)
        table = result_set.arrow()
        return fl.RecordBatchStream(table)

def main():
    location = "grpc://localhost:8816"
    file_path = os.path.join(os.path.dirname(__file__), "..", "sql_data.feather")

    if not os.path.exists(file_path):
        print(f"Error: {file_path} not found. Please run generate_feather.py first.")
        return

    fl_location = fl.Location(location)
    server = FeatherFlightSQLServer(fl_location, file_path)
    print(f"Flight SQL (simulated) server started at {location}")
    print("Registered table: 'feather_table'")
    server.serve()

if __name__ == "__main__":
    main()
