### other/openapi
- H: 1.0
- C: 1372091D
- D: 2026-11-04T06:55:34Z
- T: 2026-11-04T12:27:44Z
- G: 
- A: 0.0.0
- X: package
- F: [orders-api.yaml](orders-api.yaml.ai.md)
---
> Provides a REST API for back‑office order management, exposing listing, retrieval, and cancellation of shop orders.

#### Purpose
- Expose order data and state transitions to back‑office clients.
- Enforce OAuth2 security for write operations.

#### Responsibilities
- **Order retrieval** – list orders with optional status filter and pagination.
- **Order detail** – fetch a single order by its identifier.
- **Order cancellation** – transition an order to `CANCELLED` when allowed.

#### Key units
- **`orders-api.yaml`** – OpenAPI 3.0.3 definition of the Shop Orders API (v2.1.0).
- **`Order` schema** – represents an order with `orderId`, `status`, `total`, and deprecated `couponCode`.
- **`OrderStatus` enum** – possible order states: `OPEN`, `PAID`, `SHIPPED`, `CANCELLED`.
- **`backofficeToken` security scheme** – OAuth2 client‑credentials flow with `orders:write` scope.

#### Data flow
1. **List**: client sends `GET /orders` with optional `status` and `limit`; server returns an array of `Order`.
2. **Retrieve**: client sends `GET /orders/{orderId}`; server returns the matching `Order` or 404.
3. **Cancel**: client sends `POST /orders/{orderId}/cancel` with a valid `backofficeToken`; server returns 204 on success or 409 if already shipped.

#### Dependencies
- OAuth2 token endpoint `https://auth.shop.example/token`.
- Base API server `https://api.shop.example/backoffice`.

#### Cross-cutting
- All write operations require `orders:write` scope via `backofficeToken`.
- The `couponCode` field is marked deprecated, indicating a planned removal.
- Pagination is limited to a maximum of 200 items per request.
