### OrderResource.java
- H: 1.0
- C: 84470671
- D: 2026-11-04T06:47:08Z
- T: 2026-11-04T08:10:06Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
- N: 50211B16
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 4; TODO/FIXME: 0; @Override: 0; methods (approx): 4; constructors: 0; field declarations (w/ modifier): 4; EJB session beans: 1; message-driven beans: 0; JPA entities and embeddables: 0; JPA named queries: 0; injection points: 3; JAX-RS resource paths: 3; HTTP method annotations: 3; servlets, filters and listeners: 0; SOAP endpoints: 0; transaction annotations: 1; EJB timers: 1; security annotations: 1

> Provides REST endpoints for back‑office order management, including listing, retrieving, canceling, and nightly purging of cancelled orders.

#### Purpose
- Exposes order operations via JAX‑RS.
- Handles cancellation and audit logging.
- Schedules nightly purge of old cancelled orders.

#### Type
- `public class OrderResource`  
  - `@Stateless`, `@Path("/orders")`, `@Produces(MediaType.APPLICATION_JSON)`, `@RolesAllowed({"backoffice","admin"})`  
  - Extends `Object`.  
  - Declares 1 static field, 3 injected fields, 5 public methods, 1 scheduled method.

#### Input
- `openOrders()` – no parameters.  
- `find(long id)` – path parameter `id`.  
- `cancel(long id)` – path parameter `id`.  
- `purgeCancelled()` – no parameters (scheduled).  
- Injected: `EntityManager em`, `PaymentGateway payments`, `AuditLog audit`.

#### Output
- `openOrders()` – `List<Order>` of open orders.  
- `find(long)` – `Response` with 200 and order or 404.  
- `cancel(long)` – `Response` 204 or 404; triggers refund, status update, audit record.  
- `purgeCancelled()` – no return; updates DB, audit record.

#### Core logic
- `openOrders`: query `Order.open`.  
- `find`: `em.find(Order.class, id)` → 200 or 404.  
- `cancel`: find order; if null 404; else `payments.refund`, set status to `CANCELLED`, audit record, 204.  
- `purgeCancelled`: query `Order.purgeCancelled` with `days` param, execute update, audit record.

#### Public API
- `openOrders() -> List<Order>` – list open orders.  
- `find(long) -> Response` – retrieve order by id.  
- `cancel(long) -> Response` – cancel order by id.  

#### Dependencies
- `jakarta.annotation.security.RolesAllowed`  
- `jakarta.ejb.EJB`, `Schedule`, `Stateless`, `TransactionAttribute`, `TransactionAttributeType`  
- `jakarta.inject.Inject`  
- `jakarta.persistence.EntityManager`, `PersistenceContext`  
- `jakarta.ws.rs.GET`, `POST`, `Path`, `PathParam`, `Produces`, `Response`  
- `jakarta.ws.rs.core.MediaType`  
- `java.util.List`  
- `shop.Order`, `shop.OrderStatus`, `shop.PaymentGateway`, `shop.AuditLog`

#### Exceptions / Errors
- Returns `Response.Status.NOT_FOUND` when order missing.  
- No explicit exception handling; relies on container.

#### Concurrency
- Stateless EJB; thread‑safe by container.  
- `@Schedule` runs non‑persistent, single instance.
