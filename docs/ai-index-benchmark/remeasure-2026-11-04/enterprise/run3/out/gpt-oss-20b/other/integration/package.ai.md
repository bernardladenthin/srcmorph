### other/integration
- H: 1.0
- C: 369070B0
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T08:24:39Z
- G: 
- A: 0.0.0
- X: package
- F: [order-routes.xml](order-routes.xml.ai.md)
---
> Orchestrates inbound order processing, validation, routing to ERP or manual review, and error handling within a Spring‑Camel integration flow.

#### Purpose
- Define Camel routes for order ingestion and processing.
- Validate orders and translate invoices.
- Route orders to ERP or manual review based on total amount.
- Provide redelivery and dead‑letter handling for ERP failures.

#### Responsibilities
- **Order Validation** – `orderValidator.validate` bean checks order integrity.
- **Invoice Translation** – `invoiceTranslator` converts order data for ERP consumption.
- **Routing Logic** – Camel routes decide between ERP submission or manual review.
- **Audit & Splitting** – Wire‑tap to `seda:orderAudit`; split order lines for review queue.
- **Error Management** – Redelivery policy and dead‑letter queue for `ErpUnavailableException`.

#### Key units
- `orderValidator` (`shop.integration.OrderValidator`) – validates incoming orders.
- `invoiceTranslator` (`shop.integration.InvoiceTranslator`) – prepares ERP payload.
- `ErpUnavailableException` (`shop.integration.ErpUnavailableException`) – signals ERP outages.
- Camel context `orderIntegration` – hosts all routes and exception handling.
- Routes: `incomingOrders`, `toErp`, `manualReview` – core processing paths.

#### Data flow
1. **Incoming**: `jms:queue:{{orders.incoming.queue}}` → `orderValidator.validate`.
2. **Decision**: If `body.total > {{orders.review.threshold}}` → `direct:manualReview`; else → `direct:toErp`.
3. **Audit**: Wire‑tap to `seda:orderAudit`.
4. **ERP Path**: `invoiceTranslator` → dynamic HTTPS call to `https://{{erp.host}}/api/orders/${header.orderId}`.
5. **Manual Review Path**: Split `body.lines` → `jms:queue:review.lines`.
6. **Failure**: On `ErpUnavailableException`, retry up to 5 times with exponential back‑off; then route to `jms:queue:orders.deadletter`.

#### Dependencies
- **Spring Beans**: `shop.integration.OrderValidator`, `shop.integration.InvoiceTranslator`.
- **External Systems**: JMS queues (`orders.incoming.queue`, `orders.deadletter`, `review.lines`), ERP HTTPS endpoint (`https://{{erp.host}}/api/orders/...`).
- **Spring‑Camel Schema**: `https://camel.apache.org/schema/spring/camel-spring.xsd`.

#### Cross-cutting
- Property placeholders (`orders.incoming.queue`, `orders.review.threshold`, `erp.host`) centralize configuration.
- Unified exception handling for ERP outages with redelivery and dead‑letter strategy.
- Wire‑tap pattern for audit logging without affecting main flow.
