import pyarrow as pa
import pyarrow.feather as feather

records = [
    {"id": 1, "is_active": True, "name": "Alice", "metadata": {"dept": "IT"}},
    {"id": 2, "is_active": False, "name": "Bob", "metadata": {"dept": "HR"}},
    {"id": 3, "is_active": True, "name": "Charlie", "metadata": {"dept": "Sales"}},
    {"id": 4, "is_active": None, "name": "Daniel", "metadata": None}
]

# PyArrow automatically infers types: int64, bool, string, and struct (for dictionaries)
table = pa.Table.from_pylist(records)

file_path = 'sql_data.feather'
try:
    feather.write_feather(table, file_path)
    print(f"Success! '{file_path}' created from record list.")
except Exception as e:
    print(f"An error occurred while saving: {e}")





