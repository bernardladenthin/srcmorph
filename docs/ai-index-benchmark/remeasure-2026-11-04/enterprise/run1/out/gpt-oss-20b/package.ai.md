### ai
- H: 1.0
- C: 9F3D4F5A
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T08:12:50Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
---
> A back‑office order‑fulfilment system that exposes REST, SOAP, and BPMN interfaces, handles order lifecycle, persistence, and automated CI/CD deployment.

#### Purpose
- Deliver a fully‑automated CI/CD pipeline for the Backoffice service.
- Provide order‑fulfilment logic via BPMN, REST, and SOAP.
- Persist order, shipment, and invoice data in PostgreSQL.
- Expose Kubernetes deployment and Docker images.

#### Responsibilities
- **CI/CD** – GitHub Actions for build, image, and Kubernetes deployment.
- **Order lifecycle** – Camunda BPMN process (`orderFulfilment`) coordinating stock, payment, approval, shipping, invoicing.
- **Integration** – Camel routes for order validation, ERP routing, retry, and dead‑letter handling.
- **Persistence** – Hibernate entities (`Order`, `Shipment`, `ExpressShipment`, `DailyRevenue`) and MyBatis mapper for `Customer`.
- **Web services** – REST API (OpenAPI) for order CRUD, SOAP service for stock queries, JSP/Facelets UI for order listing and checkout.
- **Deployment** – Docker multi‑stage build, Kubernetes manifests, Helm‑style secrets.

#### Key units
- `OrderResource` (JAX‑RS) – REST endpoints for orders.
- `orderFulfilment` (Camunda BPMN) – orchestrates stock reservation, payment, approval, shipping, invoicing.
- `ReserveStockDelegate`, `shipOrder` topic – Java delegates in BPMN.
- `orderIntegration` (Camel) – routes `incomingOrders`, `toErp`, `manualReview`, `orderValidator`, `invoiceTranslator`.
- `Shipment`, `ExpressShipment` (Hibernate) – entity mappings and named queries.
- `shop.customer.CustomerMapper` (MyBatis) – dynamic SQL for customer CRUD.
- `StockService.wsdl` – SOAP contract for stock level queries.
- `.github/workflows/release.yml` – GitHub Actions workflow for CI/CD.
- `Dockerfile` – multi‑stage build for `orders-backoffice.jar`.
- `orders-backoffice` Deployment, Service, Ingress – Kubernetes resources.

#### Data flow
1. **CI/CD** – Tag push → build → test → Docker image → push → Kubernetes rollout.
2. **Order placement** – User submits via Facelets → `checkoutBean.placeOrder` → Camel `incomingOrders`.
3. **Camel validation** → audit → decision → ERP or manual review.
4. **BPMN** – `orderFulfilment` reserves stock, waits for payment, approves large orders, ships, creates invoice.
5. **Persistence** – Hibernate persists `Order`, `Shipment`; MyBatis handles `Customer`.
6. **REST** – Backoffice UI or external clients list/get/cancel orders via OpenAPI.
7. **SOAP** – External ERP queries stock via `GetStockLevel`.

#### Dependencies
- **GitHub Actions** – `actions/checkout`, `actions/setup-java`, `docker/login-action`.
- **Camunda** – Java delegates, BPMN engine.
- **Camel** – JMS, SEDA, HTTP components, redelivery policy.
- **Hibernate** – PostgreSQL dialect, JTA/RESOURCE_LOCAL units, `java:/jdbc/ShopDS`, `java:/jdbc/ReportingDS`.
- **MyBatis** – `shop.customer.CustomerMapper`, `CUSTOMER` table.
- **Docker** – Maven, Temurin JRE Alpine, `registry.shop.example`.
- **Kubernetes** – `orders-db-credentials` secret, PostgreSQL service `orders-db`.
- **JSP/Facelets** – Jakarta EE taglibs, `shop.Order`, `shop.OrderFilter`.
- **Groovy/Spock** – `ShippingRules`, `ParcelTooHeavyException`.

#### Cross‑cutting
- **Security** – OAuth2 client‑credentials (`orders:write`), role `admin` for `/admin/*`, JMS/HTTP endpoints secured via secrets.
- **Configuration** – Property placeholders (`{{…}}`) in Camel, Kubernetes env vars, Docker `JAVA_OPTS`, `SHOP_PROFILE`.
- **Error handling** – Camel redelivery & dead‑letter, BPMN boundary events, REST 404/409, SOAP fault `UnknownSkuFault`.
- **Concurrency** – Camel retry policy, Hibernate transaction types, Docker non‑root user, coroutine `Dispatchers.IO` in Kotlin invoice service.
- **Testing** – Spock specs for shipping rules, integration tests for Camel routes, unit tests for BPMN delegates.
- **Observability** – Camel audit via `seda:orderAudit`, Kubernetes readiness probes, Docker healthcheck, timing interceptor on EJBs.
