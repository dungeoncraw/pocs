package observability

import java.util.concurrent.ConcurrentLinkedQueue
import scala.jdk.CollectionConverters.*

case class TraceEntry(layer: String, functionName: String, startTime: Long, endTime: Long)

object ObservabilityStore {
  private val traces = new ConcurrentLinkedQueue[TraceEntry]()

  def record(layer: String, functionName: String, start: Long, end: Long): Unit = {
    traces.add(TraceEntry(layer, functionName, start, end))
  }

  def report(): Unit = {
    val allTraces = traces.asScala.toList
    if (allTraces.isEmpty) {
      println("No data recorded.")
    } else {
      allTraces.foreach { trace =>
        val duration = trace.endTime - trace.startTime
        println(s"Layer: ${trace.layer} | Function: ${trace.functionName} | Started: ${trace.startTime} | Ended: ${trace.endTime} | Duration: ${duration}ms")
      }
    }
  }
}

class Observability(layerName: String) {
  def track[T](functionName: String)(block: => T): T = {
    val start = System.currentTimeMillis()
    try {
      block
    } finally {
      val end = System.currentTimeMillis()
      ObservabilityStore.record(layerName, functionName, start, end)
    }
  }
}
