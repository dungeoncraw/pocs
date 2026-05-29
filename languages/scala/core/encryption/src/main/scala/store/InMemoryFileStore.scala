package store

import models.{Cypher, FileStorage}
import types.StoragePath

import scala.collection.mutable

final class InMemoryFileStore extends FileStorage:

  private val files = mutable.Map.empty[StoragePath, Cypher]

  override def put(path: StoragePath, payload: Cypher): Unit =
    files(path) = payload

  override def get(path: StoragePath): Option[Cypher] =
    files.get(path)

  override def delete(path: StoragePath): Unit =
    files.remove(path)