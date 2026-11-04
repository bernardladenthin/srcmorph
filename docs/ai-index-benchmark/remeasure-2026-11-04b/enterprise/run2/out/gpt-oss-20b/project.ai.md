### srcmorph-modelcard
- H: 1.0
- C: E7238B94
- D: 2026-11-04T06:55:35Z
- T: 2026-11-04T12:37:23Z
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
The back‑office system orchestrates order fulfilment through a BPMN workflow that reserves stock, waits for payment, handles large‑order approval, ships goods, and generates invoices. RESTful APIs (OpenAPI) and a SOAP service expose order and stock operations, while Jakarta EE components wire business beans, JMS listeners, and security into the shop application. Persistence is handled via Hibernate and MyBatis, with separate JPA units for application and reporting data, and shipping costs are calculated by a Groovy script. Continuous delivery pipelines build, containerise, and deploy the `orders-backoffice` microservice to Kubernetes, exposing it through an Ingress.

#### Packages
- java — A back‑office order management service that exposes REST APIs for querying, canceling, and auditing orders, with nightly cleanup of cancelled records.
- java/shop — Provides REST endpoints for back‑office order management, including listing, retrieving, canceling orders, and a scheduled purge of old cancellations.
- other/actions/.github — Automates the continuous delivery of the back‑office Java service from source to a Kubernetes cluster.
- other/actions/.github/workflows — Automates the build, containerization, and Kubernetes deployment of the back‑office service.
- other/actions — Automates the continuous delivery of the back‑office Java service from source to a Kubernetes cluster.
- other/bpmn — A BPMN‑based order‑fulfilment workflow that reserves stock, waits for payment, handles large‑order approval, ships goods, and creates invoices.
- other/docker — Builds and runs the `orders-backoffice` Java application in a Docker container for the shop backend.
- other/facelets — A Jakarta Faces checkout page that gathers shipping details, selects a shipping method, shows the total, and submits an order.
- other/groovy — Calculates shipping cost for orders, applying weight tiers, free‑shipping thresholds, and rejecting overweight parcels.
- other/integration — Provides Camel‑based integration for order ingestion, validation, audit, and ERP submission with retry and dead‑letter handling.
- other/jee — Provides Jakarta EE EJB and web configuration for a shop application, wiring business beans, JMS listeners, servlets, filters, and security.
- other/jsp — Provides a JSP view for listing, searching, and canceling open orders in a shop application.
- other/k8s — Deploys and exposes the orders‑backoffice microservice in a Kubernetes cluster, providing external HTTP access via an Ingress.
- other/kotlin — Generates and stores invoices for completed orders, applying tax rules and payment terms.
- other/mybatis — Provides SQL mapping for customer data persistence and querying in a shop application.
- other/openapi — Provides a RESTful API for managing shop orders, including listing, retrieving, and canceling orders with status filtering and security controls.
- other — A shop back‑office system that builds, tests, containerises, and deploys a Java order‑fulfilment service, exposes REST and SOAP APIs, orchestrates BPMN workflows, integrates with ERP, and manages persistence, shipping, invoicing, and reporting.
- other/persistence — Provides Hibernate persistence configuration for a shop application, mapping the `Shipment` entity hierarchy and defining two JPA persistence units for application and reporting data.
- other/wsdl — Provides a SOAP web service for querying stock availability by SKU, including fault handling for unknown SKUs.
- . — A comprehensive back‑office system that manages orders, stock, shipping, invoicing, and reporting, exposing REST and SOAP APIs, orchestrating BPMN workflows, and delivering the service via CI/CD to Kubernetes.
