use task_engine::tasks::Task;

fn banner() -> &'static str {
    "Task Engine"
}

fn default_worker_count() -> u8 {
    4
}

fn main() {
    println!("{} | workers: {}", banner(), default_worker_count());
    let task = Task::new(1, "my first task".to_string());
    println!("{:?}", task);
}
