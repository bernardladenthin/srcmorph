### srcmorph-modelcard
- H: 1.0
- C: 2E28F989
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T08:22:44Z
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
The project centers on the shop order lifecycle: checkout submits orders, fulfilment coordinates stock reservation, payment, approval, shipping, invoicing, and cancellation, while integration routes orders for review or ERP submission. Back-office services and interfaces support order lookup and cancellation, alongside customer, shipment, stock, and invoice data access and persistence. Workflow definitions, service contracts, container packaging, and Kubernetes and CI/CD configuration support deployment of the Orders Backoffice application.

#### Packages
- java — Backoffice order management provides secure order lookup, refund-backed cancellation with auditing, and scheduled cleanup of aged cancelled orders.
- java/shop — Backoffice order management supports order lookup, refund-backed cancellation, auditing, and scheduled removal of aged cancelled orders.
- other/actions/.github — Automates verification, versioned container publication, and production deployment for the Orders Backoffice application.
- other/actions/.github/workflows — Automates verification, container-image publication, and production Kubernetes deployment for the Orders Backoffice application.
- other/actions — Automates verification, container release, and production deployment of the Orders Backoffice application.
- other/bpmn — Orchestrates order fulfilment from stock reservation and payment through approval, shipping, invoicing, or cancellation when stock is unavailable.
- other/docker — Packages the Orders Backoffice Java application as a production-oriented container that exposes its HTTP service and reports liveness.
- other/facelets — The checkout flow collects delivery details and shipping preferences, recalculates the order total, and submits the order.
- other/groovy — Calculates parcel shipping charges from order value and weight, including free-shipping eligibility and rejection of parcels above the carrier’s weight limit.
- other/integration — Processes incoming orders by validating them, routing high-value orders for manual review, and submitting other orders to the ERP with audit and failure handling.
- other/jee — Configures the shop’s back-office web access and enterprise services for reporting, stock reservation, checkout, and order-placement events.
- other/jsp — Provides staff with a searchable view of open orders, including order totals and overdue status, and a way to cancel orders.
- other/k8s — Runs the Orders back-office application and makes it available internally and through HTTP at `backoffice.shop.example`.
- other/kotlin — Converts completed orders into numbered invoices, calculates line totals and category-based tax rates, and records invoices with payment deadlines.
- other/mybatis — Provides database access for customer records, including lookup, filtered search, loyalty-tier updates, insertion, and inactive-customer deletion.
- other/openapi — Defines how shop backoffice clients list and retrieve customer orders and request cancellation of orders that have not shipped.
- other — Defines the shop’s order, checkout, fulfilment, invoicing, customer, shipment, and stock workflows, together with the interfaces and deployment configuration that support them.
- other/persistence — Persists shipment and order-related data through Hibernate mappings and configures JTA shop and resource-local reporting persistence units.
- other/wsdl — Defines the contract clients use to query a product’s available stock by SKU and handle unknown-SKU faults.
- . — Supports the shop’s order lifecycle from checkout and fulfilment through cancellation, invoicing, back-office management, and deployment.
