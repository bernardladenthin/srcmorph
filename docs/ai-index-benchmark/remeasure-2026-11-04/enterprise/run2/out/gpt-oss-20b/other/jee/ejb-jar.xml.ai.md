### ejb-jar.xml
- H: 1.0
- C: 7FD305D6
- D: 2026-11-04T06:47:44Z
- T: 2026-11-04T08:17:15Z
- G: 
- A: 0.0.0
- X: file
- P: 326D0B29
- N: ED1F2692
---
**Facts (exact, whole file):** elements: 36; namespace declarations: 1; id and name attributes: 0; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0; servlets: 0; servlet mappings: 0; URL patterns: 0; filters: 0; filter mappings: 0; listeners: 0; context parameters: 0; session beans: 2; stateless session beans: 1; message-driven beans: 1; entity beans (EJB 2.x): 0; interceptors: 1; timers: 0; EJB references: 1; resource references: 0; persistence references: 0; environment entries: 1; security roles: 0; security constraints: 0; container transactions: 1; method permissions: 0; EAR modules: 0

> Configures Jakarta EE EJB components, their environment, transactions, and interceptors for a shop application.

#### Purpose
- Declares EJBs: stateless, stateful, and message‑driven beans.
- Sets environment entry for reservation minutes.
- Defines local reference for `CheckoutSessionBean`.
- Configures activation for `OrderPlacedListener`.
- Applies transaction attribute and interceptor binding.

#### Type
- Jakarta EE descriptor: `ejb-jar.xml`  
- Root element: `ejb-jar`  
- Namespace: `https://jakarta.ee/xml/ns/jakartaee` (prefix `xmlns`)

#### Definitions
- **EJBs**
  - `StockReservationBean` – class `shop.stock.StockReservationBean`, stateless, env‑entry `reservationMinutes` = `45`.
  - `CheckoutSessionBean` – class `shop.checkout.CheckoutSessionBean`, stateful, local ref `ejb/StockReservation` to `shop.stock.StockReservation`.
  - `OrderPlacedListener` – class `shop.events.OrderPlacedListener`, message‑driven, activation property `destinationLookup` = `java:/jms/queue/OrderPlaced`.
- **Interceptors**
  - `shop.audit.TimingInterceptor` applied globally via `<interceptor>` and `<interceptor-binding>`.
- **Transactions**
  - Method `reserve` of `StockReservationBean` uses `RequiresNew`.

#### Configuration
- `reservationMinutes` env‑entry: `45`.
- Activation property `destinationLookup`: `java:/jms/queue/OrderPlaced`.
- Transaction attribute for `reserve`: `RequiresNew`.

#### Dependencies
- Java classes: `shop.stock.StockReservationBean`, `shop.checkout.CheckoutSessionBean`, `shop.events.OrderPlacedListener`, `shop.audit.TimingInterceptor`, `shop.stock.StockReservation`.  
- JMS queue: `java:/jms/queue/OrderPlaced`.
