use task_engine::tasks::{Task};
use task_engine::orchestrator::Orchestrator;
use std::env;
fn banner() -> &'static str {
    "Task Engine"
}

fn default_worker_count() -> u8 {
    4
}

fn main() {
    println!("{} | workers: {}", banner(), default_worker_count());

    let args: Vec<String> = env::args().collect();
    match args.get(1).map(String::as_str) {
        Some("list") => println!("list tasks"),
        Some("run") => println!("run task"),
        Some("add") => println!("add task"),
        _ => println!("unknown command, must be one of: list, run, add"),
    }
    let vector: Vec<Task> = vec![
        Task::new(1, "task 1"),
        Task::new(2, "task 2"),
        Task::new(3, "task 3"),
        Task::new(4, "task 4"),
    ];
    let mut orchestrator: Orchestrator = Orchestrator::new();
    for task in vector.iter() {
        orchestrator.add_task(task);
    }
    orchestrator.consume_task();

    Orchestrator::inspect_task(&vector[0]);
    Orchestrator::find_task(&vector, 1);
}
