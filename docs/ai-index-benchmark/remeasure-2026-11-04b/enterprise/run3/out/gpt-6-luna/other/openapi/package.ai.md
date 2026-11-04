### other/openapi
- H: 1.0
- C: 1372091D
- D: 2026-11-04T06:55:34Z
- T: 2026-11-04T12:40:18Z
- G: 
- A: 0.0.0
- X: package
- F: [orders-api.yaml](orders-api.yaml.ai.md)
---
> Defines the backoffice API contract for retrieving shop orders and requesting order cancellations.

#### Purpose
- Describes order listing and lookup, including order status, totals, and optional coupon codes.
- Specifies order cancellation and its success and conflict responses.

#### Responsibilities
- Order retrieval: list orders with status and limit filters, or fetch an order by ID.
- Order cancellation: submit a cancellation request for an order.

#### Key units
- `Shop Orders API` — OpenAPI 3.0.3 contract, version `2.1.0`.
- `listOrders` — `GET /orders`, returning an array of `Order`.
- `getOrder` — `GET /orders/{orderId}`, returning an `Order` or a `404`.
- `cancelOrder` — `POST /orders/{orderId}/cancel`, returning `204` or `409`.
- `OrderStatus` — enum of `OPEN`, `PAID`, `SHIPPED`, and `CANCELLED`.
- `Order` — schema requiring `orderId`, `status`, and `total`; optional deprecated `couponCode`.

#### Data flow
- `GET /orders` accepts optional `status` and `limit` query parameters and returns orders.
- `GET /orders/{orderId}` uses the required order ID to retrieve one order.
- `POST /orders/{orderId}/cancel` uses the required order ID to request cancellation.

#### Dependencies
- Server: `https://api.shop.example/backoffice`.
- `cancelOrder` requires OAuth 2.0 client-credentials security via `backofficeToken`, with token URL `https://auth.shop.example/token` and scope `orders:write`.
