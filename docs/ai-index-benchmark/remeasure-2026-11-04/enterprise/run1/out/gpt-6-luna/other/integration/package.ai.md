### other/integration
- H: 1.0
- C: 369070B0
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T08:08:56Z
- G: 
- A: 0.0.0
- X: package
- F: [order-routes.xml](order-routes.xml.ai.md)
---
> Processes incoming orders through validation, high-value review, and ERP submission, with audit and failure handling.

#### Purpose
- Integrates order intake with manual review and ERP order submission.
- Routes failed ERP deliveries to a dead-letter queue after retries.

#### Responsibilities
- **Order routing:** Validate incoming JMS orders, branch on the configured review threshold, and wire-tap orders to an audit stream.
- **Review and ERP processing:** Split review orders into lines for a JMS queue; translate other orders and submit them to the ERP endpoint.
- **Failure handling:** Retry `ErpUnavailableException` deliveries and route exhausted failures to the dead-letter queue.

#### Key units
- `orderIntegration` — Camel context containing the order routes and property placeholder.
- `incomingOrders` — validates, branches, and audits incoming orders.
- `toErp` — translates orders and sends them to the configured ERP URL.
- `manualReview` — splits orders into lines for review.
- `orderValidator` (`shop.integration.OrderValidator`) — validates orders.
- `invoiceTranslator` (`shop.integration.InvoiceTranslator`) — translates orders for ERP submission.

#### Data flow
- Configured incoming JMS queue → `OrderValidator.validate` → audit wire-tap to `seda:orderAudit` and threshold-based routing.
- High-value orders → `manualReview` → split order lines → `review.lines` JMS queue.
- Other orders → `toErp` → `InvoiceTranslator` → ERP URL using the order ID.
- `ErpUnavailableException` → up to five retries with delay and exponential backoff → `orders.deadletter`.

#### Dependencies
- `classpath:integration.properties` supplies the incoming queue, review threshold, and ERP host.
- `shop.integration.OrderValidator`, `shop.integration.InvoiceTranslator`, and `shop.integration.ErpUnavailableException`.
- JMS queues, `seda:orderAudit`, and the configured ERP HTTP endpoint.

#### Cross-cutting
- Spring/Camel XML configuration with externalized property placeholders.
- ERP failures use five redeliveries, a 2000 ms delay, and exponential backoff; handled failures go to the dead-letter queue.
