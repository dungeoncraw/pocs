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
}