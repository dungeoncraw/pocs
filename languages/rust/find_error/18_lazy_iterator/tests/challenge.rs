use challenge_18_lazy_iterator::*;

fn rec(key: &str, value: i32) -> Record {
    Record {
        key: key.to_string(),
        value,
    }
}

#[test]
fn clean_input_is_fully_imported() {
    let imp = Importer::new();
    let out = imp.import(&["a=1", "b = 2", "c=3"]);
    assert_eq!(out, vec![rec("a", 1), rec("b", 2), rec("c", 3)]);
    assert_eq!(imp.seen(), 3);
    assert!(imp.rejected().is_empty());
}

#[test]
fn malformed_lines_are_skipped_not_fatal() {
    let imp = Importer::new();
    let out = imp.import(&["a=1", "oops", "b=2", "c=three", "d=4"]);
    assert_eq!(out, vec![rec("a", 1), rec("b", 2), rec("d", 4)]);
}

#[test]
fn malformed_lines_are_reported() {
    let imp = Importer::new();
    imp.import(&["a=1", "oops", "b=2", "c=three"]);
    assert_eq!(imp.rejected(), vec!["oops", "c=three"]);
    assert_eq!(imp.seen(), 4);
}

#[test]
fn counters_accumulate_over_multiple_imports() {
    let imp = Importer::new();
    imp.import(&["x=1", "bad"]);
    imp.import(&["y=2", "worse"]);
    assert_eq!(imp.seen(), 4);
    assert_eq!(imp.rejected(), vec!["bad", "worse"]);
}
