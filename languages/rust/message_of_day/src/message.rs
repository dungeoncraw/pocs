use std::fmt::{self, Display};
pub use message_core::{Message, Mood, Weekday};
use crate::error::MessageError;

pub struct TextMessage {
    pub content: String,
}

impl Display for TextMessage {
    fn fmt(&self, formatter: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(formatter, "{}", self.content)
    }
}

pub trait MessageSource {
    type Item: Display;
    fn next_message(&mut self) -> Option<Self::Item>;
}

pub struct SingleMessageSource {
    pub message: Option<TextMessage>,
}

impl MessageSource for SingleMessageSource {
    type Item = TextMessage;
    fn next_message(&mut self) -> Option<TextMessage> {
        self.message.take()
    }
}

pub fn print_next<S: MessageSource>(source: &mut S) {
    match source.next_message() {
        Some(message) => println!("Message: {}", message),
        None => println!("No more messages"),
    }
}

pub fn validate_mood(mood: &Mood) -> Result<(), MessageError> {
    match mood {
        Mood::Happy | Mood::Sad | Mood::Angry => Ok(()),
        _ => Err(MessageError::InvalidMood),
    }
}

pub fn validate_day(day: u8) -> Result<(), MessageError> {
    match day {
        1..=7 => Ok(()),
        _ => Err(MessageError::InvalidDay),
    }
}

pub fn repository_find_message(
    _mood: &Mood,
    _day: u8,
) -> Result<Option<String>, sqlx::Error> {
    Ok(Some(
        "The road ahead looks strangely quiet.".to_string(),
    ))
}

pub fn get_message(mood: &Mood, day: u8) -> Result<String, MessageError> {
    validate_mood(mood)?;

    validate_day(day)?;

    let message = repository_find_message(mood, day)?;

    message.ok_or(MessageError::NotFound)
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn text_message_displays_content() {
        let message = TextMessage {
            content: String::from("hello"),
        };
        assert_eq!(message.to_string(), "hello");
    }

    #[test]
    fn single_message_source_yields_message_then_none() {
        let mut source = SingleMessageSource {
            message: Some(TextMessage {
                content: String::from("only once"),
            }),
        };
        let first = source.next_message();
        assert!(first.is_some());
        assert_eq!(first.unwrap().to_string(), "only once");
        assert!(source.next_message().is_none());
    }

    #[test]
    fn empty_single_message_source_returns_none() {
        let mut source = SingleMessageSource { message: None };
        assert!(source.next_message().is_none());
    }

    #[test]
    fn validate_mood_accepts_valid_moods() {
        assert!(validate_mood(&Mood::Happy).is_ok());
        assert!(validate_mood(&Mood::Sad).is_ok());
        assert!(validate_mood(&Mood::Angry).is_ok());
    }

    #[test]
    fn validate_mood_rejects_invalid_mood() {
        match validate_mood(&Mood::Excited) {
            Err(MessageError::InvalidMood) => (),
            _ => panic!("expected InvalidMood error"),
        }
    }

    #[test]
    fn validate_day_accepts_valid_days() {
        for day in 1..=7 {
            assert!(validate_day(day).is_ok());
        }
    }

    #[test]
    fn validate_day_rejects_invalid_days() {
        match validate_day(0) {
            Err(MessageError::InvalidDay) => (),
            _ => panic!("expected InvalidDay error for 0"),
        }
        match validate_day(8) {
            Err(MessageError::InvalidDay) => (),
            _ => panic!("expected InvalidDay error for 8"),
        }
    }

    #[test]
    fn repository_find_message_returns_message() {
        let res = repository_find_message(&Mood::Happy, 3);
        assert_eq!(
            res.unwrap(),
            Some("The road ahead looks strangely quiet.".to_string())
        );
    }

    #[test]
    fn get_message_success() {
        let msg = get_message(&Mood::Happy, 3);
        assert_eq!(
            msg.unwrap(),
            "The road ahead looks strangely quiet.".to_string()
        );
    }

    #[test]
    fn get_message_fails_on_invalid_mood() {
        match get_message(&Mood::Excited, 3) {
            Err(MessageError::InvalidMood) => (),
            _ => panic!("expected InvalidMood error"),
        }
    }

    #[test]
    fn get_message_fails_on_invalid_day() {
        match get_message(&Mood::Happy, 10) {
            Err(MessageError::InvalidDay) => (),
            _ => panic!("expected InvalidDay error"),
        }
    }

    #[test]
    fn core_struct_message_usage() {
        let msg = Message {
            id: 100,
            text: "Hello from core".to_string(),
            day_tags: vec![Weekday::Monday, Weekday::Friday],
            mood_tags: vec![Mood::Happy],
            weight: 5,
            times_served: 0,
        };
        assert_eq!(msg.id, 100);
        assert_eq!(msg.text, "Hello from core");
        assert_eq!(msg.day_tags, vec![Weekday::Monday, Weekday::Friday]);
        assert_eq!(msg.mood_tags, vec![Mood::Happy]);
        assert_eq!(msg.weight, 5);
        assert_eq!(msg.times_served, 0);
    }

    #[test]
    fn weekday_enum_usage() {
        let monday = Weekday::Monday;
        assert_eq!(monday.number(), 1);
        assert_eq!(monday.to_string(), "monday");
    }
}
