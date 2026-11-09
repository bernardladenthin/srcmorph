### other/jee
- H: 1.0
- C: 90176FB5
- D: 2026-11-04T06:47:44Z
- T: 2026-11-04T12:36:38Z
- G: 
- A: 0.0.0
- X: package
- F: [ejb-jar.xml](ejb-jar.xml.ai.md)
- F: [web.xml](web.xml.ai.md)
---
> Provides Jakarta EE EJB and web configuration for a shop application, wiring business beans, JMS listeners, servlets, filters, and security.

#### Purpose
- Deploys EJB components (stateless, stateful, message‑driven) with transaction and interceptor settings.
- Configures web module with servlet, filter, listener, session timeout, security, and JDBC resource.

#### Responsibilities
- **EJB Deployment** – declares `StockReservationBean`, `CheckoutSessionBean`, and `OrderPlacedListener`; sets environment entries, activation config, and container transactions.
- **Interception** – applies `shop.audit.TimingInterceptor` to all EJBs.
- **Web Configuration** – maps `ReportDownloadServlet` to `/reports/*`, applies `RequestIdFilter` globally, registers `StartupAuditListener`, and secures `/admin/*` for role `admin`.
- **Resource Management** – references JDBC data source `jdbc/ShopDS` and sets session timeout to 20 min.

#### Key units
- `shop.stock.StockReservationBean` – stateless EJB, method `reserve` runs in `RequiresNew` transaction.
- `shop.checkout.CheckoutSessionBean` – stateful EJB, local reference to `StockReservation`.
- `shop.events.OrderPlacedListener` – message‑driven bean listening on `java:/jms/queue/OrderPlaced`.
- `shop.audit.TimingInterceptor` – interceptor applied to all EJBs.
- `shop.web.ReportDownloadServlet` – servlet handling `/reports/*`.
- `shop.web.RequestIdFilter` – filter applied to all requests.
- `shop.web.StartupAuditListener` – listener for application startup.
- `jdbc/ShopDS` – JDBC data source referenced by the web module.

#### Data flow
1. **Order Placement** → JMS queue `OrderPlaced` → `OrderPlacedListener` processes event.
2. **Stock Reservation** → `StockReservationBean.reserve` invoked by `CheckoutSessionBean` during checkout.
3. **Report Download** → client requests `/reports/*` → `ReportDownloadServlet` serves files from `/var/shop/reports`.
4. **Request Handling** → `RequestIdFilter` injects request ID into each request before servlet processing.
5. **Application Startup** → `StartupAuditListener` performs audit initialization.

#### Dependencies
- **Internal**: EJB classes (`shop.stock.*`, `shop.checkout.*`, `shop.events.*`), interceptor (`shop.audit.*`), web classes (`shop.web.*`).
- **External**: JMS destination `java:/jms/queue/OrderPlaced`, JDBC data source `jdbc/ShopDS`, Jakarta EE container (EJB, web, security).

#### Cross-cutting
- **Interceptors**: `TimingInterceptor` applied universally to EJBs via `<interceptor-binding>`.
- **Transactions**: `StockReservationBean.reserve` uses `RequiresNew`; other methods inherit container defaults.
- **Security**: `/admin/*` protected by role `admin`; role declared in `<security-role>`.
- **Configuration**: Context parameter `shop.reportDirectory` and session timeout set in `web.xml`; environment entry `reservationMinutes` set in `ejb-jar.xml`.
