### java/shop
- H: 1.0
- C: 769C75B1
- D: 2026-11-04T06:47:08Z
- T: 2026-11-04T08:14:47Z
- G: 
- A: 0.0.0
- X: package
- F: [OrderResource.java](OrderResource.java.ai.md)
---
> Backoffice users can inspect orders, cancel them with payment refunds, and automatically remove long-cancelled orders.

#### Purpose
- Exposes order lookup and cancellation operations for backoffice users.
- Purges cancelled orders after 30 days.

#### Responsibilities
- **Order access:** List open orders and retrieve an order by ID.
- **Cancellation and cleanup:** Refund and cancel orders, audit cancellations, and periodically purge old cancelled orders while auditing the removal count.

#### Key units
- `OrderResource` — `/orders` REST resource for order listing, retrieval, cancellation, and scheduled cleanup.
- `PURGE_AFTER_DAYS` — 30-day retention threshold for cancelled orders.

#### Data flow
- `openOrders()` queries `Order.open` and returns `List<Order>`; `find(id)` returns the matching order or `404 NOT_FOUND`.
- `cancel(id)` returns `404 NOT_FOUND` if absent; otherwise refunds the payment reference for the order total, sets `OrderStatus.CANCELLED`, records `order.cancelled`, and returns `204 No Content`.
- `purgeCancelled()` runs at 02:15, executes `Order.purgeCancelled` with the retention threshold, and records `order.purged` with the removal count.

#### Dependencies
- Domain and services: `Order`, `OrderStatus`, `PaymentGateway`, `AuditLog`.
- Persistence: `EntityManager` from persistence unit `shop`; named queries `Order.open` and `Order.purgeCancelled`.
- Jakarta EE APIs: EJB scheduling, transactions, and stateless session beans; Jakarta REST; injection and persistence; role-based security.

#### Cross-cutting
- `@RolesAllowed({"backoffice", "admin"})` protects the resource.
- Cancellation uses `REQUIRED` transaction semantics; cleanup uses a non-persistent EJB schedule.
- Missing orders are handled with `404 NOT_FOUND`; no other explicit exception handling is described.
