use challenge_17_return_ref_to_local::*;

#[test]
fn slugify_lowercases_and_dashes() {
    let s = slugify("  Hello   Rusty World ");
    assert_eq!(&*s, "hello-rusty-world");
}

#[test]
fn join_path_uses_single_slash() {
    let p = join_path("/var/log/", "app.log");
    assert_eq!(&*p, "/var/log/app.log");
    let q = join_path("src", "lib.rs");
    assert_eq!(&*q, "src/lib.rs");
}

#[test]
fn ticket_label_is_zero_padded() {
    let t = Ticket::new(42, "Fix login");
    let label = t.label();
    assert_eq!(&*label, "#0042 Fix login");
}

#[test]
fn results_survive_after_inputs_are_gone() {
    let slug = {
        let title = String::from("Temporary Title");
        slugify(&title)
    };
    assert_eq!(&*slug, "temporary-title");
}
