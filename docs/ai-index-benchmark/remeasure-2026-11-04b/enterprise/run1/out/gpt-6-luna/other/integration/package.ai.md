### other/integration
- H: 1.0
- C: 369070B0
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T12:24:37Z
- G: 
- A: 0.0.0
- X: package
- F: [order-routes.xml](order-routes.xml.ai.md)
---
> Processes incoming orders by validating them, directing high-value orders to manual review and eligible orders to ERP, while auditing and dead-lettering failures.

#### Purpose
- Connects JMS order intake to validation, manual review, ERP submission, audit, and failure handling.
- Loads routing and ERP settings from `classpath:integration.properties`.

#### Responsibilities
- **Order routing:** Validate incoming orders and direct them to manual review or ERP based on the configured total threshold.
- **ERP integration:** Translate orders and submit them to an order-specific ERP API endpoint.
- **Review and audit:** Split review-bound orders into lines for a JMS queue and wire-tap incoming orders to an audit endpoint.
- **Failure handling:** Handle `ErpUnavailableException` by sending messages to the dead-letter queue, with configured redelivery.

#### Key units
- `incomingOrders` — consumes the configured incoming JMS queue, validates orders, routes by total, and wire-taps for audit.
- `toErp` — translates orders and sends them to the ERP API using the order ID.
- `manualReview` — splits order lines and sends them to `jms:queue:review.lines`.
- `orderValidator` — Spring bean for `shop.integration.OrderValidator`.
- `invoiceTranslator` — Spring bean for `shop.integration.InvoiceTranslator`.

#### Data flow
- Incoming JMS orders pass through validation, then route to `direct:manualReview` when `${body.total}` exceeds the configured threshold or to `direct:toErp` otherwise; the intake route also taps messages to `seda:orderAudit`.
- The ERP route translates orders and sends them to `https://{{erp.host}}/api/orders/${header.orderId}`; the review route splits `${body.lines}` to `jms:queue:review.lines`.
- `shop.integration.ErpUnavailableException` is handled by sending to `jms:queue:orders.deadletter`.

#### Dependencies
- Java integration classes: `shop.integration.OrderValidator`, `shop.integration.InvoiceTranslator`, and `shop.integration.ErpUnavailableException`.
- Configuration: `classpath:integration.properties`, including the incoming queue, review threshold, and ERP host placeholders.
- Camel endpoints: JMS queues, `direct:toErp`, `direct:manualReview`, `seda:orderAudit`, and the ERP HTTPS endpoint.

#### Cross-cutting
- Spring beans and Apache Camel routes are defined in XML.
- Redelivery is configured with `maximumRedeliveries=5`, `redeliveryDelay=2000`, and `useExponentialBackOff=true`.
