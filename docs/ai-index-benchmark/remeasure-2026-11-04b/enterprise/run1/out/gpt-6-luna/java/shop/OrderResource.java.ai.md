### OrderResource.java
- H: 1.0
- C: 84470671
- D: 2026-11-04T06:47:08Z
- T: 2026-11-04T12:22:06Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
- N: 37CE394E
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 4; TODO/FIXME: 0; @Override: 0; methods (approx): 4; constructors: 0; field declarations (w/ modifier): 4; EJB session beans: 1; message-driven beans: 0; JPA entities and embeddables: 0; JPA named queries: 0; injection points: 3; JAX-RS resource paths: 3; HTTP method annotations: 3; servlets, filters and listeners: 0; SOAP endpoints: 0; transaction annotations: 1; EJB timers: 1; security annotations: 1

> Backoffice users can list and retrieve orders, cancel orders with payment refunds, and purge old cancelled orders.

#### Purpose
- `OrderResource` exposes order lookup and cancellation operations for the backoffice.
- A nightly job deletes cancelled orders older than 30 days and records the purge.

#### Type
- `public class OrderResource` — class; `@Stateless`, `@Path("/orders")`, `@Produces(MediaType.APPLICATION_JSON)`, `@RolesAllowed({"backoffice", "admin"})`; no declared supertypes.
- `PURGE_AFTER_DAYS` — package-private `static final int`, value `30`.

#### Input
- `find(long id)` and `cancel(long id)` receive the order ID from the `{id}` path parameter.
- Injected `EntityManager em` uses persistence unit `"shop"`; `PaymentGateway payments` and `AuditLog audit` are injected dependencies.
- `purgeCancelled()` uses `PURGE_AFTER_DAYS` as the `"days"` query parameter.

#### Output
- `openOrders()` returns the result list of `Order.open`.
- `find(long id)` returns HTTP 404 when no order exists; otherwise returns the order with HTTP 200.
- `cancel(long id)` returns HTTP 404 when no order exists; otherwise refunds payment, updates order status, records an audit event, and returns HTTP 204.
- `purgeCancelled()` executes the purge query and records the number removed.

#### Core logic
- `openOrders()` calls `em.createNamedQuery("Order.open", Order.class).getResultList()`.
- `find(long id)` calls `em.find(Order.class, id)`; builds `Response.status(Response.Status.NOT_FOUND).build()` for a missing order, otherwise `Response.ok(order).build()`.
- `cancel(long id)`:
  - Calls `em.find(Order.class, id)`; returns 404 if missing.
  - Calls `payments.refund(order.getPaymentReference(), order.getTotal())`.
  - Sets `order.setStatus(OrderStatus.CANCELLED)`.
  - Calls `audit.record("order.cancelled", id)`.
  - Returns `Response.noContent().build()`.
- `purgeCancelled()` runs `em.createNamedQuery("Order.purgeCancelled").setParameter("days", PURGE_AFTER_DAYS).executeUpdate()` and calls `audit.record("order.purged", removed)`.

#### Public API
- `openOrders()` -> `List<Order>` — list open orders.
- `find(long id)` -> `Response` — retrieve an order.
- `cancel(long id)` -> `Response` — refund and cancel an order.

#### Dependencies
- Jakarta APIs: `RolesAllowed`, `EJB`, `Schedule`, `Stateless`, `TransactionAttribute`, `TransactionAttributeType`, `Inject`, `EntityManager`, `PersistenceContext`, `GET`, `POST`, `Path`, `PathParam`, `Produces`, `MediaType`, `Response`.
- Java: `List`.
- Project types: `Order`, `OrderStatus`, `PaymentGateway`, `AuditLog`.

#### Exceptions / Errors
- Missing order IDs produce HTTP 404 responses in `find` and `cancel`.
- No exceptions are explicitly caught or thrown.

#### Concurrency
- `@Stateless` EJB; `cancel` uses `@TransactionAttribute(TransactionAttributeType.REQUIRED)`.
- `purgeCancelled()` is scheduled daily at 02:15 with `persistent = false`.
