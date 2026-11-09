### other/jee
- H: 1.0
- C: 90176FB5
- D: 2026-11-04T06:47:44Z
- T: 2026-11-04T08:18:23Z
- G: 
- A: 0.0.0
- X: package
- F: [ejb-jar.xml](ejb-jar.xml.ai.md)
- F: [web.xml](web.xml.ai.md)
---
> A Jakarta EE back‑office module that wires EJB services, web servlets, filters, listeners, and security for a shop application.

#### Purpose
- Exposes EJB components (stateless, stateful, message‑driven) and configures their environment, transactions, and interceptors.
- Provides a servlet for report downloads, a request‑ID filter, and a startup audit listener.
- Enforces admin‑only access to `/admin/*` and configures a JDBC data source.

#### Responsibilities
- **EJB configuration** – declares `StockReservationBean`, `CheckoutSessionBean`, and `OrderPlacedListener`; sets env‑entry, activation, transaction attributes, and global interceptor binding.
- **Web layer** – defines `ReportDownloadServlet`, `RequestIdFilter`, and `StartupAuditListener`; sets session timeout, security constraints, and JDBC resource reference.
- **Security** – restricts `/admin/*` to role `admin` and declares the role.

#### Key units
- `StockReservationBean` (`shop.stock.StockReservationBean`) – stateless EJB, reserves stock, uses `reservationMinutes` env‑entry, `RequiresNew` transaction.
- `CheckoutSessionBean` (`shop.checkout.CheckoutSessionBean`) – stateful EJB, local reference to `StockReservation`.
- `OrderPlacedListener` (`shop.events.OrderPlacedListener`) – message‑driven EJB listening on `java:/jms/queue/OrderPlaced`.
- `TimingInterceptor` (`shop.audit.TimingInterceptor`) – global interceptor applied to all EJB methods.
- `ReportDownloadServlet` (`shop.web.ReportDownloadServlet`) – handles `/reports/*` requests, load‑on‑startup.
- `RequestIdFilter` (`shop.web.RequestIdFilter`) – filter applied to all URLs.
- `StartupAuditListener` (`shop.web.StartupAuditListener`) – context listener for startup audit.
- `jdbc/ShopDS` – JDBC `DataSource` referenced by the web layer.

#### Data flow
1. **Web request** → `RequestIdFilter` assigns a request ID → `ReportDownloadServlet` processes `/reports/*` → accesses `jdbc/ShopDS` for data.
2. **Order placement** → JMS queue `OrderPlaced` triggers `OrderPlacedListener` → calls `StockReservationBean.reserve()` (transaction `RequiresNew`) → `TimingInterceptor` logs timing.
3. **Checkout** → `CheckoutSessionBean` interacts with `StockReservationBean` via local reference.

#### Dependencies
- **Internal**: EJB classes (`shop.stock.*`, `shop.checkout.*`, `shop.events.*`, `shop.audit.*`), servlet/filter/listener classes (`shop.web.*`).
- **External**: JMS queue `java:/jms/queue/OrderPlaced`, JDBC `DataSource` `jdbc/ShopDS`, Jakarta EE container for EJB, Servlet, and JMS support.

#### Cross-cutting
- Global `TimingInterceptor` applied to all EJB methods.
- Environment entry `reservationMinutes` shared by `StockReservationBean`.
- Security constraint uses role `admin` for `/admin/*`.
- Session timeout set to 20 minutes; context parameter `shop.reportDirectory` points to `/var/shop/reports`.
