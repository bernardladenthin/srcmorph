### other
- H: 1.0
- C: 5D62DE46
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T08:12:33Z
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
> A Java‑based shop back‑office that builds, tests, containerises, and deploys an order‑fulfilment service, exposing REST, SOAP, and BPMN interfaces while persisting orders, shipments, and invoices in a PostgreSQL database.

#### Purpose
- Deliver a fully‑automated CI/CD pipeline for the Backoffice service.
- Provide order‑fulfilment logic via BPMN, REST, and SOAP.
- Persist order, shipment, and invoice data with Hibernate/MyBatis.
- Expose Kubernetes deployment and Docker images.

#### Responsibilities
- **CI/CD** – GitHub Actions workflows for build, image, and Kubernetes deployment.
- **Order lifecycle** – Camunda BPMN process (`orderFulfilment`) coordinating stock, payment, approval, shipping, invoicing.
- **Integration** – Camel routes validating orders, routing to ERP or manual review, with retry and dead‑letter handling.
- **Persistence** – Hibernate entities (`Order`, `Shipment`, `ExpressShipment`, `DailyRevenue`) and MyBatis mappers for `Customer`.
- **Web services** – REST API (OpenAPI) for order CRUD, SOAP service for stock queries, JSP/Facelets UI for order listing and checkout.
- **Deployment** – Docker multi‑stage build, Kubernetes manifests, and Helm‑style secrets.

#### Key units
- **GitHub Actions**: `.github/workflows/release.yml` (build, image, deploy).
- **BPMN**: `orderFulfilment` process, delegates `ReserveStockDelegate`, `shipOrder` topic, `createInvoice` call.
- **Camel**: `orderIntegration` context, routes `incomingOrders`, `toErp`, `manualReview`, `orderValidator`, `invoiceTranslator`.
- **Hibernate**: `Shipment`, `ExpressShipment` mappings; named queries `Shipment.undelivered`, `Shipment.countByCarrier`.
- **MyBatis**: `shop.customer.CustomerMapper` with dynamic `search`, `insert`, `promote`, `deleteInactive`.
- **REST**: `orders-api.yaml` (list, get, cancel) with `OrderStatus` enum.
- **SOAP**: `StockService.wsdl` exposing `GetStockLevel`.
- **Docker**: `Dockerfile` (build & runtime stages), `orders-backoffice.jar`.
- **Kubernetes**: `orders-backoffice` Deployment, Service, Ingress.
- **Facelets/JSP**: `checkout.xhtml`, `checkoutBean`, `order-list.jsp`, `OrderFilter`.
- **Groovy**: `ShippingRules` and `ShippingRulesSpec` for DHL cost logic.

#### Data flow
1. **CI/CD**: Tag push → build → test → Docker image → push → Kubernetes rollout.
2. **Order placement**: User submits via Facelets → `checkoutBean.placeOrder` → Camel `incomingOrders`.
3. **Camel validation** → audit → decision → ERP or manual review.
4. **BPMN**: `orderFulfilment` reserves stock, waits for payment, approves large orders, ships, creates invoice.
5. **Persistence**: Hibernate persists `Order`, `Shipment`; MyBatis handles `Customer`.
6. **REST**: Backoffice UI or external clients list/get/cancel orders via OpenAPI.
7. **SOAP**: External ERP queries stock via `GetStockLevel`.

#### Dependencies
- **GitHub Actions**: `actions/checkout`, `actions/setup-java`, `docker/login-action`.
- **Camunda**: `camunda` namespace, Java delegates.
- **Camel**: JMS queues, SEDA, HTTP component, redelivery policy.
- **Hibernate**: PostgreSQL dialect, JTA/RESOURCE_LOCAL units, `java:/jdbc/ShopDS`, `java:/jdbc/ReportingDS`.
- **MyBatis**: `shop.customer.CustomerMapper`, `CUSTOMER` table.
- **Docker**: Maven, Temurin JRE Alpine, `registry.shop.example`.
- **Kubernetes**: `orders-db-credentials` secret, PostgreSQL service `orders-db`.
- **JSP/Facelets**: Jakarta EE taglibs, `shop.Order`, `shop.OrderFilter`.
- **Groovy/Spock**: `ShippingRules`, `ParcelTooHeavyException`.

#### Cross‑cutting
- **Security**: OAuth2 client‑credentials (`orders:write`) for REST; role `admin` for `/admin/*`; JMS and HTTP endpoints secured via secrets.
- **Configuration**: Property placeholders (`{{…}}`) in Camel, Kubernetes env vars, Docker `JAVA_OPTS`, `SHOP_PROFILE`.
- **Error handling**: Camel redelivery & dead‑letter, BPMN boundary events, REST 404/409, SOAP fault `UnknownSkuFault`.
- **Concurrency**: Camel retry policy, Hibernate transaction types, Docker non‑root user, coroutine `Dispatchers.IO` in Kotlin invoice service.
- **Testing**: Spock specs for shipping rules, integration tests implied for Camel routes, unit tests for BPMN delegates.
- **Observability**: Camel audit via `seda:orderAudit`, Kubernetes readiness probes, Docker healthcheck, timing interceptor on EJBs.
