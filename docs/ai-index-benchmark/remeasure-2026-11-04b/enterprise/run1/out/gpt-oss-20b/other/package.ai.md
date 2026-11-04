### other
- H: 1.0
- C: 5D62DE46
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T12:27:57Z
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
> A comprehensive, multi‑layered e‑commerce platform that manages orders, inventory, shipping, invoicing, and reporting across CI/CD, BPMN, Camel, Jakarta EE, and Kubernetes environments.

#### Purpose
- Deliver a fully automated order‑fulfilment lifecycle from placement to invoicing.
- Provide CI/CD, containerization, and cloud deployment for the back‑office service.
- Expose REST and SOAP APIs for external integration and internal microservices.

#### Responsibilities
- **CI/CD & Deployment** – GitHub Actions, Docker, Kubernetes manifests.
- **Business Process** – Camunda BPMN workflow for order fulfilment.
- **Integration** – Camel routes for validation, audit, ERP posting, and dead‑letter handling.
- **Enterprise Java** – Jakarta EE configuration of EJBs, JMS listeners, servlets, and security.
- **Persistence** – Hibernate/JPA entities, MyBatis mappers, and reporting queries.
- **Web UI** – Facelets and JSP pages for checkout and order management.
- **Shipping & Invoicing** – Groovy shipping rules, Kotlin invoice service.
- **API Exposure** – OpenAPI REST contract and SOAP WSDL for stock queries.

#### Key units
- **GitHub Workflow `release-backoffice`** – orchestrates build, image, and deploy jobs.
- **BPMN Process `orderFulfilment`** – executable order lifecycle.
- **Camel Context `orderIntegration`** – routes `incomingOrders`, `toErp`, `manualReview`.
- **EJBs** – `StockReservationBean`, `CheckoutSessionBean`, `OrderPlacedListener`.
- **Servlets** – `ReportDownloadServlet`, `RequestIdFilter`.
- **Hibernate Entities** – `Shipment`, `ExpressShipment`.
- **MyBatis Mapper** – `shop.customer.CustomerMapper`.
- **Groovy Class** – `ShippingRules`.
- **Kotlin Service** – `InvoiceService`.
- **OpenAPI Spec** – `orders-api.yaml`.
- **WSDL** – `StockService.wsdl`.

#### Data flow
1. **Tag push** → GitHub Actions build → Docker image → Kubernetes rollout.
2. **Order placed** → BPMN `reserveStock` → wait for `msgPaymentReceived` → approval or shipping → invoicing → completion.
3. **Camel route** receives JMS order → validates → audits → routes to ERP or manual review → handles retries and dead‑letter.
4. **EJB layer** processes checkout, reserves stock, listens to order events, serves reports.
5. **Web UI** collects shipping info, calculates totals via Groovy rules, submits order.
6. **API layer** exposes order data; OpenAPI endpoints enforce OAuth2 scopes.
7. **SOAP service** answers stock level queries; faults for unknown SKUs.

#### Dependencies
- **External**: Docker registry, Kubernetes cluster, ERP HTTP/SOAP endpoints, JMS broker, OAuth2 auth server, PostgreSQL database.
- **Internal**: Camunda engine, Apache Camel, Jakarta EE container, Spring Boot (back‑office), MyBatis, Hibernate, Groovy, Kotlin coroutines.
- **Configuration**: Secrets (`REGISTRY_USER`, `REGISTRY_TOKEN`, `orders-db-credentials`), property placeholders (`orders.incoming.queue`, `erp.host`), JNDI resources (`jdbc/ShopDS`, `jdbc/ReportingDS`).

#### Cross‑cutting
- **Security**: OAuth2 for REST writes, role‑based access for `/admin/*`, request‑ID propagation.
- **Error handling**: BPMN boundary events, Camel exception policy (5 retries, exponential back‑off), `ParcelTooHeavyException`, `ErpUnavailableException`.
- **Concurrency**: Stateless services, stateless EJBs, coroutine‑based invoice persistence.
- **Configuration**: Environment variables for Docker, Kubernetes secrets for DB credentials, property placeholders for Camel routes.
- **Health & readiness**: Docker `HEALTHCHECK`, Kubernetes readiness probe `/health/ready`.
- **Logging & metrics**: Global `TimingInterceptor` on EJBs, Camel `wireTap` for audit logging.
