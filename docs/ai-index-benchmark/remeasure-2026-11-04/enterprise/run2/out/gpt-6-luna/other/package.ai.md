### other
- H: 1.0
- C: 5D62DE46
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T08:16:13Z
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
> Implements the shop’s order and back-office lifecycle across checkout, fulfilment, shipping, invoicing, customer and shipment persistence, and operational integration.

#### Purpose
- Defines application contracts, workflows, user interfaces, and deployment configuration for processing shop orders and supporting back-office operations.
- Covers the path from checkout and order intake through fulfilment, invoice issuance, staff access, and production deployment.

#### Responsibilities
- **Order processing and fulfilment:** BPMN coordinates stock reservation, payment, approval for orders over 1000, shipping, invoicing, and out-of-stock cancellation; Camel routes incoming JMS orders to manual review or the ERP.
- **Checkout and shipping:** Facelets collects delivery and shipping selections; Groovy `ShippingRules` calculates tiered charges, free shipping, and overweight rejection.
- **Invoices and service contracts:** Kotlin issues invoices for completed orders; WSDL defines SOAP stock-availability lookup and unknown-SKU faults; OpenAPI exposes order retrieval and cancellation.
- **Back-office access:** JSP presents order search, status, totals, overdue indicators, and cancellation links; Jakarta EE descriptors configure web endpoints, EJB services, and order-event handling.
- **Data access:** MyBatis maps customer queries and mutations; JPA/Hibernate maps shipments and configures shop and reporting persistence units.
- **Release and runtime:** GitHub Actions validates and publishes versioned images; Docker builds the runtime image; Kubernetes deploys and exposes the back office.

#### Key units
- `orderFulfilment` — BPMN process for order payment, approval, shipping, invoicing, and cancellation.
- `orderIntegration` — Camel routes for JMS intake, validation, manual review, ERP submission, auditing, and dead-letter handling.
- `ShippingRules` — calculates shipping charges from order total and parcel weight.
- `InvoiceService`, `InvoiceResult`, `InvoiceLine` — issue invoices, represent outcomes, and calculate line and invoice totals.
- `checkout.xhtml` — customer checkout form bound to `checkoutBean`.
- `order-list.jsp` — staff order search and order-list view.
- `StockReservationBean`, `CheckoutSessionBean`, `OrderPlacedListener` — configured EJB components for reservation, checkout, and order-placed events.
- `CustomerMapper` — MyBatis customer lookup, filtering, insertion, promotion, and deletion operations.
- `Shipment`, `ExpressShipment` — JPA mappings for shipments, parcels, and express-shipment details.
- `GET /orders`, `GET /orders/{orderId}`, `POST /orders/{orderId}/cancel` — order API operations.
- `StockPortType.GetStockLevel` — SOAP contract for stock availability by SKU.
- `release-backoffice`, `Dockerfile`, `orders-backoffice` Deployment — release workflow, container build, and Kubernetes workload.

#### Data flow
- Checkout captures address and shipping method; shipping changes trigger total recalculation, and submission invokes `checkoutBean.placeOrder`.
- Order processing reserves stock and waits for payment; BPMN routes high-value orders for approval and sends successful fulfilment to invoicing, while stock errors cancel the order.
- Camel validates incoming JMS orders, audits them, routes high-value orders to manual review, and translates other orders for ERP submission; handled ERP outages retry before dead-letter routing.
- Completed orders enter `InvoiceService`, which rejects empty orders or numbers and persists an invoice with a payment due date.
- Customer and shipment data are mapped to database records through MyBatis and JPA; API, JSP, and SOAP contracts expose order and stock operations.
- Release checks precede image publishing and deployment; Kubernetes routes ingress traffic through the Service to application pods.

#### Dependencies
- **Domain and application types:** `Order`, `CompletedOrder`, `Invoice`, `InvoiceRepository`, `Customer`, `Parcel`, `StockReservation`, `checkoutBean`.
- **Messaging and workflow:** Camunda BPMN, Apache Camel, JMS queues, `shop.bpm.ReserveStockDelegate`, and the `invoicing` BPMN activity.
- **Persistence:** MyBatis, Hibernate/JPA, PostgreSQL, `java:/jdbc/ShopDS`, `java:/jdbc/ReportingDS`, and database Secret `orders-db-credentials`.
- **External services:** ERP API, shipping external-task topic, OAuth 2.0 token service, and SOAP stock endpoint.
- **Runtime and delivery:** Maven, Java 21, GitHub Actions, container registry, and Kubernetes.

#### Cross-cutting
- XML-based configuration appears in Camel routes, Jakarta EE descriptors, persistence configuration, BPMN, and WSDL; OpenAPI defines HTTP contracts.
- Error paths include BPMN `OutOfStock` cancellation, Camel `ErpUnavailableException` retries and dead-letter routing, Groovy overweight rejection, and Kotlin invoice rejection.
- Runtime configuration spans integration properties, production container environment variables, JTA shop persistence, and resource-local reporting persistence.
- The Kotlin invoice sequence is unsynchronized; MyBatis search interpolates `${sortColumn}` into SQL.
