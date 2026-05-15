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

#[cfg(test)]
mod tests {
    use super::*;
    use crate::tasks::TaskStatus;

    #[test]
    fn new_orchestrator_starts_empty() {
        let orchestrator = Orchestrator::new();

        assert!(orchestrator.tasks.is_empty());
    }

    #[test]
    fn add_task_stores_a_clone() {
        let task = Task::new(1, "original");
        let mut orchestrator = Orchestrator::new();

        orchestrator.add_task(&task);

        assert_eq!(orchestrator.tasks.len(), 1);
        assert_eq!(orchestrator.tasks[0].id, 1);
        assert_eq!(orchestrator.tasks[0].payload, "original");
        assert_eq!(orchestrator.tasks[0].status, TaskStatus::Pending);
    }

    #[test]
    fn find_task_returns_matching_task() {
        let tasks = vec![
            Task::new(1, "first"),
            Task::new(2, "second"),
            Task::new(3, "third"),
        ];

        let found = Orchestrator::find_task(&tasks, 2);

        assert!(found.is_some());
        assert_eq!(found.unwrap().payload, "second");
    }

    #[test]
    fn find_task_returns_none_when_id_is_missing() {
        let tasks = vec![
            Task::new(1, "first"),
            Task::new(2, "second"),
        ];

        let found = Orchestrator::find_task(&tasks, 99);

        assert!(found.is_none());
    }

    #[test]
    fn consume_task_removes_last_added_task() {
        let mut orchestrator = Orchestrator::new();
        orchestrator.add_task(&Task::new(1, "first"));
        orchestrator.add_task(&Task::new(2, "second"));

        orchestrator.consume_task();

        assert_eq!(orchestrator.tasks.len(), 1);
        assert_eq!(orchestrator.tasks[0].id, 1);
    }
}