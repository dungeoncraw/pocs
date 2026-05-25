package services

import domain.{ShowDate, Zone}

class PricingService:

  def calculatePrice(
                      showDate: ShowDate,
                      zone: Zone
                    ): BigDecimal =
    showDate.basePrice + zone.priceModifier