### java/shop
- H: 1.0
- C: 769C75B1
- D: 2026-11-04T06:47:08Z
- T: 2026-11-04T12:39:07Z
- G: 
- A: 0.0.0
- X: package
- F: [OrderResource.java](OrderResource.java.ai.md)
---
> Backoffice staff can manage order lifecycles by viewing orders, cancelling them with payment refunds, and removing old cancellations.

#### Purpose
- Exposes secured order-management operations for backoffice and admin users.
- Coordinates cancellation refunds and audit records, and periodically purges cancelled orders.

#### Responsibilities
- **Order access:** List open orders and retrieve an order by ID, returning HTTP 404 when it is missing.
- **Cancellation:** Refund the order payment, mark the order cancelled, record an audit event, and return HTTP 204.
- **Scheduled cleanup:** Purge cancelled orders older than 30 days at 02:15 and audit the number removed.

#### Key units
- `OrderResource` — stateless JAX-RS resource at `/orders`, secured for `backoffice` and `admin`; provides order retrieval, cancellation, and scheduled cleanup.
- `Order.open` — named query used to list open orders.
- `Order.purgeCancelled` — named update query used to purge eligible cancelled orders.

#### Data flow
- `openOrders()` runs `Order.open` through the `shop` persistence unit and returns its result list.
- `find(id)` loads an `Order` by ID and returns either HTTP 404 or the order in HTTP 200.
- `cancel(id)` loads the order; if present, refunds its payment reference and total, marks it `CANCELLED`, records `order.cancelled`, and returns HTTP 204.
- `purgeCancelled()` supplies `PURGE_AFTER_DAYS` (30) to `Order.purgeCancelled`, then records the removed count as `order.purged`.

#### Dependencies
- `EntityManager` for order lookup and named queries; persistence unit `shop`.
- `PaymentGateway` for cancellation refunds and `AuditLog` for cancellation and purge events.
- Application types: `Order`, `OrderStatus`.
- Jakarta EE APIs for JAX-RS, injection, EJB, scheduling, transactions, and role-based security.

#### Cross-cutting
- `OrderResource` is a stateless EJB and all resource operations require the `backoffice` or `admin` role.
- Cancellation uses `TransactionAttributeType.REQUIRED`.
- The purge schedule is non-persistent; missing orders are handled with HTTP 404, with no other explicit exception handling noted.
