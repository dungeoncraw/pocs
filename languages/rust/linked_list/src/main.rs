use crate::lib::first::List;
use crate::lib::miri_example::miri_test;

mod lib;

fn main() {
    let mut list: List = List::new();
    list.push(1);
    list.push(2);
    list.push(3);
    list.push(4);
    list.pop();
    // testing of MIRI compiler
    miri_test();
    println!("{:?}", list);
}