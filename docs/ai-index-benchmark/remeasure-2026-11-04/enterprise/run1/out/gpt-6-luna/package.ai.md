### ai
- H: 1.0
- C: 9F3D4F5A
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T08:09:52Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
---
> Processes shop orders from checkout and fulfilment through invoicing, persistence, and back-office lookup, cancellation, and retention.

#### Purpose
- Supports the shop order lifecycle across customer-facing checkout, integrations, operational workflows, and back-office management.
- Provides persistence mappings, service contracts, and deployment and release artifacts for order-processing components.

#### Responsibilities
- **Checkout and shipping:** `checkout.xhtml` and `checkoutBean` collect delivery and shipping choices; `ShippingRules` calculates charges, free shipping, and overweight rejection.
- **Order workflow and integrations:** `orderFulfilment` coordinates stock reservation, payment, approval, shipping, invoicing, and out-of-stock cancellation; Camel routes validate, audit, review, and submit incoming orders to ERP.
- **Invoicing and stock services:** Kotlin invoice units issue invoices for completed orders; SOAP contracts expose stock availability lookup and unknown-SKU faults.
- **Back-office operations:** `OrderResource` lists and retrieves orders, cancels orders with refunds, and purges long-cancelled orders; JSP and OpenAPI artifacts describe order operations.
- **Persistence and customer data:** Hibernate/JPA map shipments and configure shop and reporting persistence; `CustomerMapper` reads and mutates `CUSTOMER`.
- **Runtime and delivery:** Jakarta EE descriptors configure services; Docker, Kubernetes resources, and actions workflows support packaging, deployment, and release automation.

#### Key units
- `OrderResource` — role-restricted order API and scheduled cancelled-order purge.
- `PURGE_AFTER_DAYS` — sets cancelled-order retention to 30 days.
- `orderFulfilment` — BPMN workflow for stock reservation through order completion or cancellation.
- `orderIntegration`, `incomingOrders`, `toErp`, `manualReview` — Camel validation, audit, review, and ERP routing.
- `checkout.xhtml`, `checkoutBean`, `ShippingRules`, `ShippingRulesSpec` — capture checkout details, calculate shipping, and verify shipping rules.
- `InvoiceService`, `InvoiceLine`, `InvoiceNumber`, `InvoiceResult` — create invoices and represent line amounts, totals, and issue outcomes.
- `StockReservationBean`, `CheckoutSessionBean`, `OrderPlacedListener` — provide configured stock reservation, checkout, and order-event handling.
- `CustomerMapper` — maps customer queries and mutations to `CUSTOMER`.
- `Shipment`, `ExpressShipment`, `shop`, `reporting` — define shipment mappings and shop/reporting persistence units.
- `StockPortType`, `StockService` — define and publish the SOAP stock-level operation.
- `order-list.jsp`, Shop Orders OpenAPI operations — present open orders and describe listing, retrieval, and cancellation.
- `Dockerfile`, `Deployment/orders-backoffice`, `Service/orders-backoffice`, `Ingress/orders-backoffice` — package and route the back-office application.

#### Data flow
- Checkout selections bind to `checkoutBean`; shipping changes recalculate totals before order submission.
- BPMN processes placed orders through stock reservation, payment, amount-based approval, shipping, and invoicing; `OutOfStock` leads to cancellation.
- Camel validates incoming JMS orders and wire-taps them to `seda:orderAudit`; high-value orders go to manual review, others are translated and sent to ERP, with exhausted failures routed to `orders.deadletter`.
- Completed orders pass to `InvoiceService`, which rejects empty orders or missing invoice inputs and returns an invoice for valid orders.
- Back-office cancellation retrieves an order, refunds its payment reference and total through `PaymentGateway`, marks it `CANCELLED`, and records `order.cancelled`; daily purge removes orders older than 30 days and records `order.purged`.
- Docker builds `orders-backoffice.jar`; Kubernetes routes HTTP traffic through Ingress and Service to application pods configured with database settings.

#### Dependencies
- **Domain and persistence:** `Order`, `OrderStatus`, `CompletedOrder`, `Invoice`, `Customer`, `Shipment`, `Parcel`; `CUSTOMER`, `SHIPMENT`, `EXPRESS_SHIPMENT`, `SHIPMENT_SEQ`, `ShopDS`, and `ReportingDS`.
- **Integrations:** `PaymentGateway`, `AuditLog`, JMS, Camel, ERP HTTP endpoint, BPMN `PaymentReceived` message, `shipping` topic, and the `invoicing` process.
- **External contracts:** Shop Orders API with OAuth 2.0; SOAP stock service at `https://erp.shop.example/soap/stock`.
- **Platform and delivery:** Jakarta EE and Faces, Hibernate, MyBatis, Maven, Java 21 images, Kubernetes Secret `orders-db-credentials`.

#### Cross-cutting
- Externalized configuration spans Camel placeholders, Jakarta EE descriptors, persistence units, container environment settings, and Kubernetes resources.
- Workflows define approval, cancellation, ERP retry, and dead-letter outcomes.
- Validation and access controls include checkout postal-code checks, incoming-order validation, back-office `backoffice`/`admin` roles, and OAuth `orders:write` scope.
- Operational and audit behavior includes container liveness, Kubernetes readiness, request filtering, startup auditing, and order audit wire-tapping.
