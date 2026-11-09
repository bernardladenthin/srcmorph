### other/integration
- H: 1.0
- C: 369070B0
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T08:18:19Z
- G: 
- A: 0.0.0
- X: package
- F: [order-routes.xml](order-routes.xml.ai.md)
---
> Orchestrates order intake, validation, and routing to ERP or manual review, with robust error handling and audit logging.

#### Purpose
- Define Camel integration routes for order processing.
- Validate orders and translate invoices.
- Route orders to ERP or manual review based on total.
- Provide redelivery and dead‑letter handling for ERP failures.

#### Responsibilities
- **Order Intake** – consume orders from a JMS queue.
- **Validation & Translation** – apply business rules and convert to ERP format.
- **Routing Logic** – decide between ERP submission or manual review.
- **Error Management** – retry, back‑off, and dead‑letter queue for ERP outages.
- **Audit** – wire‑tap orders to an audit channel.

#### Key units
- **Beans**
  - `orderValidator` (`shop.integration.OrderValidator`) – validates order payload.
  - `invoiceTranslator` (`shop.integration.InvoiceTranslator`) – converts order to ERP invoice.
- **Exception**
  - `shop.integration.ErpUnavailableException` – signals ERP downtime.
- **Camel Context** `orderIntegration`
  - Routes: `incomingOrders`, `toErp`, `manualReview`.
  - Property placeholders: `orders.incoming.queue`, `orders.review.threshold`, `erp.host`, `orders.deadletter`.

#### Data flow
1. **Incoming Orders** – `jms:queue:{{orders.incoming.queue}}` → `orderValidator.validate`.
2. **Decision** – if `body.total > {{orders.review.threshold}}` → `direct:manualReview`; else → `direct:toErp`.
3. **Audit** – wire‑tap to `seda:orderAudit`.
4. **ERP Path** – `direct:toErp` → `invoiceTranslator` → dynamic HTTP POST to `https://{{erp.host}}/api/orders/${header.orderId}`.
5. **Manual Review Path** – `direct:manualReview` → split `body.lines` → `jms:queue:review.lines`.

#### Dependencies
- **Internal** – `OrderValidator`, `InvoiceTranslator`, `ErpUnavailableException`.
- **External** – JMS queues: `orders.incoming.queue`, `orders.deadletter`, `review.lines`; HTTP endpoint: `https://{{erp.host}}/api/orders/${header.orderId}`.

#### Cross-cutting
- Property placeholders for queue names, thresholds, and ERP host.
- Centralized exception handling with redelivery policy and dead‑letter queue.
- Wire‑tap for audit logging via `seda:orderAudit`.
