### other/integration
- H: 1.0
- C: 369070B0
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T08:11:51Z
- G: 
- A: 0.0.0
- X: package
- F: [order-routes.xml](order-routes.xml.ai.md)
---
> Provides a Camel‑based integration layer that validates, audits, and routes order messages to an ERP system or a manual review queue, with retry and dead‑letter handling for ERP failures.

#### Purpose
- Configure Camel routes for order processing.
- Validate orders and translate invoices.
- Route orders to ERP or manual review based on total amount.
- Handle ERP unavailability with retries and dead‑letter queue.

#### Responsibilities
- **Order validation** – `orderValidator` bean checks order structure and business rules.
- **Invoice translation** – `invoiceTranslator` bean converts order objects to ERP‑compatible payloads.
- **Routing logic** – Camel routes decide destination (`toErp` or `manualReview`) and audit messages.
- **Error handling** – Redelivery policy for `ErpUnavailableException` and dead‑letter queue.

#### Key units
- `orderValidator` (`shop.integration.OrderValidator`) – validates incoming orders.
- `invoiceTranslator` (`shop.integration.InvoiceTranslator`) – translates orders to ERP format.
- `ErpUnavailableException` (`shop.integration.ErpUnavailableException`) – signals ERP connectivity issues.
- Camel context `orderIntegration` – hosts all routes and exception handling.
- Routes: `incomingOrders`, `toErp`, `manualReview`, (commented `legacyFtpImport`).

#### Data flow
1. **Incoming**: `jms:queue:{{orders.incoming.queue}}` → `incomingOrders`.
2. **Validation**: `orderValidator`.
3. **Audit**: wire‑tap to `seda:orderAudit`.
4. **Decision**: if `body.total > {{orders.review.threshold}}` → `manualReview`; else → `toErp`.
5. **Manual review**: split `body.lines` → `jms:queue:review.lines`.
6. **ERP**: translate via `invoiceTranslator` → HTTP POST to `https://{{erp.host}}/api/orders/${header.orderId}`.
7. **Failure**: on `ErpUnavailableException`, retry up to 5 times, then send to `jms:queue:orders.deadletter`.

#### Dependencies
- **Internal**: `shop.integration.OrderValidator`, `shop.integration.InvoiceTranslator`, `shop.integration.ErpUnavailableException`.
- **External**: JMS queues (`orders.deadletter`, `review.lines`), SEDA channel (`orderAudit`), HTTP endpoint (`https://{{erp.host}}/api/orders/...`).
- **Configuration**: `integration.properties` for queue names, thresholds, and ERP host.

#### Cross-cutting
- Property placeholders (`{{…}}`) for runtime configuration.
- Centralized exception handling with redelivery and dead‑letter strategy.
- Wire‑tap for audit logging without affecting main flow.
