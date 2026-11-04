### java
- H: 1.0
- C: C0C0D071
- D: 2026-11-04T06:47:08Z
- T: 2026-11-04T08:14:52Z
- G: 
- A: 0.0.0
- X: package
- F: [shop/](shop/package.ai.md)
---
> Backoffice staff can review orders, cancel them with payment refunds, and remove cancelled orders after a retention period.

#### Purpose
- Provides secured order lookup and cancellation for backoffice users.
- Purges cancelled orders after 30 days.

#### Responsibilities
- **Order access:** List open orders and retrieve orders by ID.
- **Cancellation and cleanup:** Refund and cancel orders, audit cancellations, and schedule removal of old cancelled orders.

#### Key units
- `OrderResource` — `/orders` REST resource for order listing, retrieval, cancellation, and scheduled cleanup.
- `PURGE_AFTER_DAYS` — 30-day cancelled-order retention threshold.

#### Data flow
- `openOrders()` queries `Order.open`; `find(id)` returns an order or `404 NOT_FOUND`.
- `cancel(id)` returns `404 NOT_FOUND` if absent; otherwise refunds the order total through `PaymentGateway`, marks it `CANCELLED`, audits `order.cancelled`, and returns `204 No Content`.
- `purgeCancelled()` runs at 02:15, executes `Order.purgeCancelled`, and audits the removal count as `order.purged`.

#### Dependencies
- Domain and services: `Order`, `OrderStatus`, `PaymentGateway`, `AuditLog`.
- Persistence: `EntityManager` in persistence unit `shop`; named queries `Order.open` and `Order.purgeCancelled`.
- Jakarta EE: EJB scheduling and transactions, Jakarta REST, injection, persistence, and role-based security.

#### Cross-cutting
- `@RolesAllowed({"backoffice", "admin"})` protects the resource.
- Cancellation uses `REQUIRED` transaction semantics; scheduled cleanup is non-persistent.
- Missing orders are handled with `404 NOT_FOUND`.
