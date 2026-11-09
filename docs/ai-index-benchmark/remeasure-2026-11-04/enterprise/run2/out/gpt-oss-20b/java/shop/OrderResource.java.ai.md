### OrderResource.java
- H: 1.0
- C: 84470671
- D: 2026-11-04T06:47:08Z
- T: 2026-11-04T08:16:38Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
- N: 50211B16
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 4; TODO/FIXME: 0; @Override: 0; methods (approx): 4; constructors: 0; field declarations (w/ modifier): 4; EJB session beans: 1; message-driven beans: 0; JPA entities and embeddables: 0; JPA named queries: 0; injection points: 3; JAX-RS resource paths: 3; HTTP method annotations: 3; servlets, filters and listeners: 0; SOAP endpoints: 0; transaction annotations: 1; EJB timers: 1; security annotations: 1

> Provides REST endpoints for back‑office order management, including listing, retrieving, canceling, and nightly purging of cancelled orders.

#### Purpose
- Exposes order back‑office REST API.
- Handles order cancellation and nightly purge of cancelled orders.

#### Type
- `public class OrderResource` (Stateless, @Path("/orders"), @Produces(JSON), @RolesAllowed({"backoffice","admin"}))
  - Extends `Object`.
  - Annotations: `@Stateless`, `@Path`, `@Produces`, `@RolesAllowed`.
  - Fields:
    - `@PersistenceContext(unitName = "shop") private EntityManager em;`
    - `@Inject private PaymentGateway payments;`
    - `@EJB private AuditLog audit;`
  - Methods:
    - `public List<Order> openOrders()`
    - `public Response find(long id)`
    - `public Response cancel(long id)`
    - `void purgeCancelled()`

#### Input
- `openOrders`: none.
- `find`: `@PathParam("id") long id`.
- `cancel`: `@PathParam("id") long id`; uses `EntityManager.find`, `PaymentGateway.refund`, `Order.getPaymentReference`, `Order.getTotal`.
- `purgeCancelled`: none; uses named query parameter `days`.

#### Output
- `openOrders`: list of `Order`.
- `find`: `Response` with 200 and order or 404.
- `cancel`: `Response` no‑content or 404; updates order status, records audit, refunds payment.
- `purgeCancelled`: audit record of removed count; no HTTP response.

#### Core logic
- `openOrders`: query `"Order.open"` for all open orders.
- `find`: lookup order by id; return 404 if missing.
- `cancel`: lookup order; if missing return 404; otherwise refund via `payments.refund`, set status to `CANCELLED`, audit record, return 204.
- `purgeCancelled`: scheduled at 02:15 nightly; delete cancelled orders older than `PURGE_AFTER_DAYS`; audit record of count removed.

#### Public API
- `openOrders() -> List<Order>`: list open orders.
- `find(long) -> Response` (returns order or 404).
- `cancel(long) -> Response` (cancels order or 404).
- `purgeCancelled()` (no public visibility; scheduled).

#### Dependencies
- `jakarta.annotation.security.RolesAllowed`
- `jakarta.ejb.EJB`, `Schedule`, `Stateless`, `TransactionAttribute`, `TransactionAttributeType`
- `jakarta.inject.Inject`
- `jakarta.persistence.EntityManager`, `PersistenceContext`
- `jakarta.ws.rs.GET`, `POST`, `Path`, `PathParam`, `Produces`
- `jakarta.ws.rs.core.MediaType`, `Response`
- `java.util.List`
- `shop.Order`, `shop.OrderStatus`, `shop.PaymentGateway`, `shop.AuditLog`

#### Exceptions / Errors
- Returns `Response.Status.NOT_FOUND` when order not found.
- No explicit exception handling; relies on container for transaction rollback on failure.

#### Concurrency
- Stateless EJB; thread‑safe by container. Transactional on `cancel`. Scheduled method non‑persistent. No explicit synchronization.
