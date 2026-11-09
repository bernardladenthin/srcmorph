### other
- H: 1.0
- C: 5D62DE46
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T12:40:33Z
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
> Supports the shop’s order lifecycle across checkout, fulfilment, billing, back-office operations, and the deployment and data services that keep them running.

#### Purpose
- Provides customer-facing checkout and staff-facing order operations, alongside order fulfilment, shipping, invoicing, and stock-service contracts.
- Defines the database mappings, integration routes, runtime configuration, and release workflows supporting those functions.

#### Responsibilities
- **Order lifecycle:** Facelets checkout collects delivery and shipping details; BPMN handles stock reservation, payment, approval, shipping, invoicing, and cancellation; the OpenAPI contract exposes order retrieval and cancellation.
- **Back-office operations:** JSP supports order search and cancellation; Jakarta EE descriptors configure reports, request processing, checkout and stock beans, and order-event handling.
- **Commerce rules and billing:** Groovy shipping rules calculate charges and reject overweight parcels; Kotlin invoice services issue invoices and calculate line totals and category-based taxes.
- **Integration and service contracts:** Apache Camel routes validate and route incoming orders to review or ERP, with auditing and failure recovery; WSDL defines stock-availability queries.
- **Persistence and data access:** MyBatis handles customer lookup and lifecycle updates; Hibernate mappings and JPA units configure shipment, order, customer, and reporting persistence.
- **Delivery and deployment:** Docker packages the back-office JAR; Kubernetes runs and exposes it; GitHub Actions verifies, publishes, and deploys tagged releases.

#### Key units
- `orderFulfilment` — BPMN process coordinating stock, payment, approval, shipping, invoicing, and cancellation.
- `checkout.xhtml` / `checkoutBean` — checkout form bindings, shipping-method recalculation, and order submission.
- `ShippingRules` — weight-limit, free-shipping, and weight-tier cost calculation.
- `InvoiceService`, `InvoiceResult`, `InvoiceLine` — invoice issuance outcomes, persisted billing data, and line calculations.
- `incomingOrders`, `toErp`, `manualReview` — Camel routes for order validation, review, audit, and ERP submission.
- `CustomerMapper` — MyBatis customer queries, inserts, loyalty promotion, and inactive-customer deletion.
- `Shipment`, `ExpressShipment`, `shop` and `reporting` persistence units — shipment mappings and application/reporting persistence configuration.
- `Shop Orders API` — OpenAPI contract for listing, retrieving, and cancelling orders.
- `StockPortType` / `StockService` — SOAP contract and endpoint for stock-level queries.
- `StockReservationBean`, `CheckoutSessionBean`, `OrderPlacedListener` — Jakarta EE stock, checkout, and order-event components.
- `order-list.jsp` — staff order listing, filtering controls, overdue indicators, and cancellation links.
- `release-backoffice`, `Dockerfile`, `Deployment/orders-backoffice` — release workflow, container packaging, and Kubernetes workload.

#### Data flow
- Checkout inputs bind to `checkoutBean`; changing shipping method triggers total recalculation, and submission calls `placeOrder`.
- The BPMN lifecycle reserves stock, waits for payment, routes high-value orders for approval, then ships and invoices; an out-of-stock error leads to cancellation.
- Incoming JMS orders are validated and audited, then sent either to manual review as line-item messages or to ERP after translation; ERP failures retry before dead-letter delivery.
- Completed orders can be issued as invoices through `InvoiceRepository`; customer and shipment data are accessed through MyBatis and JPA/Hibernate persistence.
- Release workflows run Maven verification before publishing a versioned image and invoking Kubernetes deployment; Kubernetes routes HTTP traffic to the back-office container and supplies database settings.

#### Dependencies
- **Internal components:** `checkoutBean`, `shop.bpm.ReserveStockDelegate`, `shop.integration.OrderValidator`, `shop.integration.InvoiceTranslator`, `InvoiceRepository`, `shop.customer.Customer`, and shop order, shipment, and reporting models.
- **Messaging and services:** JMS queues, Camel, SEDA audit endpoint, `shipping` external-task topic, ERP HTTPS API, `invoicing` BPMN process, and the stock SOAP endpoint.
- **Persistence and infrastructure:** `CUSTOMER`, `SHIPMENT`, `EXPRESS_SHIPMENT`, `SHIPMENT_SEQ`, `ShopDS`, `ReportingDS`, PostgreSQL, and Kubernetes Secret `orders-db-credentials`.
- **Build and runtime:** Maven, Java 21, Docker, GitHub Actions, Kubernetes, and `registry.shop.example/orders-backoffice`.

#### Cross-cutting
- Configuration is supplied through property placeholders, Jakarta EE descriptors, persistence-unit settings, container environment variables, and Kubernetes resources.
- Order processing uses explicit routing and failure paths, including BPMN stock-error handling and Camel ERP retries with dead-letter delivery.
- Security and operational checks include admin-role protection, API OAuth 2.0 for cancellation, container liveness and Kubernetes readiness probes, and production-environment deployment.
- Monetary values use `BigDecimal` in invoice calculations; invoice issuance uses coroutine `Dispatchers.IO`, while its mutable invoice sequence has no explicit synchronization.
