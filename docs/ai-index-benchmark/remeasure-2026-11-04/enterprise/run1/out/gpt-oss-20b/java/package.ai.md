### java
- H: 1.0
- C: C0C0D071
- D: 2026-11-04T06:47:08Z
- T: 2026-11-04T08:11:21Z
- G: 
- A: 0.0.0
- X: package
- F: [shop/](shop/package.ai.md)
---
> A stateless JAX‑RS service that manages back‑office order lifecycle, including retrieval, cancellation with payment refund, and nightly cleanup of stale cancelled orders.

#### Purpose
- Expose order CRUD‑like operations via REST.
- Process order cancellations with payment refunds and audit logging.
- Schedule nightly purge of cancelled orders older than a configurable threshold.

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
