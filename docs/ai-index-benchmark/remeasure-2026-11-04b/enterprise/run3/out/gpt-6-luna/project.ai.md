### srcmorph-modelcard
- H: 1.0
- C: 0D46D947
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T12:40:55Z
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
The shop order lifecycle spans checkout, stock reservation, payment, fulfilment, shipping, and invoicing, with BPMN orchestration and integration services routing orders through approval, ERP submission, or cancellation. Back-office interfaces and an API support order lookup, filtering, cancellation refunds, and cleanup, while persistence and commerce services provide order, customer, shipment, stock, and reporting data. Java container packaging, Kubernetes deployment, and automated verification and publishing workflows support production operations.

#### Packages
- java — Backoffice staff can manage order lifecycles, including retrieving open orders, processing cancellation refunds, and purging aged cancellations.
- java/shop — Backoffice staff can manage order lifecycles by viewing orders, cancelling them with payment refunds, and removing old cancellations.
- other/actions/.github — Automates verification, versioned container publishing, and production deployment for the orders backoffice.
- other/actions/.github/workflows — Validates the orders backoffice across supported Java versions, publishes a version-tagged container image, and delegates production Kubernetes deployment.
- other/actions — Automates verification, versioned container publishing, and production deployment for the orders backoffice.
- other/bpmn — Coordinates order fulfilment from stock reservation and payment through approval, shipping, invoicing, or cancellation when stock is unavailable.
- other/docker — Packages and runs the Shop orders back-office service as a production-configured Java container.
- other/facelets — Collects delivery details and shipping preferences, recalculates the order total, and submits the order for checkout.
- other/groovy — Calculates order delivery charges from parcel weight and order value, including free-shipping eligibility and rejection of overweight parcels.
- other/integration — Processes incoming orders by validating them, routing high-value orders for line-item review, and submitting other orders to the ERP with failure recovery.
- other/jee — Configures the shop back-office and commerce services for report access, stock reservation, checkout, and order-event handling.
- other/jsp — Staff can review open orders, filter them by customer email and shipping status, and cancel individual orders.
- other/k8s — Runs the Orders back-office application on Kubernetes, connects it to the shop database, and exposes it over HTTP.
- other/kotlin — Completed customer orders are converted into numbered invoices with payment deadlines, persisted for billing, and assigned category-based tax rates.
- other/mybatis — Supports customer lookup, filtering, creation, loyalty-tier promotion, and cleanup of inactive shop customers.
- other/openapi — Defines the backoffice API contract for retrieving shop orders and requesting order cancellations.
- other — Supports the shop’s order lifecycle across checkout, fulfilment, billing, back-office operations, and the deployment and data services that keep them running.
- other/persistence — Persists shop shipments, orders, customers, and reporting data through Hibernate mappings and configured Jakarta Persistence units.
- other/wsdl — Provides a SOAP interface for querying stock availability by SKU and reporting unknown SKUs.
- . — Coordinates the shop’s order lifecycle from checkout and stock reservation through payment, fulfilment, invoicing, back-office management, and operational deployment.
