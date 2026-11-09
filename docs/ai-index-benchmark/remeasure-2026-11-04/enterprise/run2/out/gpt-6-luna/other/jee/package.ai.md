### other/jee
- H: 1.0
- C: 90176FB5
- D: 2026-11-04T06:47:44Z
- T: 2026-11-04T08:15:35Z
- G: 
- A: 0.0.0
- X: package
- F: [ejb-jar.xml](ejb-jar.xml.ai.md)
- F: [web.xml](web.xml.ai.md)
---
> Configures the shop back office’s web access and the EJB services that reserve stock, process checkout, and handle order-placed events.

#### Purpose
- Declares Jakarta EE web and EJB runtime components and their deployment settings.
- Configures report access, admin authorization, stock reservation, checkout, and order-event handling.

#### Responsibilities
- **Web application:** Registers report downloads, request-ID filtering, and startup auditing; sets report storage, session timeout, and the `admin`-protected URL space.
- **EJB services:** Declares stock reservation and checkout session beans, plus a message-driven listener for order-placed events.
- **EJB behavior:** Binds timing interception to all EJBs and configures `StockReservationBean.reserve` with `RequiresNew` transaction behavior.

#### Key units
- `ReportDownloadServlet` (`shop.web.ReportDownloadServlet`) — serves the `/reports/*` endpoint.
- `RequestIdFilter` (`shop.web.RequestIdFilter`) — filters requests mapped to `/*`.
- `StartupAuditListener` (`shop.web.StartupAuditListener`) — registered as the web application listener.
- `StockReservationBean` (`shop.stock.StockReservationBean`) — stateless bean with `reservationMinutes=45`.
- `CheckoutSessionBean` (`shop.checkout.CheckoutSessionBean`) — stateful bean with a local reference to `StockReservation`.
- `OrderPlacedListener` (`shop.events.OrderPlacedListener`) — message-driven bean configured for the order-placed queue.
- `TimingInterceptor` (`shop.audit.TimingInterceptor`) — interceptor bound to all EJBs.

#### Data flow
- Web requests pass through `RequestIdFilter`; report requests can reach `ReportDownloadServlet`, configured with `/var/shop/reports` as the report directory.
- Checkout’s `StockReservation` local EJB reference connects `CheckoutSessionBean` to stock reservation; `reserve` runs with `RequiresNew`.
- `OrderPlacedListener` consumes events from `java:/jms/queue/OrderPlaced`.

#### Dependencies
- Jakarta EE 6.0 web descriptor and Jakarta EE EJB 4.0 descriptor.
- JMS destination `java:/jms/queue/OrderPlaced`.
- Web resource reference `jdbc/ShopDS` of type `javax.sql.DataSource`.
- EJB classes `shop.stock.StockReservation`, `shop.stock.StockReservationBean`, `shop.checkout.CheckoutSessionBean`, `shop.events.OrderPlacedListener`, and `shop.audit.TimingInterceptor`.

#### Cross-cutting
- `TimingInterceptor` is bound to every EJB.
- `/admin/*` requires the `admin` role; web sessions time out after 20 minutes.
- `StockReservationBean.reserve` uses `RequiresNew`; the servlet is configured to load on startup.
