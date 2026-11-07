### srcmorph-modelcard
- H: 1.0
- C: 48A1D4F2
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T08:13:03Z
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
The back‑office order‑fulfilment system exposes REST, SOAP, and BPMN interfaces for managing the entire order lifecycle, from creation and cancellation with payment refund to nightly cleanup of stale orders. A Camunda‑based BPMN workflow orchestrates stock reservation, payment, approval, shipping, and invoicing, while a Camel integration layer routes validated orders to an ERP system or a manual review queue with retry and dead‑letter handling. Persistence is handled via Hibernate and MyBatis mappings against a PostgreSQL database, and invoices are generated in Kotlin applying tax rules and payment terms. Continuous delivery is automated through GitHub Actions, Docker builds, and Kubernetes deployments, ensuring the service is built, tested, containerised, and deployed to production reliably.

#### Packages
- java — A stateless JAX‑RS service that manages back‑office order lifecycle, including retrieval, cancellation with payment refund, and nightly cleanup of stale cancelled orders.
- java/shop — Provides a stateless JAX‑RS resource for back‑office order management, exposing CRUD‑like operations, cancellation with refund, and nightly purge of cancelled orders.
- other/actions/.github — Automates the CI/CD pipeline for the Backoffice Java application, building, testing, containerizing, and deploying it to production.
- other/actions/.github/workflows — Automates the CI/CD pipeline for the Backoffice service, building, testing, containerizing, and deploying it to production.
- other/actions — Automates the CI/CD pipeline for the Backoffice Java application, building, testing, containerizing, and deploying it to production.
- other/bpmn — Provides a Camunda‑based BPMN workflow for end‑to‑end order fulfilment, coordinating stock reservation, payment, approval, shipping, and invoicing.
- other/docker — Builds and deploys a Java back‑office service as a Docker image, compiling with Maven and running in an Alpine JRE container.
- other/facelets — A JSF Facelets page that gathers shipping details, lets the user pick a shipping method, shows the order total, and submits the order.
- other/groovy — A Groovy test suite that verifies DHL shipping cost calculations, free‑shipping thresholds, tiered pricing, and overweight handling.
- other/integration — Provides a Camel‑based integration layer that validates, audits, and routes order messages to an ERP system or a manual review queue, with retry and dead‑letter handling for ERP failures.
- other/jee — Provides the Jakarta EE configuration for a shop application, wiring enterprise beans, web components, and security.
- other/jsp — Provides a web interface for viewing, searching, and canceling open orders in the shop system.
- other/k8s — Deploys and exposes the `orders-backoffice` microservice in a Kubernetes cluster.
- other/kotlin — Generates and stores invoices for completed orders, applying tax rules and payment terms.
- other/mybatis — Provides MyBatis SQL mappings for CRUD and search operations on customer data in the shop domain.
- other/openapi — A back‑office service for managing shop orders: listing, retrieving, and cancelling orders with status and total amount information.
- other — A Java‑based shop back‑office that builds, tests, containerises, and deploys an order‑fulfilment service, exposing REST, SOAP, and BPMN interfaces while persisting orders, shipments, and invoices in a PostgreSQL database.
- other/persistence — Provides Hibernate‑based persistence for shop orders, shipments, and reporting, exposing entity mappings, queries, and persistence unit configuration.
- other/wsdl — Provides a SOAP web service for querying stock levels by SKU.
- . — A back‑office order‑fulfilment system that exposes REST, SOAP, and BPMN interfaces, handles order lifecycle, persistence, and automated CI/CD deployment.
