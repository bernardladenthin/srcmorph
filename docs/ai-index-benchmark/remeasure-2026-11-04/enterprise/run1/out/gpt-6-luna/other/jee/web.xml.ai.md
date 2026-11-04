### web.xml
- H: 1.0
- C: 8EA108C7
- D: 2026-11-04T06:47:44Z
- T: 2026-11-04T08:07:30Z
- G: 
- A: 0.0.0
- X: file
- P: 326D0B29
- N: ED1F2692
---
**Facts (exact, whole file):** elements: 33; namespace declarations: 1; id and name attributes: 0; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0; servlets: 1; servlet mappings: 1; URL patterns: 3; filters: 1; filter mappings: 1; listeners: 1; context parameters: 1; session beans: 0; stateless session beans: 0; message-driven beans: 0; entity beans (EJB 2.x): 0; interceptors: 0; timers: 0; EJB references: 0; resource references: 1; persistence references: 0; environment entries: 0; security roles: 1; security constraints: 1; container transactions: 0; method permissions: 0; EAR modules: 0

> Configures the shop back-office web application’s report endpoint, request filtering, startup audit, session lifetime, admin access control, and database resource reference.

#### Purpose
- Declares web application settings and components for `shop-backoffice`.
- Protects admin URLs and configures a JDBC data source reference.

#### Type
- Jakarta EE web application descriptor; root element `web-app`, version `6.0`.
- Namespace: default `https://jakarta.ee/xml/ns/jakartaee`.

#### Definitions
- Display name: `shop-backoffice`.
- Servlet: `ReportDownloadServlet` → `shop.web.ReportDownloadServlet`; URL pattern `/reports/*`.
- Filter: `RequestIdFilter` → `shop.web.RequestIdFilter`; URL pattern `/*`.
- Listener: `shop.web.StartupAuditListener`.
- Session timeout: `20` minutes.
- Security constraint `backoffice` protects `/admin/*`; required role: `admin`.
- Security role: `admin`.
- Resource reference: `jdbc/ShopDS`, type `javax.sql.DataSource`.

#### Configuration
- Context parameter `shop.reportDirectory` = `/var/shop/reports`.
- `ReportDownloadServlet` load-on-startup value: `1`.
- Session timeout: `20` minutes.

#### Dependencies
- Servlet class: `shop.web.ReportDownloadServlet`.
- Filter class: `shop.web.RequestIdFilter`.
- Listener class: `shop.web.StartupAuditListener`.
- Resource type: `javax.sql.DataSource`.
