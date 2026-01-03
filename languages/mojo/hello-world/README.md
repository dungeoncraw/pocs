# Mojo Hello World

This is a simple "Hello World" project written in Mojo, demonstrating basic input and output.

## Prerequisites

This project uses [uv](https://github.com/astral-sh/uv) for dependency and environment management. Make sure you have it installed:

```bash
curl -LsSf https://astral.sh/uv/install.sh | sh
```

## Installation

To install the project dependencies, including Mojo:

```bash
uv sync
```

This will create a virtual environment and install Mojo as specified in `pyproject.toml`.

## Running the Project

You can run the `hello.mojo` script using `uv run`:

```bash
uv run mojo hello.mojo
```

When prompted, enter your name to see the greeting.

### Example

```text
Who are you? Junie
This is a mojo hello world
Hi, Junie!
```
