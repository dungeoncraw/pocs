use std::fmt::{self, Display, Formatter};
use std::str::FromStr;
use serde::{Deserialize, Serialize};
use crate::error::MessageError;

#[derive(Debug, Clone, Copy, PartialEq, Eq, Hash, PartialOrd, Ord, Serialize, Deserialize)]
#[serde(rename_all = "lowercase")]
pub enum Weekday {
    Monday,
    Tuesday,
    Wednesday,
    Thursday,
    Friday,
    Saturday,
    Sunday,
}

impl Weekday {
    pub const ALL: [Weekday; 7] = [
        Weekday::Monday,
        Weekday::Tuesday,
        Weekday::Wednesday,
        Weekday::Thursday,
        Weekday::Friday,
        Weekday::Saturday,
        Weekday::Sunday,
    ];

    pub fn number(&self) -> u8 {
        match self {
            Weekday::Monday => 1,
            Weekday::Tuesday => 2,
            Weekday::Wednesday => 3,
            Weekday::Thursday => 4,
            Weekday::Friday => 5,
            Weekday::Saturday => 6,
            Weekday::Sunday => 7,
        }
    }

    pub fn as_str(&self) -> &'static str {
        match self {
            Weekday::Monday => "monday",
            Weekday::Tuesday => "tuesday",
            Weekday::Wednesday => "wednesday",
            Weekday::Thursday => "thursday",
            Weekday::Friday => "friday",
            Weekday::Saturday => "saturday",
            Weekday::Sunday => "sunday",
        }
    }
}

impl Display for Weekday {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        write!(f, "{}", self.as_str())
    }
}

impl TryFrom<u8> for Weekday {
    type Error = MessageError;

    fn try_from(value: u8) -> Result<Self, Self::Error> {
        match value {
            1 => Ok(Weekday::Monday),
            2 => Ok(Weekday::Tuesday),
            3 => Ok(Weekday::Wednesday),
            4 => Ok(Weekday::Thursday),
            5 => Ok(Weekday::Friday),
            6 => Ok(Weekday::Saturday),
            7 => Ok(Weekday::Sunday),
            _ => Err(MessageError::InvalidDay),
        }
    }
}

impl From<Weekday> for u8 {
    fn from(weekday: Weekday) -> Self {
        weekday.number()
    }
}

impl FromStr for Weekday {
    type Err = MessageError;

    fn from_str(value: &str) -> Result<Self, Self::Err> {
        match value.trim().to_ascii_lowercase().as_str() {
            "monday" | "mon" => Ok(Weekday::Monday),
            "tuesday" | "tue" | "tues" => Ok(Weekday::Tuesday),
            "wednesday" | "wed" => Ok(Weekday::Wednesday),
            "thursday" | "thu" | "thur" | "thurs" => Ok(Weekday::Thursday),
            "friday" | "fri" => Ok(Weekday::Friday),
            "saturday" | "sat" => Ok(Weekday::Saturday),
            "sunday" | "sun" => Ok(Weekday::Sunday),
            _ => Err(MessageError::InvalidDay),
        }
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn parse_valid_weekdays() {
        assert_eq!("monday".parse::<Weekday>().unwrap(), Weekday::Monday);
        assert_eq!("Tue".parse::<Weekday>().unwrap(), Weekday::Tuesday);
        assert_eq!("WEDNESDAY".parse::<Weekday>().unwrap(), Weekday::Wednesday);
        assert_eq!("thu".parse::<Weekday>().unwrap(), Weekday::Thursday);
        assert_eq!("Friday".parse::<Weekday>().unwrap(), Weekday::Friday);
        assert_eq!("sat".parse::<Weekday>().unwrap(), Weekday::Saturday);
        assert_eq!(" SUN ".parse::<Weekday>().unwrap(), Weekday::Sunday);
    }

    #[test]
    fn parse_invalid_weekday() {
        assert_eq!("funday".parse::<Weekday>().unwrap_err(), MessageError::InvalidDay);
    }

    #[test]
    fn u8_conversions() {
        for day_num in 1..=7 {
            let weekday = Weekday::try_from(day_num).unwrap();
            assert_eq!(weekday.number(), day_num);
            assert_eq!(u8::from(weekday), day_num);
        }
        assert_eq!(Weekday::try_from(0).unwrap_err(), MessageError::InvalidDay);
        assert_eq!(Weekday::try_from(8).unwrap_err(), MessageError::InvalidDay);
    }

    #[test]
    fn weekday_display() {
        assert_eq!(Weekday::Monday.to_string(), "monday");
        assert_eq!(Weekday::Friday.to_string(), "friday");
    }

    #[test]
    fn weekday_serde() {
        let serialized = serde_json::to_string(&Weekday::Monday).unwrap();
        assert_eq!(serialized, "\"monday\"");
        let deserialized: Weekday = serde_json::from_str(&serialized).unwrap();
        assert_eq!(deserialized, Weekday::Monday);
    }
}
