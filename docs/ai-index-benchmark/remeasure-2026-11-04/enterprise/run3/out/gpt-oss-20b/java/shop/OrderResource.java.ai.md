### OrderResource.java
- H: 1.0
- C: 84470671
- D: 2026-11-04T06:47:08Z
- T: 2026-11-04T08:22:48Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
- N: 50211B16
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 4; TODO/FIXME: 0; @Override: 0; methods (approx): 4; constructors: 0; field declarations (w/ modifier): 4; EJB session beans: 1; message-driven beans: 0; JPA entities and embeddables: 0; JPA named queries: 0; injection points: 3; JAX-RS resource paths: 3; HTTP method annotations: 3; servlets, filters and listeners: 0; SOAP endpoints: 0; transaction annotations: 1; EJB timers: 1; security annotations: 1

> Provides REST endpoints for back‑office order management, including listing open orders, retrieving a specific order, canceling orders, and nightly purging of old cancellations.

#### Purpose
- Exposes CRUD‑like operations for orders via JAX‑RS.
- Handles cancellation logic and audit logging.
- Schedules nightly purge of cancelled orders.

#### Type
- `public class OrderResource`  
  - `@Stateless`, `@Path("/orders")`, `@Produces(MediaType.APPLICATION_JSON)`, `@RolesAllowed({"backoffice","admin"})`  
  - Extends `Object`.  
  - Declares fields: `EntityManager em`, `PaymentGateway payments`, `AuditLog audit`.  
  - Declares methods: `openOrders()`, `find(long)`, `cancel(long)`, `purgeCancelled()`.

#### Input
- `openOrders()` – no parameters.  
- `find(long id)` – path parameter `id`.  
- `cancel(long id)` – path parameter `id`.  
- `purgeCancelled()` – no parameters, invoked by EJB scheduler.

#### Output
- `openOrders()` – `List<Order>` of open orders.  
- `find(long)` – `Response` with 200 and order or 404.  
- `cancel(long)` – `Response` 204 or 404; triggers payment refund, status update, audit record.  
- `purgeCancelled()` – no return; updates DB and records audit.

#### Core logic
- `openOrders()` queries named query `Order.open`.  
- `find(id)` retrieves order by primary key; returns 404 if null.  
- `cancel(id)`  
  1. Find order.  
  2. If null → 404.  
  3. Refund via `payments.refund(reference, total)`.  
  4. Set status to `CANCELLED`.  
  5. Record audit `"order.cancelled"`.  
  6. Return 204.  
- `purgeCancelled()`  
  1. Execute named query `Order.purgeCancelled` with `days` parameter.  
  2. Record audit `"order.purged"` with count of removed rows.

#### Public API
- `openOrders() -> List<Order>` – list open orders.  
- `find(long) -> Response` – get order by id.  
- `cancel(long) -> Response` – cancel order.  

#### Dependencies
- `jakarta.annotation.security.RolesAllowed`  
- `jakarta.ejb.EJB`, `Schedule`, `Stateless`, `TransactionAttribute`, `TransactionAttributeType`  
- `jakarta.inject.Inject`  
- `jakarta.persistence.EntityManager`, `PersistenceContext`  
- `jakarta.ws.rs.GET`, `POST`, `Path`, `PathParam`, `Produces`, `Response`  
- `jakarta.ws.rs.core.MediaType`  
- `shop.Order`, `shop.OrderStatus`, `shop.PaymentGateway`, `shop.AuditLog`

#### Exceptions / Errors
- Returns `Response.Status.NOT_FOUND` when order not found.  
- No explicit exception handling; relies on container transaction rollback on failure.

#### Concurrency
- Stateless session bean; thread‑safe by container.  
- `purgeCancelled()` runs in a scheduled, non‑persistent context.
