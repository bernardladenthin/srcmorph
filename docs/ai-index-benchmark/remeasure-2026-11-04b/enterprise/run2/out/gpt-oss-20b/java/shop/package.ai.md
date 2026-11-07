### java/shop
- H: 1.0
- C: 769C75B1
- D: 2026-11-04T06:47:08Z
- T: 2026-11-04T12:35:34Z
- G: 
- A: 0.0.0
- X: package
- F: [OrderResource.java](OrderResource.java.ai.md)
---
> Provides REST endpoints for back‑office order management, including listing, retrieving, canceling orders, and a scheduled purge of old cancellations.

#### Purpose
- Exposes order operations via JAX‑RS.
- Handles cancellation and audit logging.
- Schedules nightly purge of cancelled orders.

#### Responsibilities
- REST API for order queries (`openOrders`, `find`).
- Order cancellation workflow (`cancel`).
- Scheduled cleanup of cancelled orders (`purgeCancelled`).
- Audit trail integration (`AuditLog`).

#### Key units
- **OrderResource** – stateless EJB, JAX‑RS resource at `/orders`, role‑restricted to `backoffice`/`admin`.
  - Fields: `EntityManager em`, `PaymentGateway payments`, `AuditLog audit`.
  - Methods: `openOrders()`, `find(long)`, `cancel(long)`, `purgeCancelled()`.

#### Data flow
- `openOrders()` → `em` query `Order.open` → `List<Order>`.
- `find(id)` → `em.find(Order)` → `Response` 200 or 404.
- `cancel(id)` → `em.find(Order)` → `payments.refund(...)` → update status → `audit.record(...)` → `Response` 204 or 404.
- `purgeCancelled()` → `em.createNamedQuery("Order.purgeCancelled")` → `executeUpdate()` → `audit.record(...)`.

#### Dependencies
- Jakarta EE annotations: `@Stateless`, `@Schedule`, `@RolesAllowed`, `@Inject`, `@EJB`, `@PersistenceContext`.
- JAX‑RS: `@Path`, `@GET`, `@POST`, `@Produces`, `@PathParam`.
- JPA: `EntityManager`, named query `Order.purgeCancelled`.
- External services: `shop.PaymentGateway`, `shop.AuditLog`.
- Domain entities: `shop.Order`, `shop.OrderStatus`.

#### Cross-cutting
- Stateless EJB ensures thread safety.
- Security via `@RolesAllowed`.
- Transactional behavior via `@TransactionAttribute`.
- Scheduled task (`@Schedule`) runs nightly for cleanup.
- Audit logging is central to all state changes.
