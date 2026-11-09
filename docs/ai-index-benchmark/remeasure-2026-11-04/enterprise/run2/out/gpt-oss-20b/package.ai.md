### ai
- H: 1.0
- C: 9F3D4F5A
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T08:19:14Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
---
> A back‑office order management system that exposes REST and SOAP APIs, orchestrates fulfillment via BPMN and Camel, and automates CI/CD to Kubernetes.

#### Purpose
- Provide stateless REST endpoints for order lifecycle (list, retrieve, cancel, purge).
- Orchestrate end‑to‑end order processing: validation, stock reservation, payment, approval, shipping, invoicing, and cancellation.
- Deliver the back‑office service continuously to a Kubernetes cluster.

#### Responsibilities
- **API exposure** – REST (`/orders`) and SOAP (`StockService`) for order and stock operations.
- **Order lifecycle** – listing, retrieval, cancellation with payment refund, audit logging, nightly purge of cancelled orders.
- **Workflow orchestration** – Camunda BPMN (`orderFulfilment`) and Camel routes (`orderIntegration`) for intake, validation, ERP routing, and external task handling.
- **Business logic** – Kotlin invoice generation, Groovy shipping rules, Java EJBs for stock and checkout.
- **Persistence** – Hibernate entities (`Shipment`, `ExpressShipment`), MyBatis customer mapper, JDBC datasource.
- **CI/CD & deployment** – GitHub Actions, Docker, Kubernetes manifests, health checks, and observability.

#### Key units
- `OrderResource` – Stateless EJB exposing `openOrders()`, `find(id)`, `cancel(id)`, and scheduled `purgeCancelled()`.
- `PaymentGateway` – Service injected into `OrderResource` for refunds.
- `AuditLog` – EJB recording audit events during cancellation.
- `orderFulfilment` BPMN process – delegates `reserveStock`, external task `shipOrder`, call activity `createInvoice`.
- `orderIntegration` Camel context – routes `orderValidator`, `invoiceTranslator`, handles `ErpUnavailableException`.
- `InvoiceService` (Kotlin) – generates invoices and persists them.
- `Shipment` / `ExpressShipment` Hibernate entities – track shipping status.
- `shop.customer.CustomerMapper` (MyBatis) – CRUD for customers.
- `release.yml` – GitHub Actions workflow for build, image, and deploy jobs.

#### Data flow
1. **Order creation** – UI → REST `/orders` → Camel queue → validator → translator → ERP or manual review.
2. **Fulfillment** – BPMN reserves stock, waits for payment, routes large orders for approval, ships via external task, creates invoice via call activity.
3. **Cancellation** – `OrderResource.cancel(id)` refunds payment, updates status, logs audit, persists change.
4. **Nightly purge** – `purgeCancelled()` deletes cancelled orders older than `PURGE_AFTER_DAYS`.
5. **Deployment** – GitHub Actions builds JAR → Docker image → pushes to registry → Kubernetes rollout; Ingress routes traffic to pods.

#### Dependencies
- **Frameworks & runtimes** – Jakarta EE, JPA, Hibernate, MyBatis, Camunda BPM, Apache Camel, Kotlin/JVM, Groovy, Spock, Docker, Kubernetes.
- **External services** – ERP HTTP API, stock SOAP endpoint, OAuth2 token service, PostgreSQL, Redis (optional).
- **Databases** – `jdbc/ShopDS` (shop), `jdbc/ReportingDS` (reporting).
- **Security** – OAuth2 client‑credentials (`orders:write`), role‑based access (`backoffice`, `admin`).

#### Cross‑cutting
- Stateless EJBs ensure thread safety; container‑managed transactions.
- Security via `@RolesAllowed` and OAuth2 scopes.
- Scheduled tasks (`@Schedule`) for nightly cleanup.
- Error handling: Camel redelivery policy, BPMN boundary events, SOAP fault `UnknownSkuFault`.
- Observability: health endpoints, audit listeners, metrics, logging interceptors, Kubernetes probes.
- Configuration: environment variables (`APP_VERSION`, `JAVA_OPTS`, `SHOP_PROFILE`, `SHOP_DB_URL`, `SHOP_DB_PASSWORD`), property placeholders in Camel/K8s.
