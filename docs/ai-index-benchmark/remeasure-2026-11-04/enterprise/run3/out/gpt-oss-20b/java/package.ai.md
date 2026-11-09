### java
- H: 1.0
- C: C0C0D071
- D: 2026-11-04T06:47:08Z
- T: 2026-11-04T08:24:00Z
- G: 
- A: 0.0.0
- X: package
- F: [shop/](shop/package.ai.md)
---
> A Jakarta EE back‑office service that manages customer orders, handling listing, retrieval, cancellation with refunds, and nightly cleanup of obsolete cancellations.

#### Purpose
- Expose RESTful endpoints for order management.
- Process order cancellations with payment refunds and audit logging.
- Schedule nightly purge of cancelled orders.

#### Responsibilities
- **Order retrieval** – list open orders, fetch by ID.
- **Order cancellation** – refund payment, update status, audit.
- **Scheduled maintenance** – nightly purge of old cancelled orders.

#### Key units
- `OrderResource` – JAX‑RS resource at `/orders`; methods: `openOrders()`, `find(id)`, `cancel(id)`, `purgeCancelled()`.
- `PaymentGateway` – injected service for processing refunds.
- `AuditLog` – injected logger for audit events.
- `EntityManager em` – JPA persistence context for `Order` entity queries.

#### Data flow
1. **Request** → `OrderResource` endpoint → `EntityManager` queries `Order` (named queries `Order.open`, `Order.purgeCancelled`).
2. **Cancellation**: `cancel(id)` → fetch `Order` → `payments.refund(reference, total)` → set status `CANCELLED` → `audit.record("order.cancelled")` → commit transaction.
3. **Purge**: scheduled `purgeCancelled()` → execute `Order.purgeCancelled` with `days` param → `audit.record("order.purged", count)`.

#### Dependencies
- Jakarta EE: `@Stateless`, `@Schedule`, `@RolesAllowed`, `@Transactional`, `@Inject`, `@PersistenceContext`.
- JAX‑RS: `@Path`, `@GET`, `@POST`, `@Produces`, `Response`.
- JPA: `EntityManager`, named queries on `Order`.
- Domain: `shop.Order`, `shop.OrderStatus`, `shop.PaymentGateway`, `shop.AuditLog`.

#### Cross-cutting
- Stateless session bean ensures thread safety.
- Container‑managed transactions; rollback on failure.
- Role‑based security (`backoffice`, `admin`) on all endpoints.
- Audit logging for cancellation and purge actions.
