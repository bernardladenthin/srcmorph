### order-routes.xml
- H: 1.0
- C: C08413DB
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T08:13:47Z
- G: 
- A: 0.0.0
- X: file
- P: 326D0B29
- N: 06F61DFE
---
**Facts (exact, whole file):** elements: 30; namespace declarations: 3; id and name attributes: 7; XSD type definitions: 0; XSLT templates: 0; Spring beans: 4; Camel routes and route templates: 3; Camel REST services: 0; Camel consumer endpoints (from): 3; Camel producer endpoints: 6; Mule flows and sub-flows: 0; Mule flow references: 0; Spring Integration channels: 0; channel adapters and gateways: 0; Synapse proxies, APIs, sequences and endpoints (named): 0; error handlers: 1; property placeholders: 6

> Routes incoming orders through validation and review-or-ERP processing, with audit tapping and dead-letter handling for ERP outages.

#### Type
- Spring XML / Apache Camel integration routes; root `beans`.
- Namespaces: default `http://www.springframework.org/schema/beans`; `xsi` → `http://www.w3.org/2001/XMLSchema-instance`; nested Camel context default → `http://camel.apache.org/schema/spring`.

#### Definitions
- Beans: `orderValidator` → `shop.integration.OrderValidator`; `invoiceTranslator` → `shop.integration.InvoiceTranslator`.
- Camel context: `orderIntegration`; property placeholder `properties` loads `classpath:integration.properties`.
- Routes:
  - `incomingOrders`: consumes `jms:queue:{{orders.incoming.queue}}`; calls `orderValidator.validate`; sends orders with `${body.total} > {{orders.review.threshold}}` to `direct:manualReview`, otherwise to `direct:toErp`; wire-taps `seda:orderAudit`.
  - `toErp`: consumes `direct:toErp`; calls `invoiceTranslator`; sends to dynamic URL `https://{{erp.host}}/api/orders/${header.orderId}`.
  - `manualReview`: consumes `direct:manualReview`; splits `${body.lines}` and sends each line to `jms:queue:review.lines`.
- Exception handling: `shop.integration.ErpUnavailableException` is handled; routes to `jms:queue:orders.deadletter` after configured redelivery.

#### Configuration
- `orders.incoming.queue`, `orders.review.threshold`, `erp.host`: values supplied by `classpath:integration.properties`.
- Redelivery for `shop.integration.ErpUnavailableException`: `maximumRedeliveries="5"`, `redeliveryDelay="2000"`, `useExponentialBackOff="true"`.

#### Dependencies
- Classes: `shop.integration.OrderValidator`, `shop.integration.InvoiceTranslator`, `shop.integration.ErpUnavailableException`.
- Referenced properties file: `classpath:integration.properties`.
- JMS queues: configured incoming queue, `orders.deadletter`, `review.lines`; other endpoints: `direct:manualReview`, `direct:toErp`, `seda:orderAudit`.
- External ERP API host supplied by `erp.host`; order ID supplied by `${header.orderId}`.
