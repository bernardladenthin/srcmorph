### ai
- H: 1.0
- C: 9F3D4F5A
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T12:37:13Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
---
> A comprehensive back‑office system that manages orders, stock, shipping, invoicing, and reporting, exposing REST and SOAP APIs, orchestrating BPMN workflows, and delivering the service via CI/CD to Kubernetes.

#### Purpose
- Deliver a fully‑featured order‑fulfilment microservice.
- Provide REST/SOAP interfaces for order and stock operations.
- Automate build, test, containerisation, and deployment to Kubernetes.

#### Responsibilities
- **CI/CD** – GitHub Actions workflow for building, testing, Docker image creation, and K8s deployment.
- **API Exposure** – OpenAPI 3.0.3 REST spec for orders, WSDL for stock lookup.
- **Business Process** – Camunda BPMN `orderFulfilment` with Java delegates, external tasks, and error handling.
- **Integration** – Camel routes for order ingestion, validation, ERP posting, and audit.
- **Persistence** – Hibernate for shipment hierarchy, MyBatis for customer CRUD.
- **Domain Services** – Groovy shipping calculator, Kotlin invoice generator, Jakarta EE EJBs for stock reservation and checkout.
- **UI** – Jakarta Faces checkout page and JSP order list view.

#### Key units
- **GitHub Actions**: `release-backoffice` workflow (build, image, deploy jobs).
- **Docker**: multi‑stage `Dockerfile` (Alpine JRE runtime).
- **Kubernetes**: `orders-backoffice` Deployment, Service, Ingress, secrets.
- **OpenAPI**: `orders-api.yaml` (orders CRUD, cancel, OAuth2).
- **WSDL**: `StockService.wsdl` (GetStockLevel, UnknownSku fault).
- **BPMN**: `orderFulfilment` process, delegates `ReserveStockDelegate`, external task `shipping`, call activity `createInvoice`.
- **Camel**: `orderIntegration` context, routes `incomingOrders`, `toErp`, `manualReview`, `orders.deadletter`.
- **Hibernate**: `Shipment` entity, `ExpressShipment` subclass, queries `undelivered`, `countByCarrier`.
- **MyBatis**: `shop.customer.CustomerMapper` (CRUD, search, batch promote, deleteInactive).
- **Groovy**: `ShippingRules` cost calculator, `ShippingRulesSpec` tests.
- **Kotlin**: `InvoiceService` (issue, nextNumber, tax logic, `InvoiceRepository`).
- **Jakarta EE**: `StockReservationBean`, `CheckoutSessionBean`, `OrderPlacedListener`, `TimingInterceptor`, `ReportDownloadServlet`, `RequestIdFilter`, `StartupAuditListener`.

#### Data flow
1. **Commit → CI/CD**: GitHub Actions builds Maven artifacts → Docker image → K8s deployment.
2. **Client request**: REST (`/orders`) or SOAP (`GetStockLevel`) → back‑office service.
3. **Order placement**: JMS `OrderPlaced` → `OrderPlacedListener` → `CheckoutSessionBean` → `StockReservationBean.reserve`.
4. **BPMN**: `orderFulfilment` reserves stock, waits for payment, routes large orders for approval, ships via external task, creates invoice.
5. **Camel**: `incomingOrders` validates, audits, routes to ERP or manual review, handles retries and dead‑letter.
6. **Persistence**: Hibernate manages `Shipment` entities; MyBatis handles `Customer` CRUD.
7. **UI**: Faces checkout submits order; JSP lists open orders and allows cancellation.

#### Dependencies
- **External**: Docker registry, Kubernetes cluster, PostgreSQL (`orders-db`), OAuth2 provider, ERP REST endpoint, JMS queues, SOAP stock endpoint.
- **Internal**: GitHub Actions plugins, Camunda BPM engine, Apache Camel, Spring, Jakarta EE container, Hibernate, MyBatis, Groovy/Spock, Kotlin coroutines.
- **Configuration**: `integration.properties`, `persistence.xml`, `orders-api.yaml`, `StockService.wsdl`, Kubernetes secrets, Dockerfile env vars.

#### Cross‑cutting
- **Security**: OAuth2 client‑credentials (`orders:write`), Jakarta EE role `admin` for `/admin/*`.
- **Transactions & Interceptors**: `TimingInterceptor` on all EJBs, `RequiresNew` on `StockReservationBean.reserve`.
- **Error handling**: BPMN boundary events (`errOutOfStock`), Camel retry/back‑off, Groovy `ParcelTooHeavyException`, OpenAPI 409 for already shipped orders.
- **Configuration & Properties**: Placeholders (`{{…}}`), environment variables (`SHOP_DB_URL`, `SHOP_DB_PASSWORD`, `JAVA_OPTS`, `SHOP_PROFILE`).
- **Testing**: Spock specs for shipping rules; unit tests for BPMN delegates and Camel routes.
- **Performance**: Hibernate batch size, Docker healthcheck, Kubernetes readiness probe, Kotlin `Dispatchers.IO` for I/O.
- **Logging & Auditing**: Camel wire‑tap to `orderAudit`, Jakarta EE startup audit listener, request ID filter.
