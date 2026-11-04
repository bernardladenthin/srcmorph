### srcmorph-modelcard
- H: 1.0
- C: E0E32A4E
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T08:16:35Z
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
The project centers on end-to-end shop order processing: customer checkout feeds fulfilment, where stock reservation and payment lead to approval, shipping, invoicing, or out-of-stock cancellation. Back-office web and API interfaces let staff review orders and cancel them with refunds, while persistence, customer management, and ERP integration support the order lifecycle. Container and deployment automation package and hand off the back office for production.

#### Packages
- java — Backoffice staff can review orders, cancel them with payment refunds, and remove cancelled orders after a retention period.
- java/shop — Backoffice users can inspect orders, cancel them with payment refunds, and automatically remove long-cancelled orders.
- other/actions/.github — Automates validation, publication, and production deployment handoff for versioned Orders Backoffice container images.
- other/actions/.github/workflows — Validates and releases versioned Orders Backoffice container images, then hands them off for production deployment.
- other/actions — Validates, publishes, and hands off versioned Orders Backoffice container releases for production deployment.
- other/bpmn — Coordinates order fulfilment from stock reservation and payment through approval, shipping, invoicing, or out-of-stock cancellation.
- other/docker — Packages the Orders Backoffice Java application as a production container that serves HTTP traffic on port 8080.
- other/facelets — Customers provide delivery and shipping details, review an updated EUR order total, and submit checkout.
- other/groovy — Calculates order shipping charges by applying a free-shipping threshold, weight-based price tiers, and a maximum parcel-weight limit.
- other/integration — Processes incoming orders by validating them, routing high-value orders for manual review and others to the ERP, while auditing traffic and handling ERP outages.
- other/jee — Configures the shop back office’s web access and the EJB services that reserve stock, process checkout, and handle order-placed events.
- other/jsp — Staff can search and review customer orders, including totals and overdue status, and initiate order cancellation.
- other/k8s — Runs the Shop orders backoffice and makes it reachable over HTTP while connecting it to the orders database.
- other/kotlin — Converts completed orders into persisted invoices with payment deadlines, line totals, and category-based tax rates.
- other/mybatis — Provides customer persistence operations for looking up and filtering customers, inserting records, promoting loyalty tiers, and deleting inactive customers.
- other/openapi — Provides backoffice users with API access to list, retrieve, and cancel shop orders.
- other — Implements the shop’s order and back-office lifecycle across checkout, fulfilment, shipping, invoicing, customer and shipment persistence, and operational integration.
- other/persistence — Persists shop shipments and configures transactional shop data access alongside a separate daily-revenue reporting unit.
- other/wsdl — Defines the stock-availability lookup contract for retrieving inventory by SKU and reporting unknown SKUs.
- . — Supports end-to-end shop order processing, from checkout and fulfilment through invoicing and back-office review, cancellation, and operational deployment.
