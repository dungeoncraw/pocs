pub trait Heuristic {
    fn score(&self, text: &str) -> i32;
}

pub struct LengthHeuristic;

impl Heuristic for LengthHeuristic {
    fn score(&self, text: &str) -> i32 {
        text.len() as i32
    }
}

pub struct QuestionHeuristic;

impl Heuristic for QuestionHeuristic {
    fn score(&self, text: &str) -> i32 {
        if text.contains('?') { 10 } else { 0 }
    }
}

// Static dispatch: Rust knows the concrete type at compile time.
pub fn run_generic<T: Heuristic>(heuristic: &T, text: &str) {
    println!("Generic score: {}", heuristic.score(text));
}

// Dynamic dispatch: Rust chooses the implementation at runtime.
pub fn run_dynamic(heuristic: &dyn Heuristic, text: &str) {
    println!("Dynamic score: {}", heuristic.score(text));
}

#[cfg(test)]
mod tests {
    use super::*;

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
