### OrderResource.java
- H: 1.0
- C: 84470671
- D: 2026-11-04T06:47:08Z
- T: 2026-11-04T08:06:43Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
- N: 50211B16
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 4; TODO/FIXME: 0; @Override: 0; methods (approx): 4; constructors: 0; field declarations (w/ modifier): 4; EJB session beans: 1; message-driven beans: 0; JPA entities and embeddables: 0; JPA named queries: 0; injection points: 3; JAX-RS resource paths: 3; HTTP method annotations: 3; servlets, filters and listeners: 0; SOAP endpoints: 0; transaction annotations: 1; EJB timers: 1; security annotations: 1

> Backoffice users can review orders, retrieve an order, cancel it with a payment refund, and purge old cancelled orders nightly.

#### Purpose
- Provides the `/orders` backoffice API for listing, finding, and cancelling orders.
- Purges cancelled orders older than 30 days and records order audit events.

#### Type
- `public class OrderResource` — stateless EJB; annotated `@Path("/orders")`, produces JSON, and restricted by `@RolesAllowed({"backoffice", "admin"})`.

#### Input
- Injected `EntityManager em` for order lookup and named queries.
- Injected `PaymentGateway payments` for refunds.
- Injected `AuditLog audit` for event recording.
- `find(long id)` and `cancel(long id)` receive the order ID as a path parameter.
- `PURGE_AFTER_DAYS = 30` supplies the purge age parameter.

#### Output
- `openOrders()` returns the result list of open orders.
- `find(long id)` returns HTTP 404 when absent, otherwise HTTP 200 with the order.
- `cancel(long id)` returns HTTP 404 when absent, otherwise HTTP 204 after refund, status update, and audit recording.
- `purgeCancelled()` executes the purge query and records the number removed.

#### Core logic
- `openOrders()` executes named query `Order.open`.
- `find(long id)` loads an `Order` by ID and builds a not-found or success response.
- `cancel(long id)` loads the order; if found, refunds its payment reference and total, sets status to `OrderStatus.CANCELLED`, and records `order.cancelled`.
- `cancel(long id)` uses `@TransactionAttribute(TransactionAttributeType.REQUIRED)`.
- `purgeCancelled()` runs daily at 02:15, binds `days` to `PURGE_AFTER_DAYS`, executes `Order.purgeCancelled`, and records `order.purged`.

#### Public API
- `openOrders()` -> `List<Order>` — list open orders
- `find(long id)` -> `Response` — retrieve order by ID
- `cancel(long id)` -> `Response` — refund and cancel order

#### Dependencies
- `Order`, `OrderStatus`, `PaymentGateway`, `AuditLog`
- `EntityManager`, `List`, `Response`, `MediaType`
- Jakarta annotations: `@Stateless`, `@Path`, `@Produces`, `@RolesAllowed`, `@PersistenceContext`, `@Inject`, `@EJB`, `@GET`, `@POST`, `@PathParam`, `@TransactionAttribute`, `@Schedule`

#### Exceptions / Errors
- Missing orders produce HTTP 404 for lookup and cancellation.
- No explicit exception handling; refund, persistence, or audit failures are not handled here.
