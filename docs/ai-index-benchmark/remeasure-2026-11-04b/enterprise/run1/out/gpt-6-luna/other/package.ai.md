### other
- H: 1.0
- C: 5D62DE46
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T12:25:22Z
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
> Defines the shop’s order, checkout, fulfilment, invoicing, shipping, stock, and back-office interfaces, runtime configuration, and release deployment.

#### Purpose
- Collects the contracts, workflows, application configuration, and infrastructure for processing and managing shop orders.
- Covers customer checkout through fulfilment and invoicing, plus back-office order access, persistence, and deployment.

#### Responsibilities
- **Order fulfilment and shipping:** BPMN `orderFulfilment` coordinates stock reservation, payment, approval, shipping, invoicing, and out-of-stock cancellation; Groovy `ShippingRules` calculates weight-based charges and enforces parcel limits.
- **Customer and back-office experiences:** Facelets checkout collects delivery and shipping choices; JSP lists and filters orders; the OpenAPI contract defines order retrieval and cancellation.
- **Integration and enterprise services:** Apache Camel routes validate incoming orders, send eligible orders to ERP, route high-value orders for review, audit messages, and dead-letter failures; Jakarta EE descriptors configure web components, EJBs, security, and JMS.
- **Invoices, data, and stock contracts:** Kotlin invoice services issue invoices; MyBatis maps customer operations to SQL; Hibernate and Jakarta Persistence configure shipment mappings and persistence units; WSDL defines stock-level lookup.
- **Build and runtime delivery:** Docker packages the back-office application, Kubernetes deploys and exposes it, and GitHub Actions verifies, publishes, and deploys releases.

#### Key units
- `orderFulfilment`, `reserveStock`, `waitForPayment`, `amountCheck`, `shipOrder`, `createInvoice` — BPMN process and steps coordinating order completion or cancellation.
- `ShippingRules`, `ParcelTooHeavyException` — shipping-price rules and overweight-parcel error.
- `checkoutForm`, `checkoutBean`, `order-list.jsp` — customer checkout and back-office order listing.
- `InvoiceService`, `InvoiceLine`, `InvoiceResult`, `InvoiceNumber` — invoice issuance, calculations, and issuance outcome types.
- `incomingOrders`, `toErp`, `manualReview`, `orderValidator`, `invoiceTranslator` — Camel intake, routing, ERP, review, and translation components.
- `StockReservationBean`, `CheckoutSessionBean`, `OrderPlacedListener`, `ReportDownloadServlet` — configured enterprise services and report endpoint.
- `shop.customer.CustomerMapper`, `customerResult` — MyBatis customer SQL operations and result mapping.
- `Shipment`, `ExpressShipment`, `Shipment.undelivered`, `Shipment.countByCarrier` — shipment ORM mappings and queries.
- `StockService`, `StockPortType`, `StockLevelRequest`, `StockLevelResponse`, `UnknownSkuFault` — SOAP stock lookup contract.
- `listOrders`, `getOrder`, `cancelOrder`, `Order`, `OrderStatus` — Shop Orders API operations and data types.
- `Dockerfile`, `Deployment/orders-backoffice`, `Service/orders-backoffice`, `Ingress/orders-backoffice`, `release-backoffice` — container build, Kubernetes runtime exposure, and release workflow.

#### Data flow
- Checkout captures address and shipping selection, recalculates the EUR total, and submits the order; the BPMN process reserves stock, waits for payment, routes large orders for approval, then ships and invokes invoicing, or cancels on `OutOfStock`.
- Incoming JMS orders are validated, audited, and routed by configured total threshold to manual review or ERP; ERP failures are sent to the dead-letter queue.
- Invoice issuance validates completed-order lines, creates and saves an invoice, and returns an issued or rejected result; persistence mappings route shop and reporting work to their respective data sources.
- Back-office API and JSP interfaces expose order information and cancellation; Kubernetes routes host traffic to the application container, which is built and deployed through the release workflow.

#### Dependencies
- **Shop domain:** `shop.Order`, `shop.OrderFilter`, `shop.customer.Customer`, `CompletedOrder`, `Invoice`, `InvoiceRepository`, shipment and parcel types, and stock, checkout, event, audit, and web application classes.
- **Messaging and integration:** Apache Camel, JMS queues, `PaymentReceived`, the `shipping` external-task topic, ERP HTTPS endpoint, and invoicing process.
- **Persistence and runtime:** MyBatis, Hibernate, Jakarta Persistence, PostgreSQL configuration, `jdbc/ShopDS`, `java:/jdbc/ShopDS`, `java:/jdbc/ReportingDS`, and `orders-db`.
- **Web and API contracts:** Jakarta Faces, Jakarta Tags, OpenAPI, SOAP/WSDL, and the configured OAuth 2.0 token endpoint.
- **Delivery tooling:** Maven, Java 21, Docker images, GitHub Actions, Kubernetes, and the `orders-db-credentials` Secret.

#### Cross-cutting
- Shop-specific configuration spans XML descriptors, `integration.properties`, persistence settings, OpenAPI, WSDL, Docker, and Kubernetes manifests.
- Operational behavior includes JMS redelivery, Camel dead-letter handling, EJB timing interception, JTA and resource-local persistence units, and health/readiness probes.
- Notable implementation caveats in the summaries: Kotlin invoice-number sequencing is unsynchronized; MyBatis `search` substitutes `sortColumn` with unescaped `${}`; a permitted parcel outside all shipping-price tiers may lack a matching tier.
