use task_engine::tasks::{Task, TaskStatus};
use std::env;
fn banner() -> &'static str {
    "Task Engine"
}

fn default_worker_count() -> u8 {
    4
}

// work with slices of tasks
fn list_tasks(tasks: &[Task], status: Option<&TaskStatus>) {
    for task in tasks {
        if let Some(status) = status {
            if task.status != *status {
                continue;
            }
            println!("{:?}", task);
        }
    }
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
    let mut vector: Vec<Task> = vec![
        Task::new(1, "task 1".to_string()),
        Task::new(2, "task 2".to_string()),
        Task::new(3, "task 3".to_string())
    ];
    vector[0].mark_running();
    vector[1].mark_running();
    list_tasks(&vector, Some(&TaskStatus::Running));
}
