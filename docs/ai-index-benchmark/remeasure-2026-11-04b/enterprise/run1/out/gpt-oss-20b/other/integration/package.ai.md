### other/integration
- H: 1.0
- C: 369070B0
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T12:27:18Z
- G: 
- A: 0.0.0
- X: package
- F: [order-routes.xml](order-routes.xml.ai.md)
---
> Provides Camel‑based integration for order validation, audit, and ERP submission with dead‑letter handling.

#### Purpose
- Configure order processing routes in Camel.
- Validate orders, audit, and route to ERP or manual review.
- Handle ERP unavailability with retries and dead‑letter queue.

#### Responsibilities
- **Order Validation** – bean `orderValidator` checks order integrity.
- **Audit & Review** – split orders for manual review and audit logging.
- **ERP Communication** – translate orders via `invoiceTranslator` and POST to ERP API.
- **Error Handling** – retry logic for `ErpUnavailableException` and dead‑letter routing.

#### Key units
- `orderValidator` (`shop.integration.OrderValidator`) – validates order payload.
- `invoiceTranslator` (`shop.integration.InvoiceTranslator`) – converts order to ERP format.
- `ErpUnavailableException` (`shop.integration.ErpUnavailableException`) – signals ERP connectivity issues.
- Camel context `orderIntegration` – hosts routes and exception policy.
- Routes: `incomingOrders`, `toErp`, `manualReview`.

#### Data flow
1. **Incoming**: `jms:queue:{{orders.incoming.queue}}` → `orderValidator.validate`.
2. **Decision**: if `body.total > {{orders.review.threshold}}` → `direct:manualReview`; else → `direct:toErp`.
3. **Audit**: `wireTap` to `seda:orderAudit`.
4. **ERP Path**: `direct:toErp` → `invoiceTranslator` → HTTP POST to `https://{{erp.host}}/api/orders/${header.orderId}`.
5. **Manual Review**: split `body.lines` → `jms:queue:review.lines`.

#### Dependencies
- **Spring** – XML configuration, property placeholders.
- **Camel** – routes, exception handling, SEDA, JMS, HTTP.
- **External classes** – `OrderValidator`, `InvoiceTranslator`, `ErpUnavailableException`.
- **External systems** – JMS queues (`orders.deadletter`, `review.lines`), SEDA channel (`orderAudit`), ERP HTTP endpoint.

#### Cross-cutting
- Property placeholders (`orders.incoming.queue`, `orders.review.threshold`, `erp.host`).
- Exception policy: 5 redeliveries, 2000 ms delay, exponential back‑off.
- Dead‑letter queue `jms:queue:orders.deadletter` for unprocessable orders.
