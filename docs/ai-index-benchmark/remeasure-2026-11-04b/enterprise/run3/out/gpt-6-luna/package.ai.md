### ai
- H: 1.0
- C: 9F3D4F5A
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T12:40:44Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
---
> Coordinates the shop’s order lifecycle from checkout and stock reservation through payment, fulfilment, invoicing, back-office management, and operational deployment.

#### Purpose
- Supports customer checkout and staff order operations, including retrieval, cancellation, refunds, and cleanup.
- Integrates order processing with stock, shipping, billing, customer data, ERP, persistence, and deployment services.

#### Responsibilities
- **Checkout and fulfilment:** Bind checkout details and recalculate shipping totals; orchestrate stock reservation, payment, approval, shipping, invoicing, and cancellation.
- **Back-office order management:** Search and retrieve orders, cancel orders with refunds and audit records, and purge aged cancellations.
- **Commerce and billing:** Apply shipping weight limits and price rules; issue invoices with line totals and category-based taxes.
- **Integration and service contracts:** Validate and route incoming orders to manual review or ERP, with audit and failure recovery; expose stock queries through SOAP.
- **Persistence and operations:** Map order, customer, shipment, and reporting data; configure runtime services and package, publish, and deploy the back-office application.

#### Key units
- `OrderResource` — secured REST resource for open-order listing, lookup, cancellation, and scheduled purge.
- `Order.open` / `Order.purgeCancelled` — named queries for open orders and eligible cancellation cleanup.
- `orderFulfilment` — BPMN process coordinating stock, payment, approval, shipping, invoicing, and cancellation.
- `checkout.xhtml` / `checkoutBean` — checkout form bindings, shipping recalculation, and order submission.
- `ShippingRules` — enforces parcel weight limits and calculates free-shipping and weight-tier charges.
- `InvoiceService`, `InvoiceResult`, `InvoiceLine` — issue invoices and represent billing outcomes and line calculations.
- `incomingOrders`, `toErp`, `manualReview` — Camel routes for validation, audit, review, ERP translation, and failure handling.
- `CustomerMapper` — MyBatis customer lookup, insertion, loyalty promotion, and inactive-customer deletion.
- `Shipment`, `ExpressShipment`, `shop`, `reporting` — shipment mappings and application/reporting persistence configuration.
- `Shop Orders API` — OpenAPI contract for order listing, retrieval, and cancellation.
- `StockPortType` / `StockService` — SOAP contract and endpoint for stock queries.
- `StockReservationBean`, `CheckoutSessionBean`, `OrderPlacedListener` — Jakarta EE stock, checkout, and order-event components.
- `order-list.jsp` — staff order search, filtering, overdue indicators, and cancellation links.
- `release-backoffice`, `Dockerfile`, `Deployment/orders-backoffice` — release workflow, container packaging, and Kubernetes workload.

#### Data flow
- Checkout inputs bind to `checkoutBean`; shipping-method changes recalculate totals, and submission places the order.
- The BPMN process reserves stock, waits for payment, routes high-value orders for approval, then ships and invoices; stock errors lead to cancellation.
- Incoming JMS orders are validated and audited, then routed to manual review or ERP; ERP failures retry before dead-letter delivery.
- Back-office operations load orders through the `shop` persistence unit; cancellation refunds the payment, marks the order cancelled, and audits the event, while scheduled cleanup removes cancellations older than 30 days and audits the count.
- Invoice issuance uses `InvoiceRepository`; customer and shipment data are accessed through MyBatis and JPA/Hibernate.
- Maven verification precedes image publication and Kubernetes deployment; the workload exposes the back-office service and receives database settings from Kubernetes.

#### Dependencies
- **Shop components:** `Order`, `OrderStatus`, `checkoutBean`, `shop.bpm.ReserveStockDelegate`, `shop.integration.OrderValidator`, `shop.integration.InvoiceTranslator`, `InvoiceRepository`, and `shop.customer.Customer`.
- **Order integrations:** `PaymentGateway`, `AuditLog`, JMS queues, Camel, SEDA audit endpoint, `shipping` external-task topic, ERP HTTPS API, `invoicing` BPMN process, and stock SOAP endpoint.
- **Persistence and infrastructure:** `EntityManager`, `shop` persistence unit, MyBatis, Hibernate, `CUSTOMER`, `SHIPMENT`, `EXPRESS_SHIPMENT`, `SHIPMENT_SEQ`, `ShopDS`, `ReportingDS`, PostgreSQL, and Kubernetes Secret `orders-db-credentials`.
- **Runtime and delivery:** Jakarta EE, Maven, Java 21, Docker, GitHub Actions, Kubernetes, and `registry.shop.example/orders-backoffice`.

#### Cross-cutting
- Order APIs and staff operations use role-based security; the OpenAPI contract specifies OAuth 2.0 for cancellation.
- Cancellation is transactional; the purge schedule is non-persistent and runs at 02:15.
- Order integrations define explicit recovery paths, including BPMN stock-error handling and Camel ERP retries with dead-letter delivery.
- Configuration spans property placeholders, Jakarta EE descriptors, persistence units, container environment variables, and Kubernetes resources.
- Invoice calculations use `BigDecimal`; invoice issuance uses coroutine `Dispatchers.IO`, while the mutable invoice sequence has no explicit synchronization.
