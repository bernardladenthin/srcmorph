### web.xml
- H: 1.0
- C: 8EA108C7
- D: 2026-11-04T06:47:44Z
- T: 2026-11-04T12:23:06Z
- G: 
- A: 0.0.0
- X: file
- P: D9E787D0
- N: ED1F2692
---
**Facts (exact, whole file):** elements: 33; namespace declarations: 1; id and name attributes: 0; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0; servlets: 1; servlet mappings: 1; URL patterns: 3; filters: 1; filter mappings: 1; listeners: 1; context parameters: 1; session beans: 0; stateless session beans: 0; message-driven beans: 0; entity beans (EJB 2.x): 0; interceptors: 0; timers: 0; EJB references: 0; resource references: 1; persistence references: 0; environment entries: 0; security roles: 1; security constraints: 1; container transactions: 0; method permissions: 0; EAR modules: 0

> Configures the shop back-office web application’s report endpoint, request filtering, startup audit, session lifetime, admin access control, and data source reference.

#### Purpose
- Defines web application settings and server-managed components for `shop-backoffice`.
- Protects `/admin/*` for the `admin` role and maps report downloads to `/reports/*`.

#### Type
- Jakarta EE 6.0 web application descriptor; root element `web-app`.
- Declares the default namespace `https://jakarta.ee/xml/ns/jakartaee` (no prefix).

#### Definitions
- Display name: `shop-backoffice`.
- Servlet `ReportDownloadServlet`: class `shop.web.ReportDownloadServlet`; load-on-startup `1`; URL pattern `/reports/*`.
- Filter `RequestIdFilter`: class `shop.web.RequestIdFilter`; mapped to `/*`.
- Listener: `shop.web.StartupAuditListener`.
- Session timeout: `20` minutes.
- Security constraint `backoffice`: protects `/admin/*`; requires role `admin`.
- Security role: `admin`.
- Resource reference `jdbc/ShopDS`: type `javax.sql.DataSource`.

#### Configuration
- Context parameter `shop.reportDirectory`: `/var/shop/reports`.
- Session timeout: `20` minutes.
- Servlet startup order: `ReportDownloadServlet` loads at `1`.

#### Dependencies
- Servlet class: `shop.web.ReportDownloadServlet`.
- Filter class: `shop.web.RequestIdFilter`.
- Listener class: `shop.web.StartupAuditListener`.
- Container-managed data source: `jdbc/ShopDS` (`javax.sql.DataSource`).
