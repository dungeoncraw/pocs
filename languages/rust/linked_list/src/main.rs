use crate::lib::first::List;

mod lib;

fn main() {
    let mut list: List = List::new();
    list.push(1);
    list.push(2);
    list.push(3);
    list.push(4);
    list.pop();
    println!("{:?}", list);
}