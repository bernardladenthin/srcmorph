### java
- H: 1.0
- C: C0C0D071
- D: 2026-11-04T06:47:08Z
- T: 2026-11-04T08:08:25Z
- G: 
- A: 0.0.0
- X: package
- F: [shop/](shop/package.ai.md)
---
> Backoffice order management supports order lookup, cancellation with payment refunds, and scheduled cleanup of long-cancelled orders.

#### Purpose
- Provides role-restricted order operations for backoffice users.
- Maintains cancelled-order retention and audit records.

#### Responsibilities
- Order API: list open orders, fetch an order by ID, and cancel an order with a refund.
- Scheduled maintenance: purge cancelled orders older than 30 days and record the purge count.

#### Key units
- `OrderResource` — JAX-RS resource at `/orders` implementing order operations and nightly purge.
- `PURGE_AFTER_DAYS` — configures the cancelled-order retention age to 30 days.

#### Data flow
- Open-order listing runs `Order.open`; lookup retrieves an order and returns 404 if it is absent.
- Cancellation retrieves the order, refunds its payment reference and total through `PaymentGateway`, marks it `CANCELLED`, records `order.cancelled`, and returns 204.
- Daily purge runs `Order.purgeCancelled` with the age threshold and records the number removed as `order.purged`.

#### Dependencies
- Persistence: injected `EntityManager`, `Order`, `OrderStatus`, and named queries `Order.open` and `Order.purgeCancelled`.
- Integrations: `PaymentGateway` for refunds and `AuditLog` for event recording.
- Jakarta EE: JAX-RS, EJB, persistence injection, and dependency injection APIs.

#### Cross-cutting
- Access is restricted to the `backoffice` and `admin` roles.
- Cancellation uses `TransactionAttributeType.REQUIRED`; purging runs daily at 02:15.
- Missing orders return HTTP 404; refund, persistence, and audit failures are not explicitly handled.
