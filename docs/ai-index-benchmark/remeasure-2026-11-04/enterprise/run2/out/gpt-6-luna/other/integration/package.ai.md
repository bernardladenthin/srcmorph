### other/integration
- H: 1.0
- C: 369070B0
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T08:15:28Z
- G: 
- A: 0.0.0
- X: package
- F: [order-routes.xml](order-routes.xml.ai.md)
---
> Processes incoming orders by validating them, routing high-value orders for manual review and others to the ERP, while auditing traffic and handling ERP outages.

#### Purpose
- Integrates incoming JMS orders with validation, manual review, and external ERP order submission.
- Provides an audit tap and a dead-letter path for ERP availability failures.

#### Responsibilities
- **Order intake and routing:** Validate messages from the configured incoming queue and route by the configured total threshold.
- **Review processing:** Split orders selected for manual review into lines and send them to the review queue.
- **ERP integration:** Translate orders and submit them to the ERP API using the order ID.
- **Audit and failure handling:** Wire-tap incoming orders to an audit endpoint; retry handled ERP exceptions and then route to the dead-letter queue.

#### Key units
- `orderIntegration` — Camel context defining the order-processing routes and property placeholder.
- `incomingOrders` — consumes incoming JMS orders, validates and routes them, and wire-taps the audit endpoint.
- `manualReview` — splits reviewed orders into lines for the review queue.
- `toErp` — translates orders and sends them to the ERP API.
- `shop.integration.OrderValidator` (`orderValidator`) — validates incoming orders.
- `shop.integration.InvoiceTranslator` (`invoiceTranslator`) — translates orders for ERP submission.
- `shop.integration.ErpUnavailableException` — exception handled by the configured retry and dead-letter policy.

#### Data flow
- Incoming JMS message → `OrderValidator.validate` → branch on `${body.total}` versus `orders.review.threshold`; wire-tap to `seda:orderAudit`.
- Above threshold → `manualReview` → split `${body.lines}` → `jms:queue:review.lines`.
- Otherwise → `toErp` → `InvoiceTranslator` → ERP URL using `erp.host` and `${header.orderId}`.
- Handled `ErpUnavailableException` → up to five redeliveries with configured delay and exponential backoff → `jms:queue:orders.deadletter`.

#### Dependencies
- Application classes: `shop.integration.OrderValidator`, `shop.integration.InvoiceTranslator`, `shop.integration.ErpUnavailableException`.
- Configuration: `classpath:integration.properties`, supplying the incoming queue, review threshold, and ERP host.
- Messaging endpoints: configured incoming JMS queue, `jms:queue:review.lines`, `jms:queue:orders.deadletter`, `direct:manualReview`, `direct:toErp`, and `seda:orderAudit`.
- External service: ERP API at the configured host.

#### Cross-cutting
- Spring XML configures Apache Camel routes and loads properties through a placeholder.
- `ErpUnavailableException` uses five retries, a 2-second redelivery delay, and exponential backoff before dead-letter routing.
