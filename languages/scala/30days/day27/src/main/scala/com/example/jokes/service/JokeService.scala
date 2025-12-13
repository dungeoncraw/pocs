package com.example.jokes.service

import com.example.jokes.dto.CreateJokeRequest
import com.example.jokes.model.Joke

import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong
import scala.jdk.CollectionConverters.*
import org.springframework.stereotype.Service

@Service
class JokeService:
  private val idSeq = AtomicLong(0L)
  private val store = new ConcurrentHashMap[Long, Joke]()

  def list(): List[Joke] =
    store.values().asScala.toList.sortBy(_.id)

  def create(req: CreateJokeRequest): Joke =
    val id = idSeq.incrementAndGet()
    val joke = Joke(
      id = id,
      text = req.text.trim,
      author = req.author.map(_.trim).filter(_.nonEmpty),
      createdAt = Instant.now()
    )
    store.put(id, joke)
    joke
