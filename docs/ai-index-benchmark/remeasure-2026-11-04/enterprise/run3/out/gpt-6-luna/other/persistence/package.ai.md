### other/persistence
- H: 1.0
- C: 2F8025B8
- D: 2026-11-04T06:47:45Z
- T: 2026-11-04T08:22:14Z
- G: 
- A: 0.0.0
- X: package
- F: [Shipment.hbm.xml](Shipment.hbm.xml.ai.md)
- F: [persistence.xml](persistence.xml.ai.md)
---
> Persists shipment and order-related data through Hibernate mappings and configures JTA shop and resource-local reporting persistence units.

#### Purpose
- Maps shipments, express-shipment extensions, and their order and parcel relationships to database tables.
- Configures persistence providers, data sources, managed classes, and Hibernate properties for shop operations and reporting.

#### Responsibilities
- **Shipment persistence:** Maps `Shipment` and `ExpressShipment`, their fields and associations, and shipment lookup and carrier-count queries.
- **Persistence-unit setup:** Defines the JTA `shop` unit and resource-local `reporting` unit, including their data sources, managed classes, and mapping file.

#### Key units
- `Shipment` — maps shipment identifiers, tracking, carrier, timestamp, order, and parcels to `SHIPMENT`.
- `ExpressShipment` — joined subclass mapped to `EXPRESS_SHIPMENT`, with a `deliveryWindow` property.
- `Shipment.undelivered` — selects shipments with no delivery timestamp and a shipment time before `:cutoff`.
- `Shipment.countByCarrier` — groups shipment counts by `CARRIER`.
- `shop` persistence unit — configures Hibernate JPA with JTA and manages order-related classes.
- `reporting` persistence unit — configures resource-local persistence for `shop.reporting.DailyRevenue`.

#### Data flow
- Hibernate maps shipment records to `SHIPMENT` and express-shipment extensions to `EXPRESS_SHIPMENT`; `Shipment` associates with `shop.Order` and owns an inverse, delete-orphan-cascaded parcel set.
- The `shop` persistence unit uses `java:/jdbc/ShopDS` and `META-INF/order-queries.xml`; the `reporting` unit uses `java:/jdbc/ReportingDS`.

#### Dependencies
- Entity classes: `shop.Order`, `shop.OrderLine`, `shop.Customer`, `shop.reporting.DailyRevenue`, and `Parcel`.
- Database objects: `SHIPMENT`, `EXPRESS_SHIPMENT`, and sequence `SHIPMENT_SEQ`.
- Hibernate provider: `org.hibernate.jpa.HibernatePersistenceProvider`; dialect: `org.hibernate.dialect.PostgreSQLDialect`.
- Data sources: `java:/jdbc/ShopDS`, `java:/jdbc/ReportingDS`; mapping file: `META-INF/order-queries.xml`.

#### Cross-cutting
- Hibernate configuration sets JDBC batch size to `50`; credentials are configured in persistence properties.
- Shipment-to-parcel mapping uses inverse ownership and `all-delete-orphan` cascade; shipment-to-order is a required many-to-one association.
