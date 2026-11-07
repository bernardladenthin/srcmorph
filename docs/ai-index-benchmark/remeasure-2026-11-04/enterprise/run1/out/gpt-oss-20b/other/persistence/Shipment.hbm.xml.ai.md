### Shipment.hbm.xml
- H: 1.0
- C: 608896F9
- D: 2026-11-04T06:47:45Z
- T: 2026-11-04T08:11:06Z
- G: 
- A: 0.0.0
- X: file
- P: 326D0B29
- N: 13E13638
---
**Facts (exact, whole file):** elements: 17; namespace declarations: 0; id and name attributes: 12; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0; persistence units: 0; JTA data sources: 0; non-JTA data sources: 0; listed classes: 0; entity mappings: 2; embeddables and mapped superclasses: 0; named queries: 2; associations: 2; mapping files: 0; properties: 4

> Maps the `shop.shipping.Shipment` entity and its subclasses to database tables, defines relationships, and provides named HQL/SQL queries.

#### Purpose
- Persist `Shipment` and `ExpressShipment` entities.
- Provide queries for undelivered shipments and carrier counts.

#### Type
- **Hibernate mapping** (`hibernate-mapping` root).  
- Namespace: none.  
- Declares DTD `-//Hibernate/Hibernate Mapping DTD 3.0//EN`.

#### Definitions
- **Class** `Shipment` (table `SHIPMENT`):
  - `id` (`SHIPMENT_ID`) – sequence `SHIPMENT_SEQ`.
  - `trackingCode` (`TRACKING_CODE`, length 40, not-null).
  - `carrier` (`CARRIER`, length 12).
  - `shippedAt` (`SHIPPED_AT`, type `timestamp`).
  - `order` (`ORDER_ID`, many-to-one to `shop.Order`, not-null).
  - `parcels` (`Set` of `Parcel`, inverse, cascade `all-delete-orphan`).
- **Joined-subclass** `ExpressShipment` (table `EXPRESS_SHIPMENT`):
  - `deliveryWindow` (`DELIVERY_WINDOW`).
- **Queries**:
  - `Shipment.undelivered` – HQL: `from Shipment s where s.deliveredAt is null and s.shippedAt < :cutoff`.
  - `Shipment.countByCarrier` – SQL: `select CARRIER, count(*) from SHIPMENT group by CARRIER`.

#### Configuration
- Sequence generator: `sequence_name = SHIPMENT_SEQ`.
- Cascade: `all-delete-orphan` on `parcels`.
- Query parameters: `:cutoff` in `Shipment.undelivered`.

#### Dependencies
- `shop.Order` (many-to-one reference).
- `Parcel` (one-to-many collection).
- DTD at `http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd`.
