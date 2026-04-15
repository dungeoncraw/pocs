use crate::tasks::Task;
#[derive(Debug, Clone)]
pub struct Orchestrator {
    tasks: Vec<Task>,
}

impl Orchestrator {
    pub fn new() -> Self {
        Self {
            tasks: Vec::new(),
        }
    }

    pub fn add_task(&mut self, task: &Task) {
        self.tasks.push(task.clone());
    }

    pub fn consume_task(&mut self) {
        let mut task = self.tasks.pop().unwrap();
        println!("consume task: {}", task.payload);
        Self::run_task(&mut task);
    }

    pub fn inspect_task(task: &Task) {
        println!("inspect task: {}", task.payload);
    }

    fn run_task(task: &mut Task) {
        task.mark_running();
        println!("run task: {}", task.payload);
    }
    pub fn find_task(tasks: &[Task], id: u64) -> Option<&Task> {
        tasks.iter().find(|t| t.id == id)
    }
}