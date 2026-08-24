use std::{
    marker::PhantomPinned,
    pin::Pin,
    ptr::NonNull,
};

struct SelfRef {
    text: String,
    text_ptr: Option<NonNull<String>>,
    _pin: PhantomPinned,
}

impl SelfRef {
    fn new(text: &str) -> Self {
        Self {
            text: text.to_string(),
            text_ptr: None,
            _pin: PhantomPinned,
        }
    }

    fn init(self: Pin<&mut Self>) {
        let text_ptr = NonNull::from(&self.text);

        unsafe {
            self.get_unchecked_mut().text_ptr = Some(text_ptr);
        }
    }

    fn print(self: Pin<&Self>) {
        let current_ptr = NonNull::from(&self.text);
        let stored_ptr = self.text_ptr.unwrap();

        println!("Text: {}", self.text);
        println!("Current address: {:p}", current_ptr);
        println!("Stored address:  {:p}", stored_ptr);
        println!("Same address: {}", current_ptr == stored_ptr);
    }
}

fn main() {
    let mut value = Box::pin(SelfRef::new("hello"));

    value.as_mut().init();

    value.as_ref().print();

    let moved_pin = value;

    moved_pin.as_ref().print();

    // let inner = Pin::into_inner(moved_pin);
    // ERROR: `PhantomPinned` cannot be unpinned because `SelfRef` does not implement `Unpin`.
}