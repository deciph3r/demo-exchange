# Demo Exchange Application

A simple stock exchange simulation built using Spring Boot. It supports placing orders, matching trades, managing portfolios, and basic authentication.

---

## Tech Stack

* Java + Spring Boot
* Spring Security (Basic Auth)
* H2 In-Memory Database
* JDBC Template
* Lombok

---

## How to Run

### Prerequisites

* Java 17+
* Maven

### Steps

```bash
git clone <your-repo-url>
cd demo-exchange
mvn clean install
mvn spring-boot:run
```

Application will start on:

```
http://localhost:8080
```

---

## Database

* Uses **H2 in-memory DB**
* Schema + seed data auto-loaded from

### Default Users

| Username      | Password |
| ------------- | -------- |
| abdullah      | password |
| tech_user     | password |
| finance_user  | password |
| balanced_user | password |
| mixed_user    | password |

---

## Authentication

* Uses **HTTP Basic Auth**
* Password encoder: NoOp (plain text)

Example:

```bash
-u abdullah:password
```

---

## Headers

Every response includes:

```
X-Trace-Id: <UUID>
```

Generated via filter

---

## APIs

Base URL:

```
http://localhost:8080
```

---

### 1. Place Order

**Endpoint**

```
POST /placeOrder
```

**Description**

* Places BUY/SELL order
* Matches with existing orders
* Executes trade if price matches

**Sample Curl**

```bash
curl -X POST http://localhost:8080/placeOrder \
  -u abdullah:password \
  -H "Content-Type: application/json" \
  -d '{
    "stock": "AAPL",
    "quantity": 10,
    "price": 210,
    "side": "BUY"
}'
```

**Response**

```
<Order ID>
```

---

### 2. Cancel Order

**Endpoint**

```
POST /cancelOrder?id=<orderId>
```

**Description**

* Cancels only PENDING orders
* Only owner can cancel

**Sample Curl**

```bash
curl -X POST "http://localhost:8080/cancelOrder?id=1" \
  -u abdullah:password
```

**Response**

```
ORDER CANCELLED SUCCESSFULLY
```

---

### 3. Get Portfolio

**Endpoint**

```
GET /getPortfolio
```

**Description**

* Returns user holdings
* Includes sector aggregation

**Sample Curl**

```bash
curl -X GET http://localhost:8080/getPortfolio \
  -u abdullah:password
```

**Response**

```json
{
  "userName": "abdullah",
  "holdings": [
    {
      "symbol": "AAPL",
      "quantity": 100,
      "sector": "TECH"
    }
  ],
  "sectorHoldings": {
    "TECH": 100
  }
}
```

---

### 4. Add to Portfolio (Manual)

**Endpoint**

```
POST /addToPortfolio
```

**Description**

* Directly updates portfolio without trade
* Primarily for testing

**Sample Curl**

```bash
curl -X POST http://localhost:8080/addToPortfolio \
  -u abdullah:password \
  -H "Content-Type: application/json" \
  -d '{
    "stock": "AAPL",
    "quantity": 5
}'
```

---

## Order Matching Logic

* Matches BUY ↔ SELL
* Price condition:

    * BUY ≥ SELL
* Priority:

    * Earliest `createdAt`
* Execution price:

    * Price of earlier order

---

## Constraints

* Max **3 pending orders per user**
* SELL allowed only if:

    * User has sufficient holdings
    * Pending sell orders are considered
* Orders expire after 24 hours

---

## Notes

* No JWT / OAuth — only Basic Auth
* In-memory DB resets on restart
* Designed for demonstration, not production use

---

If you want, I can also:

* Add Postman collection
* Add Swagger/OpenAPI
* Improve README with architecture diagram
* Add Docker setup
