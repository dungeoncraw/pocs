# Scala 3 Redis Clone

A small Redis-like in-memory database written in **Scala 3**.

## RESP Parser and RESP Encoder

Redis clients and servers communicate using **RESP**, the Redis Serialization Protocol.

The parser and encoder solve opposite problems.

Ref: https://redis.io/docs/latest/develop/reference/protocol-spec/

### RESP Parser

The parser handles incoming bytes from the client.

```txt
Raw bytes from client
  -> structured data
```

Example client request:

```txt
*2\r\n$3\r\nGET\r\n$4\r\nname\r\n
```

Parser result:

```scala
RespArray(
  Some(
    List(
      RespBulkString(Some("GET".getBytes)),
      RespBulkString(Some("name".getBytes))
    )
  )
)
```

Then `Command.fromResp(...)` converts it into:

```scala
Command(
  name = "GET",
  args = List("name")
)
```

The parser answers:

```txt
What did the client send me?
```

### RESP Encoder

The encoder handles outgoing bytes to the client.

```txt
Structured response
  -> raw RESP bytes
```

Example server response:

```scala
RespBulkString(Some("Thiago".getBytes))
```

Encoder output:

```txt
$6\r\nThiago\r\n
```

The encoder answers:

```txt
How do I send this response back in Redis protocol format?
```

---

## RESP Types

| Prefix | Type | Example |
|---|---|---|
| `+` | Simple string | `+OK\r\n` |
| `-` | Error | `-ERR unknown command\r\n` |
| `:` | Integer | `:1\r\n` |
| `$` | Bulk string | `$6\r\nThiago\r\n` |
| `*` | Array | `*2\r\n$3\r\nGET\r\n$4\r\nname\r\n` |

---

## Parser and Encoder Examples

### Simple String

Raw RESP:

```txt
+OK\r\n
```

Parser result:

```scala
RespSimpleString("OK")
```

Encoder input:

```scala
RespSimpleString("OK")
```

Encoder output:

```txt
+OK\r\n
```

---

### Error

Raw RESP:

```txt
-ERR unknown command\r\n
```

Parser result:

```scala
RespError("ERR unknown command")
```

Encoder input:

```scala
RespError("ERR unknown command")
```

Encoder output:

```txt
-ERR unknown command\r\n
```

---

### Integer

Raw RESP:

```txt
:1\r\n
```

Parser result:

```scala
RespInteger(1)
```

Encoder input:

```scala
RespInteger(1)
```

Encoder output:

```txt
:1\r\n
```

Redis uses integers for commands like:

```txt
DEL
EXISTS
TTL
```

Examples:

```txt
:0\r\n
:1\r\n
:-1\r\n
:-2\r\n
```

For `TTL`:

```txt
-1 = key exists but has no expiration
-2 = key does not exist
```

---

### Bulk String

Raw RESP:

```txt
$6\r\nThiago\r\n
```

Parser result:

```scala
RespBulkString(Some("Thiago".getBytes(StandardCharsets.UTF_8)))
```

Encoder input:

```scala
RespBulkString(Some("Thiago".getBytes(StandardCharsets.UTF_8)))
```

Encoder output:

```txt
$6\r\nThiago\r\n
```

Important: `$6` means **6 bytes**, not 6 characters.

This matters for UTF-8 strings.

Example:

```scala
val value = "ação"
val bytes = value.getBytes(StandardCharsets.UTF_8)
```

The string has 4 characters, but more than 4 bytes.

RESP bulk strings must use:

```scala
bytes.length
```

not:

```scala
value.length
```

---

### Null Bulk String

Raw RESP:

```txt
$-1\r\n
```

Parser result:

```scala
RespBulkString(None)
```

Encoder input:

```scala
RespBulkString(None)
```

Encoder output:

```txt
$-1\r\n
```

Redis uses this when a key does not exist:

```txt
GET missing_key
```

In `redis-cli`, this appears as:

