### java/shop
- H: 1.0
- C: 769C75B1
- D: 2026-11-04T06:47:08Z
- T: 2026-11-04T12:26:51Z
- G: 
- A: 0.0.0
- X: package
- F: [OrderResource.java](OrderResource.java.ai.md)
---
> Provides a stateless RESTful service for back‑office order management, handling order retrieval, cancellation, and nightly cleanup of old cancelled orders.

#### Purpose
- Expose back‑office order APIs.
- Process order cancellations with payment refunds and audit logging.
- Periodically purge cancelled orders older than 30 days.

#### Responsibilities
- **Order retrieval** – list open orders, fetch by ID.
- **Order cancellation** – refund payment, update status, audit event.
- **Scheduled maintenance** – nightly purge of cancelled orders, audit record.

#### Key units
- `OrderResource` – stateless EJB, JAX‑RS resource at `/orders`.
- `EntityManager em` – JPA persistence context for `Order` entity.
- `PaymentGateway payments` – external payment service for refunds.
- `AuditLog audit` – audit event recorder.
- Named queries: `Order.open`, `Order.purgeCancelled`.

#### Data flow
1. **GET /orders** → `openOrders()` → `em.createNamedQuery("Order.open")` → `List<Order>`.
2. **GET /orders/{id}** → `find(id)` → `em.find(Order)` → `Response` (200 or 404).
3. **POST /orders/{id}/cancel** → `cancel(id)` → `payments.refund(...)`, update status, `audit.record(...)` → `Response` (204 or 404).
4. **@Schedule nightly** → `purgeCancelled()` → `em.createNamedQuery("Order.purgeCancelled")` → `audit.record(...)`.

#### Dependencies
- Jakarta EE: `@Stateless`, `@EJB`, `@Inject`, `@Schedule`, `@RolesAllowed`, JPA (`EntityManager`, `@PersistenceContext`), JAX‑RS (`@Path`, `@GET`, `@POST`, `@Produces`, `Response`).
- Domain: `shop.Order`, `shop.OrderStatus`, `shop.PaymentGateway`, `shop.AuditLog`.

#### Cross-cutting
- Stateless EJB ensures thread safety; container manages transactions.
- Security: `@RolesAllowed({"backoffice","admin"})` restricts access.
- Audit logging centralizes event recording for cancellations and purges.
- Scheduled task uses `@Schedule` for nightly cleanup without persistence.
