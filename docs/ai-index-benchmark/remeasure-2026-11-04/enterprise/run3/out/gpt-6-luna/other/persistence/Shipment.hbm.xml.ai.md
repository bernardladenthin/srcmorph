### Shipment.hbm.xml
- H: 1.0
- C: 608896F9
- D: 2026-11-04T06:47:45Z
- T: 2026-11-04T08:20:49Z
- G: 
- A: 0.0.0
- X: file
- P: 326D0B29
- N: 13E13638
---
**Facts (exact, whole file):** elements: 17; namespace declarations: 0; id and name attributes: 12; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0; persistence units: 0; JTA data sources: 0; non-JTA data sources: 0; listed classes: 0; entity mappings: 2; embeddables and mapped superclasses: 0; named queries: 2; associations: 2; mapping files: 0; properties: 4

> Maps shipment and express-shipment persistence to database tables, including order and parcel relationships and shipment lookup queries.

#### Purpose
- Defines Hibernate mappings for shipment records, express-shipment extensions, and queries for undelivered shipments and carrier counts.

#### Type
- Hibernate mapping XML; root element `hibernate-mapping`; no XML namespaces declared.
- References Hibernate Mapping DTD 3.0.

#### Definitions
- Package: `shop.shipping`.
- Entity `Shipment` maps to table `SHIPMENT`:
  - ID `id` → column `SHIPMENT_ID`; sequence generator uses `SHIPMENT_SEQ`.
  - Properties: `trackingCode` → `TRACKING_CODE`; `carrier` → `CARRIER`; `shippedAt` → `SHIPPED_AT` (`timestamp`).
  - Many-to-one `order` → class `shop.Order`, column `ORDER_ID`.
  - Set `parcels` → one-to-many class `Parcel`, keyed by `SHIPMENT_ID`; inverse with `all-delete-orphan` cascade.
- Joined subclass `ExpressShipment` maps to `EXPRESS_SHIPMENT`, keyed by `SHIPMENT_ID`; property `deliveryWindow` → `DELIVERY_WINDOW`.
- Named query `Shipment.undelivered`: selects shipments where `deliveredAt` is null and `shippedAt` is before `:cutoff`.
- SQL query `Shipment.countByCarrier`: selects `CARRIER` and `count(*)` from `SHIPMENT`, grouped by `CARRIER`.

#### Configuration
- `trackingCode`: column `TRACKING_CODE`, length `40`, not null.
- `carrier`: column `CARRIER`, length `12`.
- `order`: column `ORDER_ID`, not null.
- `parcels`: `inverse="true"`; `cascade="all-delete-orphan"`.
- `Shipment.undelivered`: parameter `:cutoff`.

#### Dependencies
- Class references: `shop.Order`, `Parcel`; package-relative mapped classes `Shipment`, `ExpressShipment`.
- Database tables and columns: `SHIPMENT`, `EXPRESS_SHIPMENT`, `SHIPMENT_ID`, `TRACKING_CODE`, `CARRIER`, `SHIPPED_AT`, `ORDER_ID`, `DELIVERY_WINDOW`.
- Sequence: `SHIPMENT_SEQ`.
- DTD: `http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd`.
