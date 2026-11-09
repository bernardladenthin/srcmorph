### order-routes.xml
- H: 1.0
- C: C08413DB
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T12:38:02Z
- G: 
- A: 0.0.0
- X: file
- P: D9E787D0
- N: 06F61DFE
---
**Facts (exact, whole file):** elements: 30; namespace declarations: 3; id and name attributes: 7; XSD type definitions: 0; XSLT templates: 0; Spring beans: 4; Camel routes and route templates: 3; Camel REST services: 0; Camel consumer endpoints (from): 3; Camel producer endpoints: 6; Mule flows and sub-flows: 0; Mule flow references: 0; Spring Integration channels: 0; channel adapters and gateways: 0; Synapse proxies, APIs, sequences and endpoints (named): 0; error handlers: 1; property placeholders: 6

> Defines order-message validation, review routing, ERP submission, line-item review delivery, auditing, and retry/dead-letter handling.

#### Purpose
- Routes incoming JMS orders to manual review or the ERP according to order total.
- Translates and submits ERP orders; handles ERP outages with retries and dead-letter delivery.

#### Type
- Spring bean configuration with Apache Camel routes; root element `beans`.
- Namespaces: default `http://www.springframework.org/schema/beans`; `xsi` = `http://www.w3.org/2001/XMLSchema-instance`; nested Camel context uses `http://camel.apache.org/schema/spring`.

#### Definitions
- Beans:
  - `orderValidator` — `shop.integration.OrderValidator`.
  - `invoiceTranslator` — `shop.integration.InvoiceTranslator`.
- Property placeholder `properties` loads `classpath:integration.properties`.
- Exception handling for `shop.integration.ErpUnavailableException`: retries with configured redelivery policy, marks handled, then sends to `jms:queue:orders.deadletter`.
- Route `incomingOrders`:
  - Consumes `jms:queue:{{orders.incoming.queue}}`.
  - Calls `orderValidator.validate`.
  - Routes orders with `${body.total} > {{orders.review.threshold}}` to `direct:manualReview`; otherwise to `direct:toErp`.
  - Wire-taps to `seda:orderAudit`.
- Route `toErp`:
  - Consumes `direct:toErp`.
  - Calls `invoiceTranslator`.
  - Dynamically sends to `https://{{erp.host}}/api/orders/${header.orderId}`.
- Route `manualReview`:
  - Consumes `direct:manualReview`.
  - Splits `${body.lines}` and sends each item to `jms:queue:review.lines`.

#### Configuration
- `maximumRedeliveries="5"`, `redeliveryDelay="2000"`, `useExponentialBackOff="true"`.
- Placeholders: `{{orders.incoming.queue}}`, `{{orders.review.threshold}}`, `{{erp.host}}`.
- Dynamic URI expression: `${header.orderId}`; route condition: `${body.total} > {{orders.review.threshold}}`.

#### Dependencies
- `classpath:integration.properties`.
- Classes: `shop.integration.OrderValidator`, `shop.integration.InvoiceTranslator`, `shop.integration.ErpUnavailableException`.
- External systems and endpoints: JMS queues `orders.incoming.queue` (configured), `orders.deadletter`, `review.lines`; SEDA endpoint `orderAudit`; ERP HTTPS API at `{{erp.host}}`.
