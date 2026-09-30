/// Turns a title into a URL-friendly slug: "Hello  World" -> "hello-world".
pub fn slugify(title: &str) -> &str {
    let slug = title
        .split_whitespace()
        .map(|w| w.to_lowercase())
        .collect::<Vec<_>>()
        .join("-");
    &slug
}

/// Joins a directory and a file name with a single slash.
pub fn join_path(dir: &str, file: &str) -> &str {
    let joined = format!("{}/{}", dir.trim_end_matches('/'), file);
    joined.as_str()
}

pub struct Ticket {
    pub id: u32,
    pub title: String,
}

impl Ticket {
    pub fn new(id: u32, title: &str) -> Self {
        Ticket {
            id,
            title: title.to_string(),
        }
    }

    /// Human readable label such as "#0042 Fix login".
    pub fn label(&self) -> &str {
        let label = format!("#{:04} {}", self.id, self.title);
        &label
    }
}
