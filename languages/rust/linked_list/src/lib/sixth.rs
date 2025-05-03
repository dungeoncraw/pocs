use std::marker::PhantomData;
use std::ptr::NonNull;

pub struct LinkedList<T> {
    front : Link<T>,
    back: Link<T>,
    len : usize,
    // Ok this is really deep, PhantomData<T> ensure that generic type T, even if struct doesn't store T
    // https://doc.rust-lang.org/std/marker/struct.PhantomData.html
    _boo: PhantomData<T>,
}

type Link<T> = Option<NonNull<Node<T>>>;

struct Node<T> {
    front : Link<T>,
    back : Link<T>,
    element : T,
}