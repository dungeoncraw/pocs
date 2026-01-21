# brainrot-info

Library for providing information about brainrots from the [Steal a Brainrot Wiki](https://stealabrainrot.fandom.com/wiki/Steal_a_Brainrot_Wiki).

## Usage

```python
from brainrot_info import get_brainrot_info, Rarity

# Get info about a specific brainrot
info = get_brainrot_info("Skibidi Toilet")

if info:
    print(f"Name: {info.name}")
    print(f"Rarity: {info.rarity.name}")
    
    # Check if it's worth buying (default threshold is RARE)
    if info.is_worth_buying():
        print("It's worth buying!")
    else:
        print("Maybe skip this one.")

    # You can also specify a custom rarity threshold
    if info.is_worth_buying(Rarity.MYTHIC):
        print("It's truly legendary!")
```

## How to Build

To build the library, you can use the `build` tool. First, ensure you have it installed:

```bash
pip install build
```

Then, run the build command from the root of the project:

```bash
python -m build
```

This will generate a `dist/` directory containing the `.tar.gz` and `.whl` files.

## How to Distribute

To distribute the library to PyPI, you can use `twine`. First, install `twine`:

```bash
pip install twine
```

Then, upload the generated files:

```bash
python -m twine upload dist/*
```

Note: You will need a PyPI account and might need to configure your credentials.
