### java/shop
- H: 1.0
- C: 769C75B1
- D: 2026-11-04T06:47:08Z
- T: 2026-11-04T12:30:26Z
- G: 
- A: 0.0.0
- X: package
- F: [OrderResource.java](OrderResource.java.ai.md)
---
> Backoffice staff can manage order lookup and cancellation, issue refunds, audit order events, and automatically remove aged cancelled orders.

#### Purpose
- Exposes order listing, retrieval, and cancellation operations for backoffice users.
- Schedules cleanup of cancelled orders after a configured retention period.

#### Responsibilities
- **Order access:** List open orders and retrieve an order by ID.
- **Cancellation:** Refund payment, mark the order cancelled, and record an audit event.
- **Retention cleanup:** Periodically delete aged cancelled orders and audit the deletion count.
- **Access control:** Restrict endpoints to the `backoffice` and `admin` roles.

#### Key units
- `OrderResource` — stateless JAX-RS resource at `/orders`; implements order operations and scheduled cleanup.
- `PURGE_AFTER_DAYS` — retention period supplied to the cancelled-order purge query.

#### Data flow
- `openOrders()` executes `Order.open` and returns `List<Order>`; `find(id)` loads an order and returns `404` if absent or `200` with the order.
- `cancel(id)` loads the order, refunds its payment through `PaymentGateway`, sets `OrderStatus.CANCELLED`, records `order.cancelled`, and returns `204`; absent orders return `404`.
- `purgeCancelled()` runs at 02:15, executes `Order.purgeCancelled` with the retention-days parameter, and records `order.purged` with the removed-row count.

#### Dependencies
- Jakarta EE: JAX-RS, EJB, CDI injection, JPA, and security annotations.
- Project types: `Order`, `OrderStatus`, `PaymentGateway`, and `AuditLog`; named queries `Order.open` and `Order.purgeCancelled`.

#### Cross-cutting
- `OrderResource` is `@Stateless`; cancellation uses a required transaction.
- Scheduled cleanup uses a non-persistent EJB timer.
- Missing order IDs produce `404 NOT_FOUND`; refund and persistence failures are not caught here.
