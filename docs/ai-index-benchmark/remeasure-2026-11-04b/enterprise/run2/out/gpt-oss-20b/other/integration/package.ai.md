### other/integration
- H: 1.0
- C: 369070B0
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T12:36:34Z
- G: 
- A: 0.0.0
- X: package
- F: [order-routes.xml](order-routes.xml.ai.md)
---
> Provides Camel‑based integration for order ingestion, validation, audit, and ERP submission with retry and dead‑letter handling.

#### Purpose
- Configure order validation, translation, and routing.
- Manage ERP communication and failure recovery.

#### Responsibilities
- **Order Validation** – bean `orderValidator` checks order integrity.
- **Invoice Translation** – bean `invoiceTranslator` converts orders for ERP.
- **Routing** – Camel routes for incoming orders, manual review, ERP submission, and audit.
- **Error Handling** – retry logic for `ErpUnavailableException` and dead‑letter queue.

#### Key units
- `orderValidator` (`shop.integration.OrderValidator`) – validates order payloads.
- `invoiceTranslator` (`shop.integration.InvoiceTranslator`) – translates orders to ERP format.
- `orderIntegration` (Camel context) – orchestrates routes and exception handling.
- `incomingOrders` route – consumes from `orders.incoming.queue`, validates, routes to review or ERP.
- `toErp` route – translates and posts to ERP API.
- `manualReview` route – splits large orders for line‑by‑line review.
- `orders.deadletter` queue – receives messages after max redelivery attempts.

#### Data flow
1. **Receive** order from `orders.incoming.queue`.
2. **Validate** via `orderValidator`.
3. **Decision**: if total > `orders.review.threshold` → `manualReview`; else → `toErp`.
4. **Audit**: wire‑tap to `orderAudit` (Seda).
5. **ERP Path**: translate with `invoiceTranslator`, POST to `erp.host/api/orders/{orderId}`.
6. **Review Path**: split lines, send each to `review.lines` queue.
7. **Failure**: on `ErpUnavailableException`, retry up to 5 times, then dead‑letter.

#### Dependencies
- External `integration.properties` for queue names, thresholds, and ERP host.
- Spring and Camel XML schemas.
- JMS queues: `orders.incoming.queue`, `orders.deadletter`, `review.lines`.
- ERP REST endpoint: `https://{erp.host}/api/orders/{orderId}`.

#### Cross-cutting
- Property placeholders (`{{…}}`) for configuration.
- Unified exception handling with exponential back‑off.
- Wire‑tap for audit logging.
- Use of `direct:` endpoints for internal routing.
