### other/openapi
- H: 1.0
- C: 1372091D
- D: 2026-11-04T06:55:34Z
- T: 2026-11-04T08:25:03Z
- G: 
- A: 0.0.0
- X: package
- F: [orders-api.yaml](orders-api.yaml.ai.md)
---
> The Shop Orders API provides back‑office endpoints for listing, retrieving, and canceling customer orders, exposing order status, totals, and optional coupon codes.

#### Purpose
- Expose CRUD‑like operations for shop orders to back‑office users.
- Enforce OAuth2 client‑credentials with `orders:write` scope for destructive actions.

#### Responsibilities
- **Order Retrieval** – list orders with optional status filter and limit; fetch a single order by ID.
- **Order Cancellation** – cancel an order, ensuring it is not already shipped.
- **Security** – protect cancel operation with OAuth2 scopes; expose public read endpoints.

#### Key units
- **`listOrders`** (`GET /orders`) – returns an array of `Order` objects, filtered by `status` and limited by `limit`.
- **`getOrder`** (`GET /orders/{orderId}`) – returns a single `Order` or 404.
- **`cancelOrder`** (`POST /orders/{orderId}/cancel`) – cancels an order, returns 204 or 409 if already shipped.
- **`Order` schema** – defines `orderId`, `status`, `total`, and deprecated `couponCode`.
- **`OrderStatus` enum** – `OPEN`, `PAID`, `SHIPPED`, `CANCELLED`.
- **`backofficeToken` security scheme** – OAuth2 client‑credentials with `orders:write` scope.

#### Data flow
1. Client calls `listOrders` or `getOrder` → server queries order store → returns `Order` data.
2. Client calls `cancelOrder` → server validates OAuth2 scope → checks order status → updates status to `CANCELLED` if eligible → returns 204 or 409.

#### Dependencies
- OAuth2 token endpoint `https://auth.shop.example/token` for `backofficeToken`.
- No external data sources referenced in the specification.

#### Cross-cutting
- All endpoints use JSON media type.
- `couponCode` field is marked deprecated but still part of the `Order` schema.
- Security is applied only to the cancel operation; read operations are public.
