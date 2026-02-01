# Comparison with python

Get some examples from https://mojo-lang.com/miji

## str_example - Multiplication Table

This directory contains a nine-nine multiplication table example implemented in both Python and Mojo.

### Running the Examples

#### Python Version
```bash
# Using uv
uv run python multiplication_table.py

# Or directly with python
python multiplication_table.py
```

#### Mojo Version
```bash
# Using pixi (recommended)
pixi run mojo run mutiplication_table.mojo

# Or directly with mojo
mojo run mutiplication_table.mojo
```

Both scripts output:
- A 9x9 multiplication table
- Start time (Unix timestamp)
- End time (Unix timestamp)
- Elapsed execution time in milliseconds