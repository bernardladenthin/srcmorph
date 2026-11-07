### other/openapi
- H: 1.0
- C: 1372091D
- D: 2026-11-04T06:55:34Z
- T: 2026-11-04T08:09:26Z
- G: 
- A: 0.0.0
- X: package
- F: [orders-api.yaml](orders-api.yaml.ai.md)
---
> Defines backoffice capabilities for browsing shop orders, viewing order details, and cancelling orders.

#### Purpose
- Specify the Shop Orders API contract for order retrieval and cancellation.
- Describe order status and total details, including the deprecated `couponCode` field.

#### Responsibilities
- Order access: list orders by optional status and limit, or retrieve one order by `orderId`.
- Order cancellation: expose a write-authorized cancellation operation for a specified order.

#### Key units
- `GET /orders` (`listOrders`) — returns an array of `Order` records, with a maximum `limit` of 200.
- `GET /orders/{orderId}` (`getOrder`) — returns an order by its required string identifier.
- `POST /orders/{orderId}/cancel` (`cancelOrder`) — cancels an order; documents `204` and `409` responses.
- `OrderStatus` — string enum: `OPEN`, `PAID`, `SHIPPED`, `CANCELLED`.
- `Order` — object requiring `orderId`, `status`, and numeric `total`; includes deprecated string `couponCode`.

#### Data flow
- Order listing accepts optional `status` and `limit` query parameters and returns `Order` objects.
- Order retrieval and cancellation use the required `orderId` path parameter; cancellation requires the `orders:write` scope.

#### Dependencies
- Backoffice API server: `https://api.shop.example/backoffice`.
- OAuth 2.0 client-credentials token endpoint: `https://auth.shop.example/token`.

#### Cross-cutting
- OpenAPI 3.0.3 contract, `Shop Orders API` version `2.1.0`.
- Cancellation is protected by the `backofficeToken` OAuth 2.0 security scheme with scope `orders:write`.
