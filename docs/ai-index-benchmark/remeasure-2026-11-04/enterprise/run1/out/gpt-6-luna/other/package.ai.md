### other
- H: 1.0
- C: 5D62DE46
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T08:09:41Z
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
> Supports the shop’s order and fulfilment lifecycle through checkout, workflow orchestration, shipping, invoicing, persistence, integrations, and back-office operations.

#### Purpose
- Defines application and infrastructure artifacts for processing shop orders, from customer checkout and fulfilment through back-office review, reporting, and cancellation.
- Provides supporting contracts, database mappings, deployment configuration, and release automation.

#### Responsibilities
- **Order processing and fulfilment:** BPMN coordinates stock reservation, payment, amount-based approval, shipping, invoicing, and cancellation; Camel routes incoming orders through validation, audit, manual review, and ERP submission.
- **Checkout and shipping:** Facelets collects delivery details and shipping choice; `ShippingRules` calculates weight-based charges, free shipping, and overweight rejection.
- **Invoicing and stock contracts:** Kotlin units issue invoices for completed orders; WSDL defines the SOAP stock-availability lookup and unknown-SKU fault.
- **Back-office operations and APIs:** Jakarta EE descriptors configure web and EJB services; JSP and OpenAPI describe order search, review, retrieval, and cancellation.
- **Persistence and customer data:** Hibernate/JPA mappings configure shipment and shop/reporting persistence; MyBatis maps customer queries and mutations to `CUSTOMER`.
- **Build and deployment:** Docker packages the Java application; Kubernetes runs and exposes it; actions workflows automate build, image publication, and production deployment.

#### Key units
- `orderFulfilment` — coordinates order completion and out-of-stock cancellation in BPMN.
- `orderIntegration`, `incomingOrders`, `toErp`, `manualReview` — validate, audit, route, and submit orders through Camel.
- `checkout.xhtml`, `checkoutBean` — capture checkout details and recalculate and submit orders.
- `ShippingRules`, `ShippingRulesSpec` — calculate shipping charges and verify shipping rules.
- `InvoiceService`, `InvoiceLine`, `InvoiceNumber`, `InvoiceResult` — create and represent invoices, line amounts, totals, and issue outcomes.
- `StockReservationBean`, `CheckoutSessionBean`, `OrderPlacedListener` — provide configured EJB stock reservation, checkout, and order-event handling.
- `CustomerMapper` — queries, inserts, promotes, and deletes customer records in `CUSTOMER`.
- `Shipment`, `ExpressShipment`, `shop` and `reporting` persistence units — map shipment entities and configure shop and reporting persistence.
- `order-list.jsp`, Shop Orders OpenAPI operations — present open orders and specify order listing, retrieval, and cancellation.
- `StockPortType`, `StockService` — define and publish the SOAP stock-level operation.
- `Dockerfile`, `Deployment/orders-backoffice`, `Service/orders-backoffice`, `Ingress/orders-backoffice` — package and run the back office and route HTTP traffic.

#### Data flow
- Checkout binds customer address and shipping selections to `checkoutBean`; shipping changes recalculate the displayed total, and order placement submits the checkout.
- In BPMN, `orderPlaced` triggers stock reservation, followed by payment and amount-based approval routing; successful shipping proceeds to invoicing and completion, while `OutOfStock` leads to cancellation.
- Camel validates incoming JMS orders and wire-taps them to `seda:orderAudit`; high-value orders are split for review, while others are translated and sent to the ERP endpoint, with exhausted failures routed to `orders.deadletter`.
- Completed orders flow into `InvoiceService`, which rejects empty orders or numbers, dates, saves, and returns an invoice for non-empty orders.
- Customer and shipment persistence paths map records and associations to database tables; reporting uses a separately configured persistence unit.
- The Docker build produces `orders-backoffice.jar`; Kubernetes routes requests through Ingress and Service to application pods configured with database connection settings.

#### Dependencies
- **Shop domain types:** `Order`, `CompletedOrder`, `Invoice`, `Customer`, `Shipment`, `Parcel`, and related repositories and exceptions.
- **Messaging and integration:** JMS queues, `seda:orderAudit`, Camel, ERP HTTP endpoint, BPMN message `PaymentReceived`, and shipping task topic `shipping`.
- **Persistence:** `CUSTOMER`, `SHIPMENT`, `EXPRESS_SHIPMENT`, `SHIPMENT_SEQ`, `ShopDS`, and `ReportingDS`.
- **External contracts:** Shop Orders API with OAuth 2.0, SOAP stock service at `https://erp.shop.example/soap/stock`, and called BPMN process `invoicing`.
- **Runtime and delivery:** Jakarta Faces, Jakarta EE, Hibernate, MyBatis, Kubernetes Secret `orders-db-credentials`, Maven, and Java 21 images.

#### Cross-cutting
- Configuration is externalized across Camel property placeholders, Jakarta EE descriptors, persistence-unit definitions, container environment settings, and Kubernetes resources.
- Order and shipment workflows use explicit routing and failure outcomes, including approval, cancellation, ERP retries, and dead-letter handling.
- Validation and authorization appear at multiple boundaries: checkout postal-code validation, incoming-order validation, admin-role protection, and OAuth `orders:write` scope.
- Several components expose operational checks or audit behavior, including container liveness, Kubernetes readiness, request filtering, startup auditing, and order audit wire-tapping.
