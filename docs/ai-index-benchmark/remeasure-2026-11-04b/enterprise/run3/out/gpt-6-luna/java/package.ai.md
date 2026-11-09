### java
- H: 1.0
- C: C0C0D071
- D: 2026-11-04T06:47:08Z
- T: 2026-11-04T12:39:13Z
- G: 
- A: 0.0.0
- X: package
- F: [shop/](shop/package.ai.md)
---
> Backoffice staff can manage order lifecycles, including retrieving open orders, processing cancellation refunds, and purging aged cancellations.

#### Purpose
- Provides secured order-management operations for backoffice and admin users.
- Coordinates order cancellation with payment refunds and audit logging, and removes cancelled orders older than 30 days.

#### Responsibilities
- **Order access:** List open orders and retrieve orders by ID, returning HTTP 404 when an order is missing.
- **Cancellation:** Refund payment, mark the order `CANCELLED`, record an audit event, and return HTTP 204.
- **Scheduled cleanup:** Purge eligible cancelled orders at 02:15 and audit the removal count.

#### Key units
- `OrderResource` — stateless JAX-RS resource at `/orders` for retrieval, cancellation, and scheduled cleanup.
- `Order.open` — named query for listing open orders.
- `Order.purgeCancelled` — named update query for purging eligible cancelled orders.

#### Data flow
- `openOrders()` executes `Order.open` through the `shop` persistence unit and returns its results.
- `find(id)` loads an `Order` and returns HTTP 200 with the order or HTTP 404.
- `cancel(id)` loads the order, refunds its payment reference and total, marks it cancelled, records `order.cancelled`, and returns HTTP 204.
- `purgeCancelled()` passes the 30-day threshold to `Order.purgeCancelled` and records the removed count as `order.purged`.

#### Dependencies
- `EntityManager` and the `shop` persistence unit for order lookup and named queries.
- `PaymentGateway` for refunds; `AuditLog` for cancellation and purge events.
- Application types `Order` and `OrderStatus`; Jakarta EE APIs for REST, injection, EJB, scheduling, transactions, and role-based security.

#### Cross-cutting
- All resource operations require the `backoffice` or `admin` role.
- Cancellation uses `TransactionAttributeType.REQUIRED`; the purge schedule is non-persistent.
- Missing orders produce HTTP 404; no other explicit exception handling is noted.
