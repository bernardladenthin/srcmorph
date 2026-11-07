### OrderResource.java
- H: 1.0
- C: 84470671
- D: 2026-11-04T06:47:08Z
- T: 2026-11-04T12:32:25Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
- N: 37CE394E
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 4; TODO/FIXME: 0; @Override: 0; methods (approx): 4; constructors: 0; field declarations (w/ modifier): 4; EJB session beans: 1; message-driven beans: 0; JPA entities and embeddables: 0; JPA named queries: 0; injection points: 3; JAX-RS resource paths: 3; HTTP method annotations: 3; servlets, filters and listeners: 0; SOAP endpoints: 0; transaction annotations: 1; EJB timers: 1; security annotations: 1

> Provides REST endpoints for back‑office order management, including listing, retrieving, canceling orders, and a scheduled purge of old cancellations.

#### Purpose
- Exposes order operations via JAX‑RS.
- Handles cancellation and audit logging.
- Schedules nightly purge of cancelled orders.

#### Type
- `public class OrderResource`  
  - `@Stateless`, `@Path("/orders")`, `@Produces(MediaType.APPLICATION_JSON)`, `@RolesAllowed({"backoffice","admin"})`  
  - Extends `Object`.  
  - Fields:  
    - `@PersistenceContext(unitName = "shop") private EntityManager em;`  
    - `@Inject private PaymentGateway payments;`  
    - `@EJB private AuditLog audit;`  
  - Methods: 5 (`openOrders`, `find`, `cancel`, `purgeCancelled`, implicit constructor).

#### Input
- `openOrders()` – no parameters.  
- `find(long id)` – path param `id`.  
- `cancel(long id)` – path param `id`.  
- `purgeCancelled()` – no parameters, scheduled by `@Schedule`.  
- Dependencies: `EntityManager em`, `PaymentGateway payments`, `AuditLog audit`.

#### Output
- `openOrders()` – `List<Order>` of open orders.  
- `find(long)` – `Response` with 200 and order or 404.  
- `cancel(long)` – `Response` 204 or 404; triggers `payments.refund`, updates order status, records audit.  
- `purgeCancelled()` – no return; records audit with removed count.

#### Core logic
- `openOrders`: query `Order.open` via `em`.  
- `find`: `em.find(Order.class, id)`; return 404 if null.  
- `cancel`:  
  1. `em.find(Order.class, id)` → `order`.  
  2. If null → 404.  
  3. `payments.refund(order.getPaymentReference(), order.getTotal())`.  
  4. `order.setStatus(OrderStatus.CANCELLED)`.  
  5. `audit.record("order.cancelled", id)`.  
  6. Return 204.  
- `purgeCancelled`:  
  1. `em.createNamedQuery("Order.purgeCancelled")` with `days = PURGE_AFTER_DAYS`.  
  2. `executeUpdate()` → `removed`.  
  3. `audit.record("order.purged", removed)`.

#### Public API
- `openOrders() -> List<Order>` – list open orders.  
- `find(long) -> Response` – get order by id.  
- `cancel(long) -> Response` – cancel order.  

#### Dependencies
- `jakarta.annotation.security.RolesAllowed`  
- `jakarta.ejb.EJB`, `Schedule`, `Stateless`, `TransactionAttribute`, `TransactionAttributeType`  
- `jakarta.inject.Inject`  
- `jakarta.persistence.EntityManager`, `PersistenceContext`  
- `jakarta.ws.rs.GET`, `POST`, `Path`, `PathParam`, `Produces`  
- `jakarta.ws.rs.core.MediaType`, `Response`  
- `java.util.List`  
- `shop.PaymentGateway`, `shop.AuditLog`, `shop.Order`, `shop.OrderStatus`

#### Exceptions / Errors
- Returns `Response.Status.NOT_FOUND` when order missing.  
- No explicit exceptions thrown; relies on container transaction rollback on failure.

#### Concurrency
- Stateless EJB; thread‑safe by container.  
- `purgeCancelled` runs non‑persistent scheduled task.
