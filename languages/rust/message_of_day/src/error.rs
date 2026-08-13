use thiserror::Error;

#[derive(Debug, Error)]
pub enum MessageError {
    #[error("message not found")]
    NotFound,

    #[error("invalid mood")]
    InvalidMood,

    #[error("invalid day")]
    InvalidDay,

    #[error("database error: {0}")]
    DbError(#[from] sqlx::Error),
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn not_found_display() {
        let err = MessageError::NotFound;
        assert_eq!(err.to_string(), "message not found");
    }

    #[test]
    fn invalid_mood_display() {
        let err = MessageError::InvalidMood;
        assert_eq!(err.to_string(), "invalid mood");
    }

    #[test]
    fn invalid_day_display() {
        let err = MessageError::InvalidDay;
        assert_eq!(err.to_string(), "invalid day");
    }

    #[test]
    fn db_error_display() {
        let sqlx_err = sqlx::Error::RowNotFound;
        let err = MessageError::from(sqlx_err);
        assert_eq!(
            err.to_string(),
            "database error: no rows returned by a query that expected to return at least one row"
        );
    }

    #[test]
    fn debug_output_is_available() {
        let err = MessageError::NotFound;
        let dbg = format!("{:?}", err);
        assert!(dbg.contains("NotFound"));
    }

    #[test]
    fn implements_std_error_trait() {
        fn assert_error<E: std::error::Error>(_: &E) {}
        let err = MessageError::InvalidDay;
        assert_error(&err);
    }
}