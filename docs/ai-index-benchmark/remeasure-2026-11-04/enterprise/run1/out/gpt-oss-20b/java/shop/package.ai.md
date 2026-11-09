### java/shop
- H: 1.0
- C: 769C75B1
- D: 2026-11-04T06:47:08Z
- T: 2026-11-04T08:11:17Z
- G: 
- A: 0.0.0
- X: package
- F: [OrderResource.java](OrderResource.java.ai.md)
---
> Provides a stateless JAX‑RS resource for back‑office order management, exposing CRUD‑like operations, cancellation with refund, and nightly purge of cancelled orders.

#### Purpose
- Expose order operations via REST.
- Handle order cancellation and audit logging.
- Schedule nightly purge of old cancelled orders.

#### Responsibilities
- **Order retrieval** – list open orders, fetch by ID.
- **Order cancellation** – refund payment, update status, audit.
- **Scheduled cleanup** – purge cancelled orders older than a threshold.

#### Key units
- `OrderResource` – stateless EJB, JAX‑RS resource at `/orders`, JSON output, role‑restricted to `backoffice`/`admin`.
- `EntityManager em` – JPA persistence context for `Order` entities.
- `PaymentGateway payments` – external service for refunds.
- `AuditLog audit` – audit trail recorder.
- `@Schedule` method `purgeCancelled()` – nightly cleanup.

#### Data flow
1. **GET /orders** → `openOrders()` → query `Order.open` → return `List<Order>`.
2. **GET /orders/{id}** → `find(id)` → `em.find(Order)` → 200 or 404.
3. **POST /orders/{id}/cancel** → `cancel(id)` → find order → `payments.refund()` → set status `CANCELLED` → `audit.record()` → 204.
4. **@Schedule** → `purgeCancelled()` → query `Order.purgeCancelled(days)` → bulk update → `audit.record()`.

#### Dependencies
- Jakarta EE: `Stateless`, `EJB`, `Schedule`, `TransactionAttribute`, `RolesAllowed`, `Inject`, `PersistenceContext`.
- JAX‑RS: `GET`, `POST`, `Path`, `PathParam`, `Produces`, `Response`, `MediaType`.
- JPA: `EntityManager`, `Order` entity.
- Domain services: `PaymentGateway`, `AuditLog`.
- Java collections: `List`.

#### Cross-cutting
- Stateless EJB ensures thread safety.
- Role‑based security via `@RolesAllowed`.
- Transactional boundaries handled by container (`@TransactionAttribute`).
- Audit logging invoked after state changes or scheduled actions.
