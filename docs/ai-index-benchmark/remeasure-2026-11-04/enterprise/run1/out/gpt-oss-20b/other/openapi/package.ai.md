### other/openapi
- H: 1.0
- C: 1372091D
- D: 2026-11-04T06:55:34Z
- T: 2026-11-04T08:12:19Z
- G: 
- A: 0.0.0
- X: package
- F: [orders-api.yaml](orders-api.yaml.ai.md)
---
> A back‑office service for managing shop orders: listing, retrieving, and cancelling orders with status and total amount information.

#### Purpose
- Provide CRUD‑like operations for shop orders via a REST API.
- Expose order status and total amounts to back‑office users.

#### Responsibilities
- **Order Retrieval** – list orders with optional status filter and limit; fetch a single order by ID.
- **Order Cancellation** – cancel an existing order, enforcing write permissions and handling shipping conflicts.
- **Security** – enforce OAuth2 client‑credentials flow with `orders:write` scope for mutating actions.

#### Key units
- **OpenAPI Document** – `orders-api.yaml` (v2.1.0, OpenAPI 3.0.3) defining the API surface.
- **Schemas** – `OrderStatus` (enum: OPEN, PAID, SHIPPED, CANCELLED); `Order` (orderId, status, total, optional deprecated couponCode).
- **Operations** – `listOrders`, `getOrder`, `cancelOrder` with corresponding HTTP methods and paths.

#### Data flow
1. **List**: `GET /orders` → query parameters (`status`, `limit`) → returns array of `Order`.
2. **Retrieve**: `GET /orders/{orderId}` → path parameter → returns single `Order` or 404.
3. **Cancel**: `POST /orders/{orderId}/cancel` → requires `backofficeToken` with `orders:write`; returns 204 or 409 if already shipped.

#### Dependencies
- **Security** – OAuth2 client‑credentials (`backofficeToken`) with `orders:write` scope.
- **Server** – `https://api.shop.example/backoffice`.
- **Internal references** – `$ref` to `#/components/schemas/OrderStatus` and `#/components/schemas/Order`.

#### Cross‑cutting
- **Error handling** – 404 for missing orders; 409 for cancellation conflicts.
- **Deprecated fields** – `couponCode` in `Order` schema marked deprecated.
- **Rate limiting / pagination** – implied by `limit` parameter (max 200).
