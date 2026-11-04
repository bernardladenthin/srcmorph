### other/persistence
- H: 1.0
- C: 2F8025B8
- D: 2026-11-04T06:47:45Z
- T: 2026-11-04T08:18:44Z
- G: 
- A: 0.0.0
- X: package
- F: [Shipment.hbm.xml](Shipment.hbm.xml.ai.md)
- F: [persistence.xml](persistence.xml.ai.md)
---
> Provides Hibernate‑based persistence configuration for a shop’s shipping domain and reporting units, mapping entities, queries, and transaction settings.

#### Purpose
- Configure Hibernate mappings for `Shipment` and its subclasses.
- Define two Jakarta Persistence units (`shop` and `reporting`) with data sources, entities, and Hibernate properties.

#### Responsibilities
- **Entity mapping** – `Shipment`, `ExpressShipment`, and related associations to `Order` and `Parcel`.
- **Query definition** – HQL and native SQL queries for shipment status and carrier statistics.
- **Persistence unit setup** – JTA and RESOURCE_LOCAL units, data source binding, and provider selection.
- **Hibernate configuration** – dialect, batch size, credentials, and mapping file inclusion.

#### Key units
- **`Shipment` entity** – maps to `SHIPMENT` table, includes `trackingCode`, `carrier`, `shippedAt`, `deliveredAt`, and relationships to `Order` and `Parcel`.
- **`ExpressShipment` subclass** – joined‑subclass in `EXPRESS_SHIPMENT`, adds `deliveryWindow`.
- **`Shipment.undelivered` HQL query** – selects undelivered shipments before a cutoff.
- **`Shipment.countByCarrier` SQL query** – aggregates shipment counts per carrier.
- **Persistence unit `shop`** – JTA, provider `org.hibernate.jpa.HibernatePersistenceProvider`, data source `java:/jdbc/ShopDS`, entities `shop.Order`, `shop.OrderLine`, `shop.Customer`, and mapping file `META-INF/order-queries.xml`.
- **Persistence unit `reporting`** – RESOURCE_LOCAL, data source `java:/jdbc/ReportingDS`, entity `shop.reporting.DailyRevenue`.

#### Data flow
1. **Application** requests a persistence unit (`shop` or `reporting`) via `EntityManagerFactory`.
2. **Hibernate** loads the corresponding `persistence.xml`, applies the mapping file (`Shipment.hbm.xml`) for `shop`, and registers entities.
3. **EntityManager** performs CRUD or query operations; HQL/SQL queries defined in the mapping file are executed against the configured data source.
4. **Results** are returned to the application layer for business logic or reporting.

#### Dependencies
- **Internal**: `shop.Order`, `shop.OrderLine`, `shop.Customer`, `shop.reporting.DailyRevenue`, `Parcel`.
- **External**: Hibernate JPA provider (`org.hibernate.jpa.HibernatePersistenceProvider`), PostgreSQL JDBC driver, JTA data source `java:/jdbc/ShopDS`, non‑JTA data source `java:/jdbc/ReportingDS`, mapping file `META-INF/order-queries.xml`.

#### Cross-cutting
- **Hibernate DTD** (`http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd`) used for entity mapping.
- **Batching**: `hibernate.jdbc.batch_size=50` for efficient inserts/updates.
- **Sequence generation**: `SHIPMENT_SEQ` for `Shipment.id`.
- **Cascade strategy**: `all-delete-orphan` on `Shipment.parcels` set.
- **Security**: credentials (`shop_app`/`***`) specified in persistence units.
