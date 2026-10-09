use std::cell::{Cell, RefCell};
use std::rc::Rc;

pub type Handler = Rc<dyn Fn(&EventBus, &str)>;

/// A tiny single-threaded event bus. Handlers receive the bus itself so
/// they can react to an event by subscribing more handlers or publishing
/// follow-up events.
#[derive(Default)]
pub struct EventBus {
    handlers: RefCell<Vec<Handler>>,
    delivered: Cell<usize>,
}

impl EventBus {
    pub fn new() -> Self {
        Self::default()
    }

    pub fn subscribe<F>(&self, handler: F)
    where
        F: Fn(&EventBus, &str) + 'static,
    {
        self.handlers.borrow_mut().push(Rc::new(handler));
    }

    /// Delivers `event` to every handler registered when publishing starts.
    pub fn publish(&self, event: &str) {
        let handlers: Vec<Handler> = self.handlers.borrow().iter().cloned().collect();

        for handler in handlers {
            handler.as_ref()(self, event);
            self.delivered.set(self.delivered.get() + 1);
        }
    }

    pub fn handler_count(&self) -> usize {
        self.handlers.borrow().len()
    }

    pub fn delivered(&self) -> usize {
        self.delivered.get()
    }
}
