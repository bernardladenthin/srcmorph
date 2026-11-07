### OrderResource.java
- H: 1.0
- C: 84470671
- D: 2026-11-04T06:47:08Z
- T: 2026-11-04T08:19:22Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
- N: 50211B16
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 4; TODO/FIXME: 0; @Override: 0; methods (approx): 4; constructors: 0; field declarations (w/ modifier): 4; EJB session beans: 1; message-driven beans: 0; JPA entities and embeddables: 0; JPA named queries: 0; injection points: 3; JAX-RS resource paths: 3; HTTP method annotations: 3; servlets, filters and listeners: 0; SOAP endpoints: 0; transaction annotations: 1; EJB timers: 1; security annotations: 1

> Backoffice users can list and look up orders, cancel orders with payment refunds, and purge old cancelled orders nightly.

#### Purpose
- Provides the order backoffice REST operations and scheduled cleanup for cancelled orders.

#### Type
- `public class OrderResource` — stateless Jakarta EE class; annotated `@Stateless`, `@Path("/orders")`, `@Produces(MediaType.APPLICATION_JSON)`, and `@RolesAllowed({"backoffice", "admin"})`.

#### Input
- Injected `EntityManager em` for persistence unit `"shop"`, `PaymentGateway payments`, and `AuditLog audit`.
- `find(long id)` and `cancel(long id)` receive an order ID from the path.
- `purgeCancelled()` uses `PURGE_AFTER_DAYS`, set to `30`.

#### Output
- `openOrders()` returns `List<Order>` from the `"Order.open"` named query.
- `find(long id)` and `cancel(long id)` return JAX-RS `Response` values.
- `cancel` updates the order status, requests a refund, and records an audit event.
- `purgeCancelled()` executes the `"Order.purgeCancelled"` update and records the removed-row count.

#### Core logic
- `openOrders()` retrieves and returns the result list of `"Order.open"`.
- `find(id)` loads the order by ID; returns `404 NOT_FOUND` if absent, otherwise `200 OK` with the order.
- `cancel(id)` runs with `REQUIRED` transaction semantics; returns `404 NOT_FOUND` if absent, otherwise refunds the payment reference for the order total, sets status to `CANCELLED`, records `"order.cancelled"` with the ID, and returns `204 No Content`.
- `purgeCancelled()` runs at 02:15 with `persistent = false`; deletes cancelled orders older than 30 days and records `"order.purged"` with the affected-row count.

#### Public API
- `openOrders() -> List<Order>` — list open orders
- `find(long id) -> Response` — look up an order
- `cancel(long id) -> Response` — refund and cancel an order

#### Dependencies
- `Order`, `OrderStatus`, `PaymentGateway`, `AuditLog`
- Jakarta EE: `EntityManager`, `PersistenceContext`, `Inject`, `EJB`, `Stateless`, `Schedule`, `TransactionAttribute`, `TransactionAttributeType`
- Jakarta REST: `GET`, `POST`, `Path`, `PathParam`, `Produces`, `MediaType`, `Response`
- Jakarta Security: `RolesAllowed`
- `java.util.List`

#### Exceptions / Errors
- Missing orders produce `404 NOT_FOUND` for lookup and cancellation.
- Refund, persistence, or audit failures are not handled in this class.
