use thiserror::Error;

#[derive(Debug, Error, Clone, PartialEq, Eq)]
pub enum MessageError {
    #[error("message not found")]
    NotFound,

    #[error("invalid mood")]
    InvalidMood,

    #[error("invalid day")]
    InvalidDay,
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_error_display() {
        assert_eq!(MessageError::NotFound.to_string(), "message not found");
        assert_eq!(MessageError::InvalidMood.to_string(), "invalid mood");
        assert_eq!(MessageError::InvalidDay.to_string(), "invalid day");
    }

    #[test]
    fn test_error_implements_std_error() {
        fn assert_error<E: std::error::Error>(_: &E) {}
        let err = MessageError::InvalidMood;
        assert_error(&err);
    }
}
