### order-routes.xml
- H: 1.0
- C: C08413DB
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T12:22:54Z
- G: 
- A: 0.0.0
- X: file
- P: D9E787D0
- N: 06F61DFE
---
**Facts (exact, whole file):** elements: 30; namespace declarations: 3; id and name attributes: 7; XSD type definitions: 0; XSLT templates: 0; Spring beans: 4; Camel routes and route templates: 3; Camel REST services: 0; Camel consumer endpoints (from): 3; Camel producer endpoints: 6; Mule flows and sub-flows: 0; Mule flow references: 0; Spring Integration channels: 0; channel adapters and gateways: 0; Synapse proxies, APIs, sequences and endpoints (named): 0; error handlers: 1; property placeholders: 6

> Defines Camel order-processing routes that validate incoming orders, route them to ERP or manual review, and handle ERP failures.

#### Purpose
- Integrates incoming JMS orders with validation, review routing, ERP submission, audit tapping, and dead-letter handling.
- Loads integration settings from `classpath:integration.properties`.

#### Type
- Spring / Apache Camel integration XML; root element `beans`.
- Namespaces: default `http://www.springframework.org/schema/beans`; `xsi` → `http://www.w3.org/2001/XMLSchema-instance`; nested Camel context uses default `http://camel.apache.org/schema/spring`.
- Schema locations: `https://www.springframework.org/schema/beans/spring-beans.xsd`; `https://camel.apache.org/schema/spring/camel-spring.xsd`.

#### Definitions
- Beans: `orderValidator` → `shop.integration.OrderValidator`; `invoiceTranslator` → `shop.integration.InvoiceTranslator`.
- Property placeholder: `properties`, loading `classpath:integration.properties`.
- Exception handling: `shop.integration.ErpUnavailableException`; handled as true, then sent to `jms:queue:orders.deadletter`.
- Routes:
  - `incomingOrders`: consumes `jms:queue:{{orders.incoming.queue}}`; calls `orderValidator.validate`; routes orders with `${body.total} > {{orders.review.threshold}}` to `direct:manualReview`, otherwise to `direct:toErp`; wire-taps to `seda:orderAudit`.
  - `toErp`: consumes `direct:toErp`; calls `invoiceTranslator`; dynamically sends to `https://{{erp.host}}/api/orders/${header.orderId}`.
  - `manualReview`: consumes `direct:manualReview`; splits `${body.lines}` and sends each part to `jms:queue:review.lines`.

#### Configuration
- Property source: `classpath:integration.properties`.
- `maximumRedeliveries=5`; `redeliveryDelay=2000`; `useExponentialBackOff=true`.
- Routing placeholders: `{{orders.incoming.queue}}`, `{{orders.review.threshold}}`, `{{erp.host}}`.
- Runtime expressions: `${body.total}`, `${body.lines}`, `${header.orderId}`.

#### Dependencies
- Classes: `shop.integration.OrderValidator`, `shop.integration.InvoiceTranslator`, `shop.integration.ErpUnavailableException`.
- Referenced file: `classpath:integration.properties`.
- Endpoints: `jms:queue:{{orders.incoming.queue}}`, `jms:queue:orders.deadletter`, `jms:queue:review.lines`, `seda:orderAudit`, `direct:manualReview`, `direct:toErp`, `https://{{erp.host}}/api/orders/${header.orderId}`.
