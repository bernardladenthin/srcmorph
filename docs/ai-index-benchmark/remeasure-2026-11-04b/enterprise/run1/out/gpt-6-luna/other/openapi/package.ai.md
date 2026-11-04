### other/openapi
- H: 1.0
- C: 1372091D
- D: 2026-11-04T06:55:34Z
- T: 2026-11-04T12:25:07Z
- G: 
- A: 0.0.0
- X: package
- F: [orders-api.yaml](orders-api.yaml.ai.md)
---
> Defines the backoffice contract for listing, retrieving, and cancelling shop orders.

#### Purpose
- Specify the Shop Orders API (`2.1.0`) for reading orders and requesting cancellation.

#### Responsibilities
- Expose order listing and retrieval by ID, including status filtering and a bounded result limit.
- Expose order cancellation, protected by an OAuth 2.0 write scope.

#### Key units
- `GET /orders` (`listOrders`) — returns an array of `Order` records, optionally filtered by `OrderStatus`.
- `GET /orders/{orderId}` (`getOrder`) — retrieves an order by ID.
- `POST /orders/{orderId}/cancel` (`cancelOrder`) — requests cancellation; documents `204` and `409` responses.
- `Order` — order ID, status, and total; also includes deprecated `couponCode`.
- `OrderStatus` — `OPEN`, `PAID`, `SHIPPED`, or `CANCELLED`.

#### Data flow
- Backoffice clients call the API at `https://api.shop.example/backoffice`; listing and retrieval return `Order` data, while cancellation requires authorization and returns a status response.

#### Dependencies
- `Order` references `OrderStatus`; the list and retrieval operations return `Order` data.
- `backofficeToken` uses OAuth 2.0 client credentials from `https://auth.shop.example/token`, with `orders:write` required for cancellation.

#### Cross-cutting
- OpenAPI 3.0.3 contract; cancellation documents a `409` response, and order lookup documents a `404` response.
