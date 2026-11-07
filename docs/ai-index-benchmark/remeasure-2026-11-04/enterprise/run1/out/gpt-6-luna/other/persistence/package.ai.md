### other/persistence
- H: 1.0
- C: 2F8025B8
- D: 2026-11-04T06:47:45Z
- T: 2026-11-04T08:09:31Z
- G: 
- A: 0.0.0
- X: package
- F: [Shipment.hbm.xml](Shipment.hbm.xml.ai.md)
- F: [persistence.xml](persistence.xml.ai.md)
---
> Persists shop shipments, orders, customers, and reporting data through Hibernate/JPA mappings, persistence units, and shipment queries.

#### Purpose
- Map `Shipment` and `ExpressShipment` to database tables, including their order and parcel associations.
- Configure JTA persistence for shop operations and resource-local persistence for reporting.

#### Responsibilities
- **Shipment persistence:** Map shipment fields, sequence-generated IDs, joined inheritance, and order/parcel associations; define undelivered-shipment and carrier-count queries.
- **Persistence-unit configuration:** Set up the `shop` and `reporting` units with their transaction types, data sources, managed classes, mapping file, and Hibernate properties.

#### Key units
- `Shipment` — maps to `SHIPMENT`, with a required `shop.Order` association and an inverse parcel set using `all-delete-orphan` cascade.
- `ExpressShipment` — joined subclass mapped to `EXPRESS_SHIPMENT`, with a `deliveryWindow` property.
- `Shipment.undelivered` — selects shipments with no delivery date shipped before `:cutoff`.
- `Shipment.countByCarrier` — native SQL query that counts shipment rows grouped by carrier.
- `shop` persistence unit — JTA unit using Hibernate, `ShopDS`, and managed `Order`, `OrderLine`, and `Customer` classes.
- `reporting` persistence unit — resource-local unit using `ReportingDS`, managing `DailyRevenue` and excluding unlisted classes.

#### Data flow
- Hibernate maps shipment entities to `SHIPMENT` and `EXPRESS_SHIPMENT`, using `SHIPMENT_SEQ` for shipment IDs and linking orders and parcels through `ORDER_ID` and `SHIPMENT_ID`.
- The `shop` persistence unit loads `META-INF/order-queries.xml` and manages shop order and customer classes; the separate `reporting` unit manages `DailyRevenue`.

#### Dependencies
- Hibernate JPA provider: `org.hibernate.jpa.HibernatePersistenceProvider`; PostgreSQL dialect: `org.hibernate.dialect.PostgreSQLDialect`.
- Data sources: `java:/jdbc/ShopDS` and `java:/jdbc/ReportingDS`.
- Mapped classes include `shop.shipping.Shipment`, `shop.shipping.ExpressShipment`, `shop.shipping.Parcel`, `shop.Order`, `shop.OrderLine`, `shop.Customer`, and `shop.reporting.DailyRevenue`.
- Database objects: `SHIPMENT`, `EXPRESS_SHIPMENT`, and sequence `SHIPMENT_SEQ`.

#### Cross-cutting
- Hibernate configuration sets JDBC batch size to `50`; connection username is `shop_app` and the password is masked.
- Shipment IDs use sequence generation; `trackingCode` is required and limited to 40 characters, while `carrier` is limited to 12 characters.
