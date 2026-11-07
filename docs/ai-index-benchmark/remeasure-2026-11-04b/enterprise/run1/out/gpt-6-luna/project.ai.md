### srcmorph-modelcard
- H: 1.0
- C: 0EBC48AC
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T12:25:44Z
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
The project supports the shop order lifecycle: checkout and validation feed fulfilment, where stock and payment are handled before approval, shipping, invoicing, or cancellation. Back-office interfaces and Java services let staff query, refund, audit, and clean up orders, alongside customer, stock, shipping, and persistence integrations. Runtime configuration and container, Kubernetes, and release workflows support deploying the back-office application to production.

#### Packages
- java — Backoffice order operations provide access to open orders, cancellation with payment refunds and audit tracking, and scheduled cleanup of aged cancelled records.
- java/shop — Backoffice order operations support querying open orders, retrieving and refunding cancellations, and purging aged cancelled records.
- other/actions/.github — Releases the orders backoffice application by verifying versioned builds, publishing container images, and deploying them to production.
- other/actions/.github/workflows — Automates release of the orders backoffice application from version-tagged builds through container publishing and production deployment.
- other/actions — Verifies, publishes, and deploys versioned orders backoffice releases to production.
- other/bpmn — Coordinates order fulfilment from stock reservation and payment through approval, shipping, invoicing, or out-of-stock cancellation.
- other/docker — Packages the Orders Backoffice Java application as a production container that serves on port 8080.
- other/facelets — Collects a customer’s delivery address and shipping choice, recalculates the euro order total, and submits the order.
- other/groovy — Determines whether an order qualifies for free delivery, calculates weight-based shipping charges, and rejects parcels exceeding the configured weight limit.
- other/integration — Processes incoming orders by validating them, directing high-value orders to manual review and eligible orders to ERP, while auditing and dead-lettering failures.
- other/jee — Configures the shop back-office web application and enterprise services for report access, admin security, stock reservations, checkout, and order-event handling.
- other/jsp — Provides an order-listing interface for searching customer orders, reviewing order totals and overdue status, and initiating cancellation.
- other/k8s — Runs the Orders backoffice application and makes it reachable over HTTP for shop operations.
- other/kotlin — Issues invoices for completed orders, applying payment terms and tax rates while rejecting orders without invoice lines.
- other/mybatis — Provides database-backed customer lookup, filtering, creation, loyalty-tier promotion, and inactive-customer cleanup.
- other/openapi — Defines the backoffice contract for listing, retrieving, and cancelling shop orders.
- other — Defines the shop’s order, checkout, fulfilment, invoicing, shipping, stock, and back-office interfaces, runtime configuration, and release deployment.
- other/persistence — Configures shop and reporting persistence while mapping shipment records, their order and parcel relationships, and shipment queries.
- other/wsdl — Provides a SOAP interface for retrieving a stock item’s available quantity by SKU and reporting unknown-SKU faults.
- . — Supports the shop order lifecycle from checkout and fulfilment through shipping, invoicing, back-office cancellation, and operational deployment.
