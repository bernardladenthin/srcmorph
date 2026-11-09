### other/persistence
- H: 1.0
- C: 2F8025B8
- D: 2026-11-04T06:47:45Z
- T: 2026-11-04T08:16:03Z
- G: 
- A: 0.0.0
- X: package
- F: [Shipment.hbm.xml](Shipment.hbm.xml.ai.md)
- F: [persistence.xml](persistence.xml.ai.md)
---
> Persists shop shipments and configures transactional shop data access alongside a separate daily-revenue reporting unit.

#### Purpose
- Maps shipment and express-shipment data, relationships, and lookup queries to database tables.
- Configures Hibernate-backed JPA persistence for shop transactions and resource-local revenue reporting.

#### Responsibilities
- **Shipment mapping:** Defines `Shipment` and joined-subclass `ExpressShipment` storage, shipment-to-order and shipment-to-parcel associations, and shipment queries.
- **Persistence-unit configuration:** Declares the JTA `shop` unit for orders, order lines, and customers, and the resource-local `reporting` unit for daily revenue.

#### Key units
- `Shipment` — maps shipment ID, tracking code, carrier, timestamp, order, and parcels to `SHIPMENT`.
- `ExpressShipment` — maps express-shipment delivery-window data to `EXPRESS_SHIPMENT` using a joined key.
- `Shipment.undelivered` — selects shipments not yet delivered and shipped before a cutoff.
- `Shipment.countByCarrier` — groups shipment counts by carrier.
- `shop` persistence unit — configures Hibernate JTA persistence for `shop.Order`, `shop.OrderLine`, and `shop.Customer`.
- `reporting` persistence unit — configures resource-local persistence for `shop.reporting.DailyRevenue`.

#### Data flow
- JPA persistence units connect managed shop and reporting classes to their configured data sources.
- Hibernate mapping persists `Shipment` records and their `Parcel` collection; `ExpressShipment` adds joined-table data.
- Shipment queries retrieve undelivered shipments by cutoff or aggregate shipment counts by carrier.

#### Dependencies
- Hibernate provider `org.hibernate.jpa.HibernatePersistenceProvider` and PostgreSQL dialect `org.hibernate.dialect.PostgreSQLDialect`.
- Data sources `java:/jdbc/ShopDS` and `java:/jdbc/ReportingDS`.
- Shipment mapping references `shop.Order`, `shop.shipping.Parcel`, and the `SHIPMENT`, `EXPRESS_SHIPMENT` tables with sequence `SHIPMENT_SEQ`.
- Shop persistence references `META-INF/order-queries.xml`.

#### Cross-cutting
- Shop transactions use JTA; reporting uses `RESOURCE_LOCAL`.
- Shipment parcels use inverse one-to-many mapping with `all-delete-orphan` cascade; shipment order association is required.
- Shop persistence configures JDBC batch size `50`; reporting excludes unlisted classes.
