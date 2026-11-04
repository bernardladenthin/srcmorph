### ejb-jar.xml
- H: 1.0
- C: 7FD305D6
- D: 2026-11-04T06:47:44Z
- T: 2026-11-04T12:38:07Z
- G: 
- A: 0.0.0
- X: file
- P: D9E787D0
- N: ED1F2692
---
**Facts (exact, whole file):** elements: 36; namespace declarations: 1; id and name attributes: 0; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0; servlets: 0; servlet mappings: 0; URL patterns: 0; filters: 0; filter mappings: 0; listeners: 0; context parameters: 0; session beans: 2; stateless session beans: 1; message-driven beans: 1; entity beans (EJB 2.x): 0; interceptors: 1; timers: 0; EJB references: 1; resource references: 0; persistence references: 0; environment entries: 1; security roles: 0; security constraints: 0; container transactions: 1; method permissions: 0; EAR modules: 0

> Configures stock reservation, checkout, and order-placement EJBs, including reservation settings, JMS activation, auditing, and transaction behavior.

#### Purpose
- Declares session and message-driven beans for shop stock and checkout operations.
- Configures an interceptor, a local EJB reference, and a transaction attribute for stock reservations.

#### Type
- Jakarta EE EJB descriptor; root element `ejb-jar`.
- Namespace: default `https://jakarta.ee/xml/ns/jakartaee`.

#### Definitions
- Session bean `StockReservationBean`: class `shop.stock.StockReservationBean`; type `Stateless`.
  - Environment entry `reservationMinutes`: type `java.lang.Integer`, value `45`.
- Session bean `CheckoutSessionBean`: class `shop.checkout.CheckoutSessionBean`; type `Stateful`.
  - Local EJB reference `ejb/StockReservation` to `shop.stock.StockReservation`.
- Message-driven bean `OrderPlacedListener`: class `shop.events.OrderPlacedListener`.
  - Activation property `destinationLookup`: `java:/jms/queue/OrderPlaced`.
- Interceptor: `shop.audit.TimingInterceptor`; bound to all beans via `ejb-name` `*`.
- Transaction attribute: `RequiresNew` for `StockReservationBean` method `reserve`.

#### Configuration
- `reservationMinutes` = `45`.
- `destinationLookup` = `java:/jms/queue/OrderPlaced`.
- `StockReservationBean.reserve` transaction attribute = `RequiresNew`.

#### Dependencies
- Classes: `shop.stock.StockReservationBean`, `shop.checkout.CheckoutSessionBean`, `shop.events.OrderPlacedListener`, `shop.audit.TimingInterceptor`.
- Local interface: `shop.stock.StockReservation`.
- JMS destination lookup: `java:/jms/queue/OrderPlaced`.
