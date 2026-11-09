### java
- H: 1.0
- C: C0C0D071
- D: 2026-11-04T06:47:08Z
- T: 2026-11-04T12:30:31Z
- G: 
- A: 0.0.0
- X: package
- F: [shop/](shop/package.ai.md)
---
> Backoffice staff can inspect and cancel orders, issue refunds, audit order changes, and automatically purge cancelled orders after a retention period.

#### Purpose
- Provides role-restricted backoffice order lookup and cancellation.
- Removes aged cancelled orders through scheduled retention cleanup.

#### Responsibilities
- **Order access:** List open orders and retrieve orders by ID.
- **Cancellation and audit:** Refund payment, mark orders cancelled, and record cancellation events.
- **Retention cleanup:** Purge cancelled orders on schedule and audit the removal count.
- **Access control:** Restrict order endpoints to `backoffice` and `admin` roles.

#### Key units
- `OrderResource` — stateless JAX-RS resource at `/orders` for order operations and scheduled cleanup.
- `PURGE_AFTER_DAYS` — retention period passed to the cancelled-order purge query.
- `Order.open` — named query for listing open orders.
- `Order.purgeCancelled` — named query for purging aged cancelled orders.

#### Data flow
- `openOrders()` runs `Order.open` and returns a `List<Order>`; `find(id)` returns `404` when absent or `200` with the order.
- `cancel(id)` loads the order, refunds through `PaymentGateway`, sets `OrderStatus.CANCELLED`, records `order.cancelled`, and returns `204`; missing orders return `404`.
- `purgeCancelled()` runs at 02:15, executes `Order.purgeCancelled` with the retention period, and records `order.purged` with the removed-row count.

#### Dependencies
- Jakarta EE: JAX-RS, EJB, CDI injection, JPA, and security annotations.
- Project types: `Order`, `OrderStatus`, `PaymentGateway`, and `AuditLog`.

#### Cross-cutting
- Cancellation uses a required transaction; scheduled cleanup uses a non-persistent EJB timer.
- Missing orders produce `404 NOT_FOUND`; refund and persistence failures are not caught by the resource.
