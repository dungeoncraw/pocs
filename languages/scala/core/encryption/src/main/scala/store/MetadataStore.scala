package store

import models.Metadata
import types.FileId

import scala.collection.mutable

final class MetadataStore:

  private val files = mutable.Map.empty[FileId, Metadata]

  def save(metadata: Metadata): Unit =
    files(metadata.fileId) = metadata

  def find(fileId: FileId): Option[Metadata] =
    files.get(fileId)

  def update(metadata: Metadata): Unit =
    files(metadata.fileId) = metadata

  def listAll(): List[Metadata] =
    files.values.toList