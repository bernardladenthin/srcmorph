### other/openapi
- H: 1.0
- C: 1372091D
- D: 2026-11-04T06:55:34Z
- T: 2026-11-04T12:31:37Z
- G: 
- A: 0.0.0
- X: package
- F: [orders-api.yaml](orders-api.yaml.ai.md)
---
> Defines the backoffice contract for listing, retrieving, and cancelling shop orders.

#### Purpose
- Specifies the Shop Orders API (`2.1.0`) for order lookup and cancellation.

#### Responsibilities
- Order access: list orders by optional status and limit, or retrieve an order by ID.
- Order cancellation: cancel by order ID, requiring OAuth authorization.

#### Key units
- `GET /orders` (`listOrders`) — returns an array of `Order` values.
- `GET /orders/{orderId}` (`getOrder`) — retrieves an `Order` by ID.
- `POST /orders/{orderId}/cancel` (`cancelOrder`) — cancels an order; documents `204` and `409` responses.
- `Order` — order ID, `OrderStatus`, total, and optional deprecated `couponCode`.
- `OrderStatus` — `OPEN`, `PAID`, `SHIPPED`, or `CANCELLED`.

#### Data flow
- Clients send order queries or an order ID to the backoffice API; successful lookup operations return `Order` data.
- Cancellation requests use an order ID and an authorized token, with success and conflict responses documented.

#### Dependencies
- Base server: `https://api.shop.example/backoffice`.
- OAuth 2.0 client-credentials token endpoint: `https://auth.shop.example/token`.

#### Cross-cutting
- `cancelOrder` requires `backofficeToken` with the `orders:write` scope.
- `listOrders` supports a `limit` capped at 200; `couponCode` is deprecated.
