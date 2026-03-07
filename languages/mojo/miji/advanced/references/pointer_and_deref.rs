fn main() {
    let val = 42;
    let ptr = &val;
    println!("The address of val is {:p}", ptr);
    println!("The value at that address is {}", *ptr);
}