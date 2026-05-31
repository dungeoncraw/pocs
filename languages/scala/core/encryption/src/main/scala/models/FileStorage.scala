package models

import types.StoragePath

trait FileStorage:
  def put(path: StoragePath, payload: CypherPayload): Unit
  def get(path: StoragePath): Option[CypherPayload]
  def delete(path: StoragePath): Unit