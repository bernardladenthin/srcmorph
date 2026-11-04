### web.xml
- H: 1.0
- C: 8EA108C7
- D: 2026-11-04T06:47:44Z
- T: 2026-11-04T08:17:18Z
- G: 
- A: 0.0.0
- X: file
- P: 326D0B29
- N: ED1F2692
---
**Facts (exact, whole file):** elements: 33; namespace declarations: 1; id and name attributes: 0; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0; servlets: 1; servlet mappings: 1; URL patterns: 3; filters: 1; filter mappings: 1; listeners: 1; context parameters: 1; session beans: 0; stateless session beans: 0; message-driven beans: 0; entity beans (EJB 2.x): 0; interceptors: 0; timers: 0; EJB references: 0; resource references: 1; persistence references: 0; environment entries: 0; security roles: 1; security constraints: 1; container transactions: 0; method permissions: 0; EAR modules: 0

> Configures the web application “shop-backoffice”, defining servlets, filters, listeners, session timeout, security constraints, and a JDBC resource reference.

#### Purpose
- Sets up servlet, filter, listener, and security configuration for the application.

#### Type
- Jakarta EE `web.xml` (Jakarta Servlet 6.0)  
- Namespace: `https://jakarta.ee/xml/ns/jakartaee`

#### Definitions
- **Servlets**
  - `ReportDownloadServlet` → class `shop.web.ReportDownloadServlet`, load‑on‑startup `1`
- **Servlet Mappings**
  - `ReportDownloadServlet` → URL pattern `/reports/*`
- **Filters**
  - `RequestIdFilter` → class `shop.web.RequestIdFilter`
- **Filter Mappings**
  - `RequestIdFilter` → URL pattern `/*`
- **Listeners**
  - `shop.web.StartupAuditListener`
- **Session Config**
  - Timeout `20` minutes
- **Security Constraint**
  - Resource collection `backoffice` → URL pattern `/admin/*`
  - Auth constraint role `admin`
- **Security Role**
  - `admin`
- **Resource Reference**
  - Name `jdbc/ShopDS`, type `javax.sql.DataSource`

#### Configuration
- Context parameter `shop.reportDirectory` = `/var/shop/reports`

#### Dependencies
- Servlet class `shop.web.ReportDownloadServlet`
- Filter class `shop.web.RequestIdFilter`
- Listener class `shop.web.StartupAuditListener`
- JDBC resource `jdbc/ShopDS` (DataSource)
