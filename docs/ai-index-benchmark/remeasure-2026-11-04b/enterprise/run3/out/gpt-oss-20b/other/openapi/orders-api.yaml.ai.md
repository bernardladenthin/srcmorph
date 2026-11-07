### orders-api.yaml
- H: 1.0
- C: 518FD0AB
- D: 2026-11-04T06:55:34Z
- T: 2026-11-04T12:41:52Z
- G: 
- A: 0.0.0
- X: file
- P: 30325B28
- N: 25FD89A2
---
**Facts (exact, whole file):** paths: 3; operations (approx): 3; operationIds: 3; parameters: 4; $ref references: 4; object schemas: 1; security schemes: 1; deprecated operations and fields: 1

> The API manages shop orders: listing, retrieving, and canceling them.

#### Purpose
- Provides endpoints to list, get, and cancel shop orders.

#### Type
- `OpenAPI 3.0.3`
- Title: `Shop Orders API`
- Version: `2.1.0`

#### Operations
- `GET /orders` – `listOrders`  
  - Query: `status` (`OrderStatus`), `limit` (integer, max 200)  
  - Response `200`: array of `Order`
- `GET /orders/{orderId}` – `getOrder`  
  - Path: `orderId` (string)  
  - Responses: `200` (`Order`), `404` (no such order)
- `POST /orders/{orderId}/cancel` – `cancelOrder`  
  - Path: `orderId` (string)  
  - Security: `backofficeToken` with scope `orders:write`  
  - Responses: `204` (cancelled), `409` (already shipped)

#### Schemas
- `OrderStatus`  
  - type: `string`  
  - enum: `OPEN`, `PAID`, `SHIPPED`, `CANCELLED`
- `Order`  
  - type: `object`  
  - required: `orderId`, `status`, `total`  
  - properties:  
    - `orderId`: `string`  
    - `status`: `$ref: '#/components/schemas/OrderStatus'`  
    - `total`: `number` (format `double`)  
    - `couponCode`: `string` (deprecated)

#### Security and servers
- Security scheme `backofficeToken`: OAuth2 client credentials, token URL `https://auth.shop.example/token`, scope `orders:write`  
- Server: `https://api.shop.example/backoffice`

#### Dependencies
- None
