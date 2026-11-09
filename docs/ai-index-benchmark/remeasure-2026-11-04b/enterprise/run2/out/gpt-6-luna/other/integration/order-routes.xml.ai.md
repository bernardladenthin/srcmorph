### order-routes.xml
- H: 1.0
- C: C08413DB
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T12:29:23Z
- G: 
- A: 0.0.0
- X: file
- P: D9E787D0
- N: 06F61DFE
---
**Facts (exact, whole file):** elements: 30; namespace declarations: 3; id and name attributes: 7; XSD type definitions: 0; XSLT templates: 0; Spring beans: 4; Camel routes and route templates: 3; Camel REST services: 0; Camel consumer endpoints (from): 3; Camel producer endpoints: 6; Mule flows and sub-flows: 0; Mule flow references: 0; Spring Integration channels: 0; channel adapters and gateways: 0; Synapse proxies, APIs, sequences and endpoints (named): 0; error handlers: 1; property placeholders: 6

> Defines Camel order-processing routes that validate incoming orders, route high-value orders for manual review, and send other orders to an ERP system.

#### Purpose
- Processes orders from a configurable JMS queue, applying validation and threshold-based routing.
- Retries ERP-unavailable failures, sends handled failures to a dead-letter queue, and copies incoming orders to an audit channel.

#### Type
- Apache Camel Spring integration configuration; root element `beans`.
- Namespaces: default `http://www.springframework.org/schema/beans`; `xsi` = `http://www.w3.org/2001/XMLSchema-instance`; `camelContext` declares the default namespace `http://camel.apache.org/schema/spring`.
- Schema locations: Spring Beans `https://www.springframework.org/schema/beans/spring-beans.xsd`; Camel Spring `https://camel.apache.org/schema/spring/camel-spring.xsd`.

#### Definitions
- Beans: `orderValidator` → `shop.integration.OrderValidator`; `invoiceTranslator` → `shop.integration.InvoiceTranslator`.
- Camel context `orderIntegration`; property placeholder source `classpath:integration.properties`.
- Route `incomingOrders`: consumes `jms:queue:{{orders.incoming.queue}}`; calls `orderValidator.validate`; if `${body.total} > {{orders.review.threshold}}`, sends to `direct:manualReview`, otherwise to `direct:toErp`; then wire-taps to `seda:orderAudit`.
- Route `toErp`: consumes `direct:toErp`; calls `invoiceTranslator`; sends dynamically to `https://{{erp.host}}/api/orders/${header.orderId}`.
- Route `manualReview`: consumes `direct:manualReview`; splits `${body.lines}` and sends each part to `jms:queue:review.lines`.
- Exception handling: catches `shop.integration.ErpUnavailableException`; retries with configured redelivery policy; marks the exception handled and sends to `jms:queue:orders.deadletter`.

#### Configuration
- `maximumRedeliveries=5`; `redeliveryDelay=2000`; `useExponentialBackOff=true`.
- Placeholders: `orders.incoming.queue`, `orders.review.threshold`, `erp.host`; values supplied through `classpath:integration.properties`.
- Dynamic URI expression: `${header.orderId}` in the ERP order URL.

#### Dependencies
- Classes: `shop.integration.OrderValidator`, `shop.integration.InvoiceTranslator`, `shop.integration.ErpUnavailableException`.
- Property file: `classpath:integration.properties`.
- External endpoints: configurable incoming JMS queue; `jms:queue:review.lines`; `jms:queue:orders.deadletter`; `seda:orderAudit`; ERP HTTPS API at `{{erp.host}}/api/orders/${header.orderId}`.
