### other/integration
- H: 1.0
- C: 369070B0
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T12:31:06Z
- G: 
- A: 0.0.0
- X: package
- F: [order-routes.xml](order-routes.xml.ai.md)
---
> Validates and routes incoming orders to ERP processing or manual review, while auditing orders and isolating failed deliveries.

#### Purpose
- Applies validation and review-threshold rules to orders received from a configurable JMS queue.
- Sends eligible orders to the ERP, routes high-value order lines for manual review, and handles processing failures.

#### Responsibilities
- **Order intake and routing:** `incomingOrders` validates orders, selects manual review or ERP processing based on the configured total threshold, and copies messages to an audit channel.
- **ERP integration:** `toErp` translates orders and submits them to an order-specific ERP HTTPS endpoint.
- **Manual review:** `manualReview` splits order lines and sends each to a review JMS queue.
- **Failure handling:** Retries ERP-unavailable failures, then marks them handled and sends them to a dead-letter queue.

#### Key units
- `orderIntegration` — Camel context with routes and property placeholders.
- `incomingOrders` — consumes incoming orders, validates and branches them, and wire-taps them to audit.
- `toErp` — translates orders and dynamically addresses the ERP API using the order ID.
- `manualReview` — splits orders into lines for review.
- `orderValidator` (`shop.integration.OrderValidator`) — validates incoming orders.
- `invoiceTranslator` (`shop.integration.InvoiceTranslator`) — translates orders for ERP submission.
- `shop.integration.ErpUnavailableException` — exception handled by the configured retry and dead-letter policy.

#### Data flow
- Configured JMS queue → `OrderValidator.validate` → threshold check → `direct:manualReview` or `direct:toErp`; incoming messages are also wire-tapped to `seda:orderAudit`.
- Manual-review branch → split order lines → `jms:queue:review.lines`.
- ERP branch → `InvoiceTranslator` → `https://{{erp.host}}/api/orders/${header.orderId}`.
- `ErpUnavailableException` → configured redelivery attempts → `jms:queue:orders.deadletter`.

#### Dependencies
- `shop.integration.OrderValidator`, `shop.integration.InvoiceTranslator`, and `shop.integration.ErpUnavailableException`.
- `classpath:integration.properties` supplies `orders.incoming.queue`, `orders.review.threshold`, and `erp.host`.
- Camel endpoints: incoming JMS queue, `direct:manualReview`, `direct:toErp`, `seda:orderAudit`, review and dead-letter JMS queues, and the ERP HTTPS API.

#### Cross-cutting
- Spring/Camel XML configuration with property placeholders sourced from `classpath:integration.properties`.
- ERP-unavailable redelivery policy: maximum 5 retries, 2000 ms delay, exponential backoff enabled.
