use crossbeam::queue::SegQueue;
use std::collections::VecDeque;
use std::sync::{Arc, Mutex};
use std::thread;


fn mutex_queue() {
    let queue = Arc::new(Mutex::new(VecDeque::new()));

    let handles: Vec<_> = (0..4)
        .map(|worker| {
            let queue = Arc::clone(&queue);

            thread::spawn(move || {
                for value in 0..5 {
                    queue
                        .lock()
                        .unwrap()
                        .push_back((worker, value));
                }
            })
        })
        .collect();

    for handle in handles {
        handle.join().unwrap();
    }

    println!("Mutex queue:");

    while let Some(value) = queue.lock().unwrap().pop_front() {
        println!("{value:?}");
    }
}
fn seg_queue() {
    let queue = Arc::new(SegQueue::new());

    let handles: Vec<_> = (0..4)
        .map(|worker| {
            let queue = Arc::clone(&queue);

            thread::spawn(move || {
                for value in 0..5 {
                    queue.push((worker, value));
                }
            })
        })
        .collect();

    for handle in handles {
        handle.join().unwrap();
    }

    println!("SegQueue:");

    while let Some(value) = queue.pop() {
        println!("{value:?}");
    }
}

fn main() {
    mutex_queue();
    seg_queue();
}