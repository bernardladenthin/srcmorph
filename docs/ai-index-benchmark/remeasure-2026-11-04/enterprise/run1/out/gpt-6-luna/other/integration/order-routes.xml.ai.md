### order-routes.xml
- H: 1.0
- C: C08413DB
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T08:07:20Z
- G: 
- A: 0.0.0
- X: file
- P: 326D0B29
- N: 06F61DFE
---
**Facts (exact, whole file):** elements: 30; namespace declarations: 3; id and name attributes: 7; XSD type definitions: 0; XSLT templates: 0; Spring beans: 4; Camel routes and route templates: 3; Camel REST services: 0; Camel consumer endpoints (from): 3; Camel producer endpoints: 6; Mule flows and sub-flows: 0; Mule flow references: 0; Spring Integration channels: 0; channel adapters and gateways: 0; Synapse proxies, APIs, sequences and endpoints (named): 0; error handlers: 1; property placeholders: 6

> Defines Camel order-processing routes that validate incoming orders, send high-value orders for review, and translate other orders for ERP submission.

#### Type
- Spring / Apache Camel integration XML; root element `beans`.
- Namespaces: default `http://www.springframework.org/schema/beans`; `xsi` — `http://www.w3.org/2001/XMLSchema-instance`; `camelContext` declares the default namespace `http://camel.apache.org/schema/spring`.

#### Definitions
- Beans: `orderValidator` — `shop.integration.OrderValidator`; `invoiceTranslator` — `shop.integration.InvoiceTranslator`.
- Camel context `orderIntegration`; property placeholder `properties` loads `classpath:integration.properties`.
- Route `incomingOrders`: consumes `jms:queue:{{orders.incoming.queue}}`; calls `orderValidator.validate`; sends orders where `${body.total} > {{orders.review.threshold}}` to `direct:manualReview`, otherwise to `direct:toErp`; wire-taps to `seda:orderAudit`.
- Route `toErp`: consumes `direct:toErp`; calls `invoiceTranslator`; dynamically sends to `https://{{erp.host}}/api/orders/${header.orderId}`.
- Route `manualReview`: consumes `direct:manualReview`; splits `${body.lines}` and sends each line to `jms:queue:review.lines`.
- Exception handling for `shop.integration.ErpUnavailableException`: retries up to 5 times, with 2000 ms delay and exponential backoff; marks the exception handled and sends to `jms:queue:orders.deadletter`.

#### Configuration
- `orders.incoming.queue`, `orders.review.threshold`, `erp.host`: externalized placeholders from `classpath:integration.properties`.
- Redelivery: `maximumRedeliveries="5"`, `redeliveryDelay="2000"`, `useExponentialBackOff="true"`.

#### Dependencies
- `classpath:integration.properties`.
- Classes: `shop.integration.OrderValidator`, `shop.integration.InvoiceTranslator`, `shop.integration.ErpUnavailableException`.
- JMS queues: configured incoming queue, `orders.deadletter`, `review.lines`; ERP endpoint host configured by `erp.host`; `seda:orderAudit`.
