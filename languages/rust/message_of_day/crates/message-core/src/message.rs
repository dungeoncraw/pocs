use std::fmt::{self, Display, Formatter};
use serde::{Deserialize, Serialize};
use crate::mood::Mood;
use crate::weekday::Weekday;

#[derive(Debug, Clone, PartialEq, Eq, Serialize, Deserialize)]
pub struct Message {
    pub id: u64,
    pub text: String,
    pub day_tags: Vec<Weekday>,
    pub mood_tags: Vec<Mood>,
    pub weight: u32,
    pub times_served: u32,
}

impl Message {
    pub fn new(
        id: u64,
        text: impl Into<String>,
        day_tags: Vec<Weekday>,
        mood_tags: Vec<Mood>,
        weight: u32,
        times_served: u32,
    ) -> Self {
        Self {
            id,
            text: text.into(),
            day_tags,
            mood_tags,
            weight,
            times_served,
        }
    }
    /// Returns true if the message matches the given day.
    /// Example:
    /// ```
    /// use message_core::Weekday;
    /// use message_core::{Message, Mood};
    /// let specific_msg = Message::new(
    ///             1,
    ///             "Friday vibes",
    ///             vec![Weekday::Friday],
    ///             vec![Mood::Excited],
    ///             1,
    ///             0,
    ///         );
    /// println!("{}", specific_msg.matches_day(&Weekday::Friday));
    /// ```
    pub fn matches_day(&self, day: &Weekday) -> bool {
        self.day_tags.is_empty() || self.day_tags.contains(day)
    }
    /// Returns true if the mood matches the given mood
    /// Example:
    /// ```
    /// use message_core::Weekday;
    /// use message_core::{Message, Mood};
    /// let specific_msg = Message::new(
    ///             1,
    ///             "Friday Sadness",
    ///             vec![Weekday::Friday],
    ///             vec![Mood::Happy],
    ///             1,
    ///             0,
    ///         );
    /// println!("{}", specific_msg.matches_mood(&Mood::Excited));
    /// ```
    pub fn matches_mood(&self, mood: &Mood) -> bool {
        self.mood_tags.contains(mood)
    }

    pub fn is_available_on(&self, day: &Weekday, mood: &Mood) -> bool {
        self.matches_day(day) && self.matches_mood(mood)
    }

    pub fn increment_times_served(&mut self) {
        self.times_served = self.times_served.saturating_add(1);
    }
}

impl Display for Message {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        write!(f, "{}", self.text)
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn message_creation_and_fields() {
        let msg = Message {
            id: 1,
            text: "Today is a great day!".to_string(),
            day_tags: vec![Weekday::Monday, Weekday::Friday],
            mood_tags: vec![Mood::Happy, Mood::Excited],
            weight: 10,
            times_served: 0,
        };

        assert_eq!(msg.id, 1);
        assert_eq!(msg.text, "Today is a great day!");
        assert_eq!(msg.day_tags, vec![Weekday::Monday, Weekday::Friday]);
        assert_eq!(msg.mood_tags, vec![Mood::Happy, Mood::Excited]);
        assert_eq!(msg.weight, 10);
        assert_eq!(msg.times_served, 0);
    }

    #[test]
    fn message_new_constructor() {
        let msg = Message::new(
            42,
            "Keep pushing forward.",
            vec![Weekday::Wednesday],
            vec![Mood::Sad],
            5,
            2,
        );

        assert_eq!(msg.id, 42);
        assert_eq!(msg.text, "Keep pushing forward.");
        assert_eq!(msg.day_tags, vec![Weekday::Wednesday]);
        assert_eq!(msg.mood_tags, vec![Mood::Sad]);
        assert_eq!(msg.weight, 5);
        assert_eq!(msg.times_served, 2);
    }

    #[test]
    fn message_matching() {
        let specific_msg = Message::new(
            1,
            "Friday vibes",
            vec![Weekday::Friday],
            vec![Mood::Excited],
            1,
            0,
        );

        assert!(specific_msg.matches_day(&Weekday::Friday));
        assert!(!specific_msg.matches_day(&Weekday::Monday));
        assert!(specific_msg.matches_mood(&Mood::Excited));
        assert!(!specific_msg.matches_mood(&Mood::Sad));
        assert!(specific_msg.is_available_on(&Weekday::Friday, &Mood::Excited));
        assert!(!specific_msg.is_available_on(&Weekday::Friday, &Mood::Sad));

        let universal_msg = Message::new(
            2,
            "Always true",
            vec![],
            vec![],
            1,
            0,
        );

        assert!(universal_msg.matches_day(&Weekday::Tuesday));
        assert!(!universal_msg.matches_mood(&Mood::Angry));
        assert!(!universal_msg.is_available_on(&Weekday::Tuesday, &Mood::Angry));
    }

    #[test]
    fn message_increment_times_served() {
        let mut msg = Message::new(1, "Test", vec![], vec![], 1, 0);
        msg.increment_times_served();
        assert_eq!(msg.times_served, 1);
    }

    #[test]
    fn message_display() {
        let msg = Message::new(1, "Hello world", vec![], vec![], 1, 0);
        assert_eq!(msg.to_string(), "Hello world");
    }

    #[test]
    fn message_serde() {
        let msg = Message::new(
            1,
            "Stay positive",
            vec![Weekday::Monday],
            vec![Mood::Happy],
            10,
            3,
        );

        let json = serde_json::to_string(&msg).unwrap();
        let deserialized: Message = serde_json::from_str(&json).unwrap();
        assert_eq!(msg, deserialized);
    }
}
