### ai
- H: 1.0
- C: 9F3D4F5A
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T12:43:19Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
---
> A back‑office order‑management system that exposes secure REST endpoints, orchestrates fulfillment via BPMN, integrates with external ERP and shipping services, and automates CI/CD, containerization, and Kubernetes deployment.

#### Purpose
- Provide a stateless REST API for order listing, detail, cancellation, and nightly cleanup.
- Orchestrate the full order lifecycle (stock reservation, payment, shipping, invoicing) and expose SOAP/REST contracts for external systems.
- Deliver automated build, container, and cloud deployment pipelines.

#### Responsibilities
- **Order API** – secure CRUD and cancellation endpoints with audit and refund handling.
- **Fulfillment Orchestration** – BPMN process for stock reservation, payment approval, shipping, invoicing, and cancellation.
- **Integration & Messaging** – Camel routes for order ingestion, validation, ERP posting, and dead‑letter handling.
- **Persistence & ORM** – Hibernate/JPA for shipments, MyBatis for customers, and SQL/HQL queries for reporting.
- **CI/CD & Deployment** – GitHub Actions workflows, Docker builds, Helm‑style Kubernetes manifests, and secrets management.
- **Web & UI** – JSF checkout, JSP order listing, servlet filters for correlation IDs, and health‑check endpoints.
- **Business Logic** – Groovy shipping cost engine, Kotlin invoice generation, and Java EJB components for stock and checkout.

#### Key units
- `OrderResource` – JAX‑RS stateless resource for order operations.
- `PaymentGateway` – service for processing refunds.
- `AuditLog` – EJB for recording audit events.
- `Order` entity – JPA mapping with named queries `"Order.open"` and `"Order.purgeCancelled"`.
- `orderFulfilment` BPMN process – orchestrates reservation, payment, shipping, invoicing.
- `ReserveStockDelegate` – Java delegate for stock reservation.
- `shipping` external task – delegates to external shipping service.
- `InvoiceService` (Kotlin) – generates invoices.
- `orderIntegration` Camel context – routes for order ingestion, validation, ERP posting.
- `shop.customer.CustomerMapper` (MyBatis) – customer CRUD and search.
- `checkout.xhtml` (JSF) – checkout UI.
- `orders-backoffice` Deployment, Service, Ingress – Kubernetes resources.
- `release-backoffice` GitHub Actions workflow – CI/CD pipeline.

#### Data flow
1. **Order Placement** – REST/WS → `orderFulfilment` BPMN → `ReserveStockDelegate` → stock DB.
2. **Payment** – `msgPaymentReceived` triggers BPMN wait → approval or direct shipping.
3. **Shipping** – external task `shipping` → external shipping service.
4. **Invoicing** – call activity `invoicing` → `InvoiceService` → invoice DB.
5. **Cancellation** – `OrderResource.cancel()` → `PaymentGateway.refund()` → status update → `AuditLog`.
6. **Nightly Purge** – `@Schedule purgeCancelled()` → bulk delete of cancelled orders older than 30 days → `AuditLog`.
7. **Integration** – Camel routes validate, audit, route to ERP or manual review, handle retries and dead‑letters.
8. **Deployment** – GitHub Actions → Docker image → Kubernetes pods with secrets and probes.

#### Dependencies
- **Jakarta EE** – `@Stateless`, `@EJB`, `@Inject`, `@PersistenceContext`, `@Schedule`, `@RolesAllowed`.
- **JPA/Hibernate** – `EntityManager`, named queries on `Order`, `Shipment` entities.
- **MyBatis** – `CustomerMapper` for customer data.
- **Camunda BPM** – BPMN engine for `orderFulfilment`.
- **Apache Camel** – routing and integration.
- **External services** – ERP HTTP API, SOAP endpoint `erp.shop.example`, shipping provider, OAuth2 provider.
- **Infrastructure** – GitHub Actions, Docker registry, Kubernetes cluster, PostgreSQL DB, JMS queues.

#### Cross-cutting
- **Security** – OAuth2 client‑credentials for write ops, role‑based access (`backoffice`, `admin`) on REST resources, request‑scoped correlation IDs via servlet filter.
- **Transactions** – container‑managed, default `REQUIRED`, rollback on runtime exceptions.
- **Scheduling** – nightly purge via `@Schedule`, asynchronous execution.
- **Audit** – every state change (cancel, purge) recorded through `AuditLog`.
- **Error Handling** – Camel redelivery policy, BPMN error events (`errOutOfStock`), SOAP fault `UnknownSkuFault`, 404 for missing orders.
- **Observability** – health‑check endpoints (`/health/live`, `/health/ready`), timing interceptor, audit listeners, request ID filter.
- **Performance** – Hibernate batch size, coroutine `Dispatchers.IO` for DB ops, Kubernetes resource limits.
- **Configuration Management** – property placeholders (`{{…}}`), environment variables (`SHOP_DB_URL`, `SHOP_DB_PASSWORD`, `JAVA_OPTS`), secrets (`REGISTRY_USER`, `REGISTRY_TOKEN`), shared labels across CI, Docker, Kubernetes, and application layers.
