### other/jee
- H: 1.0
- C: 90176FB5
- D: 2026-11-04T06:47:44Z
- T: 2026-11-04T12:39:53Z
- G: 
- A: 0.0.0
- X: package
- F: [ejb-jar.xml](ejb-jar.xml.ai.md)
- F: [web.xml](web.xml.ai.md)
---
> Configures the shop back-office and commerce services for report access, stock reservation, checkout, and order-event handling.

#### Purpose
- Defines Jakarta EE deployment settings for shop web components and EJB services.
- Configures web access control, resource references, request processing, and transactional stock reservation.

#### Responsibilities
- **Commerce services:** Declares stock reservation and checkout session beans, plus an order-placed message-driven bean and its JMS destination.
- **Back-office web application:** Configures report downloads, request ID filtering, startup auditing, session lifetime, and the report directory.
- **Operations and access:** Applies auditing, transaction behavior, admin-role protection, and a data-source reference.

#### Key units
- `StockReservationBean` — stateless bean with a 45-minute reservation setting; `reserve` uses `RequiresNew`.
- `CheckoutSessionBean` — stateful checkout bean with a local reference to `StockReservation`.
- `OrderPlacedListener` — message-driven bean activated for `java:/jms/queue/OrderPlaced`.
- `TimingInterceptor` — interceptor bound to all beans.
- `ReportDownloadServlet` — report endpoint mapped to `/reports/*`, loaded at startup.
- `RequestIdFilter` — filter mapped to all web requests (`/*`).
- `StartupAuditListener` — web application startup listener.
- `jdbc/ShopDS` — web application resource reference of type `javax.sql.DataSource`.

#### Data flow
- Web requests pass through `RequestIdFilter`; report requests under `/reports/*` are handled by `ReportDownloadServlet`, configured with `/var/shop/reports`.
- Checkout can reference stock reservation through `shop.stock.StockReservation`; the reservation operation runs with `RequiresNew`.
- Order-placed messages are received by `OrderPlacedListener` from the configured JMS destination.

#### Dependencies
- Application classes and interface: `shop.stock.StockReservationBean`, `shop.stock.StockReservation`, `shop.checkout.CheckoutSessionBean`, `shop.events.OrderPlacedListener`, `shop.audit.TimingInterceptor`, `shop.web.ReportDownloadServlet`, `shop.web.RequestIdFilter`, `shop.web.StartupAuditListener`.
- Container resources: `java:/jms/queue/OrderPlaced` and `jdbc/ShopDS`.
- Jakarta EE EJB and web application descriptors.

#### Cross-cutting
- `TimingInterceptor` applies to all EJBs.
- Web security protects `/admin/*` for role `admin`; the web session timeout is 20 minutes.
- EJB configuration sets `reservationMinutes` to `45`; report configuration sets `shop.reportDirectory` to `/var/shop/reports`.
