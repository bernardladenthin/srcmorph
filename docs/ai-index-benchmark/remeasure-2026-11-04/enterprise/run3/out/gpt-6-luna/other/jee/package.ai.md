### other/jee
- H: 1.0
- C: 90176FB5
- D: 2026-11-04T06:47:44Z
- T: 2026-11-04T08:21:46Z
- G: 
- A: 0.0.0
- X: package
- F: [ejb-jar.xml](ejb-jar.xml.ai.md)
- F: [web.xml](web.xml.ai.md)
---
> Configures the shop’s back-office web access and enterprise services for reporting, stock reservation, checkout, and order-placement events.

#### Purpose
- Declares Jakarta EE web and EJB components, their runtime settings, and container policies.
- Defines admin access controls and the shop data-source reference.

#### Responsibilities
- **Back-office web application:** maps report downloads, applies a request filter, registers a startup listener, sets session policy, and protects admin URLs.
- **Shop services and events:** declares stock reservation and checkout session beans, an order-placement message listener, and timing interception.
- **Container configuration:** sets the reservation duration, JMS destination, transaction behavior, report directory, and data-source reference.

#### Key units
- `ReportDownloadServlet` — serves requests mapped to `/reports/*`.
- `RequestIdFilter` — applies to all web request paths (`/*`).
- `StartupAuditListener` — registered as a web application listener.
- `StockReservationBean` — stateless session bean configured with a 45-minute `reservationMinutes` environment entry.
- `CheckoutSessionBean` — stateful session bean with a local reference to `shop.stock.StockReservation`.
- `OrderPlacedListener` — message-driven bean listening at `java:/jms/queue/OrderPlaced`.
- `TimingInterceptor` — interceptor bound to all EJBs.
- `jdbc/ShopDS` — web application resource reference of type `javax.sql.DataSource`.

#### Data flow
- Web requests pass through `RequestIdFilter`; report requests are routed to `ReportDownloadServlet`.
- Checkout has a local EJB reference to stock reservation; `StockReservationBean.reserve` runs with `RequiresNew` transaction behavior.
- Order-placement messages are directed to `OrderPlacedListener` through the configured JMS destination.

#### Dependencies
- Application classes: `shop.web.ReportDownloadServlet`, `shop.web.RequestIdFilter`, `shop.web.StartupAuditListener`, `shop.stock.StockReservationBean`, `shop.checkout.CheckoutSessionBean`, `shop.events.OrderPlacedListener`, `shop.audit.TimingInterceptor`.
- EJB interface: `shop.stock.StockReservation`.
- JMS destination: `java:/jms/queue/OrderPlaced`.
- Data-source type: `javax.sql.DataSource`.

#### Cross-cutting
- Jakarta EE descriptors configure both the web application and EJB container components.
- `/admin/*` requires the `admin` role; web sessions time out after 20 minutes.
- `shop.reportDirectory` is `/var/shop/reports`; the report servlet loads on startup.
