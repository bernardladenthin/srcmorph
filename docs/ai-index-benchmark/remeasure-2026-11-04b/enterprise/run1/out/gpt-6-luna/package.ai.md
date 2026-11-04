### ai
- H: 1.0
- C: 9F3D4F5A
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T12:25:34Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
---
> Supports the shop order lifecycle from checkout and fulfilment through shipping, invoicing, back-office cancellation, and operational deployment.

#### Purpose
- Define the shop’s order-processing workflows, interfaces, persistence mappings, and runtime configuration.
- Provide customer checkout and back-office order management, including cancellation, refunds, cleanup, and audit tracking.

#### Responsibilities
- **Checkout and fulfilment:** Capture delivery and shipping choices; coordinate stock reservation, payment, approval, shipping, invoicing, and out-of-stock cancellation.
- **Order intake and integration:** Validate and audit incoming JMS orders, route them to manual review or ERP, and handle failed delivery through a dead-letter queue.
- **Shipping, stock, and invoicing:** Apply weight-based shipping charges and parcel limits; expose SOAP stock lookup; issue invoices for completed orders.
- **Customer and back-office interfaces:** Provide Facelets checkout, JSP order listing, and OpenAPI operations for retrieving and cancelling orders.
- **Persistence and runtime:** Configure MyBatis, Hibernate, Jakarta Persistence, web components, EJBs, security, JMS, and data sources.
- **Operations and delivery:** Build and deploy the back-office application with Docker, Kubernetes, and GitHub Actions; schedule cleanup of aged cancelled orders.

#### Key units
- `orderFulfilment`, `reserveStock`, `waitForPayment`, `amountCheck`, `shipOrder`, `createInvoice` — BPMN workflow and steps for order completion or cancellation.
- `OrderResource`, `PURGE_AFTER_DAYS` — authorized back-office order operations and the 30-day cancelled-order cleanup threshold.
- `ShippingRules`, `ParcelTooHeavyException` — weight-based shipping charges, parcel limits, and overweight-parcel error.
- `checkoutForm`, `checkoutBean`, `order-list.jsp` — customer checkout and back-office order listing interfaces.
- `InvoiceService`, `InvoiceLine`, `InvoiceResult`, `InvoiceNumber` — invoice issuance, calculations, and outcomes.
- `incomingOrders`, `toErp`, `manualReview`, `orderValidator`, `invoiceTranslator` — Camel intake, validation, routing, and translation components.
- `StockReservationBean`, `CheckoutSessionBean`, `OrderPlacedListener`, `ReportDownloadServlet` — configured enterprise services and report endpoint.
- `shop.customer.CustomerMapper`, `customerResult` — MyBatis customer operations and result mapping.
- `Shipment`, `ExpressShipment`, `Shipment.undelivered`, `Shipment.countByCarrier` — shipment ORM mappings and queries.
- `StockService`, `StockPortType`, `StockLevelRequest`, `StockLevelResponse`, `UnknownSkuFault` — SOAP stock lookup contract.
- `listOrders`, `getOrder`, `cancelOrder`, `Order`, `OrderStatus` — Shop Orders API operations and data types.
- `Dockerfile`, `Deployment/orders-backoffice`, `Service/orders-backoffice`, `Ingress/orders-backoffice`, `release-backoffice` — container build, Kubernetes exposure, and release workflow.

#### Data flow
- Checkout captures address and shipping selection, recalculates the EUR total, and submits an order; BPMN reserves stock, waits for payment, routes large orders for approval, then ships and invokes invoicing, or cancels on `OutOfStock`.
- JMS orders are validated and audited, then routed by total threshold to manual review or ERP; ERP failures go to the dead-letter queue.
- Invoice issuance validates completed-order lines, saves an invoice, and returns an issued or rejected result.
- Back-office cancellation retrieves an order, refunds payment, marks it `CANCELLED`, and audits `order.cancelled`; daily cleanup deletes cancelled orders older than 30 days and audits the deletion count.
- Kubernetes routes traffic to the application container built and deployed through the release workflow.

#### Dependencies
- **Shop domain:** `Order`, `OrderFilter`, `OrderStatus`, `CompletedOrder`, `Invoice`, `InvoiceRepository`, shipment and parcel types, payment, stock, checkout, event, audit, and web application classes.
- **Messaging and integration:** Apache Camel, JMS queues, `PaymentReceived`, the `shipping` external-task topic, ERP HTTPS endpoint, and invoicing process.
- **Persistence and runtime:** MyBatis, Hibernate, Jakarta Persistence, PostgreSQL, `jdbc/ShopDS`, `java:/jdbc/ShopDS`, `java:/jdbc/ReportingDS`, and `orders-db`.
- **Web and API contracts:** Jakarta Faces, Jakarta Tags, OpenAPI, SOAP/WSDL, and the configured OAuth 2.0 token endpoint.
- **Delivery tooling:** Maven, Java 21, Docker, GitHub Actions, Kubernetes, and the `orders-db-credentials` Secret.

#### Cross-cutting
- Shop configuration spans XML descriptors, `integration.properties`, persistence settings, OpenAPI, WSDL, Docker, and Kubernetes manifests.
- Operational patterns include JMS redelivery, Camel dead-letter handling, EJB timing interception, JTA and resource-local persistence units, health/readiness probes, and role-restricted back-office access (`backoffice`, `admin`).
- Noted caveats: Kotlin invoice-number sequencing is unsynchronized; MyBatis `search` substitutes `sortColumn` with unescaped `${}`; a permitted parcel outside all shipping-price tiers may lack a matching tier.
