# Conway's Game of Life in Mojo

This project is a simple implementation of Conway's Game of Life using the [Mojo](https://www.modular.com/mojo) programming language. It demonstrates Mojo's ability to interface with Python libraries, specifically using `pygame` for the graphical display.

## Features

- **Mojo-powered logic**: The grid evolution logic is implemented in Mojo for high performance.
- **Python Integration**: Uses `pygame` for rendering the simulation window.
- **Random Initialization**: Starts with a random grid of 128x128 cells.

## Prerequisites

- [Mojo SDK](https://docs.modular.com/mojo/manual/get-started/)
- Python 3.12+
- `pygame` library (can be installed via the included `pyproject.toml` or manually)

## Installation

If you are using `uv`, you can install the dependencies with:

```bash
uv sync
```

Otherwise, ensure you have `pygame` installed in your Python environment:

```bash
pip install pygame
```

## Running the Simulation

To run the Game of Life simulation, use the `mojo` command:

```bash
mojo life.mojo
```

### Controls

- **Q** or **Escape**: Quit the simulation.
- **Close Window**: Quit the simulation.

## Project Structure

- `gridv1.mojo`: Contains the `Grid` struct and the Game of Life evolution logic.
- `life.mojo`: The main entry point that sets up the `pygame` window and runs the simulation loop.
- `pyproject.toml`: Project configuration and dependencies.
