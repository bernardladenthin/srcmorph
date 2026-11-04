### srcmorph-modelcard
- H: 1.0
- C: E5A980DA
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T12:32:22Z
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
The project covers the shop order lifecycle, from customer checkout and shipping-charge calculation through stock reservation, payment, approval, fulfilment, and invoicing. Java back-office services and staff views support order lookup, cancellation, refunds, and auditing, alongside customer and order persistence, ERP integration, and API contracts. Docker and Kubernetes package and run the back office, while release workflows verify, publish, and deploy it.

#### Packages
- java — Backoffice staff can inspect and cancel orders, issue refunds, audit order changes, and automatically purge cancelled orders after a retention period.
- java/shop — Backoffice staff can manage order lookup and cancellation, issue refunds, audit order events, and automatically remove aged cancelled orders.
- other/actions/.github — Version-tagged orders backoffice releases are built and verified, published as container images, and handed off for production deployment.
- other/actions/.github/workflows — Automates verification, container publication, and production deployment of the orders backoffice on version-tagged releases.
- other/actions — Automates verification, container-image publication, and production deployment of versioned orders backoffice releases.
- other/bpmn — Coordinates order fulfilment from stock reservation and payment through approval, shipping, invoicing, or out-of-stock cancellation.
- other/docker — Packages the Shop orders backoffice Java application for production deployment and runtime health monitoring.
- other/facelets — Customers provide delivery details, select shipping, review the EUR order total, and submit an order.
- other/groovy — Calculates parcel shipping charges from order value and weight, applying free-shipping eligibility, weight-tier rates, and carrier weight limits.
- other/integration — Validates and routes incoming orders to ERP processing or manual review, while auditing orders and isolating failed deliveries.
- other/jee — Configures the shop back-office web application and its EJB services for report access, checkout, stock reservation, and order-placement event handling.
- other/jsp — Provides staff with a searchable view of customer orders and links to cancel individual orders.
- other/k8s — Runs the orders backoffice application and makes it available internally and at `backoffice.shop.example`.
- other/kotlin — Completed orders with invoice lines are assigned numbered invoices with payment deadlines, tax rates, and persistent storage.
- other/mybatis — Provides database-backed customer lookup, search, creation, promotion, and inactive-customer cleanup for the shop.
- other/openapi — Defines the backoffice contract for listing, retrieving, and cancelling shop orders.
- other — Supports the shop’s order lifecycle across checkout, fulfilment, shipping, invoicing, customer data, integrations, and back-office deployment.
- other/persistence — Configures persistence for shop orders, customers, shipments, and reporting data, including shipment mappings and lookup queries.
- other/wsdl — Defines the SOAP contract for retrieving a stock quantity by SKU and reporting unknown-SKU faults.
- . — Supports the shop order lifecycle from checkout and fulfilment through invoicing, back-office cancellation, integration, and deployment.
