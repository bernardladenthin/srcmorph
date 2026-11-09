### other
- H: 1.0
- C: 5D62DE46
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T08:18:53Z
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
> A full‑stack shop platform that builds, tests, containerises, and deploys a Java back‑office service, orchestrates order fulfilment via BPMN, Camel, and SOAP, and exposes REST and SOAP APIs for order and stock management while persisting data with Hibernate/MyBatis and generating invoices in Kotlin.

#### Purpose
- End‑to‑end order lifecycle: intake → validation → stock reservation → payment → approval → shipping → invoicing → cancellation.
- Continuous delivery of the back‑office microservice to Kubernetes.
- Unified persistence and reporting across JDBC, Hibernate, and MyBatis.

#### Responsibilities
- **CI/CD** – GitHub Actions build, test, image, and deploy to K8s.
- **Workflow orchestration** – Camunda BPMN (`orderFulfilment`) and Camel routes (`orderIntegration`) for order intake and ERP routing.
- **Service packaging** – Dockerfile, Maven build, and Kubernetes manifests.
- **User interfaces** – JSF checkout (`checkout.xhtml`) and JSP order list (`order-list.jsp`).
- **Business logic** – Kotlin invoice service, Groovy shipping rules, Java EJBs for stock and checkout.
- **Data access** – Hibernate entities (`Shipment`, `ExpressShipment`), MyBatis customer mapper, JDBC datasource.
- **API exposure** – OpenAPI REST for orders, WSDL SOAP for stock queries.
- **Security & audit** – OAuth2 scopes, request‑ID filter, timing interceptor, audit listeners.

#### Key units
- **CI/CD**: `release.yml`, `build`, `image`, `deploy` jobs.
- **BPMN**: `orderFulfilment` process, `reserveStock` delegate, `shipOrder` external task, `createInvoice` call activity.
- **Camel**: `orderIntegration` context, `orderValidator`, `invoiceTranslator`, `ErpUnavailableException`.
- **Docker/K8s**: `Dockerfile`, `orders-backoffice` Deployment, Service, Ingress.
- **UI**: `checkout.xhtml`, `checkoutBean`, `order-list.jsp`, `OrderFilter`.
- **Business**: `InvoiceService`, `InvoiceNumber`, `InvoiceLine`, `InvoiceResult`; `ShippingRules`; `StockReservationBean`; `CheckoutSessionBean`; `OrderPlacedListener`; `TimingInterceptor`.
- **Persistence**: `Shipment`, `ExpressShipment`, `Shipment.undelivered`, `Shipment.countByCarrier`; `persistence.xml` units `shop` & `reporting`; MyBatis `shop.customer.CustomerMapper`.
- **APIs**: OpenAPI `listOrders`, `getOrder`, `cancelOrder`; WSDL `StockService.wsdl`, `GetStockLevel` operation.

#### Data flow
1. **Order creation** – UI submits to REST `/orders`; Camel consumes JMS queue → validator → translator → ERP or manual review.
2. **Fulfilment** – BPMN reserves stock, waits for payment, routes large orders to back‑office approval, ships via external task, creates invoice via call activity.
3. **Persistence** – Hibernate manages `Shipment` entities; MyBatis handles customer CRUD; Kotlin service persists invoices.
4. **Deployment** – GitHub Actions builds JAR → Docker image → pushes to registry → K8s rollout; Ingress routes external traffic to pods.

#### Dependencies
- **Build**: Maven, Docker, GitHub Actions, Kubernetes.
- **Runtime**: Java 21, Eclipse Temurin Alpine, Camunda BPM, Apache Camel, Jakarta EE container, Hibernate, MyBatis, Kotlin/JVM, Groovy, Spock.
- **External services**: ERP HTTP API, shipping external task topic, stock SOAP endpoint, OAuth2 token service, PostgreSQL, Redis (if used for caching), Docker registry.
- **Data sources**: `jdbc/ShopDS`, `jdbc/ReportingDS`, `orders-db-credentials` secret.

#### Cross‑cutting
- **Configuration**: Environment variables (`APP_VERSION`, `JAVA_OPTS`, `SHOP_PROFILE`, `SHOP_DB_URL`, `SHOP_DB_PASSWORD`), property placeholders in Camel/K8s.
- **Security**: OAuth2 client‑credentials (`orders:write` scope), role‑based access (`admin` for `/admin/*`), request‑ID filter, timing interceptor.
- **Error handling**: Camel redelivery policy with dead‑letter queue, BPMN boundary events for stock errors, WSDL fault `UnknownSkuFault`.
- **Testing**: Spock tests for shipping rules, unit tests for EJBs, integration tests for Camel routes.
- **Observability**: Health checks (`/health/live`, `/health/ready`), audit listeners, logging via interceptors, metrics exposed by Kubernetes probes.
