### web.xml
- H: 1.0
- C: 8EA108C7
- D: 2026-11-04T06:47:44Z
- T: 2026-11-04T12:32:55Z
- G: 
- A: 0.0.0
- X: file
- P: D9E787D0
- N: ED1F2692
---
**Facts (exact, whole file):** elements: 33; namespace declarations: 1; id and name attributes: 0; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0; servlets: 1; servlet mappings: 1; URL patterns: 3; filters: 1; filter mappings: 1; listeners: 1; context parameters: 1; session beans: 0; stateless session beans: 0; message-driven beans: 0; entity beans (EJB 2.x): 0; interceptors: 0; timers: 0; EJB references: 0; resource references: 1; persistence references: 0; environment entries: 0; security roles: 1; security constraints: 1; container transactions: 0; method permissions: 0; EAR modules: 0

> Configures servlet mappings, filters, listeners, session timeout, security constraints, and a JDBC resource reference for a Jakarta EE web application.

#### Purpose
- Defines servlet, filter, listener, and resource configurations for the web module.
- Sets session timeout and security roles for protected resources.

#### Type
- Jakarta EE web descriptor (`web.xml`), root element `web-app` with namespace `https://jakarta.ee/xml/ns/jakartaee` version `6.0`.

#### Definitions
- **Servlets**
  - `ReportDownloadServlet` → class `shop.web.ReportDownloadServlet`, load‑on‑startup `1`.
- **Servlet‑mappings**
  - `ReportDownloadServlet` → URL pattern `/reports/*`.
- **Filters**
  - `RequestIdFilter` → class `shop.web.RequestIdFilter`.
- **Filter‑mappings**
  - `RequestIdFilter` → URL pattern `/*`.
- **Listeners**
  - `shop.web.StartupAuditListener`.
- **Session Config**
  - Timeout `20` minutes.
- **Security Constraints**
  - Resource collection `backoffice` → URL pattern `/admin/*`.
  - Auth constraint role `admin`.
- **Security Roles**
  - `admin`.
- **Resource References**
  - Name `jdbc/ShopDS`, type `javax.sql.DataSource`.

#### Configuration
- Context param `shop.reportDirectory` = `/var/shop/reports`.

#### Dependencies
- Servlet class `shop.web.ReportDownloadServlet`.
- Filter class `shop.web.RequestIdFilter`.
- Listener class `shop.web.StartupAuditListener`.
- JDBC resource `jdbc/ShopDS`.
