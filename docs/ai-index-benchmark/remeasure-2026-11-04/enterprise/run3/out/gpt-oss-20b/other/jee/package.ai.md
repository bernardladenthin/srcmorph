### other/jee
- H: 1.0
- C: 90176FB5
- D: 2026-11-04T06:47:44Z
- T: 2026-11-04T08:24:43Z
- G: 
- A: 0.0.0
- X: package
- F: [ejb-jar.xml](ejb-jar.xml.ai.md)
- F: [web.xml](web.xml.ai.md)
---
> Provides Jakarta EE configuration for a shop back‑office application, wiring enterprise beans, web components, security, and data‑source resources.

#### Purpose
- Declares enterprise beans, web servlets, filters, listeners, and security constraints for the shop back‑office.

#### Responsibilities
- **EJB deployment**: session beans, message‑driven beans, interceptors, and transaction settings.  
- **Web configuration**: servlet, filter, listener, session timeout, and JDBC resource reference.  
- **Security**: role‑based access control for admin URLs.

#### Key units
- `StockReservationBean` (`shop.stock.StockReservationBean`) – stateless, reserves stock, uses `RequiresNew` transaction.  
- `CheckoutSessionBean` (`shop.checkout.CheckoutSessionBean`) – stateful, references `StockReservation`.  
- `OrderPlacedListener` (`shop.events.OrderPlacedListener`) – listens on `java:/jms/queue/OrderPlaced`.  
- `TimingInterceptor` (`shop.audit.TimingInterceptor`) – global timing interceptor.  
- `ReportDownloadServlet` (`shop.web.ReportDownloadServlet`) – handles `/reports/*`.  
- `RequestIdFilter` (`shop.web.RequestIdFilter`) – applies to all requests.  
- `StartupAuditListener` (`shop.web.StartupAuditListener`) – application‑startup listener.  
- `jdbc/ShopDS` – JDBC DataSource resource reference.

#### Data flow
1. **Order placement** → `OrderPlacedListener` receives JMS message.  
2. Listener triggers business logic that may call `StockReservationBean.reserve`.  
3. `CheckoutSessionBean` coordinates checkout, invoking `StockReservation`.  
4. Web layer: `ReportDownloadServlet` serves report files from `/var/shop/reports`.  
5. `RequestIdFilter` injects request identifiers into all requests.  
6. `StartupAuditListener` performs audit actions on application startup.

#### Dependencies
- **Internal**: `shop.stock.StockReservationBean`, `shop.checkout.CheckoutSessionBean`, `shop.events.OrderPlacedListener`, `shop.audit.TimingInterceptor`, `shop.web.ReportDownloadServlet`, `shop.web.RequestIdFilter`, `shop.web.StartupAuditListener`.  
- **External**: Jakarta EE container (EJB, servlet, JMS), JDBC DataSource `jdbc/ShopDS`.

#### Cross-cutting
- Global interceptor `TimingInterceptor` applied to all EJB methods.  
- Transaction attribute `RequiresNew` on `StockReservationBean.reserve`.  
- Environment entry `reservationMinutes` (45) for stock reservation timeout.  
- Security constraint restricts `/admin/*` to role `admin`.  
- Session timeout set to 20 minutes.  
- Context parameter `shop.reportDirectory` supplies report path.
