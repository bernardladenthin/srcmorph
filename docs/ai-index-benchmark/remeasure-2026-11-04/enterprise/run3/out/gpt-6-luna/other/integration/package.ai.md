### other/integration
- H: 1.0
- C: 369070B0
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T08:21:40Z
- G: 
- A: 0.0.0
- X: package
- F: [order-routes.xml](order-routes.xml.ai.md)
---
> Processes incoming orders by validating them, routing high-value orders for manual review, and submitting other orders to the ERP with audit and failure handling.

#### Purpose
- Defines Camel routes and Spring beans for order integration.
- Loads route configuration from `classpath:integration.properties`.

#### Responsibilities
- Validate incoming orders, apply the review-threshold decision, and route orders to manual review or ERP submission.
- Translate ERP-bound orders, publish order audit events, and handle ERP failures with retries and dead-letter routing.

#### Key units
- `orderIntegration` — Camel context containing the order-processing routes and property placeholder.
- `incomingOrders` — consumes the configured JMS order queue, invokes `OrderValidator`, routes by `orders.review.threshold`, and wire-taps to `seda:orderAudit`.
- `toErp` — invokes `InvoiceTranslator` and sends orders to the ERP endpoint using the `orderId` header.
- `manualReview` — splits order lines and sends them to `jms:queue:review.lines`.
- `orderValidator` (`shop.integration.OrderValidator`) — validates incoming orders.
- `invoiceTranslator` (`shop.integration.InvoiceTranslator`) — translates orders for ERP submission.

#### Data flow
- Configured incoming JMS queue → `OrderValidator` → audit wire-tap and threshold decision.
- Orders above the threshold → `manualReview` → split lines → `review.lines`; other orders → `toErp` → `InvoiceTranslator` → ERP HTTPS endpoint.
- `shop.integration.ErpUnavailableException` → up to five redelivery attempts, then `orders.deadletter`.

#### Dependencies
- Integration classes: `shop.integration.OrderValidator`, `shop.integration.InvoiceTranslator`, `shop.integration.ErpUnavailableException`.
- Configuration: `classpath:integration.properties`; placeholders include the incoming queue, review threshold, and ERP host.
- Endpoints: JMS incoming, review-line, and dead-letter queues; `direct:manualReview`, `direct:toErp`, `seda:orderAudit`, and the ERP HTTPS API.

#### Cross-cutting
- Camel exception handling retries `ErpUnavailableException` up to five times with a 2000 ms delay and exponential backoff, then handles the exception by sending the message to the dead-letter queue.
