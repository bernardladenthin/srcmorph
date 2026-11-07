### java
- H: 1.0
- C: C0C0D071
- D: 2026-11-04T06:47:08Z
- T: 2026-11-04T12:42:07Z
- G: 
- A: 0.0.0
- X: package
- F: [shop/](shop/package.ai.md)
---
> A stateless REST service that manages back‑office orders, providing listing, detail retrieval, cancellation with payment refund, and nightly cleanup of old cancelled orders.

#### Purpose
- Expose a secure REST API for back‑office order management.
- Automate order cancellation and periodic purge of obsolete records.

#### Responsibilities
- **Order retrieval** – list open orders, fetch a specific order.
- **Order cancellation** – process payment refunds, update status, audit the event.
- **Scheduled maintenance** – nightly purge of cancelled orders older than 30 days.

#### Key units
- `OrderResource` – JAX‑RS resource, `@Stateless`, secured for `backoffice`/`admin`.
- `PaymentGateway` – injected service for processing refunds.
- `AuditLog` – EJB used to record audit events.
- `EntityManager` – JPA persistence context for `Order` entities.
- Named queries: `"Order.open"` (open orders), `"Order.purgeCancelled"` (old cancelled orders).

#### Data flow
1. **GET /orders** → `openOrders()` → executes `"Order.open"` → returns `List<Order>`.
2. **GET /orders/{id}** → `find(id)` → `em.find(Order)` → 200 with `Order` or 404.
3. **POST /orders/{id}/cancel** → `cancel(id)` → find order → `payments.refund(...)` → set status to `CANCELLED` → `audit.record(...)` → 204.
4. **@Schedule purgeCancelled()** → runs nightly → executes `"Order.purgeCancelled"` with `days=30` → bulk delete → `audit.record(...)`.

#### Dependencies
- Jakarta EE: `@Stateless`, `@EJB`, `@Inject`, `@PersistenceContext`, `@Schedule`, `@RolesAllowed`, JAX‑RS annotations.
- JPA: `EntityManager`, named queries on `Order`.
- Domain: `shop.Order`, `shop.OrderStatus`, `shop.PaymentGateway`, `shop.AuditLog`.

#### Cross-cutting
- **Security** – `@RolesAllowed({"backoffice","admin"})` on the resource.
- **Transactions** – container‑managed, `@TransactionAttribute` defaults to required.
- **Scheduling** – `@Schedule` for nightly purge, asynchronous execution.
- **Audit** – every state change (cancel, purge) recorded via `AuditLog`.
- **Error handling** – 404 responses for missing orders; otherwise rely on container rollback for runtime exceptions.
