### other/jee
- H: 1.0
- C: 90176FB5
- D: 2026-11-04T06:47:44Z
- T: 2026-11-04T08:11:57Z
- G: 
- A: 0.0.0
- X: package
- F: [ejb-jar.xml](ejb-jar.xml.ai.md)
- F: [web.xml](web.xml.ai.md)
---
> Provides the Jakarta EE configuration for a shop application, wiring enterprise beans, web components, and security.

#### Purpose
- Declares and configures enterprise beans (session, message‑driven, interceptors) for stock reservation, checkout, and order events.
- Sets up the web layer: servlets, filters, listeners, session timeout, security constraints, and JDBC resource reference.

#### Responsibilities
- **Enterprise Bean Configuration** – session beans, message‑driven listeners, environment entries, transaction attributes, and interceptor bindings.
- **Web Deployment** – servlet, filter, listener, context parameters, session timeout, security constraints, and resource references.

#### Key units
- `StockReservationBean` (stateless, `shop.stock.StockReservationBean`) – reserves stock, uses `reservationMinutes` env entry, transaction `RequiresNew`.
- `CheckoutSessionBean` (stateful, `shop.checkout.CheckoutSessionBean`) – references `StockReservation`.
- `OrderPlacedListener` (message‑driven, `shop.events.OrderPlacedListener`) – listens on `java:/jms/queue/OrderPlaced`.
- `TimingInterceptor` (`shop.audit.TimingInterceptor`) – applied to all EJBs.
- `ReportDownloadServlet` (`shop.web.ReportDownloadServlet`) – handles `/reports/*`.
- `RequestIdFilter` (`shop.web.RequestIdFilter`) – applied to all requests.
- `StartupAuditListener` (`shop.web.StartupAuditListener`) – application startup listener.
- `shop.reportDirectory` context param – `/var/shop/reports`.
- `jdbc/ShopDS` DataSource reference.

#### Data flow
1. **Order Placement** → `OrderPlacedListener` receives JMS message → triggers business logic (not shown) that may call `StockReservationBean.reserve`.
2. **Checkout Process** → `CheckoutSessionBean` interacts with `StockReservationBean` via local reference.
3. **Web Requests** → `RequestIdFilter` assigns request IDs → `ReportDownloadServlet` serves report files from `/var/shop/reports` → audit via `StartupAuditListener`.

#### Dependencies
- Jakarta EE APIs: EJB, JMS, Servlet, Security.
- External resource: JDBC DataSource `jdbc/ShopDS`.
- Internal classes: `shop.stock.StockReservationBean`, `shop.checkout.CheckoutSessionBean`, `shop.events.OrderPlacedListener`, `shop.audit.TimingInterceptor`, `shop.web.ReportDownloadServlet`, `shop.web.RequestIdFilter`, `shop.web.StartupAuditListener`.

#### Cross-cutting
- **Timing Interceptor** applied globally to all EJBs.
- **Environment Entry** `reservationMinutes` shared by `StockReservationBean`.
- **Security**: `/admin/*` protected by role `admin`; session timeout set to 20 minutes.
- **Configuration**: context param `shop.reportDirectory` and JDBC resource reference used across web components.
