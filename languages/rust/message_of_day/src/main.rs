use std::fmt::{self, Display};

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
}