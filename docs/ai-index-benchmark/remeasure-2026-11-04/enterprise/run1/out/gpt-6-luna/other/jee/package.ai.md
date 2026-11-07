### other/jee
- H: 1.0
- C: 90176FB5
- D: 2026-11-04T06:47:44Z
- T: 2026-11-04T08:09:01Z
- G: 
- A: 0.0.0
- X: package
- F: [ejb-jar.xml](ejb-jar.xml.ai.md)
- F: [web.xml](web.xml.ai.md)
---
> Configures the shop back office’s web access and the EJB services that reserve stock, process checkout, and receive order-placement events.

#### Purpose
- Defines Jakarta EE web application and EJB deployment settings for shop reporting, admin access, stock reservation, checkout, and order events.
- Declares component wiring, runtime configuration, and transaction behavior.

#### Responsibilities
- **Web application:** Maps report downloads, applies request-ID filtering, configures startup auditing and session lifetime, and protects admin URLs with the `admin` role.
- **EJB services and messaging:** Declares stock reservation and checkout session beans, an order-placement message listener, and a JMS queue destination.
- **Runtime and resources:** Configures the report directory, JDBC data source reference, stock reservation duration, timing interception, and reservation transaction attribute.

#### Key units
- `ReportDownloadServlet` (`shop.web.ReportDownloadServlet`) — serves requests at `/reports/*` and loads on startup.
- `RequestIdFilter` (`shop.web.RequestIdFilter`) — filters requests matching `/*`.
- `StartupAuditListener` (`shop.web.StartupAuditListener`) — declared as a web application listener.
- `StockReservationBean` (`shop.stock.StockReservationBean`) — stateless session bean with `reservationMinutes=45`; `reserve` uses `RequiresNew`.
- `CheckoutSessionBean` (`shop.checkout.CheckoutSessionBean`) — stateful session bean with a local reference to `shop.stock.StockReservation`.
- `OrderPlacedListener` (`shop.events.OrderPlacedListener`) — message-driven bean configured for `java:/jms/queue/OrderPlaced`.
- `TimingInterceptor` (`shop.audit.TimingInterceptor`) — bound to all EJBs.

#### Data flow
- Web requests pass through `RequestIdFilter`; report requests route to `ReportDownloadServlet`, with the report directory configured as `/var/shop/reports`.
- `CheckoutSessionBean` references `StockReservation`; the reservation bean’s `reserve` method runs with `RequiresNew`.
- Order-placement messages are received by `OrderPlacedListener` from the configured JMS destination.

#### Dependencies
- EJB classes and interface: `shop.stock.StockReservationBean`, `shop.stock.StockReservation`, `shop.checkout.CheckoutSessionBean`, `shop.events.OrderPlacedListener`, `shop.audit.TimingInterceptor`.
- Web classes: `shop.web.ReportDownloadServlet`, `shop.web.RequestIdFilter`, `shop.web.StartupAuditListener`.
- External resources: JMS destination `java:/jms/queue/OrderPlaced`; `javax.sql.DataSource` reference `jdbc/ShopDS`.

#### Cross-cutting
- Descriptors use Jakarta EE namespaces: EJB descriptor version `4.0` and web descriptor version `6.0`.
- EJB timing interception applies to `*`; web security requires the `admin` role for `/admin/*`.
- Web sessions time out after 20 minutes.
