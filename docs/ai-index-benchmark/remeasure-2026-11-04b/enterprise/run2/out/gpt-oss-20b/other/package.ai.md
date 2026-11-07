### other
- H: 1.0
- C: 5D62DE46
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T12:37:05Z
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
> A shop back‑office system that builds, tests, containerises, and deploys a Java order‑fulfilment service, exposes REST and SOAP APIs, orchestrates BPMN workflows, integrates with ERP, and manages persistence, shipping, invoicing, and reporting.

#### Purpose
- Continuous delivery of the orders‑backoffice Java service to Kubernetes.
- Expose order management APIs and SOAP stock queries.
- Orchestrate order fulfilment via BPMN and Camel routes.
- Persist and query shop data with Hibernate/MyBatis.

#### Responsibilities
- **CI/CD** – GitHub Actions workflow for build, test, Docker image, and K8s deployment.
- **Containerisation** – Dockerfile for multi‑stage build and runtime image.
- **Kubernetes** – Deployment, Service, Ingress, and secrets for the back‑office pod.
- **Web APIs** – OpenAPI 3.0.3 REST spec and WSDL for stock lookup.
- **Business Process** – Camunda BPMN `orderFulfilment` with Java delegates, external tasks, and error handling.
- **Integration** – Camel routes for order ingestion, validation, ERP posting, and audit.
- **Persistence** – Hibernate mappings for `Shipment` hierarchy and MyBatis mappers for `Customer`.
- **Domain Services** – Groovy shipping calculator, Kotlin invoice generator, Jakarta EE EJBs for stock reservation and checkout.
- **UI** – Jakarta Faces checkout page and JSP order list view.

#### Key units
- **GitHub Actions**: `release-backoffice` workflow, `build`, `image`, `deploy` jobs.
- **Docker**: `Dockerfile` (multi‑stage, Alpine JRE).
- **K8s**: `orders-backoffice` Deployment, Service, Ingress.
- **OpenAPI**: `orders-api.yaml` (orders CRUD, cancel, OAuth2).
- **WSDL**: `StockService.wsdl` (GetStockLevel, UnknownSku fault).
- **BPMN**: `orderFulfilment` process, delegates `ReserveStockDelegate`, external task `shipping`, call activity `createInvoice`.
- **Camel**: `orderIntegration` context, routes `incomingOrders`, `toErp`, `manualReview`, `orders.deadletter`.
- **Hibernate**: `Shipment` entity, `ExpressShipment` subclass, queries `undelivered`, `countByCarrier`.
- **MyBatis**: `shop.customer.CustomerMapper` with CRUD, search, batch promote, deleteInactive.
- **Groovy**: `ShippingRules` cost calculator, `ShippingRulesSpec` tests.
- **Kotlin**: `InvoiceService` with `issue`, `nextNumber`, tax logic, `InvoiceRepository`.
- **Jakarta EE**: `StockReservationBean`, `CheckoutSessionBean`, `OrderPlacedListener`, `TimingInterceptor`, `ReportDownloadServlet`, `RequestIdFilter`, `StartupAuditListener`.

#### Data flow
1. **Source commit** → GitHub Actions `build` → Maven artifacts → `image` job builds `orders-backoffice` Docker image → `deploy` job triggers K8s workflow.
2. **Client** calls REST (`/orders`) or SOAP (`GetStockLevel`) → back‑office service processes request.
3. **Order placement** → JMS `OrderPlaced` → `OrderPlacedListener` → `CheckoutSessionBean` → `StockReservationBean.reserve`.
4. **BPMN**: `orderFulfilment` reserves stock, waits for payment, routes large orders to approval, ships via external task, creates invoice.
5. **Camel**: `incomingOrders` route validates, audits, routes to ERP or manual review, handles retries and dead‑letter.
6. **Persistence**: Hibernate manages `Shipment` entities; MyBatis handles `Customer` CRUD.
7. **UI**: Faces checkout page submits order; JSP lists open orders and allows cancellation.

#### Dependencies
- **External**: Docker registry `registry.shop.example`, Kubernetes cluster, PostgreSQL (`orders-db`), OAuth2 provider `auth.shop.example`, ERP REST endpoint, JMS queues, SOAP endpoint `erp.shop.example/soap/stock`.
- **Internal**: GitHub Actions plugins (`checkout`, `setup-java`, `docker/login-action`), Camunda BPM engine, Apache Camel, Spring, Jakarta EE container, Hibernate, MyBatis, Groovy/Spock, Kotlin coroutines.
- **Configuration**: `integration.properties`, `persistence.xml`, `orders-api.yaml`, `StockService.wsdl`, Kubernetes secrets, Dockerfile env vars.

#### Cross‑cutting
- **Security**: OAuth2 client‑credentials (`orders:write`), Jakarta EE role `admin` for `/admin/*`.
- **Transactions & Interceptors**: `TimingInterceptor` on all EJBs, `RequiresNew` on `StockReservationBean.reserve`.
- **Error handling**: BPMN boundary events (`errOutOfStock`), Camel exception handling with retry/back‑off, Groovy `ParcelTooHeavyException`, OpenAPI 409 for already shipped orders.
- **Configuration & Properties**: Use of placeholders (`{{…}}`), environment variables (`SHOP_DB_URL`, `SHOP_DB_PASSWORD`, `JAVA_OPTS`, `SHOP_PROFILE`).
- **Testing**: Spock specs for shipping rules, unit tests implied for BPMN delegates and Camel routes.
- **Performance**: Hibernate batch size, Docker healthcheck, Kubernetes readiness probe, Kotlin `Dispatchers.IO` for I/O.
- **Logging & Auditing**: Camel wire‑tap to `orderAudit`, Jakarta EE startup audit listener, request ID filter.
