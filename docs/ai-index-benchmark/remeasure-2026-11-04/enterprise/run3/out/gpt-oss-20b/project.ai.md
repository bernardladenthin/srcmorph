### srcmorph-modelcard
- H: 1.0
- C: 9D9A76F5
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T08:25:25Z
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
The Jakarta EE back‑office system orchestrates the entire order lifecycle, from placement and stock reservation to invoicing and cancellation, exposing a RESTful Shop Orders API and a SOAP service for stock queries. A BPMN workflow drives fulfillment, while Spring‑Camel integration routes inbound orders to ERP or manual review and handles errors. The modular architecture includes MyBatis‑based persistence, Hibernate/JPA mapping, and a Kotlin invoicing service, all built, containerized, and deployed to Kubernetes via GitHub Actions pipelines. The front‑end comprises JSF checkout pages and JSP order listings, with shipping cost logic implemented in Groovy.

#### Packages
- java — A Jakarta EE back‑office service that manages customer orders, handling listing, retrieval, cancellation with refunds, and nightly cleanup of obsolete cancellations.
- java/shop — Provides RESTful back‑office services for managing customer orders, including listing, retrieval, cancellation, and nightly cleanup of obsolete cancellations.
- other/actions/.github — Automates the CI/CD pipeline for a Java Backoffice service, from build and test through Docker image creation to Kubernetes deployment.
- other/actions/.github/workflows — Automates building, testing, containerizing, and deploying a Java Backoffice service to a production Kubernetes cluster via GitHub Actions.
- other/actions — Automates the CI/CD pipeline for a Java Backoffice service, from build and test through Docker image creation to Kubernetes deployment.
- other/bpmn — Provides a BPMN‑based workflow for handling order fulfilment, from stock reservation through payment, approval, shipping, invoicing, and cancellation.
- other/docker — Builds and deploys a Java back‑office service as a Docker image, compiling the Maven project into a JAR and running it in an Alpine JRE container with health monitoring.
- other/facelets — A JSF checkout page that gathers shipping details, lets the user pick a shipping method, shows the order total, and submits the order.
- other/groovy — Provides shipping cost calculation logic and tests for DHL parcel shipping rules.
- other/integration — Orchestrates inbound order processing, validation, routing to ERP or manual review, and error handling within a Spring‑Camel integration flow.
- other/jee — Provides Jakarta EE configuration for a shop back‑office application, wiring enterprise beans, web components, security, and data‑source resources.
- other/jsp — A JSP page that lists open orders, supports filtering by customer email and status, and lets users cancel individual orders.
- other/k8s — Deploys and exposes the orders‑backoffice microservice in a Kubernetes cluster.
- other/kotlin — A service that creates, numbers, and persists invoices for completed orders, calculating due dates and tax rates.
- other/mybatis — Maps customer domain objects to SQL statements for CRUD and search operations.
- other/openapi — The Shop Orders API provides back‑office endpoints for listing, retrieving, and canceling customer orders, exposing order status, totals, and optional coupon codes.
- other — A modular shop‑backoffice system that builds, tests, containers, deploys, and exposes services for order processing, inventory, invoicing, and reporting.
- other/persistence — Provides Hibernate/JPA configuration for the shop shipping domain, mapping entities to PostgreSQL tables and defining persistence units for transactional and reporting data access.
- other/wsdl — Provides a SOAP web service for querying stock availability by SKU, with fault handling for unknown SKUs.
- . — A Jakarta EE back‑office system that orchestrates order lifecycle, from placement through stock reservation, invoicing, and cancellation, while providing CI/CD pipelines, BPMN workflows, and multi‑platform integrations.
