### other/openapi
- H: 1.0
- C: 1372091D
- D: 2026-11-04T06:55:34Z
- T: 2026-11-04T12:43:01Z
- G: 
- A: 0.0.0
- X: package
- F: [orders-api.yaml](orders-api.yaml.ai.md)
---
> A RESTful API for managing shop orders, including listing, retrieving, and canceling orders.

#### Purpose
- Exposes endpoints for order lifecycle operations in a shop back‑office system.

#### Responsibilities
- **Order retrieval**: list orders with optional status filter and pagination; fetch a single order by ID.
- **Order cancellation**: cancel an existing order, enforcing business rules (e.g., cannot cancel shipped orders).
- **Security**: protect write operations with OAuth2 client‑credentials (`orders:write` scope).

#### Key units
- **OpenAPI document** (`orders-api.yaml`): defines the API contract, including paths, operations, schemas, and security.
- **Schemas**  
  - `OrderStatus`: enum of order states (`OPEN`, `PAID`, `SHIPPED`, `CANCELLED`).  
  - `Order`: object with `orderId`, `status`, `total`, and deprecated `couponCode`.
- **Security scheme** `backofficeToken`: OAuth2 client‑credentials flow, token endpoint `https://auth.shop.example/token`.

#### Data flow
1. **Client request** → API gateway → `GET /orders` or `GET /orders/{orderId}` or `POST /orders/{orderId}/cancel`.  
2. **Server** validates request parameters, checks security (for cancel), performs business logic, and returns the appropriate response (`200`, `204`, `404`, or `409`).

#### Dependencies
- OAuth2 provider at `https://auth.shop.example/token` for token issuance.
- No external data stores or services referenced in the OpenAPI spec.

#### Cross-cutting
- **Versioning**: API version `2.1.0`.  
- **Deprecation**: `couponCode` field in `Order` schema is marked deprecated.  
- **Error handling**: standard HTTP status codes (`404` for not found, `409` for conflict).
