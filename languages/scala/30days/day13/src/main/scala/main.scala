package com.tetokeguii.day13

import scala.util.{Try, Success, Failure}

// opaque types
opaque type UserId = Int
object UserId:
  def apply(id: Int): UserId = id
  def fromString(s: String): Option[UserId] = Try(s.toInt).toOption.filter(_ > 0).map(UserId.apply)

opaque type OrderId = Long
object OrderId:
  def apply(id: Long): OrderId = id
  def generate(): OrderId = OrderId(System.currentTimeMillis())

opaque type ProductId = String
object ProductId:
  def apply(s: String): ProductId =
    require(s.nonEmpty && s.matches("^[A-Z]{2}\\d{4}$"), "ProductId deve ter formato XX0000")
    s

//Union types
type ApiResponse = SuccessResponse | ErrorResponse
case class SuccessResponse(data: String, timestamp: Long)
case class ErrorResponse(message: String, code: Int)

//process different types
def processInput(input: String | Int | Boolean): String = input match
  case s: String => s"String: $s"
  case i: Int => s"Int: $i (${if i % 2 == 0 then "even" else "odd"})"
  case b: Boolean => s"Boolean: $b ${if b then "True" else "False"}"


// interesection types

trait Readable:
  def read(): String

trait Writable:
  def write(s: String): Unit

trait Serializable:
  def serialize(): Array[Byte]

// implement multiple traits
class Document(private var content:String) extends Readable with Writable with Serializable:
  def read(): String = content
  def write(s: String): Unit = content = s
  def serialize(): Array[Byte] = content.getBytes()

def processDocument(doc: Readable & Writable): String =
  val originalContent = doc.read()
  doc.write(s"Processed: $originalContent")
  doc.read()


@main
def main(): Unit = {
  println("=== OPAQUE TYPES ===")

  val userId = UserId(123)
  val orderId = OrderId.generate()
  val productId = ProductId("AB1234")

  // this must throw an exception
  // def processOrder(userId: UserId, orderId: OrderId): Unit = ???
  // processOrder(orderId, userId) // ERR!

  println(s"User ID: $userId")
  println(s"Order ID: $orderId")
  println(s"Product ID: $productId")

  UserId.fromString("456") match
    case Some(id) => println(s"ID valid: $id")
    case None => println("ID invalid")

  println("\n=== UNION TYPES ===")

  // Simulação de API responses
  val responses: List[ApiResponse] = List(
    SuccessResponse("Dados carregados com sucesso", System.currentTimeMillis()),
    ErrorResponse("Usuário não encontrado", 404),
    SuccessResponse("Pedido criado", System.currentTimeMillis())
  )

  responses.foreach {
    case SuccessResponse(data, timestamp) =>
      println(s"✓ Sucesso: $data (em $timestamp)")
    case ErrorResponse(message, code) =>
      println(s"✗ Erro $code: $message")
  }

  println("\n=== INTERSECTION TYPES - Combinação de capacidades ===")

  val document = Document("Conteúdo original do documento")

  val processed = processDocument(document)
  println(s"Documento processado: $processed")

  // type safety and intersection types
  def requiresReadable(r: Readable): String = r.read()
  def requiresWritable(w: Writable): Unit = w.write("New info")
  def requiresSerializable(s: Serializable): Array[Byte] = s.serialize()
  println(s"As Readable: ${requiresReadable(document)}")
  requiresWritable(document)
  val serialized = requiresSerializable(document)
  println(s"As Serializable: ${serialized.length} bytes")
}