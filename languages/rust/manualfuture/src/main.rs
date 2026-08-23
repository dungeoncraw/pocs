use std::{
    future::Future,
    pin::Pin,
    sync::{Arc, Mutex},
    task::{Context, Poll, Wake, Waker},
    thread,
    time::{Duration, Instant}
};

struct SharedState {
    completed: bool,
    waker: Option<Waker>,
}

struct Delay {
    shared: Arc<Mutex<SharedState>>,
}

impl Delay {
    fn new(duration: Duration) -> Self {
        let deadline = Instant::now() + duration;

        let shared = Arc::new(Mutex::new(SharedState {
            completed: false,
            waker: None,
        }));

        let thread_shared = Arc::clone(&shared);

        thread::spawn(move || {
            let remaining =
                deadline.saturating_duration_since(Instant::now());

            thread::sleep(remaining);

            let waker = {
                let mut state = thread_shared.lock().unwrap();

                println!("[worker] delay finished");

                state.completed = true;

                state.waker.take()
            };

            if let Some(waker) = waker {
                println!("[worker] waking up the wake()");

                waker.wake();
            }
        });

        Self { shared }
    }
}

impl Future for Delay {
    type Output = &'static str;

    fn poll(
        self: Pin<&mut Self>,
        cx: &mut Context<'_>,
    ) -> Poll<Self::Output> {
        println!("[Delay::poll] starting");

        let mut state = self.shared.lock().unwrap();

        if state.completed {
            println!("[Delay::poll] ready");

            Poll::Ready("Delay done")
        } else {
            println!("[Delay::poll] pending");

            state.waker = Some(cx.waker().clone());

            Poll::Pending
        }
    }
}

struct ThreadWaker {
    thread: thread::Thread,
}

impl Wake for ThreadWaker {
    fn wake(self: Arc<Self>) {
        println!("[Waker] waking the executor");

        self.thread.unpark();
    }

    fn wake_by_ref(self: &Arc<Self>) {
        println!("[Waker] waking the executor");

        self.thread.unpark();
    }
}

fn block_on<F: Future>(future: F) -> F::Output {
    let waker = Waker::from(Arc::new(ThreadWaker {
        thread: thread::current(),
    }));

    let mut context = Context::from_waker(&waker);

    let mut future = Box::pin(future);

    loop {
        println!("[executor] poll");

        match future.as_mut().poll(&mut context) {
            Poll::Ready(value) => {
                println!("[executor] Future done");

                return value;
            }

            Poll::Pending => {
                println!("[executor] Future  Pending");
                println!("[executor] waiting thread");

                thread::park();

                println!("[executor] ready!");
            }
        }
    }
}

fn main() {
    let start = Instant::now();

    println!("Delay");

    let delay = Delay::new(Duration::from_secs(2));

    println!("Running Delay");

    let result = block_on(delay);

    println!("Result: {result}");
    println!("Time: {:?}", start.elapsed());
}