### other/jee
- H: 1.0
- C: 90176FB5
- D: 2026-11-04T06:47:44Z
- T: 2026-11-04T12:42:43Z
- G: 
- A: 0.0.0
- X: package
- F: [ejb-jar.xml](ejb-jar.xml.ai.md)
- F: [web.xml](web.xml.ai.md)
---
> Provides the Jakarta EE deployment descriptors for a shop back‑office application, configuring EJBs, a message‑driven bean, a servlet, filter, listener, security, and JDBC resources.

#### Purpose
- Deploy and configure EJB components and web resources for the shop back‑office.
- Define transaction behavior, security constraints, and resource references.

#### Responsibilities
- **EJB Deployment** – session beans, message‑driven bean, interceptor, and transaction settings.
- **Web Configuration** – servlet, filter, listener, session timeout, security, and JDBC resource.

#### Key units
- `StockReservationBean` (`shop.stock.StockReservationBean`) – stateless EJB, `reserve` uses `RequiresNew`.
- `CheckoutSessionBean` (`shop.checkout.CheckoutSessionBean`) – stateful EJB, references `StockReservation`.
- `OrderPlacedListener` (`shop.events.OrderPlacedListener`) – message‑driven bean listening on `java:/jms/queue/OrderPlaced`.
- `TimingInterceptor` (`shop.audit.TimingInterceptor`) – global EJB interceptor.
- `ReportDownloadServlet` (`shop.web.ReportDownloadServlet`) – handles `/reports/*`.
- `RequestIdFilter` (`shop.web.RequestIdFilter`) – applied to all requests.
- `StartupAuditListener` (`shop.web.StartupAuditListener`) – application‑start listener.
- `jdbc/ShopDS` – JDBC `DataSource` referenced by the web app.

#### Data flow
1. **Order Placement** → JMS queue `OrderPlaced` → `OrderPlacedListener` processes event.
2. **Stock Reservation** → `CheckoutSessionBean` calls `StockReservationBean.reserve` (new transaction).
3. **Report Download** → HTTP request to `/reports/*` → `ReportDownloadServlet` serves files from `/var/shop/reports`.
4. **Request ID** → `RequestIdFilter` injects a unique ID into each request before reaching servlets or EJBs.
5. **Audit** → `StartupAuditListener` logs startup events; `TimingInterceptor` measures EJB method execution times.

#### Dependencies
- **Internal**: `shop.stock.StockReservationBean`, `shop.checkout.CheckoutSessionBean`, `shop.events.OrderPlacedListener`, `shop.audit.TimingInterceptor`, `shop.web.ReportDownloadServlet`, `shop.web.RequestIdFilter`, `shop.web.StartupAuditListener`.
- **External**: Jakarta EE container, JMS queue `java:/jms/queue/OrderPlaced`, JDBC `DataSource` `jdbc/ShopDS`, security role `admin`.

#### Cross-cutting
- Global EJB interceptor (`TimingInterceptor`) for performance logging.
- Request‑scoped filter (`RequestIdFilter`) for correlation IDs.
- Common environment entry `reservationMinutes` (45) used by `StockReservationBean`.
- Security constraint restricting `/admin/*` to role `admin`.
- Session timeout set to 20 minutes for web sessions.
