use std::sync::{Arc, Mutex};
use std::thread;


struct User {
    name: Arc<String>,
}

fn main() {
    let user = User {
        name: Arc::new(String::from("Alice")),
    };

    let handle = thread::spawn(move || {
        println!("{}", user.name);
    });

    handle.join().unwrap();

    let value = Arc::new(Mutex::new(0));

    let value2 = Arc::clone(&value);

    let handle2 = thread::spawn(move || {
        let mut number = value2.lock().unwrap();
        *number += 1;
    });

    handle2.join().unwrap();
    println!("{}", *value.lock().unwrap());
}