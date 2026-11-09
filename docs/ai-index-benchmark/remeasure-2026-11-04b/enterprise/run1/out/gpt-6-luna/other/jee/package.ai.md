### other/jee
- H: 1.0
- C: 90176FB5
- D: 2026-11-04T06:47:44Z
- T: 2026-11-04T12:24:42Z
- G: 
- A: 0.0.0
- X: package
- F: [ejb-jar.xml](ejb-jar.xml.ai.md)
- F: [web.xml](web.xml.ai.md)
---
> Configures the shop back-office web application and enterprise services for report access, admin security, stock reservations, checkout, and order-event handling.

#### Purpose
- Declares web and EJB runtime components and their container-managed configuration.
- Connects back-office requests and shop services to security, data-source, transaction, and JMS settings.

#### Responsibilities
- **Web application:** maps `ReportDownloadServlet` to `/reports/*`, applies `RequestIdFilter` to all requests, runs `StartupAuditListener`, and sets report-directory and session-timeout configuration.
- **Access control and resources:** protects `/admin/*` for the `admin` role and declares the `jdbc/ShopDS` data-source reference.
- **Enterprise services:** configures stateless stock reservation and stateful checkout beans, plus the order-placed message-driven listener.
- **EJB behavior:** applies `TimingInterceptor` to all EJBs and sets `StockReservationBean.reserve` to `RequiresNew`.

#### Key units
- `ReportDownloadServlet` — serves the report endpoint at `/reports/*`.
- `RequestIdFilter` — filters requests mapped to `/*`.
- `StartupAuditListener` — web-application startup listener.
- `StockReservationBean` — stateless bean with a `reservationMinutes` environment entry set to `45`.
- `CheckoutSessionBean` — stateful bean with a local reference to `shop.stock.StockReservation`.
- `OrderPlacedListener` — message-driven bean listening at `java:/jms/queue/OrderPlaced`.
- `shop.audit.TimingInterceptor` — interceptor bound to all EJBs.
- `jdbc/ShopDS` — container-managed `javax.sql.DataSource` reference.

#### Data flow
- Web requests pass through `RequestIdFilter`; report requests route to `ReportDownloadServlet`, while `/admin/*` access requires the `admin` role.
- Checkout can reference the stock reservation business interface; `StockReservationBean.reserve` runs with `RequiresNew`.
- Order-placement messages are delivered to `OrderPlacedListener` through `java:/jms/queue/OrderPlaced`.

#### Dependencies
- Application classes in `shop.web`, `shop.stock`, `shop.checkout`, `shop.events`, and `shop.audit`.
- Local business interface `shop.stock.StockReservation`.
- Container-managed resources: `jdbc/ShopDS` and JMS destination `java:/jms/queue/OrderPlaced`.

#### Cross-cutting
- Jakarta EE deployment descriptors define container-managed component wiring, security, resources, and lifecycle behavior.
- Session timeout is 20 minutes; `shop.reportDirectory` is `/var/shop/reports`.
- `TimingInterceptor` applies across EJBs; admin authorization is role-based.
