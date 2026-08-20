use std::fmt::{self, Display, Formatter};
use std::str::FromStr;
use serde::{Deserialize, Serialize};
use crate::error::MessageError;

#[derive(Debug, Clone, Copy, PartialEq, Eq, Hash, PartialOrd, Ord, Serialize, Deserialize)]
#[serde(rename_all = "lowercase")]
pub enum Mood {
    Happy,
    Sad,
    Angry,
    Excited,
}

impl Mood {
    pub const ALL: [Mood; 4] = [
        Mood::Happy,
        Mood::Sad,
        Mood::Angry,
        Mood::Excited,
    ];

    pub fn as_str(&self) -> &'static str {
        match self {
            Mood::Happy => "happy",
            Mood::Sad => "sad",
            Mood::Angry => "angry",
            Mood::Excited => "excited",
        }
    }
}

impl Display for Mood {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        write!(f, "{}", self.as_str())
    }
}

impl FromStr for Mood {
    type Err = MessageError;

    fn from_str(value: &str) -> Result<Self, Self::Err> {
        match value.trim().to_ascii_lowercase().as_str() {
            "happy" => Ok(Mood::Happy),
            "sad" => Ok(Mood::Sad),
            "angry" => Ok(Mood::Angry),
            "excited" => Ok(Mood::Excited),
            _ => Err(MessageError::InvalidMood),
        }
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn parse_valid_moods() {
        assert_eq!("happy".parse::<Mood>().unwrap(), Mood::Happy);
        assert_eq!("Sad".parse::<Mood>().unwrap(), Mood::Sad);
        assert_eq!("ANGRY".parse::<Mood>().unwrap(), Mood::Angry);
        assert_eq!(" excited ".parse::<Mood>().unwrap(), Mood::Excited);
    }

    #[test]
    fn parse_invalid_mood() {
        assert_eq!("unknown".parse::<Mood>().unwrap_err(), MessageError::InvalidMood);
    }

    #[test]
    fn mood_display() {
        assert_eq!(Mood::Happy.to_string(), "happy");
        assert_eq!(Mood::Sad.to_string(), "sad");
        assert_eq!(Mood::Angry.to_string(), "angry");
        assert_eq!(Mood::Excited.to_string(), "excited");
    }

    #[test]
    fn mood_serde() {
        let serialized = serde_json::to_string(&Mood::Happy).unwrap();
        assert_eq!(serialized, "\"happy\"");
        let deserialized: Mood = serde_json::from_str(&serialized).unwrap();
        assert_eq!(deserialized, Mood::Happy);
    }
}
