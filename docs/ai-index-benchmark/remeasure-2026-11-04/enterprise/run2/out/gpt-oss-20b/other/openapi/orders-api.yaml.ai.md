### orders-api.yaml
- H: 1.0
- C: 518FD0AB
- D: 2026-11-04T06:55:34Z
- T: 2026-11-04T08:17:34Z
- G: 
- A: 0.0.0
- X: file
- P: 34DB4705
- N: 25FD89A2
---
**Facts (exact, whole file):** paths: 3; operations (approx): 3; operationIds: 3; parameters: 4; $ref references: 4; object schemas: 1; security schemes: 1; deprecated operations and fields: 1

> The API lets backoffice users list, retrieve, and cancel shop orders, exposing order status and total.

#### Purpose
- List orders with optional status filter and limit.
- Retrieve a single order by ID.
- Cancel an order (requires write scope).

#### Type
- OpenAPI 3.0.3  
- Title: `Shop Orders API`  
- Version: `2.1.0`  
- Server URL: `https://api.shop.example/backoffice`

#### Operations
- `GET /orders` – `listOrders` – query `status` (`OrderStatus`), `limit` (int, max 200); 200 → array of `Order`.
- `GET /orders/{orderId}` – `getOrder` – path `orderId` (string); 200 → `Order`; 404 → not found.
- `POST /orders/{orderId}/cancel` – `cancelOrder` – path `orderId` (string); security `backofficeToken` with scope `orders:write`; 204 → cancelled; 409 → already shipped.

#### Schemas
- `OrderStatus` – enum: `OPEN`, `PAID`, `SHIPPED`, `CANCELLED`.
- `Order` – object, required: `orderId`, `status`, `total`; properties:  
  - `orderId`: string  
  - `status`: `OrderStatus`  
  - `total`: number (double)  
  - `couponCode`: string (deprecated)

#### Security and servers
- Security scheme `backofficeToken`: OAuth2 client credentials, token URL `https://auth.shop.example/token`; scope `orders:write` (change orders).  
- Server: `https://api.shop.example/backoffice`.

#### Dependencies
- `$ref` to `#/components/schemas/OrderStatus` and `#/components/schemas/Order`.
