### java
- H: 1.0
- C: C0C0D071
- D: 2026-11-04T06:47:08Z
- T: 2026-11-04T12:24:03Z
- G: 
- A: 0.0.0
- X: package
- F: [shop/](shop/package.ai.md)
---
> Backoffice order operations provide access to open orders, cancellation with payment refunds and audit tracking, and scheduled cleanup of aged cancelled records.

#### Purpose
- Support authorized backoffice order listing, lookup, and cancellation.
- Purge cancelled orders older than 30 days and audit the cleanup.

#### Responsibilities
- **Order access:** list open orders and retrieve orders by ID, returning 404 when absent.
- **Cancellation:** refund payment, mark the order cancelled, and record an audit event.
- **Scheduled cleanup:** delete aged cancelled orders and audit the removal count.

#### Key units
- `OrderResource` — `/orders` JAX-RS resource and stateless EJB for backoffice order operations.
- `PURGE_AFTER_DAYS` — 30-day threshold for cancelled-order cleanup.

#### Data flow
- `openOrders()` queries `Order.open`; `find(id)` retrieves an `Order` through `EntityManager`.
- `cancel(id)` retrieves the order, refunds through `PaymentGateway`, sets `OrderStatus.CANCELLED`, and records `order.cancelled`.
- Daily `purgeCancelled()` runs `Order.purgeCancelled` with the age threshold and records `order.purged` with the deletion count.

#### Dependencies
- Project types: `Order`, `OrderStatus`, `PaymentGateway`, and `AuditLog`; persistence unit `shop`.
- Jakarta EE APIs for JAX-RS, EJB scheduling and transactions, security, dependency injection, and JPA.

#### Cross-cutting
- Access is restricted to the `backoffice` and `admin` roles.
- Cancellation uses a required transaction; cleanup runs on a non-persistent daily schedule at 02:15.
