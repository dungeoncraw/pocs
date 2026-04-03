# Task Engine

A simple task management engine implemented in Rust.

## Description

The Task Engine is a Proof of Concept (PoC) application that handles tasks with different statuses. It supports listing, running, and adding tasks via a command-line interface.

## Prerequisites

- **Rust**: Edition 2024 (requires Rust 1.85.0 or later).

## Installation

To build and run the Task Engine, you'll need the Rust toolchain installed.

1. Clone the repository (if applicable) or navigate to the project directory.
2. Build the project using Cargo:

```bash
cargo build
```

## Running the Engine

You can run the engine directly using `cargo run`:

```bash
cargo run -- [COMMAND]
```

## CLI Parameters

The engine accepts the following commands as the first argument:

- `list`: Lists the tasks (currently prints "list tasks" and filters some hardcoded tasks).
- `run`: Runs a task (currently prints "run task").
- `add`: Adds a new task (currently prints "add task").

If an unknown command or no command is provided, the engine will display:
`unknown command, must be one of: list, run, add`

### Example Usage

```bash
# List tasks
cargo run -- list

# Run a task
cargo run -- run

# Add a task
cargo run -- add
```

## Internal Features

- **Banner**: Displays "Task Engine" and the configured worker count.
- **Workers**: Default worker count is set to 4.
- **Task Statuses**: Tasks can be `Pending`, `Running`, or `Done`.
- **Filtering**: The application currently demonstrates filtering tasks by `Running` status.
