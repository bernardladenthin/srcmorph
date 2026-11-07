### Shipment.hbm.xml
- H: 1.0
- C: 608896F9
- D: 2026-11-04T06:47:45Z
- T: 2026-11-04T12:38:56Z
- G: 
- A: 0.0.0
- X: file
- P: D9E787D0
- N: 13E13638
---
**Facts (exact, whole file):** elements: 17; namespace declarations: 0; id and name attributes: 12; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0; persistence units: 0; JTA data sources: 0; non-JTA data sources: 0; listed classes: 0; entity mappings: 2; embeddables and mapped superclasses: 0; named queries: 2; associations: 2; mapping files: 0; properties: 4

> Maps shipment and express-shipment persistence to database tables, including order and parcel relationships, and defines shipment lookup queries.

#### Purpose
- Defines Hibernate persistence mappings for shipments and the `ExpressShipment` subclass.
- Provides HQL and native SQL queries for undelivered shipments and carrier counts.

#### Type
- Hibernate mapping (`*.hbm.xml`); root element `hibernate-mapping`.
- Declared namespaces: none.

#### Definitions
- Entity `shop.shipping.Shipment` → table `SHIPMENT`:
  - ID `id` → column `SHIPMENT_ID`; sequence generator uses `SHIPMENT_SEQ`.
  - Properties: `trackingCode` → `TRACKING_CODE` (length `40`, not null); `carrier` → `CARRIER` (length `12`); `shippedAt` → `SHIPPED_AT` (type `timestamp`).
  - Many-to-one `order` → class `shop.Order`, column `ORDER_ID`, not null.
  - Set `parcels` → inverse, cascade `all-delete-orphan`; key `SHIPMENT_ID`; one-to-many class `Parcel`.
- Joined subclass `ExpressShipment` → table `EXPRESS_SHIPMENT`; key `SHIPMENT_ID`; property `deliveryWindow` → `DELIVERY_WINDOW`.
- Named HQL query `Shipment.undelivered`: selects shipments with `deliveredAt` null and `shippedAt` before `:cutoff`.
- Named SQL query `Shipment.countByCarrier`: selects `CARRIER` and `count(*)` from `SHIPMENT`, grouped by `CARRIER`.

#### Configuration
- Mapping package: `shop.shipping`.
- Identifier generator: `sequence`; sequence name `SHIPMENT_SEQ`.
- `trackingCode`: length `40`, not null.
- `carrier`: length `12`.
- `shippedAt`: type `timestamp`.
- `order`: not null.
- `parcels`: inverse `true`; cascade `all-delete-orphan`.
- HQL parameter: `:cutoff`.

#### Dependencies
- Classes: `shop.shipping.Shipment`, `shop.Order`, `Parcel`, `ExpressShipment`.
- Database tables: `SHIPMENT`, `EXPRESS_SHIPMENT`.
- Database sequence: `SHIPMENT_SEQ`.
- Hibernate Mapping DTD: `http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd`.
