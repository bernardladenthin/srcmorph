### java
- H: 1.0
- C: C0C0D071
- D: 2026-11-04T06:47:08Z
- T: 2026-11-04T08:17:54Z
- G: 
- A: 0.0.0
- X: package
- F: [shop/](shop/package.ai.md)
---
> A stateless REST service that manages back‑office order lifecycle, including listing, retrieval, cancellation with payment refund, audit logging, and nightly purge of cancelled orders.

#### Purpose
- Expose a stateless REST API for back‑office order management.
- Handle order cancellation and nightly cleanup of cancelled orders.

#### Responsibilities
- **Order retrieval** – list open orders, fetch by ID.
- **Order cancellation** – refund payment, update status, audit.
- **Scheduled maintenance** – nightly purge of old cancelled orders.

#### Key units
- `OrderResource` – Stateless EJB, `@Path("/orders")`, exposes `openOrders()`, `find(id)`, `cancel(id)`, and scheduled `purgeCancelled()`.
- `PaymentGateway` – injected payment service used for refunds.
- `AuditLog` – EJB used to record audit events.
- `EntityManager` (`shop`) – JPA persistence context for `Order` entities.

#### Data flow
1. **Open orders** – `openOrders()` queries named query `"Order.open"` via `EntityManager`, returns list of `Order`.
2. **Find order** – `find(id)` looks up `Order` by primary key; returns 200 with order or 404.
3. **Cancel order** – `cancel(id)`:
   - Find `Order`.
   - Call `payments.refund(order.getPaymentReference(), order.getTotal())`.
   - Set status to `CANCELLED`.
   - Persist change and record audit via `audit`.
   - Return 204 or 404.
4. **Purge cancelled** – scheduled at 02:15 nightly, deletes `Order` entities older than `PURGE_AFTER_DAYS` using a named query, logs count removed.

#### Dependencies
- Jakarta EE APIs: `@Stateless`, `@EJB`, `@Inject`, `@PersistenceContext`, `@Schedule`, `@RolesAllowed`, `@Path`, `@GET`, `@PathParam`, `@Produces`, `Response`, `MediaType`.
- JPA: `EntityManager`, named queries `"Order.open"`.
- Domain classes: `shop.Order`, `shop.OrderStatus`, `shop.PaymentGateway`, `shop.AuditLog`.

#### Cross-cutting
- Stateless EJB ensures thread safety; container‑managed transactions.
- Security via `@RolesAllowed({"backoffice","admin"})`.
- No explicit exception handling; container rolls back on failure.
- Scheduled method `purgeCancelled()` runs nightly without persistence context.
