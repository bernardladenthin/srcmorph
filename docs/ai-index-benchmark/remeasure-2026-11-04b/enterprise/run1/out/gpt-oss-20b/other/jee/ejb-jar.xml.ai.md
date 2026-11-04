### ejb-jar.xml
- H: 1.0
- C: 7FD305D6
- D: 2026-11-04T06:47:44Z
- T: 2026-11-04T12:26:11Z
- G: 
- A: 0.0.0
- X: file
- P: D9E787D0
- N: ED1F2692
---
**Facts (exact, whole file):** elements: 36; namespace declarations: 1; id and name attributes: 0; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0; servlets: 0; servlet mappings: 0; URL patterns: 0; filters: 0; filter mappings: 0; listeners: 0; context parameters: 0; session beans: 2; stateless session beans: 1; message-driven beans: 1; entity beans (EJB 2.x): 0; interceptors: 1; timers: 0; EJB references: 1; resource references: 0; persistence references: 0; environment entries: 1; security roles: 0; security constraints: 0; container transactions: 1; method permissions: 0; EAR modules: 0

> Configures Jakarta EE EJB components, their environment, transactions, and interceptors for a shop application.

#### Purpose
- Declares EJBs and message-driven bean for stock reservation, checkout, and order events.
- Sets environment entry, local reference, activation config, transaction attributes, and interceptor bindings.

#### Type
- Jakarta EE descriptor (`ejb-jar.xml`), root element `ejb-jar` with namespace `https://jakarta.ee/xml/ns/jakartaee` version `4.0`.

#### Definitions
- **Session Beans**
  - `StockReservationBean` (`shop.stock.StockReservationBean`), `Stateless`, env-entry `reservationMinutes` (`java.lang.Integer`, value `45`).
  - `CheckoutSessionBean` (`shop.checkout.CheckoutSessionBean`), `Stateful`, local reference `ejb/StockReservation` to `shop.stock.StockReservation`.
- **Message-Driven Bean**
  - `OrderPlacedListener` (`shop.events.OrderPlacedListener`), activation property `destinationLookup` = `java:/jms/queue/OrderPlaced`.
- **Interceptors**
  - `shop.audit.TimingInterceptor` applied globally via `<interceptor>` and `<interceptor-binding>` for all EJBs.
- **Container Transaction**
  - Method `reserve` of `StockReservationBean` uses `RequiresNew`.

#### Configuration
- `reservationMinutes` = `45`.
- `destinationLookup` = `java:/jms/queue/OrderPlaced`.
- Transaction attribute for `reserve` = `RequiresNew`.

#### Dependencies
- Java classes: `shop.stock.StockReservationBean`, `shop.checkout.CheckoutSessionBean`, `shop.events.OrderPlacedListener`, `shop.audit.TimingInterceptor`, `shop.stock.StockReservation`.
