### other/persistence
- H: 1.0
- C: 2F8025B8
- D: 2026-11-04T06:47:45Z
- T: 2026-11-04T12:25:12Z
- G: 
- A: 0.0.0
- X: package
- F: [Shipment.hbm.xml](Shipment.hbm.xml.ai.md)
- F: [persistence.xml](persistence.xml.ai.md)
---
> Configures shop and reporting persistence while mapping shipment records, their order and parcel relationships, and shipment queries.

#### Purpose
- Defines Hibernate mappings for shipment storage and Jakarta Persistence units for shop transactions and reporting.
- Connects managed domain classes to separate shop and reporting data sources.

#### Responsibilities
- **Shipment ORM:** Maps `Shipment` and joined-subclass `ExpressShipment` to shipment tables, including fields and order/parcel associations.
- **Persistence setup:** Configures JTA `shop` and resource-local `reporting` units, their managed classes, data sources, and Hibernate properties.
- **Shipment queries:** Defines queries for undelivered shipments and shipment counts grouped by carrier.

#### Key units
- `shop.shipping.Shipment` — maps shipment ID, tracking code, carrier, shipment timestamp, required `shop.Order` association, and parcel collection to `SHIPMENT`.
- `ExpressShipment` — maps the joined subclass to `EXPRESS_SHIPMENT`, including `deliveryWindow`.
- `Shipment.undelivered` — selects shipments with no `deliveredAt` and `shippedAt` before `:cutoff`.
- `Shipment.countByCarrier` — returns shipment counts grouped by `CARRIER`.
- Persistence unit `shop` — JTA unit managing `shop.Order`, `shop.OrderLine`, and `shop.Customer`.
- Persistence unit `reporting` — resource-local unit managing `shop.reporting.DailyRevenue`.

#### Data flow
- Jakarta Persistence routes shop work through `java:/jdbc/ShopDS` and reporting work through `java:/jdbc/ReportingDS`.
- Hibernate maps shipment entities to `SHIPMENT` and `EXPRESS_SHIPMENT`; shipment parcels use inverse one-to-many mapping with `all-delete-orphan` cascade.
- The `shop` unit loads `META-INF/order-queries.xml` for additional mappings.

#### Dependencies
- Hibernate ORM and Jakarta Persistence; the configured provider is `org.hibernate.jpa.HibernatePersistenceProvider`.
- Data sources `java:/jdbc/ShopDS` and `java:/jdbc/ReportingDS`.
- Domain classes `shop.Order`, `shop.OrderLine`, `shop.Customer`, `shop.reporting.DailyRevenue`, `Parcel`, and `ExpressShipment`.
- Shipment mapping uses sequence `SHIPMENT_SEQ`; the shop persistence unit references `META-INF/order-queries.xml`.

#### Cross-cutting
- Hibernate settings specify PostgreSQL dialect and JDBC batch size `50`; connection credentials are configured in the persistence descriptor.
- Shipment IDs use sequence generation; `trackingCode` and the order association are non-null.
