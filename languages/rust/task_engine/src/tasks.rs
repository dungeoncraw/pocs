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