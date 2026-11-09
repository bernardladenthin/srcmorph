### ai
- H: 1.0
- C: 9F3D4F5A
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T08:22:34Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
---
> Supports the shop’s order lifecycle from checkout and fulfilment through cancellation, invoicing, back-office management, and deployment.

#### Purpose
- Coordinate customer order processing, commercial rules, stock and payment interactions, shipping, invoicing, and back-office operations.
- Provide the persistence mappings, service contracts, runtime configuration, and delivery assets needed to run the shop application.

#### Responsibilities
- **Checkout and order lifecycle:** `checkout.xhtml` and `checkoutBean` collect delivery and shipping choices; `orderFulfilment` coordinates stock reservation, payment, approval, shipping, invoicing, and stock-out cancellation.
- **Back-office orders:** `OrderResource` exposes secured order lookup and cancellation, refunds and audits cancellations, and purges cancelled orders older than 30 days; JSP supports searching open orders and cancellation links.
- **Order integration:** Camel `orderIntegration` validates and audits incoming JMS orders, routes high-value orders for review, and submits others to ERP with retry and dead-letter handling.
- **Commercial services:** `ShippingRules` calculates parcel charges and rejects overweight orders; Kotlin `InvoiceService` validates completed orders, calculates totals and tax, and issues invoices.
- **Customer and shipment persistence:** MyBatis `CustomerMapper` operates on `CUSTOMER`; Hibernate maps `Shipment` and `ExpressShipment` and configures shop and reporting persistence units.
- **Contracts and runtime delivery:** OpenAPI describes order operations; WSDL specifies SOAP stock lookup. Jakarta EE descriptors, Docker, Kubernetes, and GitHub Actions configure and deploy the application.

#### Key units
- `orderFulfilment` — BPMN process coordinating reservation, payment, approval, shipping, and invoicing.
- `checkout.xhtml` / `checkoutBean` — checkout form and its backing data and actions.
- `OrderResource` — role-restricted `/orders` operations for listing, retrieval, refund-backed cancellation, and scheduled cleanup.
- `ShippingRules` / `ShippingRulesSpec` — parcel pricing and its Spock tests.
- `InvoiceService`, `InvoiceLine`, `InvoiceNumber`, `InvoiceResult` — invoice issuance, calculations, identifiers, and outcomes.
- `orderIntegration`, `OrderValidator`, `InvoiceTranslator` — Camel routing, order validation, and ERP translation.
- `shop.customer.CustomerMapper` — MyBatis customer operations against `CUSTOMER`.
- `Shipment`, `ExpressShipment`, `shop`, `reporting` — shipment mappings and persistence-unit configuration.
- `Shop Orders API` — OpenAPI contract for order listing, retrieval, and cancellation.
- `StockPortType.GetStockLevel` — SOAP stock lookup operation with an `UnknownSku` fault.
- `ReportDownloadServlet`, `RequestIdFilter`, `StockReservationBean`, `CheckoutSessionBean`, `OrderPlacedListener` — configured Jakarta EE web and EJB components.
- `release-backoffice`, `Dockerfile`, `orders-backoffice` Deployment/Service/Ingress — release workflow, container build, and Kubernetes exposure.

#### Data flow
- Checkout inputs bind to `checkoutBean`; shipping changes recalculate the displayed total, and submission invokes order placement.
- `orderFulfilment` reserves stock, cancels on `STOCK_EMPTY`, waits for payment, routes totals above 1000 for approval, then ships and calls `invoicing`.
- Incoming JMS orders pass through Camel validation and audit; high-value orders go to manual review, while others are translated and sent to ERP.
- Completed orders reach `InvoiceService`, which rejects empty orders or creates and saves an invoice with a payment deadline.
- `OrderResource` retrieves orders through `EntityManager` and `Order.open`; cancellation refunds through `PaymentGateway`, updates status, and records `order.cancelled` through `AuditLog`. Scheduled cleanup applies a 30-day cutoff and audits the affected-row count.
- Customer and shipment operations map application types to database tables; release workflows verify, publish a container image, and deploy it to Kubernetes.

#### Dependencies
- **Domain and application types:** `Order`, `OrderStatus`, `CompletedOrder`, `Invoice`, `InvoiceRepository`, `Customer`, `Parcel`, and shop checkout, stock, integration, web, and event classes.
- **Collaborators and messaging:** `EntityManager`, `PaymentGateway`, `AuditLog`, JMS queues, Camel endpoints, BPMN message `PaymentReceived`, external task topic `shipping`, and called process `invoicing`.
- **Persistence:** `CUSTOMER`, `SHIPMENT`, `EXPRESS_SHIPMENT`, `SHIPMENT_SEQ`, `java:/jdbc/ShopDS`, `java:/jdbc/ReportingDS`, and `META-INF/order-queries.xml`.
- **External services and tools:** ERP HTTPS API, SOAP stock endpoint, OAuth 2.0 client-credentials security, Maven, Java 21, Jakarta EE, Hibernate, MyBatis, Camel, Spock, Kotlin coroutines, Docker, Kubernetes, and GitHub Actions.

#### Cross-cutting
- Back-office order operations require the `backoffice` or `admin` role; cancellation uses `REQUIRED` transaction semantics.
- Cleanup runs at 02:15 with `persistent = false`; resource-level refund, persistence, and audit failures are not handled.
- Configuration spans Jakarta EE descriptors, persistence units, integration property placeholders, container environment variables, and Kubernetes Secret-backed database credentials.
- Operational controls include request filtering, health and readiness probes, non-root container execution, resource limits, and deployment replicas.
- Explicit failure paths include stock-out cancellation, overweight-shipping rejection, empty-invoice rejection, and ERP retry/dead-letter routing.
