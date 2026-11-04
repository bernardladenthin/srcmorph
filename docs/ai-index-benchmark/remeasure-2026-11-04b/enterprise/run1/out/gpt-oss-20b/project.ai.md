### srcmorph-modelcard
- H: 1.0
- C: 3B050084
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T12:28:25Z
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
The project exposes a stateless RESTful back‑office order service that retrieves, cancels (with payment refunds), and nightly cleans up cancelled orders, and is documented via OpenAPI. A BPMN workflow orchestrates order fulfilment by reserving stock, processing payments, approving large orders, shipping items, and generating invoices, while a Camel integration layer validates orders, audits them, and submits to ERP with dead‑letter handling. The Jakarta EE configuration wires EJB services, JMS listeners, and web components, with MyBatis and Hibernate persistence for customers and shipments, and a Docker/Kubernetes deployment stack that includes health checks, routing, and database connectivity. The front‑end consists of Facelets and JSP pages for order entry and management, and a Groovy script calculates shipping costs with tiered rates and free‑shipping thresholds.

#### Packages
- java — A stateless RESTful service that manages back‑office orders, handling retrieval, cancellation with refunds, and nightly cleanup of old cancelled orders.
- java/shop — Provides a stateless RESTful service for back‑office order management, handling order retrieval, cancellation, and nightly cleanup of old cancelled orders.
- other/actions/.github — Automates the CI/CD pipeline for the Orders Backoffice application, building Java artifacts, containerizing them, and deploying to a production Kubernetes cluster on tag pushes.
- other/actions/.github/workflows — Automates the CI/CD pipeline for the Orders Backoffice application, building Java artifacts, containerizing them, and deploying to a production Kubernetes cluster on tag pushes.
- other/actions — Automates the CI/CD pipeline for the Orders Backoffice application, building Java artifacts, containerizing them, and deploying to a production Kubernetes cluster on tag pushes.
- other/bpmn — Provides a BPMN‑based order‑fulfilment workflow that reserves stock, processes payments, approves large orders, ships items, and creates invoices.
- other/docker — A Dockerfile that builds and deploys a Java back‑office service for order management.
- other/facelets — A web‑frontend page that gathers a customer’s shipping details, lets them pick a shipping method, shows the order total, and submits the order.
- other/groovy — Calculates shipping cost for orders, applying free‑shipping thresholds and tiered rates while rejecting parcels that exceed a maximum weight.
- other/integration — Provides Camel‑based integration for order validation, audit, and ERP submission with dead‑letter handling.
- other/jee — Provides the Jakarta EE configuration for a shop application, wiring EJB services, JMS listeners, and web components with security, transactions, and resource references.
- other/jsp — A JSP page that lists open orders, lets users filter by customer email or status, and cancel individual orders.
- other/k8s — Deploys and exposes the orders‑backoffice microservice on Kubernetes, handling database connectivity, health checks, and external routing.
- other/kotlin — Generates and stores invoices for completed orders, applying tax rules and payment terms.
- other/mybatis — Provides MyBatis SQL mappings for customer persistence, enabling CRUD operations and dynamic queries on the `CUSTOMER` table.
- other/openapi — Provides a REST API for back‑office order management, exposing listing, retrieval, and cancellation of shop orders.
- other — A comprehensive, multi‑layered e‑commerce platform that manages orders, inventory, shipping, invoicing, and reporting across CI/CD, BPMN, Camel, Jakarta EE, and Kubernetes environments.
- other/persistence — Provides Hibernate‑based persistence for the Shop application, mapping shipment entities, configuring JPA units, and supplying queries for shipment status and carrier statistics.
- other/wsdl — Provides a SOAP web service for querying stock levels by SKU.
- . — A stateless back‑office order service that exposes REST APIs for order retrieval, cancellation with payment refunds, and nightly cleanup of old cancelled orders, integrated into a larger e‑commerce platform with CI/CD, BPMN workflows, Camel integration, and Kubernetes deployment.
