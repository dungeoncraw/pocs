import pyarrow.flight as fl
import pandas as pd
import sys

def run_query(client, sql_query):
    print(f"\n--- Executing Query: {sql_query} ---")
    
    # Create a descriptor for the command
    descriptor = fl.FlightDescriptor.for_command(sql_query.encode('utf-8'))
    
    try:
        # Get flight info to find out how to retrieve the data
        info = client.get_flight_info(descriptor)
        
        for endpoint in info.endpoints:
            # Retrieve the data for each endpoint
            reader = client.do_get(endpoint.ticket)
            table = reader.read_all()
            
            if table.num_rows > 0:
                print(table.to_pandas())
            else:
                print("Empty result set.")
                
    except Exception as e:
        print(f"Error executing query '{sql_query}': {e}")

def main():
    location = "grpc://localhost:8816"
    client = fl.FlightClient(location)
    
    print(f"Connected to Flight SQL (simulated) server at {location}")
    
    # 1. Basic consumption: Get all records
    run_query(client, "SELECT * FROM feather_table")
    
    # 2. Filtering consumption: Get only active users
    run_query(client, "SELECT name, metadata FROM feather_table WHERE is_active = true")
    
    # 3. Aggregation consumption: Count records
    run_query(client, "SELECT count(*) as total_count FROM feather_table")

    print("\nConsumer finished.")

if __name__ == "__main__":
    main()
