### orders-api.yaml
- H: 1.0
- C: 518FD0AB
- D: 2026-11-04T06:55:34Z
- T: 2026-11-04T08:20:40Z
- G: 
- A: 0.0.0
- X: file
- P: 34DB4705
- N: 25FD89A2
---
**Facts (exact, whole file):** paths: 3; operations (approx): 3; operationIds: 3; parameters: 4; $ref references: 4; object schemas: 1; security schemes: 1; deprecated operations and fields: 1

> Shop backoffice clients list and retrieve customer orders, inspect their status and total, and cancel orders that have not shipped.

#### Purpose
- Specifies read and cancellation operations for shop backoffice orders.

#### Type
- OpenAPI 3.0.3; API `Shop Orders API`, version `2.1.0`.

#### Operations
- `GET /orders` — `listOrders`; query parameters `status` (`OrderStatus`) and `limit` (integer, maximum 200); response `200`: array of `Order`.
- `GET /orders/{orderId}` — `getOrder`; required path parameter `orderId` (string); response `200`: `Order`; response `404`: no schema specified.
- `POST /orders/{orderId}/cancel` — `cancelOrder`; required path parameter `orderId` (string); response `204`: no schema specified; response `409`: no schema specified.

#### Schemas
- `OrderStatus` — string enum: `OPEN`, `PAID`, `SHIPPED`, `CANCELLED`.
- `Order` — object; required fields: `orderId`, `status`, `total`; fields: `orderId` (string), `status` (`OrderStatus`), `total` (number, format `double`), `couponCode` (string, deprecated).

#### Security and servers
- Server: `https://api.shop.example/backoffice`.
- `backofficeToken` — OAuth 2.0 client credentials; token URL `https://auth.shop.example/token`; scope `orders:write`; required by `POST /orders/{orderId}/cancel`.

#### Dependencies
- None external.
