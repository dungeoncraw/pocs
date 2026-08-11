use thiserror::Error;

#[derive(Debug, Error)]
pub enum MessageError {
    #[error("message {0} not found")]
    NotFound(i64),

    #[error("invalid mood: {0}")]
    InvalidMood(String),

    #[error("invalid day: {0}")]
    InvalidDay(u8),
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn not_found_display_formats_id() {
        let err = MessageError::NotFound(42);
        assert_eq!(err.to_string(), "message 42 not found");
    }

    #[test]
    fn not_found_display_with_negative_id() {
        let err = MessageError::NotFound(-1);
        assert_eq!(err.to_string(), "message -1 not found");
    }

    #[test]
    fn invalid_mood_display_formats_value() {
        let err = MessageError::InvalidMood("grumpy".to_string());
        assert_eq!(err.to_string(), "invalid mood: grumpy");
    }

    #[test]
    fn invalid_mood_display_with_empty_string() {
        let err = MessageError::InvalidMood(String::new());
        assert_eq!(err.to_string(), "invalid mood: ");
    }

    #[test]
    fn invalid_day_display_formats_value() {
        let err = MessageError::InvalidDay(8);
        assert_eq!(err.to_string(), "invalid day: 8");
    }

    #[test]
    fn invalid_day_display_with_zero() {
        let err = MessageError::InvalidDay(0);
        assert_eq!(err.to_string(), "invalid day: 0");
    }

    #[test]
    fn debug_output_is_available() {
        let err = MessageError::NotFound(7);
        let dbg = format!("{:?}", err);
        assert!(dbg.contains("NotFound"));
        assert!(dbg.contains("7"));
    }

    #[test]
    fn implements_std_error_trait() {
        fn assert_error<E: std::error::Error>(_: &E) {}
        let err = MessageError::InvalidDay(3);
        assert_error(&err);
    }
}