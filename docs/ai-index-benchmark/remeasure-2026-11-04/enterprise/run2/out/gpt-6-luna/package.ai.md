### ai
- H: 1.0
- C: 9F3D4F5A
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T08:16:24Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
---
> Supports end-to-end shop order processing, from checkout and fulfilment through invoicing and back-office review, cancellation, and operational deployment.

#### Purpose
- Defines order workflows and customer/staff interfaces, integrating stock, payment, shipping, invoicing, and ERP processing.
- Provides secured back-office order management, including refunds on cancellation and scheduled purging of cancelled orders after 30 days.

#### Responsibilities
- **Order lifecycle:** `orderFulfilment` coordinates stock reservation, payment, high-value approval, shipping, invoicing, and out-of-stock cancellation; Camel `orderIntegration` validates and routes incoming JMS orders to manual review or ERP.
- **Checkout and shipping:** `checkout.xhtml` collects delivery and shipping selections; `ShippingRules` calculates tiered charges, free shipping, and overweight rejection.
- **Invoicing and service contracts:** `InvoiceService` issues invoices for completed orders; SOAP and OpenAPI contracts expose stock lookup and order operations.
- **Back-office operations:** `OrderResource` and `order-list.jsp` support staff order lookup and cancellation; cancellation refunds payment, changes order status, and audits the action.
- **Persistence and runtime:** `CustomerMapper` maps customer operations through MyBatis; `Shipment` and `ExpressShipment` map shipment data through JPA. Release automation, Docker, and Kubernetes configure delivery and deployment.

#### Key units
- `orderFulfilment` — BPMN workflow for order fulfilment, approval, invoicing, and cancellation.
- `orderIntegration` — Camel routes for JMS intake, validation, review, ERP submission, auditing, retries, and dead-letter handling.
- `OrderResource` — secured `/orders` REST operations for listing, retrieval, cancellation, and scheduled cleanup.
- `ShippingRules` — computes shipping charges from order total and parcel weight.
- `InvoiceService`, `InvoiceResult`, `InvoiceLine` — issue invoices and represent invoice outcomes and totals.
- `checkout.xhtml` — customer checkout form bound to `checkoutBean`.
- `order-list.jsp` — staff order search and status view, including overdue indicators and cancellation links.
- `StockReservationBean`, `CheckoutSessionBean`, `OrderPlacedListener` — configured EJB components for reservation, checkout, and order-placed events.
- `CustomerMapper` — MyBatis customer lookup, filtering, insertion, promotion, and deletion.
- `Shipment`, `ExpressShipment` — JPA mappings for shipments, parcels, and express-shipment details.
- `GET /orders`, `GET /orders/{orderId}`, `POST /orders/{orderId}/cancel` — order API operations; `StockPortType.GetStockLevel` — SOAP stock lookup by SKU.
- `PURGE_AFTER_DAYS` — 30-day cancelled-order retention threshold.
- `release-backoffice`, `Dockerfile`, `orders-backoffice` Deployment — release workflow, container build, and Kubernetes workload.

#### Data flow
- Checkout captures delivery and shipping choices, recalculates totals when shipping changes, and submits through `checkoutBean.placeOrder`.
- Order processing reserves stock and waits for payment; BPMN routes high-value orders for approval and successful fulfilment to invoicing, while stock errors cancel orders.
- Camel validates and audits JMS orders, routes high-value orders to manual review, and submits others to ERP; ERP outages retry before dead-letter routing.
- Completed orders enter `InvoiceService`, which rejects empty orders or order numbers and persists invoices with a payment due date.
- Back-office lookup queries open orders or retrieves by ID; cancellation refunds through `PaymentGateway`, marks the order `CANCELLED`, and audits it. Scheduled cleanup runs `Order.purgeCancelled` and audits the removal count.
- Customer and shipment data flow through MyBatis and JPA mappings; release checks precede image publishing and Kubernetes deployment.

#### Dependencies
- **Domain and application:** `Order`, `OrderStatus`, `CompletedOrder`, `Invoice`, `InvoiceRepository`, `Customer`, `Parcel`, `StockReservation`, `checkoutBean`.
- **Workflow and integration:** Camunda BPMN, Apache Camel, JMS queues, `shop.bpm.ReserveStockDelegate`, invoicing activity, ERP API, shipping external-task topic, OAuth 2.0 token service, SOAP stock endpoint.
- **Persistence:** MyBatis, Hibernate/JPA, PostgreSQL, `EntityManager`, `java:/jdbc/ShopDS`, `java:/jdbc/ReportingDS`, `orders-db-credentials`; named queries `Order.open` and `Order.purgeCancelled`.
- **Platform and delivery:** Jakarta EE REST, EJB, transactions, security, persistence and injection; Maven, Java 21, GitHub Actions, container registry, Docker, Kubernetes.

#### Cross-cutting
- XML-based configuration spans Camel routes, Jakarta EE descriptors, persistence, BPMN, and WSDL; OpenAPI defines HTTP contracts.
- Error handling includes BPMN `OutOfStock` cancellation, Camel `ErpUnavailableException` retries and dead-letter routing, overweight rejection, and invoice rejection.
- `OrderResource` restricts access to `backoffice` and `admin`; cancellation uses `REQUIRED` transaction semantics, and scheduled cleanup is non-persistent.
- Runtime configuration includes integration properties, production container environment variables, JTA shop persistence, and resource-local reporting persistence.
- The Kotlin invoice sequence is unsynchronized; MyBatis search interpolates `${sortColumn}` into SQL.
