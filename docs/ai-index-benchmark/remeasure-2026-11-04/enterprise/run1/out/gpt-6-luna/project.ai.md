### srcmorph-modelcard
- H: 1.0
- C: A624A2C6
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T08:10:02Z
- G: 
- A: 0.0.0
- X: project
- F: [java](java/package.ai.md)
- F: [java/shop](java/shop/package.ai.md)
- F: [other/actions/.github](other/actions/.github/package.ai.md)
- F: [other/actions/.github/workflows](other/actions/.github/workflows/package.ai.md)
- F: [other/actions](other/actions/package.ai.md)
- F: [other/bpmn](other/bpmn/package.ai.md)
- F: [other/docker](other/docker/package.ai.md)
- F: [other/facelets](other/facelets/package.ai.md)
- F: [other/groovy](other/groovy/package.ai.md)
- F: [other/integration](other/integration/package.ai.md)
- F: [other/jee](other/jee/package.ai.md)
- F: [other/jsp](other/jsp/package.ai.md)
- F: [other/k8s](other/k8s/package.ai.md)
- F: [other/kotlin](other/kotlin/package.ai.md)
- F: [other/mybatis](other/mybatis/package.ai.md)
- F: [other/openapi](other/openapi/package.ai.md)
- F: [other](other/package.ai.md)
- F: [other/persistence](other/persistence/package.ai.md)
- F: [other/wsdl](other/wsdl/package.ai.md)
- F: [.](package.ai.md)
---
> Project index of srcmorph-modelcard: one line per package; leads here, clickable links in the header F list.

#### Overview
The shop order lifecycle spans checkout and delivery selection, shipping calculation, stock reservation, payment, and BPMN-coordinated fulfilment, with integration handling validation, review, and ERP submission. Persistence and supporting services manage orders, shipments, customers, and invoices, while the Java back office provides order lookup, cancellation with refunds, and cleanup of old cancelled orders. OpenAPI, web interfaces, container and Kubernetes configuration, and automated workflows support access, deployment, and operation.

#### Packages
- java — Backoffice order management supports order lookup, cancellation with payment refunds, and scheduled cleanup of long-cancelled orders.
- java/shop — Backoffice users can review orders, retrieve individual orders, cancel them with payment refunds, and automatically purge old cancelled orders.
- other/actions/.github — Automates verification, container release, and production deployment of the orders backoffice.
- other/actions/.github/workflows — Automates verification, container publishing, and production deployment of the orders backoffice for releases.
- other/actions — Automates verification, container-image release, and production deployment of the orders backoffice.
- other/bpmn — Coordinates order fulfilment from stock reservation and payment receipt through approval, shipping, invoicing, or out-of-stock cancellation.
- other/docker — Packages and runs the Orders Backoffice Java application as a production-configured container.
- other/facelets — Collects a customer’s delivery details and shipping choice, updates the order total, and submits the order.
- other/groovy — Calculates order shipping charges from order value and parcel weight, with free-shipping eligibility and overweight-parcel rejection.
- other/integration — Processes incoming orders through validation, high-value review, and ERP submission, with audit and failure handling.
- other/jee — Configures the shop back office’s web access and the EJB services that reserve stock, process checkout, and receive order-placement events.
- other/jsp — Staff can search and review open orders, including totals and overdue status, and access order cancellation actions.
- other/k8s — Runs the shop orders back office and makes it available internally and through an HTTP endpoint.
- other/kotlin — Converts completed orders into numbered invoices, applies payment deadlines and category-based tax rates, and records issued invoices.
- other/mybatis — Supports customer record retrieval, creation, loyalty-tier promotion, and inactive-customer cleanup in the `CUSTOMER` table.
- other/openapi — Defines backoffice capabilities for browsing shop orders, viewing order details, and cancelling orders.
- other — Supports the shop’s order and fulfilment lifecycle through checkout, workflow orchestration, shipping, invoicing, persistence, integrations, and back-office operations.
- other/persistence — Persists shop shipments, orders, customers, and reporting data through Hibernate/JPA mappings, persistence units, and shipment queries.
- other/wsdl — Defines the SOAP contract for querying stock availability by SKU and reporting unknown-SKU faults.
- . — Processes shop orders from checkout and fulfilment through invoicing, persistence, and back-office lookup, cancellation, and retention.
