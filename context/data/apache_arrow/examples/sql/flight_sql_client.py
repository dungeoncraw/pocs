import pyarrow.flight as fl
import json

def main():
    location = "grpc://localhost:8816"
    client = fl.FlightClient(location)

    # SQL query to execute
    sql_query = "SELECT name, is_active FROM feather_table WHERE id > 1"
    print(f"Executing query: {sql_query}")

    # In Flight SQL, we'd use FlightSqlClient.execute(query)
    descriptor = fl.FlightDescriptor.for_command(sql_query.encode('utf-8'))
    
    try:
        info = client.get_flight_info(descriptor)
        endpoint = info.endpoints[0]
        reader = client.do_get(endpoint.ticket)
        
        # Read the result table
        table = reader.read_all()
        print("\nQuery results:")
        print(table.to_pandas())
        
    except Exception as e:
        print(f"Error: {e}")

if __name__ == "__main__":
    main()
