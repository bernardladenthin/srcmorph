### java
- H: 1.0
- C: C0C0D071
- D: 2026-11-04T06:47:08Z
- T: 2026-11-04T12:36:12Z
- G: 
- A: 0.0.0
- X: package
- F: [shop/](shop/package.ai.md)
---
> A back‑office order management service that exposes REST APIs for querying, canceling, and auditing orders, with nightly cleanup of cancelled records.

#### Purpose
- Provide JAX‑RS endpoints for order operations.
- Handle order cancellation and audit logging.
- Schedule nightly purge of cancelled orders.

#### Responsibilities
- **Order querying** – list open orders, retrieve by ID.
- **Order cancellation** – process refunds, update status, log audit.
- **Scheduled cleanup** – nightly purge of cancelled orders.
- **Audit trail** – record all state changes.

#### Key units
- **OrderResource** – stateless EJB, JAX‑RS resource at `/orders`; methods: `openOrders()`, `find(long)`, `cancel(long)`, `purgeCancelled()`.
- **PaymentGateway** – external service for processing refunds.
- **AuditLog** – central audit recorder.
- **Order** – JPA entity with named query `Order.purgeCancelled`.
- **OrderStatus** – enum of order states.

#### Data flow
1. `openOrders()` → JPA query `Order.open` → `List<Order>`.
2. `find(id)` → `em.find(Order)` → HTTP 200 or 404.
3. `cancel(id)` → `em.find(Order)` → `payments.refund(...)` → status update → `audit.record(...)` → HTTP 204 or 404.
4. `purgeCancelled()` → `em.createNamedQuery("Order.purgeCancelled")` → `executeUpdate()` → `audit.record(...)`.

#### Dependencies
- Jakarta EE: `@Stateless`, `@Schedule`, `@RolesAllowed`, `@Inject`, `@EJB`, `@PersistenceContext`.
- JAX‑RS: `@Path`, `@GET`, `@POST`, `@Produces`, `@PathParam`.
- JPA: `EntityManager`, named query `Order.purgeCancelled`.
- External services: `shop.PaymentGateway`, `shop.AuditLog`.
- Domain entities: `shop.Order`, `shop.OrderStatus`.

#### Cross-cutting
- Stateless EJB ensures thread safety.
- Security enforced by `@RolesAllowed`.
- Transactions via `@TransactionAttribute`.
- Scheduled nightly cleanup with `@Schedule`.
- Audit logging applied to all state changes.
