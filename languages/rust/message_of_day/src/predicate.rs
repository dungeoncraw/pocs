pub fn run_predicate<F>(predicate: F)
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
