#[derive(Debug, PartialEq, Clone)]
pub enum TaskStatus {
    Pending,
    Running,
    Done,
}

#[derive(Debug, Clone)]
pub struct Task {
    pub id: u64,
    pub payload: String,
    pub status: TaskStatus,
}

impl Task {
    pub fn new(id: u64, payload: impl Into<String>) -> Self {
        Self {
            id,
            payload: payload.into(),
            status: TaskStatus::Pending,
        }
    }
    pub fn mark_running(&mut self) {
        self.status = TaskStatus::Running;
    }

    pub fn mark_done(&mut self) {
        self.status = TaskStatus::Done;
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn new_task_starts_pending_with_expected_fields() {
        let task = Task::new(12, "some payload");
        assert_eq!(task.id, 12);
        assert_eq!(task.payload, "some payload");
        assert_eq!(task.status, TaskStatus::Pending);
    }

    #[test]
    fn mark_running_updates_status_to_running() {
        let mut task = Task::new(12, "some payload");
        task.mark_running();
        assert_eq!(task.status, TaskStatus::Running);
    }
    #[test]
    fn mark_done_updates_status_to_done() {
        let mut task = Task::new(1, "finish me");

        task.mark_done();

        assert_eq!(task.status, TaskStatus::Done);
    }

    #[test]
    fn cloned_task_preserves_fields() {
        let mut task = Task::new(7, "clone me");
        task.mark_running();

        let cloned = task.clone();

        assert_eq!(cloned.id, task.id);
        assert_eq!(cloned.payload, task.payload);
        assert_eq!(cloned.status, task.status);
    }
}