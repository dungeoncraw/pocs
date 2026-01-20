import pyarrow as pa
import pyarrow.feather as feather
import os
import random
import string

def generate_random_name(length=5):
    return ''.join(random.choices(string.ascii_letters, k=length))

def generate_records(num_records):
    cities = ["New York", "Chicago", "Los Angeles", "Houston", "Phoenix", "Philadelphia", "San Antonio", "San Diego", "Dallas", "San Jose"]
    depts = ["IT", "HR", "Sales", "Marketing", "Finance", "Legal", "Operations", "Engineering"]
    
    records = []
    for i in range(1, num_records + 1):
        is_active = random.choice([True, False, None])
        has_metadata = random.choice([True, False])
        
        record = {
            "id": i,
            "age": random.randint(18, 65),
            "city": random.choice(cities),
            "is_active": is_active,
            "name": generate_random_name(random.randint(4, 10)),
            "metadata": {"dept": random.choice(depts)} if has_metadata else None
        }
        records.append(record)
    return records

num_records = random.randint(10, 10000)
records = generate_records(num_records)

# PyArrow automatically infers types: int64, bool, string, and struct (for dictionaries)
table = pa.Table.from_pylist(records)

file_path = os.path.join(os.path.dirname(__file__), "..", 'sql_data.feather')
try:
    feather.write_feather(table, file_path)
    print(f"Success! '{file_path}' created with {num_records} random records.")
except Exception as e:
    print(f"An error occurred while saving: {e}")





