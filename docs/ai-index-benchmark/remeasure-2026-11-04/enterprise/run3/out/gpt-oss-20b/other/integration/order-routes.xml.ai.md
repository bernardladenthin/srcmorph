### order-routes.xml
- H: 1.0
- C: C08413DB
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T08:23:25Z
- G: 
- A: 0.0.0
- X: file
- P: 326D0B29
- N: 06F61DFE
---
**Facts (exact, whole file):** elements: 30; namespace declarations: 3; id and name attributes: 7; XSD type definitions: 0; XSLT templates: 0; Spring beans: 4; Camel routes and route templates: 3; Camel REST services: 0; Camel consumer endpoints (from): 3; Camel producer endpoints: 6; Mule flows and sub-flows: 0; Mule flow references: 0; Spring Integration channels: 0; channel adapters and gateways: 0; Synapse proxies, APIs, sequences and endpoints (named): 0; error handlers: 1; property placeholders: 6

> Configures a Spring‑Camel integration flow for processing incoming orders, validating them, routing to ERP or manual review, and handling exceptions.

#### Purpose
- Defines order processing routes.
- Validates orders and translates invoices.
- Routes orders to ERP or manual review based on total.
- Handles ERP unavailability with redelivery and dead‑letter queue.

#### Type
- Spring XML configuration (`<beans>` root).
- Declares Spring namespace `http://www.springframework.org/schema/beans` and Camel namespace `http://camel.apache.org/schema/spring`.
- Includes Camel schema location.

#### Definitions
- **Beans**
  - `orderValidator` → `shop.integration.OrderValidator`
  - `invoiceTranslator` → `shop.integration.InvoiceTranslator`
- **Camel Context** `orderIntegration`
  - **Property placeholder** `properties` → `classpath:integration.properties`
  - **Exception handling** for `shop.integration.ErpUnavailableException`
    - Redelivery: max 5, delay 2000 ms, exponential back‑off.
    - Handled flag set to `true`.
    - Dead‑letter queue: `jms:queue:orders.deadletter`
  - **Routes**
    - `incomingOrders`
      - From `jms:queue:{{orders.incoming.queue}}`
      - Bean `orderValidator.validate`
      - Choice: if `${body.total} > {{orders.review.threshold}}` → `direct:manualReview`; else → `direct:toErp`
      - Wire‑tap to `seda:orderAudit`
    - `toErp`
      - From `direct:toErp`
      - Bean `invoiceTranslator`
      - Dynamic to `https://{{erp.host}}/api/orders/${header.orderId}`
    - `manualReview`
      - From `direct:manualReview`
      - Split on `${body.lines}` → `jms:queue:review.lines`
    - (Commented out `legacyFtpImport` route)

#### Configuration
- `orders.incoming.queue` (property placeholder)
- `orders.review.threshold` (property placeholder)
- `erp.host` (property placeholder)
- `orders.deadletter` queue URI
- Redelivery policy: `maximumRedeliveries="5"`, `redeliveryDelay="2000"`, `useExponentialBackOff="true"`

#### Dependencies
- `shop.integration.OrderValidator`
- `shop.integration.InvoiceTranslator`
- `shop.integration.ErpUnavailableException`
- External JMS queues: `orders.incoming.queue`, `orders.deadletter`, `review.lines`
- External ERP endpoint: `https://{{erp.host}}/api/orders/${header.orderId}`
- Camel Spring schema `https://camel.apache.org/schema/spring/camel-spring.xsd`
