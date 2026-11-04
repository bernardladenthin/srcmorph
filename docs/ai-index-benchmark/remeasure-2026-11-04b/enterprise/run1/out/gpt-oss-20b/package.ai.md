### ai
- H: 1.0
- C: 9F3D4F5A
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T12:28:03Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
---
> A stateless back‑office order service that exposes REST APIs for order retrieval, cancellation with payment refunds, and nightly cleanup of old cancelled orders, integrated into a larger e‑commerce platform with CI/CD, BPMN workflows, Camel integration, and Kubernetes deployment.

#### Purpose
- Provide RESTful endpoints for back‑office order management.
- Handle order cancellations with payment refunds and audit logging.
- Periodically purge cancelled orders older than 30 days.

#### Responsibilities
- **Order Retrieval** – list open orders, fetch by ID.
- **Order Cancellation** – process refunds, update status, record audit events.
- **Scheduled Maintenance** – nightly purge of cancelled orders, audit record.

#### Key units
- `OrderResource` – stateless JAX‑RS resource at `/orders`.
- `EntityManager em` – JPA persistence context for `shop.Order`.
- `PaymentGateway payments` – external service for refunds.
- `AuditLog audit` – audit event recorder.
- Named queries: `Order.open`, `Order.purgeCancelled`.

#### Data flow
1. `GET /orders` → `openOrders()` → `em.createNamedQuery("Order.open")` → `List<Order>`.
2. `GET /orders/{id}` → `find(id)` → `em.find(Order)` → `Response` (200/404).
3. `POST /orders/{id}/cancel` → `cancel(id)` → `payments.refund(...)`, status update, `audit.record(...)` → `Response` (204/404).
4. `@Schedule` nightly → `purgeCancelled()` → `em.createNamedQuery("Order.purgeCancelled")` → `audit.record(...)`.

#### Dependencies
- Jakarta EE: `@Stateless`, `@EJB`, `@Inject`, `@Schedule`, `@RolesAllowed`, JPA (`EntityManager`), JAX‑RS (`@Path`, `@GET`, `@POST`, `@Produces`).
- Domain: `shop.Order`, `shop.OrderStatus`, `shop.PaymentGateway`, `shop.AuditLog`.

#### Cross-cutting
- Stateless EJB ensures thread safety; container manages transactions.
- Security: `@RolesAllowed({"backoffice","admin"})` restricts access.
- Audit logging centralizes event recording for cancellations and purges.
- Scheduled task uses `@Schedule` for nightly cleanup without persistence.
