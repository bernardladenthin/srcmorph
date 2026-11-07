### OrderResource.java
- H: 1.0
- C: 84470671
- D: 2026-11-04T06:47:08Z
- T: 2026-11-04T08:13:06Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
- N: 50211B16
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 4; TODO/FIXME: 0; @Override: 0; methods (approx): 4; constructors: 0; field declarations (w/ modifier): 4; EJB session beans: 1; message-driven beans: 0; JPA entities and embeddables: 0; JPA named queries: 0; injection points: 3; JAX-RS resource paths: 3; HTTP method annotations: 3; servlets, filters and listeners: 0; SOAP endpoints: 0; transaction annotations: 1; EJB timers: 1; security annotations: 1

> Backoffice users can list and retrieve orders, cancel them with a payment refund, and automatically purge old cancelled orders.

#### Purpose
- `OrderResource` serves order backoffice operations over the `/orders` REST path.
- Handles open-order lookup, cancellation, and nightly cleanup of cancelled orders.

#### Type
- `public class OrderResource` — class; annotated `@Stateless`, `@Path("/orders")`, `@Produces(MediaType.APPLICATION_JSON)`, and `@RolesAllowed({"backoffice", "admin"})`.
- `PURGE_AFTER_DAYS` — package-private `static final int`; value `30`.

#### Input
- `openOrders()` — no parameters; queries the `Order.open` named query.
- `find(long id)` — order ID from the `{id}` path parameter.
- `cancel(long id)` — order ID from the `{id}` path parameter.
- `purgeCancelled()` — no parameters; scheduled invocation.
- Injected `EntityManager em` from persistence unit `shop`, `PaymentGateway payments`, and `AuditLog audit`.
- Purge query receives `days = PURGE_AFTER_DAYS`.

#### Output
- `openOrders()` returns `List<Order>`.
- `find(long id)` and `cancel(long id)` return JAX-RS `Response`.
- `cancel` changes the order status and records an audit event; returns no-content on success.
- `purgeCancelled()` deletes matching orders and records the number removed.

#### Core logic
- `openOrders()` returns results from named query `Order.open`.
- `find(id)` looks up the `Order`; returns `404 NOT_FOUND` if absent, otherwise `200 OK` with the order.
- `cancel(id)` runs with `REQUIRED` transaction semantics:
  - Returns `404 NOT_FOUND` if the order does not exist.
  - Refunds its payment reference for its total.
  - Sets status to `OrderStatus.CANCELLED` and records `order.cancelled` with the ID.
  - Returns `204 No Content`.
- `purgeCancelled()` runs at 02:15 with `persistent = false`; executes `Order.purgeCancelled` using the 30-day threshold and records `order.purged` with the removal count.

#### Public API
- `openOrders()` -> `List<Order>` — list open orders.
- `find(long id)` -> `Response` — retrieve order by ID.
- `cancel(long id)` -> `Response` — refund and cancel order.

#### Dependencies
- `Order`, `OrderStatus`, `PaymentGateway`, `AuditLog`
- `EntityManager`
- Jakarta EJB: `EJB`, `Schedule`, `Stateless`, `TransactionAttribute`, `TransactionAttributeType`
- Jakarta injection and persistence: `Inject`, `PersistenceContext`
- Jakarta REST: `GET`, `POST`, `Path`, `PathParam`, `Produces`, `MediaType`, `Response`
- Jakarta security: `RolesAllowed`
- `java.util.List`

#### Exceptions / Errors
- Missing order IDs produce `404 NOT_FOUND` in `find` and `cancel`.
- No explicit exception handling or null checks beyond missing-order handling.
