### order-routes.xml
- H: 1.0
- C: C08413DB
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T08:19:59Z
- G: 
- A: 0.0.0
- X: file
- P: 326D0B29
- N: 06F61DFE
---
**Facts (exact, whole file):** elements: 30; namespace declarations: 3; id and name attributes: 7; XSD type definitions: 0; XSLT templates: 0; Spring beans: 4; Camel routes and route templates: 3; Camel REST services: 0; Camel consumer endpoints (from): 3; Camel producer endpoints: 6; Mule flows and sub-flows: 0; Mule flow references: 0; Spring Integration channels: 0; channel adapters and gateways: 0; Synapse proxies, APIs, sequences and endpoints (named): 0; error handlers: 1; property placeholders: 6

> Routes incoming orders through validation, review-threshold decisions, ERP submission, audit tapping, and dead-letter handling with retries.

#### Purpose
- Defines Apache Camel order-processing routes and supporting Spring beans.
- Loads integration properties from `classpath:integration.properties`.

#### Type
- Spring beans configuration with Apache Camel routes; root element: `beans`.
- Namespaces: default `http://www.springframework.org/schema/beans`; `xsi` = `http://www.w3.org/2001/XMLSchema-instance`; nested Camel context uses default `http://camel.apache.org/schema/spring`.

#### Definitions
- Beans:
  - `orderValidator` — `shop.integration.OrderValidator`.
  - `invoiceTranslator` — `shop.integration.InvoiceTranslator`.
- Camel context `orderIntegration`; property placeholder `properties`.
- Route `incomingOrders`:
  - Consumes `jms:queue:{{orders.incoming.queue}}`.
  - Calls `orderValidator.validate`.
  - Sends orders with `${body.total} > {{orders.review.threshold}}` to `direct:manualReview`; otherwise to `direct:toErp`.
  - Wire-taps to `seda:orderAudit`.
- Route `toErp`:
  - Consumes `direct:toErp`.
  - Calls `invoiceTranslator`.
  - Dynamically sends to `https://{{erp.host}}/api/orders/${header.orderId}`.
- Route `manualReview`:
  - Consumes `direct:manualReview`.
  - Splits `${body.lines}` and sends each item to `jms:queue:review.lines`.
- Exception handling for `shop.integration.ErpUnavailableException`:
  - Redelivers up to 5 times, with 2000 ms delay and exponential backoff.
  - Marks the exception handled and sends to `jms:queue:orders.deadletter`.

#### Configuration
- Property source: `classpath:integration.properties`.
- Placeholders: `{{orders.incoming.queue}}`, `{{orders.review.threshold}}`, `{{erp.host}}`.
- Redelivery: `maximumRedeliveries="5"`, `redeliveryDelay="2000"`, `useExponentialBackOff="true"`.

#### Dependencies
- Classes: `shop.integration.OrderValidator`, `shop.integration.InvoiceTranslator`, `shop.integration.ErpUnavailableException`.
- File: `classpath:integration.properties`.
- Endpoints: JMS queues `orders.incoming.queue` (property-defined), `orders.deadletter`, `review.lines`; `direct:manualReview`, `direct:toErp`; `seda:orderAudit`; ERP HTTPS endpoint `https://{{erp.host}}/api/orders/${header.orderId}`.
