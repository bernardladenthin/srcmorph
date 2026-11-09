### java/shop
- H: 1.0
- C: 769C75B1
- D: 2026-11-04T06:47:08Z
- T: 2026-11-04T12:23:57Z
- G: 
- A: 0.0.0
- X: package
- F: [OrderResource.java](OrderResource.java.ai.md)
---
> Backoffice order operations support querying open orders, retrieving and refunding cancellations, and purging aged cancelled records.

#### Purpose
- Provide authorized backoffice access to order listing, lookup, and cancellation.
- Automatically remove cancelled orders older than 30 days and audit the purge.

#### Responsibilities
- **Order access:** list open orders and retrieve an order by ID, returning 404 when absent.
- **Cancellation:** refund the order payment, mark the order cancelled, and record an audit event.
- **Scheduled cleanup:** purge cancelled orders older than 30 days and audit the number removed.

#### Key units
- `OrderResource` — `/orders` JSON JAX-RS resource and stateless EJB for backoffice order operations.
- `PURGE_AFTER_DAYS` — 30-day age threshold used by the cleanup query.

#### Data flow
- `openOrders()` retrieves results from the `Order.open` named query.
- `find(id)` looks up an `Order` through `EntityManager`, returning either 404 or the order.
- `cancel(id)` looks up the order; if found, refunds via `PaymentGateway`, sets `OrderStatus.CANCELLED`, records `order.cancelled`, and returns 204.
- `purgeCancelled()` runs `Order.purgeCancelled` with the 30-day threshold and records `order.purged` with the removal count.

#### Dependencies
- Jakarta EE APIs for JAX-RS, EJB scheduling and transactions, security, dependency injection, and JPA.
- Project types: `Order`, `OrderStatus`, `PaymentGateway`, and `AuditLog`; persistence unit `shop`.

#### Cross-cutting
- Resource access is restricted to `backoffice` and `admin` roles.
- `cancel` uses a required transaction; the stateless EJB runs a non-persistent daily schedule at 02:15 for cleanup.
