use std::fmt::{self, Display, Formatter};

trait Describe {
    fn describe(&self) -> String;
}


impl<T> Describe for T
where
    T: Display + ?Sized,
{
    fn describe(&self) -> String {
        format!("Value to display: {}", self)
    }
}

struct Product {
    name: String,
    price_in_cents: u32,
}

impl Display for Product {
    fn fmt(&self, formatter: &mut Formatter<'_>) -> fmt::Result {
        write!(
            formatter,
            "{} - US$ {:.2}",
            self.name,
            self.price_in_cents as f64 / 100.0
        )
    }
}
// Supertrait as a constraint on the types that implement the trait.
trait Message: Display {}

struct TextMessage {
    content: String,
}
impl Display for TextMessage {
    fn fmt(&self, formatter: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(formatter, "{}", self.content)
    }
}
impl Message for TextMessage {}

trait MessageSource {
    type Item: Message;
    fn next_message(&mut self) -> Option<Self::Item>;
}
struct SingleMessageSource {
    message: Option<TextMessage>
}
impl MessageSource for SingleMessageSource {
    type Item = TextMessage;
    fn next_message(&mut self) -> Option<TextMessage> {
        self.message.take()
    }
}

trait Heuristic {
    fn score(&self, text: &str) -> i32;
}

struct LengthHeuristic;

impl Heuristic for LengthHeuristic {
    fn score(&self, text: &str) -> i32 {
        text.len() as i32
    }
}

struct QuestionHeuristic;

impl Heuristic for QuestionHeuristic {
    fn score(&self, text: &str) -> i32 {
        if text.contains('?') { 10 } else { 0 }
    }
}

// Static dispatch: Rust knows the concrete type at compile time.
fn run_generic<T: Heuristic>(heuristic: &T, text: &str) {
    println!("Generic score: {}", heuristic.score(text));
}

// Dynamic dispatch: Rust chooses the implementation at runtime.
fn run_dynamic(heuristic: &dyn Heuristic, text: &str) {
    println!("Dynamic score: {}", heuristic.score(text));
}

fn print_next<S: MessageSource>(source: &mut S) {
    match source.next_message() {
        Some(message) => println!("Message: {}", message),
        None => println!("No more messages"),
    }
}

fn run_predicate<F>(predicate: F)
where
    F: for<'a> Fn(&'a str) -> bool,
{
    let long_lived = String::from("Rust");

    println!("{}", predicate(&long_lived));

    {
        let short_lived = String::from("C");

        println!("{}", predicate(&short_lived));
    }

    {
        let another = String::from("Haskell");

        println!("{}", predicate(&another));
    }
}

fn main() {
    let length = LengthHeuristic;
    let question = QuestionHeuristic;

    run_generic(&length, "Hello?");
    run_generic(&question, "Hello?");

    let heuristics: Vec<Box<dyn Heuristic>> = vec![
        Box::new(LengthHeuristic),
        Box::new(QuestionHeuristic),
    ];

    for heuristic in heuristics {
        run_dynamic(heuristic.as_ref(), "Hello?");
    }
    let mut source = SingleMessageSource {
        message: Some(TextMessage {
            content: String::from("Hello from the repository!"),
        }),
    };
    print_next(&mut source);
    print_next(&mut source);

    let number = 42;
    let text = "Rust";
    let owned_text = String::from("Bridgeway");
    let active = true;

    let product = Product {
        name: String::from("Healing Potion"),
        price_in_cents: 1250,
    };

    println!("{}", number.describe());
    println!("{}", text.describe());
    println!("{}", owned_text.describe());
    println!("{}", active.describe());
    println!("{}", product.describe());

    println!();
    println!("ToString from stdlib:");
    println!("{}", product.to_string());

    run_predicate(|s| s.len() > 3);
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn describe_formats_integer() {
        assert_eq!(42.describe(), "Value to display: 42");
    }

    #[test]
    fn describe_formats_str_slice() {
        assert_eq!("Rust".describe(), "Value to display: Rust");
    }

    #[test]
    fn describe_formats_owned_string() {
        let owned = String::from("Bridgeway");
        assert_eq!(owned.describe(), "Value to display: Bridgeway");
    }

    #[test]
    fn describe_formats_bool() {
        assert_eq!(true.describe(), "Value to display: true");
        assert_eq!(false.describe(), "Value to display: false");
    }

    #[test]
    fn product_display_formats_price_with_two_decimals() {
        let product = Product {
            name: String::from("Healing Potion"),
            price_in_cents: 1250,
        };
        assert_eq!(product.to_string(), "Healing Potion - US$ 12.50");
    }

    #[test]
    fn product_display_handles_zero_price() {
        let product = Product {
            name: String::from("Free Sample"),
            price_in_cents: 0,
        };
        assert_eq!(product.to_string(), "Free Sample - US$ 0.00");
    }

    #[test]
    fn product_describe_uses_display_impl() {
        let product = Product {
            name: String::from("Sword"),
            price_in_cents: 999,
        };
        assert_eq!(product.describe(), "Value to display: Sword - US$ 9.99");
    }

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
    fn length_heuristic_scores_string_length() {
        let heuristic = LengthHeuristic;
        assert_eq!(heuristic.score(""), 0);
        assert_eq!(heuristic.score("Hello?"), 6);
    }

    #[test]
    fn question_heuristic_scores_ten_when_question_mark_present() {
        let heuristic = QuestionHeuristic;
        assert_eq!(heuristic.score("Hello?"), 10);
    }

    #[test]
    fn question_heuristic_scores_zero_without_question_mark() {
        let heuristic = QuestionHeuristic;
        assert_eq!(heuristic.score("Hello"), 0);
    }

    #[test]
    fn heuristic_works_via_dynamic_dispatch() {
        let heuristics: Vec<Box<dyn Heuristic>> = vec![
            Box::new(LengthHeuristic),
            Box::new(QuestionHeuristic),
        ];
        let scores: Vec<i32> = heuristics.iter().map(|h| h.score("Hi?")).collect();
        assert_eq!(scores, vec![3, 10]);
    }
}