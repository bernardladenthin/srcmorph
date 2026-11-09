### other
- H: 1.0
- C: 5D62DE46
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T08:22:23Z
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
> Defines the shop’s order, checkout, fulfilment, invoicing, customer, shipment, and stock workflows, together with the interfaces and deployment configuration that support them.

#### Purpose
- Support customer checkout and order processing through fulfilment, shipping, invoicing, and back-office operations.
- Specify and package the shop’s application integrations, persistence mappings, service contracts, and deployment assets.

#### Responsibilities
- **Order lifecycle and checkout:** Facelets checkout captures delivery and shipping choices; BPMN coordinates stock reservation, payment, approval, shipping, invoicing, and cancellation; the OpenAPI contract exposes order listing, retrieval, and cancellation.
- **Back-office operations and integration:** JSP renders searchable open orders and cancellation links; Camel routes validate incoming orders, audit them, route high-value orders for review, and submit other orders to ERP.
- **Commercial rules and billing:** Groovy `ShippingRules` calculates parcel charges and rejects overweight orders; Kotlin `InvoiceService` issues invoices, calculates totals, and selects tax rates.
- **Customer and shipment data:** MyBatis maps customer lookup, search, insertion, promotion, and deletion to `CUSTOMER`; Hibernate maps shipments and express shipments and configures shop and reporting persistence units.
- **Runtime and delivery:** Jakarta EE descriptors configure web and EJB components; Docker builds the application image; Kubernetes manifests deploy and expose it; GitHub Actions verifies, publishes, and deploys releases.
- **Stock service contract:** WSDL defines SOAP stock-level lookup and the unknown-SKU fault.

#### Key units
- `orderFulfilment` — BPMN process coordinating reservation, payment, approval, shipping, and invoicing.
- `checkout.xhtml` / `checkoutBean` — checkout form and backing-bean data and actions.
- `ShippingRules` / `ShippingRulesSpec` — parcel pricing rules and Spock coverage.
- `InvoiceService`, `InvoiceLine`, `InvoiceNumber`, `InvoiceResult` — invoice issuance, calculations, identifiers, and outcomes.
- `orderIntegration`, `OrderValidator`, `InvoiceTranslator` — Camel order routing, validation, and ERP translation.
- `shop.customer.CustomerMapper` — MyBatis operations against `CUSTOMER`.
- `Shipment`, `ExpressShipment`, `shop`, `reporting` — shipment mappings and JTA/resource-local persistence units.
- `Shop Orders API` — OpenAPI contract for order list, retrieval, and cancellation.
- `StockPortType.GetStockLevel` — SOAP stock lookup operation with `UnknownSku` fault.
- `ReportDownloadServlet`, `RequestIdFilter`, `StockReservationBean`, `CheckoutSessionBean`, `OrderPlacedListener` — configured Jakarta EE web and EJB components.
- `release-backoffice`, `Dockerfile`, `orders-backoffice` Deployment/Service/Ingress — release workflow, container build, and Kubernetes runtime exposure.

#### Data flow
- Checkout inputs bind to `checkoutBean`; shipping changes recalculate the displayed total, and form submission invokes order placement.
- In fulfilment, stock reservation may cancel an order on `STOCK_EMPTY`; otherwise the process waits for payment, routes totals above 1000 for approval, then ships and calls `invoicing`.
- Incoming JMS orders pass through validation and audit routing; high-value orders go to manual review, while other orders are translated and sent to ERP, with failures retried and dead-lettered.
- Completed orders enter `InvoiceService`, which rejects empty orders or generates and saves an invoice with a payment deadline.
- Customer and shipment operations map between application types and database tables through MyBatis and Hibernate.
- Release triggers lead to Maven verification, container publication, and Kubernetes deployment; Kubernetes Ingress routes back-office HTTP traffic through a Service to application pods.

#### Dependencies
- **Application/domain types:** `Order`, `CompletedOrder`, `Invoice`, `InvoiceRepository`, `Customer`, `Parcel`, and shop checkout, stock, integration, web, and event classes.
- **Messaging and process interfaces:** JMS queues, Camel routes and endpoints, BPMN message `PaymentReceived`, external task topic `shipping`, and called process `invoicing`.
- **Databases and persistence:** `CUSTOMER`, `SHIPMENT`, `EXPRESS_SHIPMENT`, `SHIPMENT_SEQ`; `java:/jdbc/ShopDS`, `java:/jdbc/ReportingDS`, and `META-INF/order-queries.xml`.
- **External contracts and services:** ERP HTTPS API, SOAP stock endpoint, OAuth 2.0 client-credentials security for cancellation, and `registry.shop.example`.
- **Runtime tooling:** Maven, Java 21, Jakarta EE, Hibernate, MyBatis, Camel, Spock, Kotlin coroutines, Docker, Kubernetes, and GitHub Actions.

#### Cross-cutting
- Configuration spans integration property placeholders, Jakarta EE descriptors, persistence-unit settings, container environment variables, and Kubernetes Secret-backed database credentials.
- Operational controls include admin-role protection, request filtering, health/readiness probes, non-root container execution, resource limits, and deployment replicas.
- Failure handling is explicit in stock-out cancellation, overweight shipping rejection, invoice rejection for empty orders, and ERP retry/dead-letter routing.
