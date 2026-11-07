### OrderResource.java
- H: 1.0
- C: 84470671
- D: 2026-11-04T06:47:08Z
- T: 2026-11-04T12:28:28Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
- N: 37CE394E
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 4; TODO/FIXME: 0; @Override: 0; methods (approx): 4; constructors: 0; field declarations (w/ modifier): 4; EJB session beans: 1; message-driven beans: 0; JPA entities and embeddables: 0; JPA named queries: 0; injection points: 3; JAX-RS resource paths: 3; HTTP method annotations: 3; servlets, filters and listeners: 0; SOAP endpoints: 0; transaction annotations: 1; EJB timers: 1; security annotations: 1

> Backoffice users can list and retrieve orders, cancel them with payment refunds, and automatically purge old cancelled orders.

#### Purpose
- Provides `/orders` backoffice operations for open-order listing, order lookup, cancellation, and scheduled cleanup.
- Restricts access to users with the `backoffice` or `admin` role.

#### Type
- `public class OrderResource` — class; annotated `@Stateless`, `@Path("/orders")`, `@Produces(MediaType.APPLICATION_JSON)`, `@RolesAllowed({"backoffice", "admin"})`.
- `PURGE_AFTER_DAYS` — package-private `static final int`; retention period for cancelled orders.

#### Input
- `find(long id)`, `cancel(long id)` — order ID from the `{id}` path parameter.
- `openOrders()` — named query `Order.open`.
- `purgeCancelled()` — named query `Order.purgeCancelled`; supplies `PURGE_AFTER_DAYS` as the `days` parameter.
- Injected resources: `EntityManager em` for persistence, `PaymentGateway payments` for refunds, and `AuditLog audit` for event recording.

#### Output
- `openOrders()` returns `List<Order>`.
- `find(long id)` and `cancel(long id)` return JAX-RS `Response`.
- `cancel(long id)` updates the order status and records an audit event; successful cancellation returns no content.
- `purgeCancelled()` deletes matching orders and records the removed-row count.

#### Core logic
- `openOrders()` executes `em.createNamedQuery("Order.open", Order.class).getResultList()`.
- `find(long id)` calls `em.find(Order.class, id)`; returns `404 NOT_FOUND` if absent, otherwise `200 OK` with the order.
- `cancel(long id)` runs with `@TransactionAttribute(TransactionAttributeType.REQUIRED)`:
  - Calls `em.find(Order.class, id)`; returns `404 NOT_FOUND` if absent.
  - Calls `payments.refund(order.getPaymentReference(), order.getTotal())`.
  - Sets `order.setStatus(OrderStatus.CANCELLED)`.
  - Calls `audit.record("order.cancelled", id)` and returns `204 No Content`.
- `purgeCancelled()` runs at 02:15 via `@Schedule(hour = "2", minute = "15", persistent = false)`:
  - Executes `em.createNamedQuery("Order.purgeCancelled").setParameter("days", PURGE_AFTER_DAYS).executeUpdate()`.
  - Stores the result in `removed` and calls `audit.record("order.purged", removed)`.

#### Public API
- `openOrders() -> List<Order>` — list open orders.
- `find(long id) -> Response` — retrieve order by ID.
- `cancel(long id) -> Response` — refund and cancel order.

#### Dependencies
- Jakarta: `RolesAllowed`, `EJB`, `Schedule`, `Stateless`, `TransactionAttribute`, `TransactionAttributeType`, `Inject`, `EntityManager`, `PersistenceContext`, `GET`, `POST`, `Path`, `PathParam`, `Produces`, `MediaType`, `Response`.
- Java: `List`.
- Project types: `Order`, `OrderStatus`, `PaymentGateway`, `AuditLog`.

#### Exceptions / Errors
- Missing order IDs produce `404 NOT_FOUND` in `find` and `cancel`.
- Refund and persistence failures are not caught in this file.

#### Concurrency
- `@Stateless` resource; cancellation uses a required transaction.
- Scheduled purge is non-persistent.
