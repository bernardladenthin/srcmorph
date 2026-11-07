### other/persistence
- H: 1.0
- C: 2F8025B8
- D: 2026-11-04T06:47:45Z
- T: 2026-11-04T08:12:24Z
- G: 
- A: 0.0.0
- X: package
- F: [Shipment.hbm.xml](Shipment.hbm.xml.ai.md)
- F: [persistence.xml](persistence.xml.ai.md)
---
> Provides Hibernate‑based persistence for shop orders, shipments, and reporting, exposing entity mappings, queries, and persistence unit configuration.

#### Purpose
- Persist order‑related entities (`Order`, `OrderLine`, `Customer`, `Shipment`, `ExpressShipment`, `DailyRevenue`).
- Supply named queries for shipment status and carrier statistics.
- Configure JTA and RESOURCE_LOCAL persistence units with Hibernate.

#### Responsibilities
- **Entity mapping** – Hibernate XML for `Shipment` and `ExpressShipment` with relationships to `Order` and `Parcel`.
- **Persistence unit definition** – `persistence.xml` declares two units (`shop`, `reporting`) with data sources, entity classes, and Hibernate properties.
- **Query provisioning** – Named HQL/SQL queries for undelivered shipments and carrier counts.

#### Key units
- `Shipment` (table `SHIPMENT`): id, trackingCode, carrier, shippedAt, deliveredAt, order FK, parcels collection.
- `ExpressShipment` (joined‑subclass, table `EXPRESS_SHIPMENT`): deliveryWindow.
- `Shipment.undelivered` (HQL): `from Shipment s where s.deliveredAt is null and s.shippedAt < :cutoff`.
- `Shipment.countByCarrier` (SQL): `select CARRIER, count(*) from SHIPMENT group by CARRIER`.
- Persistence unit `shop`: JTA, Hibernate provider, data source `java:/jdbc/ShopDS`, entities `Order`, `OrderLine`, `Customer`, mapping file `order-queries.xml`.
- Persistence unit `reporting`: RESOURCE_LOCAL, data source `java:/jdbc/ReportingDS`, entity `DailyRevenue`.

#### Data flow
1. Application obtains an `EntityManager` from the `shop` or `reporting` unit.
2. Entities are persisted or queried via Hibernate, using the mappings in `Shipment.hbm.xml`.
3. Named queries (`Shipment.undelivered`, `Shipment.countByCarrier`) are executed against the `SHIPMENT` table.
4. Reporting data is accessed through the `reporting` unit, reading `DailyRevenue` rows.

#### Dependencies
- Hibernate ORM (`org.hibernate.jpa.HibernatePersistenceProvider`).
- Jakarta Persistence API.
- Data sources: `java:/jdbc/ShopDS`, `java:/jdbc/ReportingDS`.
- Entity classes: `shop.Order`, `shop.OrderLine`, `shop.Customer`, `shop.reporting.DailyRevenue`.
- External mapping file: `META-INF/order-queries.xml`.

#### Cross-cutting
- Sequence generator `SHIPMENT_SEQ` for `Shipment.id`.
- Cascade `all-delete-orphan` on `Shipment.parcels`.
- Hibernate properties: PostgreSQL dialect, batch size 50, credentials for `shop_app`.
- JTA transaction type for `shop`; RESOURCE_LOCAL for `reporting`.
