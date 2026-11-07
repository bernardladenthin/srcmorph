### ejb-jar.xml
- H: 1.0
- C: 7FD305D6
- D: 2026-11-04T06:47:44Z
- T: 2026-11-04T12:32:53Z
- G: 
- A: 0.0.0
- X: file
- P: D9E787D0
- N: ED1F2692
---
**Facts (exact, whole file):** elements: 36; namespace declarations: 1; id and name attributes: 0; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0; servlets: 0; servlet mappings: 0; URL patterns: 0; filters: 0; filter mappings: 0; listeners: 0; context parameters: 0; session beans: 2; stateless session beans: 1; message-driven beans: 1; entity beans (EJB 2.x): 0; interceptors: 1; timers: 0; EJB references: 1; resource references: 0; persistence references: 0; environment entries: 1; security roles: 0; security constraints: 0; container transactions: 1; method permissions: 0; EAR modules: 0

> Configures Jakarta EE EJB components, their environment entries, references, activation settings, and transaction/interceptor behavior.

#### Purpose
- Declares EJBs and their deployment descriptors.
- Specifies transaction attributes and interceptor bindings.

#### Type
- Jakarta EE descriptor (`ejb-jar.xml`), root element `ejb-jar` version `4.0`.
- Namespace: `https://jakarta.ee/xml/ns/jakartaee` (default).

#### Definitions
- **Enterprise Beans**
  - `session`  
    - `ejb-name`: `StockReservationBean`  
      - `ejb-class`: `shop.stock.StockReservationBean`  
      - `session-type`: `Stateless`  
      - `env-entry`: `reservationMinutes` (`java.lang.Integer`, value `45`)
    - `session`  
      - `ejb-name`: `CheckoutSessionBean`  
      - `ejb-class`: `shop.checkout.CheckoutSessionBean`  
      - `session-type`: `Stateful`  
      - `ejb-local-ref`: `ejb/StockReservation` → `shop.stock.StockReservation`
  - `message-driven`  
    - `ejb-name`: `OrderPlacedListener`  
    - `ejb-class`: `shop.events.OrderPlacedListener`  
    - `activation-config`  
      - `destinationLookup`: `java:/jms/queue/OrderPlaced`
- **Interceptors**
  - `shop.audit.TimingInterceptor`
- **Assembly Descriptor**
  - `container-transaction`  
    - `method` for `StockReservationBean.reserve` → `RequiresNew`
  - `interceptor-binding` for all EJBs (`*`) → `shop.audit.TimingInterceptor`

#### Configuration
- `env-entry-value` for `reservationMinutes`: `45`
- `activation-config-property-value` for `destinationLookup`: `java:/jms/queue/OrderPlaced`
- Transaction attribute `RequiresNew` for `StockReservationBean.reserve`

#### Dependencies
- Java classes: `shop.stock.StockReservationBean`, `shop.checkout.CheckoutSessionBean`, `shop.events.OrderPlacedListener`, `shop.audit.TimingInterceptor`, `shop.stock.StockReservation`
- JMS destination: `java:/jms/queue/OrderPlaced`
