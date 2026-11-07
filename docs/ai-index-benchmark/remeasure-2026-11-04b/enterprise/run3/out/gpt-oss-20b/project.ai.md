### srcmorph-modelcard
- H: 1.0
- C: 93656303
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T12:43:24Z
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
The back‑office system exposes secure REST endpoints for listing, retrieving, and canceling shop orders, and orchestrates end‑to‑end fulfillment through a BPMN engine that handles stock reservation, payment capture, shipping, invoicing, and error handling. Order data is persisted with Hibernate and MyBatis, while a Groovy‑based shipping cost engine applies tiered pricing and free‑shipping thresholds. Integration with external ERP and shipping services is achieved via Camel routes and a SOAP web service for stock queries, and the entire application is containerized in an Alpine JRE image, deployed to Kubernetes with health checks and CI/CD pipelines automated by GitHub Actions. The system also provides a JSF Facelets page for order submission and a JSP page for order management, all coordinated by Jakarta EE deployment descriptors.

#### Packages
- java — A stateless REST service that manages back‑office orders, providing listing, detail retrieval, cancellation with payment refund, and nightly cleanup of old cancelled orders.
- java/shop — Provides a stateless REST service for back‑office order management, handling listing, retrieval, cancellation with payment refund, and nightly purge of cancelled orders.
- other/actions/.github — Automates the continuous integration, containerization, and Kubernetes deployment of the Backoffice Java service.
- other/actions/.github/workflows — Automates the continuous integration, containerization, and Kubernetes deployment of the Backoffice service.
- other/actions — Automates the continuous integration, containerization, and Kubernetes deployment of the Backoffice Java service.
- other/bpmn — Orchestrates end‑to‑end order fulfilment, from stock reservation and payment capture to approval, shipping, invoicing, and error handling.
- other/docker — Builds and runs the `orders-backoffice` Java application in a lightweight Alpine JRE container.
- other/facelets — A JSF Facelets page that gathers a customer’s shipping details, lets them pick a shipping method, shows the order total, and submits the order.
- other/groovy — A Groovy module that defines a shipping cost engine and its Spock tests, applying free‑shipping thresholds and tiered pricing while rejecting overweight parcels.
- other/integration — Orchestrates order ingestion, validation, audit, and ERP transmission via Camel routes.
- other/jee — Provides the Jakarta EE deployment descriptors for a shop back‑office application, configuring EJBs, a message‑driven bean, a servlet, filter, listener, security, and JDBC resources.
- other/jsp — A JSP page that lists open orders, lets users search by customer email or status, and cancel individual orders.
- other/k8s — Deploys and exposes the orders‑backoffice microservice on Kubernetes, handling database connectivity and health checks.
- other/kotlin — Generates and stores invoices for completed orders, applying tax rates and due dates.
- other/mybatis — Provides SQL mapping for customer entities, enabling CRUD and search operations via MyBatis.
- other/openapi — A RESTful API for managing shop orders, including listing, retrieving, and canceling orders.
- other — A comprehensive back‑office system that orchestrates order fulfilment, inventory, shipping, invoicing, and reporting across CI/CD, containerization, Kubernetes, and multiple integration patterns.
- other/persistence — Provides Hibernate persistence configuration for the shop’s shipping domain, defining entity mappings, queries, and persistence units for both transactional contexts.
- other/wsdl — Provides a SOAP web service for querying stock levels by SKU, with fault handling for unknown SKUs.
- . — A back‑office order‑management system that exposes secure REST endpoints, orchestrates fulfillment via BPMN, integrates with external ERP and shipping services, and automates CI/CD, containerization, and Kubernetes deployment.
