use std::fmt::{self, Display};

// Supertrait as a constraint on the types that implement the trait.
pub trait Message: Display {}

pub struct TextMessage {
    pub content: String,
}

impl Display for TextMessage {
    fn fmt(&self, formatter: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(formatter, "{}", self.content)
    }
}

impl Message for TextMessage {}

pub trait MessageSource {
    type Item: Message;
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
}
