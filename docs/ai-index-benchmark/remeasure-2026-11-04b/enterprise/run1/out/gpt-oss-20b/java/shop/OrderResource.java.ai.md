### OrderResource.java
- H: 1.0
- C: 84470671
- D: 2026-11-04T06:47:08Z
- T: 2026-11-04T12:25:47Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
- N: 37CE394E
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 4; TODO/FIXME: 0; @Override: 0; methods (approx): 4; constructors: 0; field declarations (w/ modifier): 4; EJB session beans: 1; message-driven beans: 0; JPA entities and embeddables: 0; JPA named queries: 0; injection points: 3; JAX-RS resource paths: 3; HTTP method annotations: 3; servlets, filters and listeners: 0; SOAP endpoints: 0; transaction annotations: 1; EJB timers: 1; security annotations: 1

> Provides REST endpoints for back‑office order management, including listing open orders, retrieving a specific order, canceling orders, and a scheduled nightly purge of old cancelled orders.

#### Purpose
- Exposes order back‑office REST API.
- Cancels orders and records audit events.
- Periodically purges cancelled orders older than 30 days.

#### Type
- `public class OrderResource`  
  - `@Stateless`, `@Path("/orders")`, `@Produces(MediaType.APPLICATION_JSON)`, `@RolesAllowed({"backoffice","admin"})`  
  - Extends `Object`.  
  - Fields:  
    - `@PersistenceContext(unitName = "shop") private EntityManager em;`  
    - `@Inject private PaymentGateway payments;`  
    - `@EJB private AuditLog audit;`  
  - Methods: 5 (public: 4, private: 1).

#### Input
- `openOrders()` – no input.  
- `find(long id)` – path parameter `id`.  
- `cancel(long id)` – path parameter `id`.  
- `purgeCancelled()` – no input, scheduled by `@Schedule`.  
- Dependencies: `EntityManager`, `PaymentGateway`, `AuditLog`.

#### Output
- `openOrders()` – `List<Order>` of open orders.  
- `find(long id)` – `Response` with 200 and order or 404.  
- `cancel(long id)` – `Response` 204 or 404; triggers `payments.refund`, updates order status, records audit.  
- `purgeCancelled()` – no HTTP response; updates DB, records audit.

#### Core logic
- `openOrders()`  
  - `em.createNamedQuery("Order.open", Order.class).getResultList();`
- `find(id)`  
  - `em.find(Order.class, id)` → 404 if null, else 200 with order.
- `cancel(id)`  
  - Find order; 404 if null.  
  - `payments.refund(order.getPaymentReference(), order.getTotal());`  
  - `order.setStatus(OrderStatus.CANCELLED);`  
  - `audit.record("order.cancelled", id);`  
  - Return 204.
- `purgeCancelled()`  
  - `em.createNamedQuery("Order.purgeCancelled").setParameter("days", PURGE_AFTER_DAYS).executeUpdate();`  
  - `audit.record("order.purged", removed);`

#### Public API
- `openOrders() -> List<Order>` – list open orders.  
- `find(long) -> Response` – retrieve order by id.  
- `cancel(long) -> Response` – cancel order by id.  

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
- `shop.Order`  
- `shop.OrderStatus`  
- `shop.PaymentGateway`  
- `shop.AuditLog`

#### Exceptions / Errors
- Returns `Response.Status.NOT_FOUND` if order not found.  
- No explicit exception handling; relies on container transaction rollback on failure.

#### Concurrency
- Stateless EJB; thread‑safe by container.  
- `@Schedule` runs in container thread; uses non‑persistent schedule.