```txt
(nil)
```

---

### Array

Raw RESP command:

```txt
*2\r\n$3\r\nGET\r\n$4\r\nname\r\n
```

Parser result:

```scala
RespArray(
  Some(
    List(
      RespBulkString(Some("GET".getBytes(StandardCharsets.UTF_8))),
      RespBulkString(Some("name".getBytes(StandardCharsets.UTF_8)))
    )
  )
)
```

Command conversion:

```scala
Command(
  name = "GET",
  args = List("name")
)
```

Arrays are used heavily by Redis clients because commands are sent as arrays of bulk strings.

---

## Command Examples

### `PING`

Human command:

```txt
PING
```

RESP request:

```txt
*1\r\n$4\r\nPING\r\n
```

Command:

```scala
Command(
  name = "PING",
  args = Nil
)
```

Server response value:

```scala
RespSimpleString("PONG")
```

RESP response:

```txt
+PONG\r\n
```

---

### `SET name Thiago`

Human command:

```txt
SET name Thiago
```

RESP request:

```txt
*3\r\n$3\r\nSET\r\n$4\r\nname\r\n$6\r\nThiago\r\n
```

Formatted:

```txt
*3\r\n
$3\r\n
SET\r\n
$4\r\n
name\r\n
$6\r\n
Thiago\r\n
```

Parser result:

```scala
RespArray(
  Some(
    List(
      RespBulkString(Some("SET".getBytes(StandardCharsets.UTF_8))),
      RespBulkString(Some("name".getBytes(StandardCharsets.UTF_8))),
      RespBulkString(Some("Thiago".getBytes(StandardCharsets.UTF_8)))
    )
  )
)
```

Command conversion:

```scala
Command(
  name = "SET",
  args = List("name", "Thiago")
)
```

Server response value:

```scala
RespSimpleString("OK")
```

Encoder output:

```txt
+OK\r\n
```

---

### `GET name`

Human command:

```txt
GET name
```

RESP request:

```txt
*2\r\n$3\r\nGET\r\n$4\r\nname\r\n
```

Command conversion:

```scala
Command(
  name = "GET",
  args = List("name")
)
```

If the key exists:

```scala
RespBulkString(Some("Thiago".getBytes(StandardCharsets.UTF_8)))
```

Encoder output:

```txt
$6\r\nThiago\r\n
```

If the key does not exist:

```scala
RespBulkString(None)
```

Encoder output:

```txt
$-1\r\n
```

---

### `DEL name`

Human command:

```txt
DEL name
```

RESP request:

```txt
*2\r\n$3\r\nDEL\r\n$4\r\nname\r\n
```

Command conversion:

```scala
Command(
  name = "DEL",
  args = List("name")
)
```

If the key was deleted:

```scala
RespInteger(1)
```

Encoder output:

```txt
:1\r\n
```

If the key did not exist:

```scala
RespInteger(0)
```

Encoder output:

```txt
:0\r\n
```

---

### `EXPIRE session 10`

Human command:

```txt
EXPIRE session 10
```

RESP request:

```txt
*3\r\n$6\r\nEXPIRE\r\n$7\r\nsession\r\n$2\r\n10\r\n
```

Command conversion:

```scala
Command(
  name = "EXPIRE",
  args = List("session", "10")
)
```

If expiration was applied:

```scala
RespInteger(1)
```

Encoder output:

```txt
:1\r\n
```

If the key does not exist:

```scala
RespInteger(0)
```

Encoder output:

```txt
:0\r\n
```

---

### `TTL session`

Human command:

```txt
TTL session
```

RESP request:

```txt
*2\r\n$3\r\nTTL\r\n$7\r\nsession\r\n
```

Command conversion:

```scala
Command(
  name = "TTL",
  args = List("session")
)
```

Response examples:

```txt
:8\r\n     key expires in 8 seconds
:-1\r\n    key exists but has no expiration
:-2\r\n    key does not exist
```

---
