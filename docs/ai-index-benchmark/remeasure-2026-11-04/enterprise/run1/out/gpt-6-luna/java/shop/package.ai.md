### java/shop
- H: 1.0
- C: 769C75B1
- D: 2026-11-04T06:47:08Z
- T: 2026-11-04T08:08:20Z
- G: 
- A: 0.0.0
- X: package
- F: [OrderResource.java](OrderResource.java.ai.md)
---
> Backoffice users can review orders, retrieve individual orders, cancel them with payment refunds, and automatically purge old cancelled orders.

#### Purpose
- Exposes order listing, lookup, and cancellation operations for backoffice users.
- Purges cancelled orders older than 30 days and records audit events.

#### Responsibilities
- Order API: list open orders, retrieve an order by ID, and cancel an order with a refund.
- Scheduled maintenance: purge aged cancelled orders and record the purge count.

#### Key units
- `OrderResource` — stateless JAX-RS resource at `/orders`, providing the order API and nightly purge.
- `PURGE_AFTER_DAYS` — sets the purge age to 30 days.

#### Data flow
- `openOrders()` runs `Order.open` and returns its result list; `find(id)` loads an `Order` and returns 404 if absent.
- `cancel(id)` loads the order, returns 404 if absent, or refunds its payment reference and total, marks it `CANCELLED`, records `order.cancelled`, and returns 204.
- `purgeCancelled()` runs `Order.purgeCancelled` with the age parameter, then records the number removed as `order.purged`.

#### Dependencies
- Persistence: injected `EntityManager`, `Order`, `OrderStatus`, and named queries `Order.open` and `Order.purgeCancelled`.
- Integrations: `PaymentGateway` for refunds and `AuditLog` for event recording.
- Jakarta EE APIs: JAX-RS `Response` and annotations, EJB annotations, persistence injection, and dependency injection.

#### Cross-cutting
- Access is restricted to the `backoffice` and `admin` roles.
- Cancellation uses `TransactionAttributeType.REQUIRED`; purging is scheduled daily at 02:15.
- Missing orders return HTTP 404; refund, persistence, and audit failures are not explicitly handled.
