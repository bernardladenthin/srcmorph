### ejb-jar.xml
- H: 1.0
- C: 7FD305D6
- D: 2026-11-04T06:47:44Z
- T: 2026-11-04T08:13:54Z
- G: 
- A: 0.0.0
- X: file
- P: 326D0B29
- N: ED1F2692
---
**Facts (exact, whole file):** elements: 36; namespace declarations: 1; id and name attributes: 0; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0; servlets: 0; servlet mappings: 0; URL patterns: 0; filters: 0; filter mappings: 0; listeners: 0; context parameters: 0; session beans: 2; stateless session beans: 1; message-driven beans: 1; entity beans (EJB 2.x): 0; interceptors: 1; timers: 0; EJB references: 1; resource references: 0; persistence references: 0; environment entries: 1; security roles: 0; security constraints: 0; container transactions: 1; method permissions: 0; EAR modules: 0

> Configures stock reservation, checkout, and order-event EJBs, including their local reference, timing interceptor, and transaction behavior.

#### Purpose
- Declares session and message-driven beans for stock reservation, checkout, and order-placed event handling.
- Configures an environment entry, local EJB reference, interceptor binding, and transaction attribute.

#### Type
- Jakarta EE EJB descriptor (`ejb-jar`), version `4.0`.
- Declares the default namespace `https://jakarta.ee/xml/ns/jakartaee` (no prefix).

#### Definitions
- Session beans:
  - `StockReservationBean` — class `shop.stock.StockReservationBean`; type `Stateless`; environment entry `reservationMinutes`, type `java.lang.Integer`.
  - `CheckoutSessionBean` — class `shop.checkout.CheckoutSessionBean`; type `Stateful`; local EJB reference `ejb/StockReservation` to `shop.stock.StockReservation`.
- Message-driven bean:
  - `OrderPlacedListener` — class `shop.events.OrderPlacedListener`; activation property `destinationLookup`.
- Interceptor: `shop.audit.TimingInterceptor`; bound to all EJBs (`*`).
- Transaction attribute: `RequiresNew` for `StockReservationBean.reserve`.

#### Configuration
- `reservationMinutes=45`.
- `destinationLookup=java:/jms/queue/OrderPlaced`.
- `StockReservationBean.reserve` transaction attribute: `RequiresNew`.

#### Dependencies
- Classes: `shop.stock.StockReservationBean`, `shop.stock.StockReservation`, `shop.checkout.CheckoutSessionBean`, `shop.events.OrderPlacedListener`, `shop.audit.TimingInterceptor`.
- JMS destination: `java:/jms/queue/OrderPlaced`.
