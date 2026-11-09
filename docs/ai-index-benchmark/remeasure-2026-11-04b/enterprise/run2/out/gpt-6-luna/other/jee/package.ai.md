### other/jee
- H: 1.0
- C: 90176FB5
- D: 2026-11-04T06:47:44Z
- T: 2026-11-04T12:31:12Z
- G: 
- A: 0.0.0
- X: package
- F: [ejb-jar.xml](ejb-jar.xml.ai.md)
- F: [web.xml](web.xml.ai.md)
---
> Configures the shop back-office web application and its EJB services for report access, checkout, stock reservation, and order-placement event handling.

#### Purpose
- Declares web and EJB components, their runtime settings, and access controls for the shop application.
- Connects report and admin endpoints to web infrastructure, and checkout and inventory services to EJB and JMS configuration.

#### Responsibilities
- **Web application:** Maps report downloads, applies a request filter, runs a startup listener, configures session lifetime and report directory, and protects admin URLs by role.
- **Business services and events:** Declares stateless stock reservation and stateful checkout beans, plus a message-driven listener for order-placement events.
- **Cross-cutting EJB configuration:** Binds a timing interceptor to all EJBs and configures `reserve` to use `RequiresNew` transactions.

#### Key units
- `ReportDownloadServlet` — serves requests mapped to `/reports/*`.
- `RequestIdFilter` — filters requests mapped to `/*`.
- `StartupAuditListener` — web application startup listener.
- `StockReservationBean` — stateless bean for stock reservation; `reserve` has a `RequiresNew` transaction rule.
- `CheckoutSessionBean` — stateful checkout bean with a local reference to `StockReservation`.
- `OrderPlacedListener` — message-driven bean configured for the order-placed queue.
- `TimingInterceptor` — interceptor bound to all EJBs.
- `jdbc/ShopDS` — web application resource reference to a `javax.sql.DataSource`.
- `admin` — role permitted to access `/admin/*`.

#### Data flow
- Web requests pass through `RequestIdFilter`; report requests are routed to `ReportDownloadServlet`.
- Checkout has a local EJB reference to `StockReservation`; order-placement events are handled by `OrderPlacedListener` through the configured JMS destination.

#### Dependencies
- Application classes: `shop.web.ReportDownloadServlet`, `shop.web.RequestIdFilter`, `shop.web.StartupAuditListener`, `shop.stock.StockReservationBean`, `shop.stock.StockReservation`, `shop.checkout.CheckoutSessionBean`, `shop.events.OrderPlacedListener`, `shop.audit.TimingInterceptor`.
- Runtime resources: JMS queue `java:/jms/queue/OrderPlaced` and data source `jdbc/ShopDS`.
- Web settings include `shop.reportDirectory` set to `/var/shop/reports` and a 20-minute session timeout; EJB environment entry `reservationMinutes` is `45`.

#### Cross-cutting
- Jakarta EE deployment descriptors define both web and EJB components.
- Admin access is restricted to the `admin` role; `TimingInterceptor` applies across EJBs.
