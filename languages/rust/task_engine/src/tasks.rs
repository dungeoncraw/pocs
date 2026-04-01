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
    pub fn new(id: u64, payload: String) -> Self {
        Self {
            id,
            payload,
            status: TaskStatus::Pending,
        }
    }
}