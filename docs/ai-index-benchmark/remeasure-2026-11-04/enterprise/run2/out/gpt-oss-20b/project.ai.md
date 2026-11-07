### srcmorph-modelcard
- H: 1.0
- C: 606DBFD0
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T08:19:19Z
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
The back‑office order management system exposes REST and SOAP APIs for order and stock handling, while a stateless Java service orchestrates the order lifecycle with audit logging, payment refunds, and nightly purge of cancelled orders. Order fulfilment is driven by a Camunda BPMN workflow that reserves stock, processes payments, approves, ships, invoices, and handles errors, with a lightweight Groovy engine calculating DHL shipping costs. Persistence is handled through Hibernate and MyBatis mappings for customer and shipping data, and Kotlin code generates invoices applying tax rules. CI/CD pipelines automate building, testing, containerizing, and deploying the service to Kubernetes, with Docker images running the JAR in an Alpine JRE container.

#### Packages
- java — A stateless REST service that manages back‑office order lifecycle, including listing, retrieval, cancellation with payment refund, audit logging, and nightly purge of cancelled orders.
- java/shop — Provides a stateless REST API for back‑office order management, including listing, retrieving, canceling, and nightly purging of cancelled orders.
- other/actions/.github — Automates the CI/CD pipeline for the Backoffice Java service, building, testing, containerizing, and deploying it to a Kubernetes cluster.
- other/actions/.github/workflows — Automates the CI/CD pipeline for the Backoffice Java service, building, testing, containerizing, and deploying it to a Kubernetes cluster.
- other/actions — Automates the CI/CD pipeline for the Backoffice Java service, building, testing, containerizing, and deploying it to a Kubernetes cluster.
- other/bpmn — Provides a Camunda‑based BPMN workflow for end‑to‑end order fulfilment, from stock reservation and payment receipt to approval, shipping, invoicing, and error handling.
- other/docker — Builds and deploys a Java back‑office service as a Docker image, compiling the Maven project and running the resulting JAR in an Alpine JRE container.
- other/facelets — A JSF‑based checkout page that gathers shipping details, lets the user pick a shipping method, shows the order total, and submits the order.
- other/groovy — A lightweight shipping‑cost engine for DHL parcels, with unit tests verifying free‑shipping thresholds, tiered rates, and overweight rejection.
- other/integration — Orchestrates order intake, validation, and routing to ERP or manual review, with robust error handling and audit logging.
- other/jee — A Jakarta EE back‑office module that wires EJB services, web servlets, filters, listeners, and security for a shop application.
- other/jsp — A JSP page that lists open orders, lets users filter by customer email and status, and provides a cancel link for each order.
- other/k8s — Deploys and exposes the orders‑backoffice microservice in a Kubernetes cluster, handling database connectivity and health checks.
- other/kotlin — Generates and stores invoices for completed orders, applying tax rules and payment terms.
- other/mybatis — Provides MyBatis SQL mappings for customer persistence, including CRUD operations, result mapping, and dynamic queries for the `shop.customer.CustomerMapper` namespace.
- other/openapi — A back‑office API for managing shop orders, providing listing, retrieval, and cancellation endpoints with status filtering and security.
- other — A full‑stack shop platform that builds, tests, containerises, and deploys a Java back‑office service, orchestrates order fulfilment via BPMN, Camel, and SOAP, and exposes REST and SOAP APIs for order and stock management while persisting data with Hibernate/MyBatis and generating invoices in Kotlin.
- other/persistence — Provides Hibernate‑based persistence configuration for a shop’s shipping domain and reporting units, mapping entities, queries, and transaction settings.
- other/wsdl — Provides a SOAP web service for querying stock availability by SKU, exposing a single operation and fault handling.
- . — A back‑office order management system that exposes REST and SOAP APIs, orchestrates fulfillment via BPMN and Camel, and automates CI/CD to Kubernetes.
