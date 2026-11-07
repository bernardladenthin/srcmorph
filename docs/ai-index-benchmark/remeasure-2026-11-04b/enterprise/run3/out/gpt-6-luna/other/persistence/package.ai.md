### other/persistence
- H: 1.0
- C: 2F8025B8
- D: 2026-11-04T06:47:45Z
- T: 2026-11-04T12:40:22Z
- G: 
- A: 0.0.0
- X: package
- F: [Shipment.hbm.xml](Shipment.hbm.xml.ai.md)
- F: [persistence.xml](persistence.xml.ai.md)
---
> Persists shop shipments, orders, customers, and reporting data through Hibernate mappings and configured Jakarta Persistence units.

#### Purpose
- Maps shipment and express-shipment records, their order and parcel relationships, and shipment lookup queries.
- Configures application and reporting persistence units with their entity classes, data sources, and Hibernate settings.

#### Responsibilities
- **Shipment mapping and queries:** Maps `Shipment` and `ExpressShipment` to database tables, including shipment-to-order and shipment-to-parcel associations; defines undelivered-shipment and carrier-count queries.
- **Persistence-unit configuration:** Sets up the JTA `shop` unit for order and customer data and the resource-local `reporting` unit for daily revenue.

#### Key units
- `shop.shipping.Shipment` — maps shipment identifiers, tracking code, carrier, shipment time, associated order, and parcels to `SHIPMENT`.
- `ExpressShipment` — joined subclass mapped to `EXPRESS_SHIPMENT`, with a delivery-window property.
- `Shipment.undelivered` — selects shipments whose `deliveredAt` is null and `shippedAt` precedes `:cutoff`.
- `Shipment.countByCarrier` — counts `SHIPMENT` records grouped by carrier.
- `shop` persistence unit — configures Hibernate JPA with `ShopDS`, `Order`, `OrderLine`, `Customer`, and `META-INF/order-queries.xml`.
- `reporting` persistence unit — configures resource-local persistence with `ReportingDS` and `shop.reporting.DailyRevenue`.

#### Data flow
- Shipment persistence uses `SHIPMENT_SEQ` for IDs and stores shipment data in `SHIPMENT`; express shipments extend this mapping through `EXPRESS_SHIPMENT`.
- `Shipment` references `shop.Order` and owns an inverse `parcels` set with `all-delete-orphan` cascading.
- JPA clients use the `shop` or `reporting` persistence unit to access their configured entities through the corresponding data source.

#### Dependencies
- Hibernate mapping references `shop.Order`, `Parcel`, `ExpressShipment`, `SHIPMENT`, `EXPRESS_SHIPMENT`, and sequence `SHIPMENT_SEQ`.
- The `shop` unit uses `java:/jdbc/ShopDS`, Hibernate’s JPA provider, PostgreSQL dialect, and `META-INF/order-queries.xml`.
- The `reporting` unit uses `java:/jdbc/ReportingDS` and `shop.reporting.DailyRevenue`.

#### Cross-cutting
- Persistence is configured with Hibernate; the `shop` unit sets JDBC batch size to `50` and credentials for `shop_app`.
- Persistence modes differ by unit: JTA for `shop`, `RESOURCE_LOCAL` for `reporting`.
- The shipment parcel association uses `all-delete-orphan` cascading.
