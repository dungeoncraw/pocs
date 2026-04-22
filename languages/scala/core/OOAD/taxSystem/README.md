# TAX System

A service for calculating product tax based on:

- product
- state
- year

## Goal

Provide a contract-first API for tax calculation, with support for:

- different tax rates per state
- different tax rates per year
- different tax rules per product

## Tech Stack

- **Scala 3**
- **Apache Pekko** for HTTP/API layer
- **Slick** for database access
- **SBT** for build management

## Domain Concept

A tax quote is determined by:

- `productId`
- `state`
- `year`

The API returns:

- tax rate

## API Contract

### 1. Get Tax Quote

`GET /tax/quote/{productId}?state=CA&year=2025`

#### Path Parameters

- `productId`: (Required) ID of the product.

#### Query Parameters

- `state`: (Optional) 2-letter state code. If not sent, returns a list of states.
- `year`: (Optional) 4-digit year. If not sent, returns last 10 years.

#### Response

A list of tax rates matching the criteria, ordered by year ascending:

```json
[
  {
    "productId": "PROD_1001",
    "state": "CA",
    "year": 2025,
    "taxRate": 0.085
  }
]
```

#### Example 1: Missing year

`GET /tax/quote/PROD_1001?state=CA`

Returns last 10 years of rates for the product in CA.

```json
[
  {
    "productId": "PROD_1001",
    "state": "CA",
    "year": 2024,
    "taxRate": 0.082
  },
  {
    "productId": "PROD_1001",
    "state": "CA",
    "year": 2025,
    "taxRate": 0.085
  }
]
```

#### Example 2: Missing state and year sent

`GET /tax/quote/PROD_1001?year=2025`

Returns all states rates for the product in 2025.

```json
[
  {
    "productId": "PROD_1001",
    "state": "CA",
    "year": 2025,
    "taxRate": 0.085
  },
  {
    "productId": "PROD_1001",
    "state": "TX",
    "year": 2025,
    "taxRate": 0.0625
  }
]
```

#### Errors

```json
{
  "error": "TAX_RULE_NOT_FOUND",
  "message": "No tax rule found for product in CA for 2025"
}
```

---

### 2. List Tax Rules

`GET /tax/rules?productId=PROD_1001&state=CA&year=2025`

#### Query Parameters (All Optional)

- `productId`: ID of the product.
- `state`: 2-letter state code.
- `year`: 4-digit year.

#### Response

```json
[
  {
    "productId": "PROD_1001",
    "state": "CA",
    "year": 2025,
    "taxRate": 0.085
  }
]
```

---

### 3. Create Tax Rule

`POST /tax/rules`

#### Request Body

```json
{
  "productId": "PROD_1001",
  "state": "CA",
  "year": 2025,
  "taxRate": 0.085
}
```

#### Response

```json
{
  "productId": "PROD_1001",
  "state": "CA",
  "year": 2025,
  "taxRate": 0.085
}
```

---

### 4. Update Tax Rule

`PUT /tax/rules/{productId}/{state}/{year}`

#### Path Parameters

- `productId`: (Required) ID of the product.
- `state`: (Required) 2-letter state code.
- `year`: (Required) 4-digit year.

#### Request Body

```json
{
  "taxRate": 0.090
}
```

#### Response

```json
{
  "productId": "PROD_1001",
  "state": "CA",
  "year": 2025,
  "taxRate": 0.090
}
```

---

### 5. Delete Tax Rule

`DELETE /tax/rules/{productId}/{state}/{year}`

#### Path Parameters

- `productId`: (Required) ID of the product.
- `state`: (Required) 2-letter state code.
- `year`: (Required) 4-digit year.

#### Response

`204 No Content`

## Contract Rules

### Tax lookup precedence

1. Product-specific rule
2. Default state/year rule
3. Error if no rule exists

### Tax rate format

- Use decimal form
- Example: `0.085` means `8.5%`

### Missing rule behavior

If no matching rule exists, return an error rather than silently defaulting to zero.

## Suggested Data Models

### TaxQuoteRequest

- `productId` (path)
- `state` (optional)
- `year` (optional)

### TaxQuoteResponse

- `productId`
- `state`
- `year`
- `taxRate`

### TaxRule

- `productId`
- `state`
- `year`
- `taxRate`

## Example Use Cases

- `PROD_1001` in `CA` for `2025` → `8.5%`
- `PROD_1002` in `TX` for `2025` → `0%`
- `PROD_1003` in `MA` for `2024` → `0%`

## How to Run with Docker

This project includes a `docker-compose.yml` file to spin up both the Scala application and a PostgreSQL database.

### Prerequisites

- [Docker](https://www.docker.com/get-started)
- [Docker Compose](https://docs.docker.com/compose/install/)

### Starting the Services

To build and start the application and database, run:

```bash
docker-compose up --build
```

The application will be available at `http://localhost:8080`.

### Stopping the Services

To stop and remove the containers, run:

```bash
docker-compose down
```

### Database Access

The PostgreSQL database is exposed on port `5432` with the following credentials (defined in `docker-compose.yml`):

- **User**: `user`
- **Password**: `password`
- **Database**: `taxsystem`
- **Host**: `localhost` (from your machine) or `db` (within the Docker network)
