### Shipment.hbm.xml
- H: 1.0
- C: 608896F9
- D: 2026-11-04T06:47:45Z
- T: 2026-11-04T08:17:41Z
- G: 
- A: 0.0.0
- X: file
- P: 326D0B29
- N: 13E13638
---
**Facts (exact, whole file):** elements: 17; namespace declarations: 0; id and name attributes: 12; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0; persistence units: 0; JTA data sources: 0; non-JTA data sources: 0; listed classes: 0; entity mappings: 2; embeddables and mapped superclasses: 0; named queries: 2; associations: 2; mapping files: 0; properties: 4

> Maps the `shop.shipping.Shipment` entity and its subclasses to database tables, defines relationships, queries, and persistence behavior.

#### Purpose
- Configures Hibernate mapping for `Shipment` and related entities.
- Provides HQL and native SQL queries for shipment retrieval and statistics.

#### Type
- Hibernate mapping (`<hibernate-mapping>` root).  
- Namespace: none declared.

#### Definitions
- **Entity** `Shipment` (`table="SHIPMENT"`, `package="shop.shipping"`)
  - `id` (`column="SHIPMENT_ID"`, sequence `SHIPMENT_SEQ`)
  - `trackingCode` (`column="TRACKING_CODE"`, `length="40"`, `not-null="true"`)
  - `carrier` (`column="CARRIER"`, `length="12"`)
  - `shippedAt` (`column="SHIPPED_AT"`, `type="timestamp"`)
  - `order` (`many-to-one`, `class="shop.Order"`, `column="ORDER_ID"`, `not-null="true"`)
  - `parcels` (`set`, `inverse="true"`, `cascade="all-delete-orphan"`)
    - key: `column="SHIPMENT_ID"`
    - one-to-many: `class="Parcel"`
  - **Subclass** `ExpressShipment` (`joined-subclass`, `table="EXPRESS_SHIPMENT"`)
    - key: `column="SHIPMENT_ID"`
    - `deliveryWindow` (`column="DELIVERY_WINDOW"`)
- **Query** `Shipment.undelivered` (HQL): `from Shipment s where s.deliveredAt is null and s.shippedAt < :cutoff`
- **SQL Query** `Shipment.countByCarrier`: `select CARRIER, count(*) from SHIPMENT group by CARRIER`

#### Configuration
- Sequence generator: `sequence_name="SHIPMENT_SEQ"`
- Cascade: `all-delete-orphan` on `parcels` set.

#### Dependencies
- `shop.Order` class referenced in `many-to-one`.
- `Parcel` class referenced in `one-to-many`.
- DTD: `http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd` (public ID `-//Hibernate/Hibernate Mapping DTD 3.0//EN`).
