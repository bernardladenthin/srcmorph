### other
- H: 1.0
- C: 5D62DE46
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T12:43:12Z
- G: 
- A: 0.0.0
- X: package
- F: [actions/](actions/package.ai.md)
- F: [bpmn/](bpmn/package.ai.md)
- F: [docker/](docker/package.ai.md)
- F: [facelets/](facelets/package.ai.md)
- F: [groovy/](groovy/package.ai.md)
- F: [integration/](integration/package.ai.md)
- F: [jee/](jee/package.ai.md)
- F: [jsp/](jsp/package.ai.md)
- F: [k8s/](k8s/package.ai.md)
- F: [kotlin/](kotlin/package.ai.md)
- F: [mybatis/](mybatis/package.ai.md)
- F: [openapi/](openapi/package.ai.md)
- F: [persistence/](persistence/package.ai.md)
- F: [wsdl/](wsdl/package.ai.md)
---
> A comprehensive back‑office system that orchestrates order fulfilment, inventory, shipping, invoicing, and reporting across CI/CD, containerization, Kubernetes, and multiple integration patterns.

#### Purpose
- Deliver a fully automated order lifecycle from placement to invoicing.
- Provide CI/CD, container, and cloud deployment pipelines for the back‑office service.
- Expose REST and SOAP APIs for external systems and internal services.

#### Responsibilities
- **CI/CD & Deployment** – GitHub Actions, Docker, Kubernetes manifests, and Helm‑style reusable workflows.
- **Order Fulfilment** – BPMN process, stock reservation, payment handling, approval routing, shipping, invoicing, and cancellation.
- **Integration & Messaging** – Camel routes for order ingestion, validation, audit, ERP posting, and dead‑letter handling.
- **Persistence & ORM** – Hibernate mappings for shipments, MyBatis mappers for customers, and JPA persistence units.
- **Web & UI** – JSF Facelets for checkout, JSP for order listing, servlet/filter listeners for request correlation and reporting.
- **Service Exposure** – OpenAPI REST contract and SOAP WSDL for stock queries.
- **Business Logic** – Groovy shipping cost engine, Kotlin invoice generation, and Java EJB components for stock and checkout.

#### Key units
- **CI/CD**: `release-backoffice` workflow, `build`, `image`, `deploy` jobs.
- **Docker**: two‑stage `Dockerfile` with `APP_VERSION`, `JAVA_OPTS`, `SHOP_PROFILE`.
- **Kubernetes**: `orders-backoffice` Deployment, Service, Ingress, `orders-db-credentials` Secret.
- **BPMN**: `orderFulfilment` process, `ReserveStockDelegate`, `shipping` external task, `invoicing` call activity, `msgPaymentReceived` message, `errOutOfStock` error.
- **Camel**: `orderIntegration` context with `incomingOrders`, `toErp`, `manualReview` routes; beans `orderValidator`, `invoiceTranslator`.
- **Persistence**: `Shipment`, `ExpressShipment` entities; `shop` and `reporting` persistence units; HQL `Shipment.undelivered`, SQL `Shipment.countByCarrier`.
- **MyBatis**: `shop.customer.CustomerMapper` with `findByEmail`, `search`, `insert`, `promote`, `deleteInactive`.
- **Web**: `checkout.xhtml` with `checkoutBean`; `order-list.jsp` with `OrderFilter`; `ReportDownloadServlet`; `RequestIdFilter`; `StartupAuditListener`.
- **Business Logic**: `ShippingRules` (Groovy), `InvoiceService` (Kotlin), `StockReservationBean`, `CheckoutSessionBean`, `OrderPlacedListener`.
- **APIs**: `orders-api.yaml` (OpenAPI), `StockService.wsdl` (SOAP).

#### Data flow
1. **Order Placement** → REST/WS → `orderFulfilment` BPMN → `ReserveStockDelegate` → stock DB.
2. **Payment** → `msgPaymentReceived` → BPMN waits → approval or direct shipping.
3. **Shipping** → external task `shipping` → external shipping service.
4. **Invoicing** → call activity `invoicing` → invoice DB via `InvoiceService`.
5. **Integration** – Camel routes validate, audit, route to ERP or manual review, handle retries and dead‑letters.
6. **Persistence** – Hibernate/JPA for shipments; MyBatis for customers; JDBC for reporting.
7. **Deployment** – GitHub Actions builds JAR → Docker image → pushes to registry → Kubernetes deploys pods with secrets and probes.

#### Dependencies
- **External**: GitHub Actions, Docker registry `registry.shop.example`, Kubernetes cluster, PostgreSQL DB, JMS queues, ERP HTTP API, OAuth2 provider, SOAP endpoint `erp.shop.example`.
- **Internal**: Java EE container, Camunda BPM engine, Camel runtime, MyBatis, Hibernate, Spring (profile `production`), Groovy/Spock, Kotlin coroutines.
- **Configuration**: Property placeholders (`{{…}}`), environment variables (`SHOP_DB_URL`, `SHOP_DB_PASSWORD`, `JAVA_OPTS`), secrets (`REGISTRY_USER`, `REGISTRY_TOKEN`).

#### Cross-cutting
- **Security**: OAuth2 client‑credentials for write ops, role‑based access (`admin`) in web descriptors, request‑scoped correlation IDs.
- **Error Handling**: Camel redelivery policy, BPMN error events (`errOutOfStock`), SOAP fault `UnknownSkuFault`.
- **Testing**: Spock specs for shipping rules, unit tests in CI pipeline.
- **Performance**: Hibernate batch size, coroutine `Dispatchers.IO` for DB ops, Kubernetes resource limits.
- **Observability**: Health checks (`/health/live`, `/health/ready`), timing interceptor, audit listeners, request ID filter.
- **Configuration Management**: Shared labels, environment variables, secrets, property placeholders across CI, Docker, Kubernetes, and application layers.
