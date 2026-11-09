### other/openapi
- H: 1.0
- C: 1372091D
- D: 2026-11-04T06:55:34Z
- T: 2026-11-04T08:22:10Z
- G: 
- A: 0.0.0
- X: package
- F: [orders-api.yaml](orders-api.yaml.ai.md)
---
> Defines how shop backoffice clients list and retrieve customer orders and request cancellation of orders that have not shipped.

#### Purpose
- Specifies the Shop Orders API contract for reading order data and cancelling orders.

#### Responsibilities
- Order access: list orders by optional status and limit, or retrieve an order by ID.
- Order cancellation: expose a cancellation operation for a specified order.

#### Key units
- `Shop Orders API` — OpenAPI 3.0.3 contract, version `2.1.0`.
- `listOrders` — `GET /orders`, returning an array of `Order`.
- `getOrder` — `GET /orders/{orderId}`, returning an `Order` or a `404`.
- `cancelOrder` — `POST /orders/{orderId}/cancel`, returning `204` or `409`.
- `OrderStatus` — enum of `OPEN`, `PAID`, `SHIPPED`, and `CANCELLED`.
- `Order` — object with required `orderId`, `status`, and `total`; optional deprecated `couponCode`.
- `backofficeToken` — OAuth 2.0 client-credentials security scheme with `orders:write` scope, required for cancellation.

#### Data flow
- Clients call the API at `https://api.shop.example/backoffice`; list and retrieve operations return order data, while cancellation accepts an order ID and returns a status response.

#### Cross-cutting
- `Order` responses share the `OrderStatus` enum; `couponCode` is deprecated.
- Cancellation requires OAuth 2.0 client credentials; the contract specifies `409` as a possible cancellation response.
