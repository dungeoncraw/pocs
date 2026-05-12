# Logistics Freight Pricing API - Simple Design

## 1. Overview

Build a simple REST API that calculates freight prices for:

- Truck
- Boat
- Rail

The price is based on:

- Distance
- Weight
- Volume
- Transport type

Distance is calculated for free using:
Haversine formula plus transport multiplier

The API uses:

- Scala 3
- Spring Boot
- sbt
- PostgreSQL
- Jackson
- Plain SQL scripts
- Bruno for API testing

The API does not use:

- Swagger
- Flyway
- Google Maps API
- Paid routing APIs

## 2. Goal

1. Receive quote request.
2. Calculate distance.
3. Calculate volume.
4. Calculate price for truck, boat, or rail.
5. Return the quote response.
6. Store the quote in PostgreSQL.

## 3. Basic Price Formula

For every transport type:

```
price = 
  basePrice
  + distanceKm * pricePerKm
  + weightKg * pricePerKg
  + volumeM3 * pricePerM3
```

If the price is lower than the `minimumPrice`, use the `minimumPrice`.

Final formula:
```
finalPrice = max(calculatedPrice, minimumPrice)
```

## 4. Distance Formula

Use Haversine distance.

The request must include:

- origin latitude
- origin longitude
- destination latitude
- destination longitude

First, calculate straight-line distance:
```
straightDistanceKm = haversine(origin, destination)
```

Then apply transport multiplier:
```
distanceKm = straightDistanceKm * multiplier
```

Multipliers:

- **TRUCK**: 1.25
- **RAIL**: 1.15
- **BOAT**: 1.35

## 5. Volume Formula

The request includes package dimensions in centimeters.

Calculate volume in cubic meters:
```
volumeM3 = lengthCm * widthCm * heightCm / 1,000,000
```

## 6. Transport Rates

Store simple rates in PostgreSQL.

Example:

### TRUCK:
- basePrice = 250
- pricePerKm = 0.75
- pricePerKg = 0.20
- pricePerM3 = 15
- minimumPrice = 400

### RAIL:
- basePrice = 200
- pricePerKm = 0.55
- pricePerKg = 0.15
- pricePerM3 = 10
- minimumPrice = 350

### BOAT:
- basePrice = 400
- pricePerKm = 0.40
- pricePerKg = 0.10
- pricePerM3 = 25
- minimumPrice = 600

## 7. API Endpoints

### POST /api/quotes

**Request:**
```json
{
  "customerId": "customer-123",
  "transportType": "TRUCK",
  "origin": {
    "latitude": 34.0522,
    "longitude": -118.2437
  },
  "destination": {
    "latitude": 41.8781,
    "longitude": -87.6298
  },
  "shipment": {
    "weightKg": 1200,
    "lengthCm": 200,
    "widthCm": 120,
    "heightCm": 150
  }
}
```

**Response:**
```json
{
  "quoteId": "quote-123",
  "customerId": "customer-123",
  "transportType": "TRUCK",
  "distanceKm": 3504.31,
  "volumeM3": 3.6,
  "price": 3520.44,
  "currency": "USD",
  "expiresAt": "2026-05-11T13:00:00Z",
  "breakdown": {
    "basePrice": 250.00,
    "distanceCost": 2628.23,
    "weightCost": 240.00,
    "volumeCost": 54.00,
    "minimumPrice": 400.00
  }
}
```

### GET /api/quotes/{quoteId}

**Response:**
```json
{
  "quoteId": "quote-123",
  "customerId": "customer-123",
  "transportType": "TRUCK",
  "origin": {
    "latitude": 34.0522,
    "longitude": -118.2437
  },
  "destination": {
    "latitude": 41.8781,
    "longitude": -87.6298
  },
  "shipment": {
    "weightKg": 1200,
    "lengthCm": 200,
    "widthCm": 120,
    "heightCm": 150,
    "volumeM3": 3.6
  },
  "distance": {
    "straightDistanceKm": 2803.45,
    "distanceKm": 3504.31,
    "method": "HAVERSINE_WITH_MULTIPLIER",
    "multiplier": 1.25
  },
  "price": {
    "amount": 3520.44,
    "currency": "USD"
  },
  "breakdown": {
    "basePrice": 250.00,
    "distanceCost": 2628.23,
    "weightCost": 240.00,
    "volumeCost": 54.00,
    "minimumPrice": 400.00
  },
  "createdAt": "2026-05-11T12:30:00Z",
  "expiresAt": "2026-05-11T13:00:00Z"
}
```

