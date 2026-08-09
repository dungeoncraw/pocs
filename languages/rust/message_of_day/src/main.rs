use message_of_day::cache::Cache;
use message_of_day::describe::Describe;
use message_of_day::heuristic::{run_dynamic, run_generic, Heuristic, LengthHeuristic, QuestionHeuristic};
use message_of_day::message::{print_next, SingleMessageSource, TextMessage};
use message_of_day::predicate::run_predicate;
use message_of_day::product::Product;

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

    let cache = Cache::<String, String>::new();

    cache.insert(
        "language".to_string(),
        "Rust".to_string(),
    );

    cache.insert(
        "paradigm".to_string(),
        "Systems Programming".to_string(),
    );

    let key = "language".to_string();

    println!("{:?}", cache.get(&key));
    println!("entries: {}", cache.len());
}
