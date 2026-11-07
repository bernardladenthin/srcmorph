### other/persistence
- H: 1.0
- C: 2F8025B8
- D: 2026-11-04T06:47:45Z
- T: 2026-11-04T12:31:41Z
- G: 
- A: 0.0.0
- X: package
- F: [Shipment.hbm.xml](Shipment.hbm.xml.ai.md)
- F: [persistence.xml](persistence.xml.ai.md)
---
> Configures persistence for shop orders, customers, shipments, and reporting data, including shipment mappings and lookup queries.

#### Purpose
- Maps `shop.shipping.Shipment` and joined `ExpressShipment` persistence, with parcel and order relationships.
- Defines separate JTA shop and resource-local reporting persistence units.

#### Responsibilities
- Shipment ORM: maps shipment fields, sequence-generated IDs, joined inheritance, and parcel ownership/cascade behavior.
- Persistence-unit setup: configures entity classes, transaction modes, data sources, Hibernate provider, and shop database properties.
- Shipment queries: provides undelivered-shipment lookup and carrier shipment counts.

#### Key units
- `Shipment` — maps to `SHIPMENT`, with required `trackingCode`, carrier and timestamp fields, an order reference, and parcel collection.
- `ExpressShipment` — joined subclass stored in `EXPRESS_SHIPMENT`, with a delivery-window property.
- `Shipment.undelivered` — selects shipments with no delivery timestamp shipped before a supplied cutoff.
- `Shipment.countByCarrier` — groups shipment row counts by carrier.
- `shop` persistence unit — JTA unit for `Order`, `OrderLine`, and `Customer`, with `META-INF/order-queries.xml`.
- `reporting` persistence unit — resource-local unit for `DailyRevenue`, excluding unlisted classes.

#### Data flow
- Hibernate maps shipment records from `SHIPMENT` and `EXPRESS_SHIPMENT`; shipment IDs use `SHIPMENT_SEQ`.
- Shipment associations connect each shipment to an `Order` and its `Parcel` collection; parcel changes cascade with orphan deletion.
- The `shop` and `reporting` persistence units route persistence through their respective JTA and non-JTA data sources.

#### Dependencies
- Shipment mappings reference `shop.Order`, `Parcel`, and `ExpressShipment`; tables `SHIPMENT` and `EXPRESS_SHIPMENT`; sequence `SHIPMENT_SEQ`.
- Persistence units use `org.hibernate.jpa.HibernatePersistenceProvider` and PostgreSQL dialect configuration.
- Data sources: `java:/jdbc/ShopDS` and `java:/jdbc/ReportingDS`.
- The `shop` unit references `META-INF/order-queries.xml`.

#### Cross-cutting
- Hibernate ORM configuration is used for shipment mappings and as the provider for the JPA persistence units.
- Persistence is split by transaction mode: JTA for `shop`, `RESOURCE_LOCAL` for `reporting`.
- Shop configuration sets JDBC batch size to `50`; its configured database password is masked.
