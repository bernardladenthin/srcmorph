### java
- H: 1.0
- C: C0C0D071
- D: 2026-11-04T06:47:08Z
- T: 2026-11-04T08:21:05Z
- G: 
- A: 0.0.0
- X: package
- F: [shop/](shop/package.ai.md)
---
> Backoffice order management provides secure order lookup, refund-backed cancellation with auditing, and scheduled cleanup of aged cancelled orders.

#### Purpose
- Exposes REST operations for managing shop orders.
- Removes cancelled orders older than 30 days during scheduled cleanup.

#### Responsibilities
- Order access: list open orders and retrieve an order by ID, returning `404 NOT_FOUND` when absent.
- Cancellation: refund the order total, set status to `CANCELLED`, and record an audit event.
- Cleanup: purge cancelled orders older than 30 days and audit the number removed.

#### Key units
- `OrderResource` — secured `/orders` REST resource for order operations and scheduled cleanup.
- `openOrders()` — retrieves results from the `Order.open` named query.
- `find(long id)` — loads an order or returns `404 NOT_FOUND`.
- `cancel(long id)` — refunds and cancels an order, returning `204 No Content` on success.
- `purgeCancelled()` — runs the `Order.purgeCancelled` update at 02:15.

#### Data flow
- `EntityManager` and `Order.open` supply open orders; `find(id)` loads an order by ID.
- `cancel(id)` uses the order’s payment reference and total with `PaymentGateway`, updates its status, and records `order.cancelled` through `AuditLog`.
- `purgeCancelled()` applies a 30-day cutoff and records `order.purged` with the affected-row count.

#### Dependencies
- Domain types: `Order`, `OrderStatus`.
- Collaborators: `EntityManager`, `PaymentGateway`, `AuditLog`.
- Jakarta EE persistence, transactions, EJB scheduling, REST, and security APIs.

#### Cross-cutting
- Access is restricted to the `backoffice` and `admin` roles.
- Cancellation uses `REQUIRED` transaction semantics.
- Cleanup runs at 02:15 with `persistent = false`.
- Refund, persistence, and audit failures are not handled in the resource.
