### other/integration
- H: 1.0
- C: 369070B0
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T12:42:37Z
- G: 
- A: 0.0.0
- X: package
- F: [order-routes.xml](order-routes.xml.ai.md)
---
> Orchestrates order ingestion, validation, audit, and ERP transmission via Camel routes.

#### Purpose
- Centralizes order processing integration logic.
- Validates orders, routes them to ERP or manual review, and logs audits.
- Provides fault tolerance with dead‑letter handling.

#### Responsibilities
- **Order Validation** – uses `OrderValidator` bean to enforce business rules.
- **Audit Trail** – wire‑taps orders to `seda:orderAudit` for downstream logging.
- **Routing Decisions** – splits orders based on total amount threshold.
- **ERP Communication** – translates orders with `InvoiceTranslator` and posts to ERP API.
- **Manual Review** – splits large orders into line‑level messages for human inspection.
- **Error Management** – retries on `ErpUnavailableException`, then dead‑letters.

#### Key units
- **Beans**
  - `orderValidator` (`shop.integration.OrderValidator`) – validates order payloads.
  - `invoiceTranslator` (`shop.integration.InvoiceTranslator`) – converts orders to ERP format.
- **Camel Context** `orderIntegration`
  - **Routes**
    - `incomingOrders` – entry point from JMS, validation, choice, audit tap.
    - `toErp` – translates and posts to ERP endpoint.
    - `manualReview` – splits large orders for review.
  - **Exception Handling** – retries `ErpUnavailableException` (5×, 2 s delay, exponential), then sends to `jms:queue:orders.deadletter`.
- **Properties**
  - `orders.incoming.queue`, `orders.review.threshold`, `erp.host`, `orders.deadletter`.

#### Data flow
1. **Receive** order from `jms:queue:{{orders.incoming.queue}}`.
2. **Validate** via `orderValidator.validate`.
3. **Audit** via wire‑tap to `seda:orderAudit`.
4. **Route**:  
   - If `total > {{orders.review.threshold}}` → split lines to `jms:queue:review.lines`.  
   - Else → translate with `invoiceTranslator` and POST to `https://{{erp.host}}/api/orders/${header.orderId}`.
5. **Error**: on `ErpUnavailableException`, retry; on failure, dead‑letter to `orders.deadletter`.

#### Dependencies
- **Internal**: `shop.integration.OrderValidator`, `shop.integration.InvoiceTranslator`, `shop.integration.ErpUnavailableException`.
- **External**: JMS queues (`orders.deadletter`, `review.lines`), HTTP ERP API (`https://{{erp.host}}/api/orders/...`).

#### Cross-cutting
- Property placeholders (`{{…}}`) for queue names, thresholds, and ERP host.
- Unified exception handling with redelivery policy.
- Wire‑tap pattern for audit logging without affecting main flow.
