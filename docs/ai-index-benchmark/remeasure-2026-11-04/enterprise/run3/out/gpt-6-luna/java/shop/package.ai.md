### java/shop
- H: 1.0
- C: 769C75B1
- D: 2026-11-04T06:47:08Z
- T: 2026-11-04T08:20:59Z
- G: 
- A: 0.0.0
- X: package
- F: [OrderResource.java](OrderResource.java.ai.md)
---
> Backoffice order management supports order lookup, refund-backed cancellation, auditing, and scheduled removal of aged cancelled orders.

#### Purpose
- Exposes secured REST operations for managing shop orders.
- Cleans up cancelled orders older than 30 days on a nightly schedule.

#### Responsibilities
- Order access: list open orders and look up an order by ID, returning `404 NOT_FOUND` when absent.
- Cancellation: refund the order total through `PaymentGateway`, mark the order `CANCELLED`, and record an audit event.
- Scheduled cleanup: purge cancelled orders older than 30 days and audit the affected-row count.

#### Key units
- `OrderResource` — stateless `/orders` REST resource for backoffice order operations and scheduled cleanup.
- `openOrders()` — returns results from the `Order.open` named query.
- `find(long id)` — returns an order or `404 NOT_FOUND`.
- `cancel(long id)` — refunds and cancels an order, returning `204 No Content` on success.
- `purgeCancelled()` — executes the `Order.purgeCancelled` update at 02:15.

#### Data flow
- `openOrders()` retrieves `Order` results via `EntityManager` and the `Order.open` named query.
- `find(id)` loads an `Order` by ID and maps absence to `404`.
- `cancel(id)` loads the order, requests a refund using its payment reference and total, updates its status, and records `order.cancelled`.
- `purgeCancelled()` runs the purge update with a 30-day cutoff and records `order.purged` with the removed-row count.

#### Dependencies
- Domain types: `Order`, `OrderStatus`.
- Injected collaborators: `EntityManager` for persistence, `PaymentGateway` for refunds, and `AuditLog` for events.
- Jakarta EE: EJB, persistence, transaction, and scheduling APIs.
- Jakarta REST: resource paths, HTTP methods, and `Response`; Jakarta Security: `RolesAllowed`.

#### Cross-cutting
- Access is restricted to the `backoffice` and `admin` roles.
- Cancellation uses `REQUIRED` transaction semantics.
- Cleanup is scheduled for 02:15 with `persistent = false`.
- Missing orders produce `404 NOT_FOUND`; refund, persistence, and audit failures are not handled in this class.
