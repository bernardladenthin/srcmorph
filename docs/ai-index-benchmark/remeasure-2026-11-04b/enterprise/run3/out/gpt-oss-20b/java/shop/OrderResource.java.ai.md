### OrderResource.java
- H: 1.0
- C: 84470671
- D: 2026-11-04T06:47:08Z
- T: 2026-11-04T12:40:59Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
- N: 37CE394E
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 4; TODO/FIXME: 0; @Override: 0; methods (approx): 4; constructors: 0; field declarations (w/ modifier): 4; EJB session beans: 1; message-driven beans: 0; JPA entities and embeddables: 0; JPA named queries: 0; injection points: 3; JAX-RS resource paths: 3; HTTP method annotations: 3; servlets, filters and listeners: 0; SOAP endpoints: 0; transaction annotations: 1; EJB timers: 1; security annotations: 1

> Provides REST endpoints for back‑office order management, including listing open orders, retrieving a specific order, canceling orders with payment refund, and nightly purging of old cancelled orders.

#### Purpose
- Exposes order back‑office REST API.
- Handles order cancellation and nightly purge of cancelled orders.

#### Type
- `public class OrderResource`  
  - `@Stateless`, `@Path("/orders")`, `@Produces(MediaType.APPLICATION_JSON)`, `@RolesAllowed({"backoffice","admin"})`  
  - Extends `Object`.  
  - Declares static final `int PURGE_AFTER_DAYS = 30`.

#### Input
- `@PersistenceContext(unitName = "shop") private EntityManager em;`
- `@Inject private PaymentGateway payments;`
- `@EJB private AuditLog audit;`
- `@GET` `openOrders()` → no parameters.
- `@GET @Path("{id}") find(@PathParam("id") long id)` → path parameter `id`.
- `@POST @Path("{id}/cancel") cancel(@PathParam("id") long id)` → path parameter `id`.
- `@Schedule` `purgeCancelled()` → no parameters.

#### Output
- `openOrders()` returns `List<Order>`.
- `find(id)` returns `Response` (200 with `Order` or 404).
- `cancel(id)` returns `Response` (204 or 404) and triggers payment refund, status update, audit record.
- `purgeCancelled()` returns `void`; performs bulk delete and audit record.

#### Core logic
- `openOrders()`  
  - Executes named query `"Order.open"` to fetch open orders.
- `find(id)`  
  - `em.find(Order.class, id)` → if null, 404; else 200 with order.
- `cancel(id)`  
  - Find order; if null, 404.  
  - `payments.refund(order.getPaymentReference(), order.getTotal());`  
  - `order.setStatus(OrderStatus.CANCELLED);`  
  - `audit.record("order.cancelled", id);`  
  - Return 204.
- `purgeCancelled()`  
  - Execute named query `"Order.purgeCancelled"` with parameter `days = PURGE_AFTER_DAYS`.  
  - Capture `removed` count.  
  - `audit.record("order.purged", removed);`.

#### Public API
- `openOrders() -> List<Order>` – list open orders.  
- `find(long id) -> Response` – retrieve order by id.  
- `cancel(long id) -> Response` – cancel order and refund.  

#### Dependencies
- `jakarta.annotation.security.RolesAllowed`  
- `jakarta.ejb.EJB`  
- `jakarta.ejb.Schedule`  
- `jakarta.ejb.Stateless`  
- `jakarta.ejb.TransactionAttribute`  
- `jakarta.ejb.TransactionAttributeType`  
- `jakarta.inject.Inject`  
- `jakarta.persistence.EntityManager`  
- `jakarta.persistence.PersistenceContext`  
- `jakarta.ws.rs.GET`  
- `jakarta.ws.rs.POST`  
- `jakarta.ws.rs.Path`  
- `jakarta.ws.rs.PathParam`  
- `jakarta.ws.rs.Produces`  
- `jakarta.ws.rs.core.MediaType`  
- `jakarta.ws.rs.core.Response`  
- `java.util.List`  
- `shop.Order`  
- `shop.OrderStatus`  
- `shop.PaymentGateway`  
- `shop.AuditLog`

#### Exceptions / Errors
- `Response.status(Response.Status.NOT_FOUND)` when order not found.  
- No explicit exception handling; relies on container transaction rollback on runtime exceptions.

#### Concurrency
- Stateless session bean; thread‑safe by container.  
- `@Schedule` runs asynchronously at 02:15 UTC nightly.  
- No explicit synchronization; relies on JPA transaction boundaries.
