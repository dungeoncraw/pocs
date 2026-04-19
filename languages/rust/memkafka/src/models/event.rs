use serde::{Deserialize, Serialize};

#[derive(Debug, Deserialize, Serialize)]
pub struct Event {
    pub sub_id: String,
    pub name: String,
    pub description: String,
    pub date: String,
}

#[derive(Serialize)]
pub struct EventResponse {
    pub status: &'static str,
}