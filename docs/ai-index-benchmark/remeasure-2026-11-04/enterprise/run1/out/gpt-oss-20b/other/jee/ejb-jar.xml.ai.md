### ejb-jar.xml
- H: 1.0
- C: 7FD305D6
- D: 2026-11-04T06:47:44Z
- T: 2026-11-04T08:10:37Z
- G: 
- A: 0.0.0
- X: file
- P: 326D0B29
- N: ED1F2692
---
**Facts (exact, whole file):** elements: 36; namespace declarations: 1; id and name attributes: 0; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0; servlets: 0; servlet mappings: 0; URL patterns: 0; filters: 0; filter mappings: 0; listeners: 0; context parameters: 0; session beans: 2; stateless session beans: 1; message-driven beans: 1; entity beans (EJB 2.x): 0; interceptors: 1; timers: 0; EJB references: 1; resource references: 0; persistence references: 0; environment entries: 1; security roles: 0; security constraints: 0; container transactions: 1; method permissions: 0; EAR modules: 0

> Configures Jakarta EE enterprise beans, their environment, transactions, and interceptors for a shop application.

#### Purpose
- Declares session and message‑driven beans for stock reservation, checkout, and order events.
- Sets environment entry for reservation timeout.
- Defines local reference for checkout bean to stock reservation.
- Configures activation for order‑placed listener.
- Applies timing interceptor globally and specifies transaction attribute for reservation.

#### Type
- Jakarta EE `ejb-jar` descriptor, root element `<ejb-jar>`.
- Namespace: `https://jakarta.ee/xml/ns/jakartaee` (prefix `xmlns`).

#### Definitions
- **Session Beans**
  - `StockReservationBean` (`shop.stock.StockReservationBean`, Stateless)
    - Env entry `reservationMinutes` (`java.lang.Integer`, `45`).
  - `CheckoutSessionBean` (`shop.checkout.CheckoutSessionBean`, Stateful)
    - Local reference `ejb/StockReservation` to `shop.stock.StockReservation`.
- **Message‑Driven Bean**
  - `OrderPlacedListener` (`shop.events.OrderPlacedListener`)
    - Activation property `destinationLookup` → `java:/jms/queue/OrderPlaced`.
- **Interceptor**
  - `shop.audit.TimingInterceptor`.
- **Assembly Descriptor**
  - Transaction: method `StockReservationBean.reserve` → `RequiresNew`.
  - Interceptor binding: applies `shop.audit.TimingInterceptor` to all EJBs (`*`).

#### Configuration
- `reservationMinutes` = `45`.
- `destinationLookup` = `java:/jms/queue/OrderPlaced`.
- Transaction attribute for `reserve` = `RequiresNew`.

#### Dependencies
- Java classes: `shop.stock.StockReservationBean`, `shop.checkout.CheckoutSessionBean`, `shop.events.OrderPlacedListener`, `shop.audit.TimingInterceptor`, `shop.stock.StockReservation`.
