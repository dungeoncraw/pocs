package com.example.logistics.api

import com.example.logistics.domain.TransportRate
import com.example.logistics.repository.TransportRateRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

case class TransportRatesResponse(rates: List[TransportRate])

@RestController
@RequestMapping(Array("/api/transport-rates"))
class TransportRateController(transportRateRepository: TransportRateRepository):

  @GetMapping
  def getAllRates: ResponseEntity[TransportRatesResponse] =
    val rates = transportRateRepository.findAll()
    ResponseEntity.ok(TransportRatesResponse(rates))
