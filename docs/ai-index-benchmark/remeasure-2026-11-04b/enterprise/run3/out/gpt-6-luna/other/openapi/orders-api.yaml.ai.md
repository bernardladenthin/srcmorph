### orders-api.yaml
- H: 1.0
- C: 518FD0AB
- D: 2026-11-04T06:55:34Z
- T: 2026-11-04T12:38:48Z
- G: 
- A: 0.0.0
- X: file
- P: 30325B28
- N: 25FD89A2
---
**Facts (exact, whole file):** paths: 3; operations (approx): 3; operationIds: 3; parameters: 4; $ref references: 4; object schemas: 1; security schemes: 1; deprecated operations and fields: 1

> Specifies backoffice retrieval and cancellation of shop orders, including order status, totals, and coupon codes.

#### Purpose
- Lists and retrieves shop orders, and supports cancelling an order.

#### Type
- OpenAPI 3.0.3; `Shop Orders API` version `2.1.0`.

#### Operations
- `GET /orders` — `listOrders`; query parameters: `status` (`OrderStatus`), `limit` (integer, maximum `200`); `200`: array of `Order`.
- `GET /orders/{orderId}` — `getOrder`; path parameter: `orderId` (string, required); `200`: `Order`; `404`: no response schema specified.
- `POST /orders/{orderId}/cancel` — `cancelOrder`; path parameter: `orderId` (string, required); `204`, `409`: no response schemas specified.

#### Schemas
- `OrderStatus`: string enum `OPEN`, `PAID`, `SHIPPED`, `CANCELLED`.
- `Order`: object; required fields: `orderId` (string), `status` (`OrderStatus`), `total` (number, `double` format). Optional field: `couponCode` (string, deprecated).

#### Security and servers
- Server: `https://api.shop.example/backoffice`.
- `backofficeToken`: OAuth 2.0 client credentials; token URL `https://auth.shop.example/token`; scope `orders:write`. Required by `cancelOrder`.

#### Dependencies
