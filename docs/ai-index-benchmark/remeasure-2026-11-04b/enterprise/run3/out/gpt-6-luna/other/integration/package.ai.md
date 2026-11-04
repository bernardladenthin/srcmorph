### other/integration
- H: 1.0
- C: 369070B0
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T12:39:47Z
- G: 
- A: 0.0.0
- X: package
- F: [order-routes.xml](order-routes.xml.ai.md)
---
> Processes incoming orders by validating them, routing high-value orders for line-item review, and submitting other orders to the ERP with failure recovery.

#### Purpose
- Orchestrates order intake, review, ERP submission, and audit routing.
- Recovers from ERP outages through configured retries and dead-letter delivery.

#### Responsibilities
- **Order validation and routing:** Validate incoming JMS orders and route by configured total threshold.
- **Review and ERP processing:** Split review orders into line items for JMS delivery; translate other orders and send them to the ERP API.
- **Audit and failure handling:** Wire-tap incoming orders to an audit endpoint and handle ERP-unavailable errors with retry and dead-letter behavior.

#### Key units
- `incomingOrders` — consumes the configured incoming JMS queue, validates orders, routes them by total, and wire-taps to `seda:orderAudit`.
- `toErp` — invokes `invoiceTranslator` and sends orders to the ERP endpoint using the order ID header.
- `manualReview` — splits order lines and sends each item to `jms:queue:review.lines`.
- `orderValidator` — Spring bean for order validation, implemented by `shop.integration.OrderValidator`.
- `invoiceTranslator` — Spring bean for ERP order translation, implemented by `shop.integration.InvoiceTranslator`.
- `shop.integration.ErpUnavailableException` handler — retries failed ERP operations and sends handled failures to `jms:queue:orders.deadletter`.

#### Data flow
- Incoming JMS orders are validated, then routed to manual review when `body.total` exceeds `orders.review.threshold`, or to ERP submission otherwise.
- Incoming orders are also wire-tapped to `seda:orderAudit`.
- The review route splits `body.lines` into individual JMS messages; the ERP route translates the order and sends it to the configured HTTPS API using `header.orderId`.
- ERP-unavailable failures trigger up to five retries with a 2-second delay and exponential backoff, followed by dead-letter delivery.

#### Dependencies
- `classpath:integration.properties` supplies route placeholders, including the incoming queue, review threshold, and ERP host.
- Uses Apache Camel with Spring bean configuration, JMS queues, a SEDA audit endpoint, and the ERP HTTPS API.
- Depends on `shop.integration.OrderValidator`, `shop.integration.InvoiceTranslator`, and `shop.integration.ErpUnavailableException`.

#### Cross-cutting
- Queue names, review threshold, and ERP host are externally configured through property placeholders.
- ERP failure handling uses a configured redelivery policy and dead-letter queue.
