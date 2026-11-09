### other
- H: 1.0
- C: 5D62DE46
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T08:25:13Z
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
> A modular shop‑backoffice system that builds, tests, containers, deploys, and exposes services for order processing, inventory, invoicing, and reporting.

#### Purpose
- End‑to‑end order fulfilment and back‑office operations.
- Continuous delivery of a Java microservice to Kubernetes.
- Integration with ERP, JMS, and SOAP stock services.

#### Responsibilities
- **CI/CD** – GitHub Actions build, test, Docker image, and K8s deployment.
- **Workflow orchestration** – Camunda BPMN for order lifecycle.
- **Containerization** – Multi‑stage Dockerfile for a lightweight JRE image.
- **Web UI** – JSF checkout page and JSP order list.
- **Business logic** – Groovy shipping rules, Kotlin invoice service, Java EJBs for stock and checkout.
- **Data access** – MyBatis customer mapper, Hibernate JPA for shipments.
- **Integration** – Spring‑Camel routes for order ingestion, JMS, ERP HTTP, and dead‑letter handling.
- **API exposure** – OpenAPI spec for order CRUD and cancellation.
- **SOAP service** – WSDL for stock level queries with fault handling.
- **Kubernetes deployment** – Deployment, Service, Ingress, health probes, secrets.

#### Key units
- **CI/CD**: `release.yml`, `deploy-k8s.yml`.
- **BPMN**: `orderFulfilment` process, `reserveStock` delegate, `shipOrder` external task, `createInvoice` call activity.
- **Docker**: `Dockerfile`, `orders-backoffice.jar`.
- **JSF**: `checkout.xhtml`, `checkoutBean`.
- **JSP**: `order-list.jsp`.
- **Groovy**: `ShippingRules`, `ShippingRulesSpec`.
- **Kotlin**: `InvoiceService`, `InvoiceRepository`, `InvoiceNumber`.
- **EJB**: `StockReservationBean`, `CheckoutSessionBean`, `OrderPlacedListener`.
- **Spring‑Camel**: `orderIntegration` context, `orderValidator`, `invoiceTranslator`, `ErpUnavailableException`.
- **MyBatis**: `CustomerMapper`, `customerResult`, `customerColumns`.
- **Hibernate**: `Shipment.hbm.xml`, `ExpressShipment`, named queries `Shipment.undelivered`, `Shipment.countByCarrier`.
- **OpenAPI**: `listOrders`, `getOrder`, `cancelOrder`, `Order` schema, `OrderStatus` enum.
- **WSDL**: `StockService.wsdl`, `StockPortType`, `GetStockLevel` operation, `UnknownSkuFault`.

#### Data flow
1. **Order placement** → JMS `OrderPlaced` → `OrderPlacedListener` → `StockReservationBean.reserve`.
2. **BPMN** starts on `orderPlaced` event → reserves stock → waits for payment → approval gate → shipping → invoicing → completion.
3. **Integration flow** receives orders via JMS → validates → routes to ERP or manual review → audit via `seda:orderAudit`.
4. **Invoice service** creates and persists invoices for completed orders.
5. **API** exposes order data; cancellation updates status if not shipped.
6. **SOAP** provides stock levels to external systems.
7. **CI/CD** builds JAR, Docker image, pushes to registry, deploys to K8s; pods expose service via Ingress.

#### Dependencies
- **Build**: Maven, Java 21/25, Docker, GitHub Actions.
- **Runtime**: Eclipse Temurin JRE Alpine, PostgreSQL, Kafka/JMS, ERP HTTPS endpoint, SOAP stock service.
- **Frameworks**: Camunda BPMN, Spring‑Camel, Jakarta EE (EJB, Servlet), MyBatis, Hibernate, OpenAPI, WSDL/SOAP.
- **Secrets**: Docker registry credentials, DB passwords, OAuth2 token endpoint.
- **Configuration**: Property placeholders for queues, thresholds, ERP host, DB URLs, report directory.

#### Cross‑cutting
- Environment variables (`JAVA_OPTS`, `SHOP_PROFILE`, `APP_VERSION`) across Docker, BPMN, and services.
- Security: OAuth2 client‑credentials for cancel endpoint; role‑based access for `/admin/*`.
- Error handling: BPMN boundary events, Camel exception policy, SOAP faults.
- Concurrency: EJB transactions (`RequiresNew`), Camel redelivery with exponential back‑off, Docker healthchecks.
- Logging/metrics: Global `TimingInterceptor`, health endpoints, readiness probes.
- Validation: ZIP regex, order line checks, shipping weight limits, invoice line totals.
