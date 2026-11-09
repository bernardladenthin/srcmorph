### ai
- H: 1.0
- C: 9F3D4F5A
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T12:32:07Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
---
> Supports the shop order lifecycle from checkout and fulfilment through invoicing, back-office cancellation, integration, and deployment.

#### Purpose
- Provides customer-facing checkout and order processing alongside staff order lookup, cancellation, and retention cleanup.
- Defines the persistence, service contracts, integrations, and deployment assets needed to operate shop and back-office order capabilities.

#### Responsibilities
- **Checkout and fulfilment:** Collect delivery and shipping choices; coordinate stock reservation, payment, approval, shipping, invoicing, and out-of-stock cancellation.
- **Order integration:** Validate and audit incoming orders, route them to ERP or manual review, and handle retries and dead-letter failures.
- **Back-office operations:** Expose order listing, lookup, and cancellation; refund cancelled orders, audit changes, and purge aged cancellations.
- **Shipping, billing, and customer data:** Calculate shipping charges and invoice amounts; map customer and shipment records to persistence.
- **Contracts and operations:** Define order and stock service contracts, configure application components and data sources, and build, publish, and deploy the back-office application.

#### Key units
- `orderFulfilment` — BPMN process coordinating stock reservation, payment, approval, shipping, invoicing, and cancellation.
- `OrderResource` — JAX-RS `/orders` resource for back-office order operations and scheduled cancelled-order cleanup.
- `Order.open` / `Order.purgeCancelled` — named queries for listing open orders and purging aged cancelled orders.
- `checkout.xhtml` / `checkoutBean` — checkout form and backing component for delivery details, shipping selection, totals, and order submission.
- `ShippingRules` — calculates parcel charges subject to carrier limits and free-shipping eligibility.
- `InvoiceService` — creates invoices for completed orders and calculates tax and totals through `InvoiceRepository`.
- `CustomerMapper` — MyBatis customer queries and lifecycle updates against `CUSTOMER`.
- `Shipment` / `ExpressShipment` — Hibernate mappings for shipment records and the joined express-shipment subtype.
- `orderIntegration` — Camel routes for order validation, auditing, ERP/manual-review routing, and failure handling.
- `OrderPlacedListener`, `StockReservationBean`, `CheckoutSessionBean` — EJB components for order events, stock reservation, and checkout.
- Shop Orders OpenAPI — contract for listing, retrieving, and cancelling orders; cancellation uses OAuth 2.0.
- `StockService` / `StockPortType` — SOAP stock lookup contract with an `UnknownSku` fault.
- `release-backoffice`, `Dockerfile`, and Kubernetes `orders-backoffice` resources — build, package, publish, and deploy the back-office application.

#### Data flow
- Checkout captures delivery and shipping choices, calculates charges with `ShippingRules`, and submits orders into fulfilment and event processing.
- `orderFulfilment` reserves stock, waits for payment, routes high-value orders for approval, then ships and invokes invoicing; stock errors lead to cancellation.
- Integration orders are validated and audited before routing by value threshold to ERP submission or manual review; ERP-unavailable failures are retried and dead-lettered.
- Completed orders with lines are converted into numbered invoices with payment deadlines; customer and shipment data use mapper and persistence configurations.
- Back-office cancellation refunds payment, marks the order cancelled, and records an audit event; scheduled cleanup records the count of purged orders.
- CI verifies and publishes the application container, then invokes Kubernetes deployment, where ingress routes HTTP traffic to application pods.

#### Dependencies
- Application collaborators include `Order`, `OrderStatus`, `PaymentGateway`, `AuditLog`, `shop.bpm.ReserveStockDelegate`, `CompletedOrder`, `InvoiceRepository`, and `shop.customer.Customer`.
- Persistence includes `CUSTOMER`, `SHIPMENT`, `EXPRESS_SHIPMENT`, `SHIPMENT_SEQ`, `java:/jdbc/ShopDS`, and `java:/jdbc/ReportingDS`.
- Messaging and integration use JMS order, review, and dead-letter queues; Camel audit and direct routes; ERP HTTPS; and BPMN `shipping` and `invoicing` endpoints.
- Jakarta EE APIs support JAX-RS, EJB, CDI, JPA, and security; other application infrastructure includes Spring/Camel, MyBatis, and Hibernate.
- Deployment uses Maven, Java 21/25 CI builds, `registry.shop.example`, Kubernetes resources, and the `orders-db` endpoint.

#### Cross-cutting
- Monetary calculations use `BigDecimal`; shipping and invoicing apply explicit thresholds and policies.
- The shop persistence unit is JTA; reporting persistence is resource-local.
- Configuration spans integration property placeholders, EJB transaction and interceptor settings, Kubernetes secrets and probes, and production container settings.
- Failure handling includes out-of-stock cancellation, ERP retry and dead-letter routing, overweight-parcel exceptions, and rejection of invoices for empty orders.
- Back-office cancellation uses a required transaction; scheduled purge uses a non-persistent EJB timer. Missing orders return `404`; refund and persistence failures are not caught by the resource.
