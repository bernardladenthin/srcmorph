### other/openapi
- H: 1.0
- C: 1372091D
- D: 2026-11-04T06:55:34Z
- T: 2026-11-04T08:18:41Z
- G: 
- A: 0.0.0
- X: package
- F: [orders-api.yaml](orders-api.yaml.ai.md)
---
> A back‑office API for managing shop orders, providing listing, retrieval, and cancellation endpoints with status filtering and security.

#### Purpose
- Expose CRUD‑like operations for shop orders to back‑office users.
- Enforce OAuth2 scopes for write actions.

#### Responsibilities
- **Order Retrieval** – list orders with optional status filter and limit; fetch a single order by ID.
- **Order Cancellation** – cancel an existing order, guarded by `orders:write` scope.
- **Security** – OAuth2 client‑credentials flow with scoped access.

#### Key units
- **`listOrders`** (`GET /orders`) – returns an array of `Order` objects, filtered by `OrderStatus` and limited to 200.
- **`getOrder`** (`GET /orders/{orderId}`) – returns a single `Order` or 404.
- **`cancelOrder`** (`POST /orders/{orderId}/cancel`) – cancels an order, 204 on success, 409 if already shipped.
- **`OrderStatus`** – enum (`OPEN`, `PAID`, `SHIPPED`, `CANCELLED`).
- **`Order`** – schema with `orderId`, `status`, `total`; `couponCode` is deprecated.
- **`backofficeToken`** – OAuth2 client‑credentials security scheme, scope `orders:write`.

#### Data flow
1. Client calls `listOrders` → server queries order store → returns list of `Order`.
2. Client calls `getOrder` → server fetches order by ID → returns `Order` or 404.
3. Client calls `cancelOrder` → server validates scope, checks order status → updates status to `CANCELLED` or returns 409.

#### Dependencies
- OAuth2 token endpoint `https://auth.shop.example/token`.
- Server base URL `https://api.shop.example/backoffice`.
- Internal references to `#/components/schemas/OrderStatus` and `#/components/schemas/Order`.

#### Cross-cutting
- All endpoints use JSON media type.
- `OrderStatus` enum centralizes status values across operations.
- Security scope `orders:write` is required only for cancellation.
