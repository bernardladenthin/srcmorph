### OrderResource.java
- H: 1.0
- C: 84470671
- D: 2026-11-04T06:47:08Z
- T: 2026-11-04T12:37:27Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
- N: 37CE394E
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 4; TODO/FIXME: 0; @Override: 0; methods (approx): 4; constructors: 0; field declarations (w/ modifier): 4; EJB session beans: 1; message-driven beans: 0; JPA entities and embeddables: 0; JPA named queries: 0; injection points: 3; JAX-RS resource paths: 3; HTTP method annotations: 3; servlets, filters and listeners: 0; SOAP endpoints: 0; transaction annotations: 1; EJB timers: 1; security annotations: 1

> Backoffice users can list and inspect orders, cancel orders with refunds, and purge old cancelled orders nightly.

#### Purpose
- Provides secured order-management operations for open-order retrieval, order lookup, cancellation, and scheduled cleanup.
- Refunds payment and records audit events when an order is cancelled; records the number of orders purged.

#### Type
- `public class OrderResource` — class; `@Stateless`, `@Path("/orders")`, `@Produces(MediaType.APPLICATION_JSON)`, `@RolesAllowed({"backoffice", "admin"})`.
- No explicit supertypes or generics.

#### Input
- Injected `EntityManager em` from persistence unit `"shop"` via `@PersistenceContext`.
- Injected `PaymentGateway payments` via `@Inject`; `AuditLog audit` via `@EJB`.
- `find(long id)` and `cancel(long id)` receive an order ID from `{id}`.
- `purgeCancelled()` reads `PURGE_AFTER_DAYS` and passes it as the query parameter `days`.

#### Output
- `openOrders()` returns the result list of `Order.open`.
- `find(long id)` returns HTTP 404 when no order exists, otherwise HTTP 200 with the order.
- `cancel(long id)` returns HTTP 404 when no order exists; otherwise refunds payment, marks the order cancelled, audits the event, and returns HTTP 204.
- `purgeCancelled()` executes the purge update and audits the number removed.
- `cancel` runs with `TransactionAttributeType.REQUIRED`.

#### Core logic
- `openOrders()` calls `em.createNamedQuery("Order.open", Order.class).getResultList()`.
- `find(long id)` calls `em.find(Order.class, id)`; builds `Response.status(Response.Status.NOT_FOUND)` for a missing order or `Response.ok(order)` otherwise.
- `cancel(long id)` calls `em.find(Order.class, id)`; if found, calls `payments.refund(order.getPaymentReference(), order.getTotal())`, sets `OrderStatus.CANCELLED`, calls `audit.record("order.cancelled", id)`, and returns `Response.noContent()`.
- Scheduled `purgeCancelled()` runs at 02:15 with `persistent = false`; calls `em.createNamedQuery("Order.purgeCancelled").setParameter("days", PURGE_AFTER_DAYS).executeUpdate()`, then calls `audit.record("order.purged", removed)`.
- `PURGE_AFTER_DAYS` is `30`.

#### Public API
- `openOrders() -> List<Order>` — list open orders.
- `find(long id) -> Response` — retrieve order by ID.
- `cancel(long id) -> Response` — cancel order and refund payment.

#### Dependencies
- Jakarta APIs: `RolesAllowed`, `EJB`, `Schedule`, `Stateless`, `TransactionAttribute`, `TransactionAttributeType`, `Inject`, `EntityManager`, `PersistenceContext`, `GET`, `POST`, `Path`, `PathParam`, `Produces`, `MediaType`, `Response`.
- `java.util.List`.
- Application types: `Order`, `OrderStatus`, `PaymentGateway`, `AuditLog`.

#### Exceptions / Errors
- Missing orders produce HTTP 404 in `find` and `cancel`.
- No explicit exception handling or null checks beyond the missing-order checks.

#### Concurrency
- Stateless EJB; cancellation uses a required transaction.
- Purge is a non-persistent scheduled task.
