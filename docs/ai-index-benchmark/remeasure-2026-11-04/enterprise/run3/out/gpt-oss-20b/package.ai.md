### ai
- H: 1.0
- C: 9F3D4F5A
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T08:25:19Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
---
> A Jakarta EE back‑office system that orchestrates order lifecycle, from placement through stock reservation, invoicing, and cancellation, while providing CI/CD pipelines, BPMN workflows, and multi‑platform integrations.

#### Purpose
- Expose RESTful order APIs and manage cancellations with refunds and audit logging.
- Automate nightly cleanup of obsolete cancelled orders.
- Deliver a fully integrated, containerized microservice with CI/CD, BPMN orchestration, and external system integrations.

#### Responsibilities
- **Order API** – list, retrieve, cancel, and purge orders via JAX‑RS.
- **Payment & Refund** – process refunds through an injected `PaymentGateway`.
- **Audit & Logging** – record cancellation and purge events with `AuditLog`.
- **Scheduled Maintenance** – nightly purge of cancelled orders.
- **CI/CD & Deployment** – build, test, containerize, and deploy to Kubernetes.
- **Workflow Orchestration** – Camunda BPMN for order lifecycle, stock reservation, shipping, and invoicing.
- **Integration** – JMS, ERP HTTP, SOAP stock service, Spring‑Camel routes, MyBatis/Hibernate data access.
- **UI & API Exposure** – JSF checkout, JSP order list, OpenAPI spec for CRUD operations.

#### Key units
- `OrderResource` – REST resource at `/orders`; methods: `openOrders()`, `find(id)`, `cancel(id)`, `purgeCancelled()`.
- `PaymentGateway` – service for processing refunds.
- `AuditLog` – audit event logger.
- `Order` entity – JPA model with named queries `Order.open`, `Order.purgeCancelled`.
- `OrderPlacedListener` – EJB listening to JMS `OrderPlaced` events.
- `StockReservationBean` – EJB handling stock reservation.
- `InvoiceService` – Kotlin service persisting invoices.
- `orderFulfilment` BPMN process – orchestrates reservation, payment, shipping, invoicing.
- `orderIntegration` Spring‑Camel context – routes orders from JMS to ERP or manual review.
- `ShippingRules` (Groovy) – shipping cost logic.
- `StockService.wsdl` – SOAP contract for stock level queries.

#### Data flow
1. **Order placement** → JMS `OrderPlaced` → `OrderPlacedListener` → `StockReservationBean.reserve`.
2. **BPMN** starts on `orderPlaced` event → reserves stock → waits for payment → approval gate → shipping → invoicing → completion.
3. **API**: `OrderResource` handles list, get, cancel, purge requests, querying `EntityManager` for `Order`.
4. **Cancellation**: `cancel(id)` fetches `Order`, calls `PaymentGateway.refund`, updates status to `CANCELLED`, logs audit, commits.
5. **Purge**: scheduled `purgeCancelled()` runs nightly, executing `Order.purgeCancelled` query, logs count.
6. **Integration**: Spring‑Camel validates orders, routes to ERP or manual queue, audits via `seda:orderAudit`.
7. **UI**: JSF checkout page submits orders; JSP lists orders.
8. **CI/CD**: GitHub Actions builds JAR, Docker image, pushes to registry, deploys to Kubernetes; pods expose service via Ingress.

#### Dependencies
- Jakarta EE (EJB, JAX‑RS, CDI, JPA, Transactions).
- Camunda BPMN, Spring‑Camel, MyBatis, Hibernate.
- OpenAPI, WSDL/SOAP for external contracts.
- PostgreSQL, Kafka/JMS, ERP HTTPS endpoint, SOAP stock service.
- Docker, Maven, GitHub Actions, Kubernetes.
- OAuth2 client‑credentials, role‑based security (`backoffice`, `admin`).

#### Cross-cutting
- Stateless session beans and container‑managed transactions ensure thread safety.
- Role‑based security annotations on all endpoints.
- Global audit logging for cancellations and purges.
- Environment variables (`JAVA_OPTS`, `SHOP_PROFILE`, `APP_VERSION`) drive configuration across Docker, BPMN, and services.
- Error handling via BPMN boundary events, Camel exception policy, SOAP faults.
- Concurrency: EJB `RequiresNew` transactions, Camel redelivery with exponential back‑off, Docker health checks.
- Logging/metrics: `TimingInterceptor`, readiness probes, global loggers.
