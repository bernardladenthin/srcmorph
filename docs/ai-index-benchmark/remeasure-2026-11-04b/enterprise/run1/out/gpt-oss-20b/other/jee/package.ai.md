### other/jee
- H: 1.0
- C: 90176FB5
- D: 2026-11-04T06:47:44Z
- T: 2026-11-04T12:27:20Z
- G: 
- A: 0.0.0
- X: package
- F: [ejb-jar.xml](ejb-jar.xml.ai.md)
- F: [web.xml](web.xml.ai.md)
---
> Provides the Jakarta EE configuration for a shop application, wiring EJB services, JMS listeners, and web components with security, transactions, and resource references.

#### Purpose
- Declares and configures EJB session beans, a message‑driven bean, and web module components for a shop system.

#### Responsibilities
- **EJB configuration** – session beans for stock reservation and checkout, a JMS listener for order events, and global timing interceptor.
- **Web configuration** – servlet for report downloads, request‑ID filter, startup audit listener, security constraints for admin area, and JDBC data source reference.

#### Key units
- `shop.stock.StockReservationBean` – stateless EJB, `reserve` method runs in a new transaction, uses `reservationMinutes` env‑entry.
- `shop.checkout.CheckoutSessionBean` – stateful EJB, references `StockReservation`.
- `shop.events.OrderPlacedListener` – message‑driven bean listening on `java:/jms/queue/OrderPlaced`.
- `shop.audit.TimingInterceptor` – interceptor applied to all EJBs.
- `shop.web.ReportDownloadServlet` – handles `/reports/*` requests, loads on startup.
- `shop.web.RequestIdFilter` – filter applied to all requests.
- `shop.web.StartupAuditListener` – context listener for startup audit.
- `jdbc/ShopDS` – JNDI data source for database access.

#### Data flow
1. **Order placement** → JMS queue `OrderPlaced` → `OrderPlacedListener` processes event.  
2. **Stock reservation** → `StockReservationBean.reserve()` invoked by `CheckoutSessionBean` during checkout.  
3. **Report download** → client requests `/reports/*` → `ReportDownloadServlet` serves files from `/var/shop/reports`.  
4. **Request handling** → `RequestIdFilter` injects request IDs into all servlet calls.  
5. **Startup** → `StartupAuditListener` performs audit initialization.

#### Dependencies
- **Internal**: EJBs reference each other (`CheckoutSessionBean` → `StockReservation`), interceptor applied globally.  
- **External**: JMS queue `java:/jms/queue/OrderPlaced`, JDBC data source `jdbc/ShopDS`, servlet container for web descriptors.

#### Cross-cutting
- Global `TimingInterceptor` for performance logging on all EJB methods.  
- Environment entry `reservationMinutes` shared by `StockReservationBean`.  
- Security constraint restricts `/admin/*` to role `admin`.  
- Session timeout set to 20 minutes for web module.
