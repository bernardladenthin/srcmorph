### other/persistence
- H: 1.0
- C: 2F8025B8
- D: 2026-11-04T06:47:45Z
- T: 2026-11-04T12:27:50Z
- G: 
- A: 0.0.0
- X: package
- F: [Shipment.hbm.xml](Shipment.hbm.xml.ai.md)
- F: [persistence.xml](persistence.xml.ai.md)
---
> Provides Hibernate‑based persistence for the Shop application, mapping shipment entities, configuring JPA units, and supplying queries for shipment status and carrier statistics.

#### Purpose
- Persist shipment and order data using Hibernate.
- Expose JPA persistence units for application and reporting layers.

#### Responsibilities
- **Entity mapping** – `Shipment`, `ExpressShipment`, and related associations to `Order` and `Parcel`.
- **Query provision** – HQL for undelivered shipments and SQL for carrier counts.
- **JPA configuration** – Two persistence units (`shop`, `reporting`) with data sources, entity classes, and Hibernate settings.

#### Key units
- **`Shipment` entity** – maps to `SHIPMENT` table, includes `trackingCode`, `carrier`, `shippedAt`, `deliveredAt`, and a many‑to‑one to `shop.Order`.
- **`ExpressShipment` subclass** – joined‑table strategy, adds `deliveryWindow`.
- **`Shipment.undelivered` HQL query** – selects shipments with null `deliveredAt` before a cutoff.
- **`Shipment.countByCarrier` SQL query** – aggregates shipment counts per carrier.
- **`persistence.xml`** – defines `shop` (JTA, Hibernate provider, `ShopDS`) and `reporting` (RESOURCE_LOCAL, `ReportingDS`) units, lists entity classes, and sets Hibernate properties.

#### Data flow
1. **Persisting**: `Shipment` objects are saved via the `shop` persistence unit; `ExpressShipment` instances use joined inheritance.
2. **Retrieval**:  
   - `Shipment.undelivered` is executed to fetch pending shipments.  
   - `Shipment.countByCarrier` aggregates carrier statistics for reporting.
3. **Reporting**: `reporting` unit accesses `shop.reporting.DailyRevenue` and other reporting entities using `ReportingDS`.

#### Dependencies
- **Domain entities**: `shop.Order`, `shop.OrderLine`, `shop.Customer`, `shop.reporting.DailyRevenue`.
- **Hibernate**: `org.hibernate.jpa.HibernatePersistenceProvider`, PostgreSQL dialect.
- **Data sources**: `java:/jdbc/ShopDS` (JTA), `java:/jdbc/ReportingDS` (RESOURCE_LOCAL).
- **Database objects**: tables `SHIPMENT`, `EXPRESS_SHIPMENT`; sequence `SHIPMENT_SEQ`.

#### Cross-cutting
- **Inheritance strategy**: joined subclass mapping for `ExpressShipment`.
- **Cascade rules**: `all-delete-orphan` on `Shipment.parcels`.
- **Configuration properties**: batch size, dialect, connection credentials, and mapping file inclusion (`META-INF/order-queries.xml`).
- **Exception handling**: relies on Hibernate/JPA standard transaction and persistence exceptions.
