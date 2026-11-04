### web.xml
- H: 1.0
- C: 8EA108C7
- D: 2026-11-04T06:47:44Z
- T: 2026-11-04T12:26:13Z
- G: 
- A: 0.0.0
- X: file
- P: D9E787D0
- N: ED1F2692
---
**Facts (exact, whole file):** elements: 33; namespace declarations: 1; id and name attributes: 0; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0; servlets: 1; servlet mappings: 1; URL patterns: 3; filters: 1; filter mappings: 1; listeners: 1; context parameters: 1; session beans: 0; stateless session beans: 0; message-driven beans: 0; entity beans (EJB 2.x): 0; interceptors: 0; timers: 0; EJB references: 0; resource references: 1; persistence references: 0; environment entries: 0; security roles: 1; security constraints: 1; container transactions: 0; method permissions: 0; EAR modules: 0

> Configures servlet mappings, filters, listeners, security constraints, and resource references for the shop‑backoffice web application.

#### Purpose
- Defines servlet, filter, listener, and security configuration for a Jakarta EE web module.

#### Type
- Jakarta EE web descriptor (`web.xml`)  
  Root element: `web-app`  
  Namespace: `https://jakarta.ee/xml/ns/jakartaee` (prefix `jakartaee` implied)

#### Definitions
- **Context parameter**  
  - `shop.reportDirectory` → `/var/shop/reports`
- **Servlet**  
  - `ReportDownloadServlet` (`shop.web.ReportDownloadServlet`) – load‑on‑startup `1`  
  - (LegacyExportServlet commented out)
- **Servlet mapping**  
  - `ReportDownloadServlet` → `/reports/*`
- **Filter**  
  - `RequestIdFilter` (`shop.web.RequestIdFilter`)  
- **Filter mapping**  
  - `RequestIdFilter` → `/*`
- **Listener**  
  - `shop.web.StartupAuditListener`
- **Session config**  
  - Timeout: `20` minutes
- **Security constraint**  
  - Resource collection `backoffice` → `/admin/*`  
  - Auth constraint: role `admin`
- **Security role**  
  - `admin`
- **Resource reference**  
  - Name: `jdbc/ShopDS`  
  - Type: `javax.sql.DataSource`

#### Configuration
- `shop.reportDirectory` = `/var/shop/reports`
- `load-on-startup` for `ReportDownloadServlet` = `1`
- Session timeout = `20`
- URL patterns: `/reports/*`, `/*`, `/admin/*`

#### Dependencies
- Servlet class: `shop.web.ReportDownloadServlet`
- Filter class: `shop.web.RequestIdFilter`
- Listener class: `shop.web.StartupAuditListener`
- DataSource JNDI name: `jdbc/ShopDS` (type `javax.sql.DataSource`)
