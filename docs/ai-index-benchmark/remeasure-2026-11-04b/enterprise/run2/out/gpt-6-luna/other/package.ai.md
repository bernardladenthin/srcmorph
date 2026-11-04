### other
- H: 1.0
- C: 5D62DE46
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T12:31:56Z
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
> Supports the shop’s order lifecycle across checkout, fulfilment, shipping, invoicing, customer data, integrations, and back-office deployment.

#### Purpose
- Defines the shop’s order and back-office capabilities, from customer checkout and order processing to invoicing, reporting, and cancellation.
- Groups application contracts, persistence and integration configuration, and build and runtime deployment assets.

#### Responsibilities
- **Order lifecycle and staff operations:** BPMN order fulfilment routes stock reservation, payment, approval, shipping, invoicing, and out-of-stock cancellation; JSP and OpenAPI expose order search, lookup, and cancellation.
- **Checkout, shipping, and billing:** Facelets collects delivery details and shipping choices; Groovy `ShippingRules` calculates parcel charges; Kotlin invoice services issue invoices and calculate tax and totals.
- **Customer and order data:** MyBatis maps customer lookup, search, creation, promotion, and cleanup to `CUSTOMER`; JPA/Hibernate configures shipment mappings and shop and reporting persistence units.
- **External processing and service contracts:** Camel routes validate and audit incoming orders, send them to ERP or manual review, and handle failures; WSDL defines stock lookup and unknown-SKU faults.
- **Application operations:** Jakarta EE descriptors configure web and EJB components, while Docker, Kubernetes, and GitHub Actions package, publish, and deploy the backoffice.

#### Key units
- `orderFulfilment` — BPMN process coordinating stock reservation, payment, approval, shipping, invoicing, and cancellation.
- `checkout.xhtml` — checkout form bound to `checkoutBean` for address entry, shipping selection, total refresh, and order submission.
- `ShippingRules` — computes parcel charges from order value and weight, enforcing carrier limits and free-shipping eligibility.
- `InvoiceService` — issues invoices for completed orders, calculates tax rates, and persists invoices through `InvoiceRepository`.
- `CustomerMapper` — MyBatis operations mapping customer queries and lifecycle updates to the `CUSTOMER` table.
- `Shipment` and `ExpressShipment` — Hibernate mappings for shipment records and their joined express-shipment subtype.
- `orderIntegration` — Camel context routing validated orders to ERP or manual review, with audit and failure handling.
- `OrderPlacedListener`, `StockReservationBean`, and `CheckoutSessionBean` — EJB components for order events, stock reservation, and checkout.
- Shop Orders OpenAPI — contract for listing, retrieving, and cancelling orders.
- `StockService` and `StockPortType` — SOAP contract exposing stock-level lookup and an `UnknownSku` fault.
- `release-backoffice`, `Dockerfile`, and Kubernetes `orders-backoffice` resources — build, package, publish, and run the backoffice application.

#### Data flow
- Customers enter delivery details and select shipping at checkout; `ShippingRules` calculates charges, and order placement feeds the fulfilment and event-processing capabilities.
- `orderFulfilment` reserves stock, waits for payment, routes high-value orders for approval, then ships and invokes invoicing; stock errors lead to cancellation.
- Incoming integration orders are validated and audited, then routed by configured value threshold to ERP submission or per-line manual review; ERP-unavailable failures are retried and dead-lettered.
- Completed orders with lines are converted into numbered invoices, assigned payment deadlines, and saved; customer and shipment records are handled by mapper and persistence configurations.
- Back-office build and release workflows verify the application, publish its container image, and invoke Kubernetes deployment; Kubernetes routes HTTP traffic to application pods.

#### Dependencies
- Application types and services include `shop.bpm.ReserveStockDelegate`, `checkoutBean`, `Order`, `InvoiceRepository`, `CompletedOrder`, `shop.customer.Customer`, and `shop.integration` validator, translator, and exception types.
- Data infrastructure includes `CUSTOMER`, `SHIPMENT`, `EXPRESS_SHIPMENT`, `SHIPMENT_SEQ`, `java:/jdbc/ShopDS`, `java:/jdbc/ReportingDS`, and the `orders-db` Kubernetes endpoint.
- Messaging and integration endpoints include JMS order, review, and dead-letter queues; Camel audit and direct routes; ERP HTTPS; and the BPMN `shipping` task topic and `invoicing` process.
- API and service contracts include OAuth 2.0 for order cancellation and SOAP-over-HTTP stock lookup.
- Deployment tooling includes Maven, Java 21/25 CI builds, the `registry.shop.example` container registry, and Kubernetes Deployment, Service, and Ingress resources.

#### Cross-cutting
- Order totals, shipping prices, invoice amounts, and tax calculations use monetary values including `BigDecimal`; shipping and invoice rules apply explicit thresholds and policies.
- Jakarta EE, Spring/Camel, MyBatis, and Hibernate provide application configuration and persistence; the shop persistence unit is JTA, while reporting is resource-local.
- Configuration spans integration property placeholders, EJB transaction and interceptor settings, Kubernetes secrets and probes, and production container settings.
- Failure handling includes BPMN out-of-stock cancellation, ERP retry and dead-letter routing, overweight-parcel exceptions, and rejected invoices for empty orders.
