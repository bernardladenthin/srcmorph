### other/persistence
- H: 1.0
- C: 2F8025B8
- D: 2026-11-04T06:47:45Z
- T: 2026-11-04T12:36:59Z
- G: 
- A: 0.0.0
- X: package
- F: [Shipment.hbm.xml](Shipment.hbm.xml.ai.md)
- F: [persistence.xml](persistence.xml.ai.md)
---
> Provides Hibernate persistence configuration for a shop application, mapping the `Shipment` entity hierarchy and defining two JPA persistence units for application and reporting data.

#### Purpose
- Configure Hibernate mappings for `Shipment` and its subclasses.
- Define JPA persistence units `shop` and `reporting` with data sources, entities, and Hibernate settings.

#### Responsibilities
- **Entity mapping**: `Shipment` hierarchy, relationships, and queries.
- **Persistence unit definition**: JTA and resource‑local units, data source wiring, and Hibernate properties.
- **Query provisioning**: HQL and SQL queries for undelivered shipments and carrier counts.

#### Key units
- **`Shipment` entity** – maps to `SHIPMENT` table, includes `trackingCode`, `carrier`, `shippedAt`, `deliveredAt`, `order` FK, and a one‑to‑many `parcels` set.
- **`ExpressShipment` subclass** – joined‑subclass in `EXPRESS_SHIPMENT` table, adds `deliveryWindow`.
- **`Shipment.undelivered` HQL query** – selects shipments with null `deliveredAt` before a cutoff.
- **`Shipment.countByCarrier` SQL query** – aggregates shipment counts per carrier.
- **`persistence.xml`** – defines `shop` (JTA, Hibernate provider, `ShopDS`) and `reporting` (resource‑local, `ReportingDS`) units, lists entity classes, and sets Hibernate properties.

#### Data flow
1. **Application startup** loads `persistence.xml`, creating two `EntityManagerFactory` instances.
2. **Hibernate** reads `Shipment.hbm.xml` to map `Shipment` and `ExpressShipment` to tables, establishing relationships and cascade rules.
3. **Queries** (`Shipment.undelivered`, `Shipment.countByCarrier`) are registered and can be invoked via `EntityManager` or `Session`.

#### Dependencies
- **Hibernate** (`org.hibernate.jpa.HibernatePersistenceProvider`, `hibernate-dialect` PostgreSQL).
- **JPA** (`jakarta.persistence`).
- **Database**: PostgreSQL via `java:/jdbc/ShopDS` and `java:/jdbc/ReportingDS`.
- **Entities**: `shop.Order`, `shop.OrderLine`, `shop.Customer`, `shop.reporting.DailyRevenue`, `Parcel`.
- **Mapping file**: `META-INF/order-queries.xml`.

#### Cross-cutting
- **Sequence generation**: `SHIPMENT_SEQ` for `Shipment.id`.
- **Cascade strategy**: `all-delete-orphan` on `parcels` set.
- **Query parameters**: `:cutoff` for undelivered shipments.
- **Batching**: `hibernate.jdbc.batch_size=50` for performance.
- **Exception handling**: standard JPA/Hibernate transaction and persistence exceptions.
