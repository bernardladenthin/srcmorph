### Shipment.hbm.xml
- H: 1.0
- C: 608896F9
- D: 2026-11-04T06:47:45Z
- T: 2026-11-04T08:14:36Z
- G: 
- A: 0.0.0
- X: file
- P: 326D0B29
- N: 13E13638
---
**Facts (exact, whole file):** elements: 17; namespace declarations: 0; id and name attributes: 12; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0; persistence units: 0; JTA data sources: 0; non-JTA data sources: 0; listed classes: 0; entity mappings: 2; embeddables and mapped superclasses: 0; named queries: 2; associations: 2; mapping files: 0; properties: 4

> Maps shipment and express-shipment records to database tables, defines their order and parcel associations, and provides shipment lookup and carrier-count queries.

#### Purpose
- Hibernate mapping for shipment persistence and related queries.
- Maps `Shipment` inheritance, shipment metadata, and parcel ownership.

#### Type
- Hibernate mapping (`*.hbm.xml`); root element: `hibernate-mapping`.
- Declares no XML namespace; references Hibernate Mapping DTD 3.0 at `http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd`.

#### Definitions
- Package: `shop.shipping`.
- Entity `Shipment` → table `SHIPMENT`:
  - ID `id` → column `SHIPMENT_ID`; sequence generator `SHIPMENT_SEQ`.
  - Properties: `trackingCode` → `TRACKING_CODE`; `carrier` → `CARRIER`; `shippedAt` → `SHIPPED_AT` (`timestamp`).
  - Many-to-one `order` → class `shop.Order`, column `ORDER_ID`.
  - Set `parcels` → inverse, cascade `all-delete-orphan`; key column `SHIPMENT_ID`; one-to-many class `Parcel`.
- Joined subclass `ExpressShipment` → table `EXPRESS_SHIPMENT`; key column `SHIPMENT_ID`; property `deliveryWindow` → `DELIVERY_WINDOW`.
- Named query `Shipment.undelivered`: selects shipments where `deliveredAt` is null and `shippedAt` precedes `:cutoff`.
- Native SQL query `Shipment.countByCarrier`: selects `CARRIER` and `count(*)` from `SHIPMENT`, grouped by `CARRIER`.

#### Configuration
- `trackingCode`: length `40`, not null.
- `carrier`: length `12`.
- `order`: not null.
- `parcels`: inverse `true`; cascade `all-delete-orphan`.
- ID sequence: `SHIPMENT_SEQ`.
- Query parameter: `:cutoff`.

#### Dependencies
- DTD: `http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd`.
- Classes: `shop.shipping.Shipment`, `shop.shipping.ExpressShipment`, `shop.shipping.Parcel`, `shop.Order`.
- Database tables: `SHIPMENT`, `EXPRESS_SHIPMENT`; sequence: `SHIPMENT_SEQ`.
