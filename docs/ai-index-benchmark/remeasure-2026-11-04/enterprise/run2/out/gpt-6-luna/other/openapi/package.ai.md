### other/openapi
- H: 1.0
- C: 1372091D
- D: 2026-11-04T06:55:34Z
- T: 2026-11-04T08:15:59Z
- G: 
- A: 0.0.0
- X: package
- F: [orders-api.yaml](orders-api.yaml.ai.md)
---
> Provides backoffice users with API access to list, retrieve, and cancel shop orders.

#### Purpose
- Defines the Shop Orders API for reading orders and changing their status through cancellation.

#### Responsibilities
- Expose order-listing and order-retrieval operations, with status and result-limit filtering for listings.
- Expose order cancellation, protected by OAuth 2.0 authorization.
- Define the order response shape and allowed order statuses.

#### Key units
- `GET /orders` (`listOrders`) — returns an array of orders; accepts optional `status` and `limit` query parameters.
- `GET /orders/{orderId}` (`getOrder`) — retrieves an order by ID.
- `POST /orders/{orderId}/cancel` (`cancelOrder`) — requests cancellation; requires `orders:write` scope.
- `Order` — schema requiring `orderId`, `status`, and `total`; includes deprecated `couponCode`.
- `OrderStatus` — enum of `OPEN`, `PAID`, `SHIPPED`, and `CANCELLED`.

#### Data flow
- Backoffice clients send requests to the API server; list and retrieval operations return `Order` data, while cancellation returns status-only responses.

#### Dependencies
- API server: `https://api.shop.example/backoffice`.
- OAuth 2.0 client-credentials token service: `https://auth.shop.example/token`; cancellation requires `backofficeToken` with `orders:write` scope.

#### Cross-cutting
- OpenAPI 3.0.3 specification, version `2.1.0`.
- Listing `limit` is an integer capped at 200; order retrieval and cancellation identify orders by string `orderId`.
- `Order.couponCode` is deprecated.
