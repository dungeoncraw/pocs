import java.util.concurrent.atomic.AtomicReference
import scala.annotation.tailrec
// based on https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ConcurrentLinkedQueue.html
final class CustomConcurrentLinkedQueue[T]:

  private final class Node(val value: Option[T]):
    val next: AtomicReference[Node] = new AtomicReference[Node](null)

  private val sentinel = new Node(None)
  private val head: AtomicReference[Node] = new AtomicReference[Node](sentinel)
  private val tail: AtomicReference[Node] = new AtomicReference[Node](sentinel)

  def offer(value: T): Boolean =
    if value == null then throw new NullPointerException("null values are not permitted")
    val newNode = new Node(Some(value))

    @tailrec
    def loop(): Boolean =
      val curTail = tail.get()
      val tailNext = curTail.next.get()
      if tailNext != null then
        tail.compareAndSet(curTail, tailNext)
        loop()
      else if curTail.next.compareAndSet(null, newNode) then
        tail.compareAndSet(curTail, newNode)
        true
      else loop()

    loop()

  def poll(): Option[T] =
    @tailrec
    def loop(): Option[T] =
      val curHead = head.get()
      val curTail = tail.get()
      val first = curHead.next.get()
      if first == null then
        None
      else if curHead eq curTail then
        tail.compareAndSet(curTail, first)
        loop()
      else if head.compareAndSet(curHead, first) then first.value
      else loop()

    loop()

  def peek(): Option[T] =
    val first = head.get().next.get()
    if first == null then None else first.value

  def isEmpty: Boolean = head.get().next.get() == null

  def size: Int =
    var count = 0
    var cur = head.get().next.get()
    while cur != null do
      count += 1
      cur = cur.next.get()
    count
