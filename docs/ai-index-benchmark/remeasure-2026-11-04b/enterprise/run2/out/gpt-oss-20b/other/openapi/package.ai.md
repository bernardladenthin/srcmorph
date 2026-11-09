### other/openapi
- H: 1.0
- C: 1372091D
- D: 2026-11-04T06:55:34Z
- T: 2026-11-04T12:36:57Z
- G: 
- A: 0.0.0
- X: package
- F: [orders-api.yaml](orders-api.yaml.ai.md)
---
> Provides a RESTful API for managing shop orders, including listing, retrieving, and canceling orders with status filtering and security controls.

#### Purpose
- Expose order data to back‑office clients.
- Allow cancellation of orders that have not shipped.

#### Responsibilities
- **Order Retrieval** – list orders with optional status and limit, fetch single order by ID.
- **Order Cancellation** – cancel an order, enforcing write‑scope authentication.
- **Security** – OAuth2 client‑credentials flow with `orders:write` scope.

#### Key units
- **`orders-api.yaml`** – OpenAPI 3.0.3 definition, title *Shop Orders API*, version *2.1.0*.
- **`OrderStatus`** – enum (`OPEN`, `PAID`, `SHIPPED`, `CANCELLED`).
- **`Order`** – object schema with `orderId`, `status`, `total`, and deprecated `couponCode`.
- **`backofficeToken`** – OAuth2 security scheme, token URL `https://auth.shop.example/token`, scope `orders:write`.

#### Data flow
1. Client calls `GET /orders` → server returns array of `Order` filtered by `status` and limited by `limit`.
2. Client calls `GET /orders/{orderId}` → server returns single `Order` or 404.
3. Client calls `POST /orders/{orderId}/cancel` with valid `backofficeToken` → server cancels order, returns 204 or 409 if already shipped.

#### Dependencies
- Internal references to `OrderStatus` and `Order` schemas.
- External OAuth2 provider at `https://auth.shop.example/token`.
- Server base URL `https://api.shop.example/backoffice`.

#### Cross-cutting
- All endpoints use the same OpenAPI 3.0.3 structure.
- Security is centralized via the `backofficeToken` scheme.
- Deprecated field `couponCode` indicates versioning strategy.
