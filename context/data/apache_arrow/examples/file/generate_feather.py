import pyarrow as pa
import pyarrow.feather as feather
import os

records = [
    {"id": 1, "age": 25, "city": "New York", "is_active": True, "name": "Alice", "metadata": {"dept": "IT"}},
    {"id": 2, "age": 17, "city": "New York", "is_active": False, "name": "Bob", "metadata": {"dept": "HR"}},
    {"id": 3, "age": 30, "city": "Chicago", "is_active": True, "name": "Charlie", "metadata": {"dept": "Sales"}},
    {"id": 4, "age": 22, "city": "New York", "is_active": None, "name": "Daniel", "metadata": None},
    {"id": 5, "age": 16, "city": "Los Angeles", "is_active": True, "name": "Eve", "metadata": {"dept": "IT"}}
]

# PyArrow automatically infers types: int64, bool, string, and struct (for dictionaries)
table = pa.Table.from_pylist(records)

file_path = os.path.join(os.path.dirname(__file__), "..", 'sql_data.feather')
try:
    feather.write_feather(table, file_path)
    print(f"Success! '{file_path}' created from record list.")
except Exception as e:
    print(f"An error occurred while saving: {e}")





