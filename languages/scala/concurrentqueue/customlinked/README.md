### Custom ConcurrentLinkedQueue Implementation

A lock-free, thread-safe, unbounded queue implemented in Scala, inspired by `java.util.concurrent.ConcurrentLinkedQueue`.

---

### How It Works

The queue is based on the **Michael & Scott non-blocking queue algorithm**. It uses a singly-linked list of nodes, with two atomic references — `head` and `tail` — pointing to the front and back of the list respectively. All mutations are performed using **Compare-And-Swap (CAS)** operations, making the queue lock-free and safe for concurrent access by multiple threads without synchronization primitives like locks or monitors.

#### Core Data Structure

- **Node**: Each node holds an optional value and an `AtomicReference` to the next node.
- **Sentinel Node**: The queue is initialized with a dummy (sentinel) node. Both `head` and `tail` point to this sentinel at creation. This simplifies edge cases when the queue is empty.

#### Key Operations

##### `offer(value: T): Boolean` — Enqueue
1. Create a new node with the given value.
2. Read the current `tail` node and its `next` pointer.
3. If `tail.next` is `null`, attempt a CAS to set `tail.next` to the new node.
   - On success, attempt a CAS to advance `tail` to the new node (this is a "helping" step — if it fails, another thread already advanced it).
   - On failure, retry from step 2.
4. If `tail.next` is not `null`, the tail is lagging — help advance it via CAS, then retry.

##### `poll(): Option[T]` — Dequeue
1. Read the current `head` node.
2. Read `head.next`.
3. If `head.next` is `null`, the queue is empty — return `None`.
4. Otherwise, attempt a CAS to swing `head` to `head.next`.
   - On success, return the value from the old `head.next` and null out its reference (to aid GC).
   - On failure, another thread dequeued first — retry from step 1.

##### `peek(): Option[T]` — Peek
1. Read `head.next`.
2. If `null`, return `None`; otherwise return the value without removing it.

##### `isEmpty: Boolean`
Returns `true` if `head.next` is `null`.

##### `size: Int`
Traverses the linked list from `head.next` counting nodes. Note: this is **not** an O(1) operation and the result is a snapshot that may be immediately stale in a concurrent context.

---

### Design Details

| Aspect | Detail |
|---|---|
| **Thread Safety** | Achieved entirely via CAS (`AtomicReference`); no locks. |
| **Progress Guarantee** | Lock-free — at least one thread always makes progress. |
| **Memory** | Unbounded; grows as elements are added. Dequeued nodes become eligible for GC. |
| **Ordering** | FIFO (first-in, first-out). |
| **Nulls** | Not permitted as element values (following `ConcurrentLinkedQueue` convention). |
| **Sentinel Node** | A dummy head node avoids special-casing for empty queue operations. |
| **Tail Lagging** | The `tail` pointer may lag behind the actual last node by one step. Threads cooperatively advance it ("helping"), which is key to the algorithm's correctness. |

---

### What is CAS (Compare-And-Swap)?

**Compare-And-Swap (CAS)** is a hardware-level atomic instruction used to achieve lock-free synchronization. It is the fundamental building block behind this queue's thread safety.

#### How CAS Works

CAS operates on a memory location and takes three arguments:

1. **Address** — the memory location (variable) to update.
2. **Expected value** — the value you believe is currently stored there.
3. **New value** — the value you want to write.

The operation atomically does the following:
- Reads the current value at the address.
- If it matches the expected value, it writes the new value and returns `true` (success).
- If it does **not** match, it does nothing and returns `false` (failure), meaning another thread modified the value first.

Because the CPU executes this as a single atomic instruction, no other thread can intervene between the read and the write.

#### Pseudocode

```
function CAS(address, expectedValue, newValue) -> Boolean:
    // --- executed atomically by the CPU ---
    if *address == expectedValue then
        *address = newValue
        return true
    else
        return false
    end if
```

#### CAS Retry Loop (Typical Usage Pattern)

Since CAS can fail when another thread modifies the value concurrently, it is almost always used inside a retry loop:

```
function atomicIncrement(counter):
    loop forever:
        oldValue = read(counter)
        newValue = oldValue + 1
        if CAS(counter, oldValue, newValue) then
            return  // success
        end if
        // CAS failed — another thread changed counter; retry
    end loop
```

#### CAS in This Queue

In the `ConcurrentLinkedQueue`, CAS is used via Java's `AtomicReference.compareAndSet()`. Key CAS sites include:

| Operation | CAS Target | Purpose |
|---|---|---|
| `offer` | `tail.next` | Atomically link a new node at the end of the list. |
| `offer` | `tail` | Advance the tail pointer to the newly added node. |
| `poll` | `head` | Swing the head pointer to the next node (dequeue). |

If a CAS fails, the thread simply retries — this is what makes the algorithm **lock-free**: threads never block, they just retry until they succeed.

#### Why CAS Instead of Locks?

| Locks | CAS |
|---|---|
| A thread acquires a lock; others **block** (wait). | No blocking — threads retry on conflict. |
| Risk of deadlock, priority inversion. | No deadlocks possible. |
| OS-level context switches on contention. | Spins in user-space; cheaper under low contention. |
| Simpler to reason about correctness. | Harder to design, but higher throughput. |

---

### Concurrency Guarantees

- **Linearizability**: Each `offer` and `poll` has a single linearization point (the successful CAS), making operations appear atomic.
- **ABA Safety**: Scala's garbage-collected runtime prevents the ABA problem that plagues CAS-based algorithms in unmanaged languages, since nodes are not reused after being dequeued.
- **No Starvation of Helpers**: If a thread observes a lagging tail, it helps advance it before retrying its own operation, ensuring system-wide progress.

---

### File Structure

```
src/main/scala/
  main.scala                  — Entry point / demo usage
  ConcurrentLinkedQueue.scala — Custom queue implementation (to be created)
```

---

### References

- Michael, M. M., & Scott, M. L. (1996). *Simple, Fast, and Practical Non-Blocking and Blocking Concurrent Queue Algorithms*.
- `java.util.concurrent.ConcurrentLinkedQueue` (OpenJDK source).
