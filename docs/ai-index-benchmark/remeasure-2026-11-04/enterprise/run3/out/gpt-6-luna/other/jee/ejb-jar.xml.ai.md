### ejb-jar.xml
- H: 1.0
- C: 7FD305D6
- D: 2026-11-04T06:47:44Z
- T: 2026-11-04T08:20:06Z
- G: 
- A: 0.0.0
- X: file
- P: 326D0B29
- N: ED1F2692
---
**Facts (exact, whole file):** elements: 36; namespace declarations: 1; id and name attributes: 0; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0; servlets: 0; servlet mappings: 0; URL patterns: 0; filters: 0; filter mappings: 0; listeners: 0; context parameters: 0; session beans: 2; stateless session beans: 1; message-driven beans: 1; entity beans (EJB 2.x): 0; interceptors: 1; timers: 0; EJB references: 1; resource references: 0; persistence references: 0; environment entries: 1; security roles: 0; security constraints: 0; container transactions: 1; method permissions: 0; EAR modules: 0

> Configures shop session beans, an order-placement message listener, timing interception, and transaction behavior.

#### Purpose
- Declares EJBs and container policies for stock reservation, checkout, and order-placement events.

#### Type
- Jakarta EE EJB descriptor (`ejb-jar.xml`), root `ejb-jar`; namespace `https://jakarta.ee/xml/ns/jakartaee` (default).

#### Definitions
- Session beans:
  - `StockReservationBean` — Stateless; class `shop.stock.StockReservationBean`; environment entry `reservationMinutes` (`java.lang.Integer`).
  - `CheckoutSessionBean` — Stateful; class `shop.checkout.CheckoutSessionBean`; local EJB reference `ejb/StockReservation` to `shop.stock.StockReservation`.
- Message-driven bean `OrderPlacedListener` — class `shop.events.OrderPlacedListener`; activation property `destinationLookup`.
- Interceptor `shop.audit.TimingInterceptor`, bound to all EJBs via `ejb-name` `*`.
- Container transaction: `StockReservationBean.reserve` uses `RequiresNew`.

#### Configuration
- `reservationMinutes` = `45`.
- `destinationLookup` = `java:/jms/queue/OrderPlaced`.

#### Dependencies
- Classes: `shop.stock.StockReservationBean`, `shop.checkout.CheckoutSessionBean`, `shop.events.OrderPlacedListener`, `shop.audit.TimingInterceptor`.
- Local EJB interface: `shop.stock.StockReservation`.
- JMS destination: `java:/jms/queue/OrderPlaced`.
