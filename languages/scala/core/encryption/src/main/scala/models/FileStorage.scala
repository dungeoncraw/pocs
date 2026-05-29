package models

import types.StoragePath

trait FileStorage:
  def put(path: StoragePath, payload: Cypher): Unit
  def get(path: StoragePath): Option[Cypher]
  def delete(path: StoragePath): Unit