### GET /api/transport-rates

**Response:**
```json
{
  "rates": [
    {
      "transportType": "TRUCK",
      "basePrice": 250.00,
      "pricePerKm": 0.75,
      "pricePerKg": 0.20,
      "pricePerM3": 15.00,
      "minimumPrice": 400.00,
      "distanceMultiplier": 1.25,
      "currency": "USD"
    },
    {
      "transportType": "RAIL",
      "basePrice": 200.00,
      "pricePerKm": 0.55,
      "pricePerKg": 0.15,
      "pricePerM3": 10.00,
      "minimumPrice": 350.00,
      "distanceMultiplier": 1.15,
      "currency": "USD"
    },
    {
      "transportType": "BOAT",
      "basePrice": 400.00,
      "pricePerKm": 0.40,
      "pricePerKg": 0.10,
      "pricePerM3": 25.00,
      "minimumPrice": 600.00,
      "distanceMultiplier": 1.35,
      "currency": "USD"
    }
  ]
}
```

## 10. Database Tables

1. `transport_rates`
2. `quotes`

## 11. Table: transport_rates

**SQL:**
```sql
CREATE TABLE transport_rates (
  transport_type VARCHAR(20) PRIMARY KEY,
  base_price NUMERIC(19,4) NOT NULL,
  price_per_km NUMERIC(19,4) NOT NULL,
  price_per_kg NUMERIC(19,4) NOT NULL,
  price_per_m3 NUMERIC(19,4) NOT NULL,
  minimum_price NUMERIC(19,4) NOT NULL,
  distance_multiplier NUMERIC(10,4) NOT NULL,
  currency CHAR(3) NOT NULL
);
```

## 12. Table: quotes

Stores generated quotes.

**SQL:**
```sql
CREATE TABLE quotes (
  id UUID PRIMARY KEY,
  customer_id VARCHAR(100) NOT NULL,
  transport_type VARCHAR(20) NOT NULL,

  origin_latitude NUMERIC(10,7) NOT NULL,
  origin_longitude NUMERIC(10,7) NOT NULL,
  destination_latitude NUMERIC(10,7) NOT NULL,
  destination_longitude NUMERIC(10,7) NOT NULL,

  weight_kg NUMERIC(19,4) NOT NULL,
  length_cm NUMERIC(19,4) NOT NULL,
  width_cm NUMERIC(19,4) NOT NULL,
  height_cm NUMERIC(19,4) NOT NULL,

  straight_distance_km NUMERIC(19,4) NOT NULL,
  distance_km NUMERIC(19,4) NOT NULL,
  volume_m3 NUMERIC(19,4) NOT NULL,

  base_price NUMERIC(19,4) NOT NULL,
  distance_cost NUMERIC(19,4) NOT NULL,
  weight_cost NUMERIC(19,4) NOT NULL,
  volume_cost NUMERIC(19,4) NOT NULL,
  minimum_price NUMERIC(19,4) NOT NULL,
  final_price NUMERIC(19,4) NOT NULL,

  currency CHAR(3) NOT NULL,

  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  expires_at TIMESTAMPTZ NOT NULL
);
```

## 14. Project Structure

```text
src/main/scala/com/example/logistics
├── LogisticsApplication.scala
├── api
│   ├── QuoteController.scala
│   └── TransportRateController.scala
├── service
│   ├── QuoteService.scala
│   └── DistanceService.scala
├── repository
│   ├── QuoteRepository.scala
│   └── TransportRateRepository.scala
├── domain
│   ├── TransportType.scala
│   ├── TransportRate.scala
│   └── Quote.scala
└── dto
    ├── CreateQuoteRequest.scala
    └── QuoteResponse.scala
```

## 15. Main Services

### QuoteService
- Validates request
- Loads transport rate
- Calculates distance
- Calculates volume
- Calculates price
- Stores quote
- Returns response

### DistanceService
- Calculates Haversine distance
- Applies transport multiplier

### TransportRateRepository
- Loads rates from `transport_rates` table

### QuoteRepository
- Stores quote
- Finds quote by ID

## 16. Build Dependencies

**Use:**
- `spring-boot-starter-web`
- `spring-boot-starter-validation`
- `spring-boot-starter-jdbc`
- `jackson-module-scala`
- `postgresql`
- `spring-boot-starter-test`

**Do not use:**
- `springdoc-openapi`
- `Flyway`

## 17. Bruno Collections

Use Bruno for API testing.
