package com.example.logistics.api

import com.example.logistics.dto.{CreateQuoteRequest, QuoteResponse}
import com.example.logistics.service.QuoteService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping(Array("/api/quotes"))
class QuoteController(quoteService: QuoteService):

  @PostMapping
  def createQuote(@RequestBody request: CreateQuoteRequest): ResponseEntity[QuoteResponse] =
    val response = quoteService.createQuote(request)
    ResponseEntity.ok(response)

  @GetMapping(Array("/{quoteId}"))
  def getQuote(@PathVariable quoteId: UUID): ResponseEntity[QuoteResponse] =
    quoteService.getQuote(quoteId)
      .map(ResponseEntity.ok)
      .getOrElse(ResponseEntity.notFound().build())
