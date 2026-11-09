### Shipment.hbm.xml
- H: 1.0
- C: 608896F9
- D: 2026-11-04T06:47:45Z
- T: 2026-11-04T12:30:15Z
- G: 
- A: 0.0.0
- X: file
- P: D9E787D0
- N: 13E13638
---
**Facts (exact, whole file):** elements: 17; namespace declarations: 0; id and name attributes: 12; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0; persistence units: 0; JTA data sources: 0; non-JTA data sources: 0; listed classes: 0; entity mappings: 2; embeddables and mapped superclasses: 0; named queries: 2; associations: 2; mapping files: 0; properties: 4

> Maps shipment persistence, including its order and parcel relationships, express-shipment inheritance, and shipment lookup queries.

#### Purpose
- Defines Hibernate ORM mappings for shipments and express shipments.
- Provides HQL and native SQL queries for undelivered shipments and carrier counts.

#### Type
- Hibernate mapping (`*.hbm.xml`); root element `hibernate-mapping`; no XML namespace declared.
- Declares Hibernate Mapping DTD 3.0, with system ID `http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd`.

#### Definitions
- Entity `shop.shipping.Shipment` maps to table `SHIPMENT`.
  - ID `id` maps to `SHIPMENT_ID`; sequence generator uses `SHIPMENT_SEQ`.
  - `trackingCode` → `TRACKING_CODE`, length `40`, not null.
  - `carrier` → `CARRIER`, length `12`.
  - `shippedAt` → `SHIPPED_AT`, type `timestamp`.
  - Many-to-one `order` references `shop.Order` via `ORDER_ID`, not null.
  - Set `parcels` is inverse with cascade `all-delete-orphan`; key `SHIPMENT_ID`; one-to-many class `Parcel`.
- Joined subclass `ExpressShipment` maps to `EXPRESS_SHIPMENT`; key `SHIPMENT_ID`; property `deliveryWindow` → `DELIVERY_WINDOW`.
- Named query `Shipment.undelivered`: selects shipments where `deliveredAt` is null and `shippedAt` precedes `:cutoff`.
- Native SQL query `Shipment.countByCarrier`: selects `CARRIER` and row count from `SHIPMENT`, grouped by `CARRIER`.

#### Configuration
- Package: `shop.shipping`.
- ID generation: sequence `SHIPMENT_SEQ`.
- `trackingCode` length `40`, required; `carrier` length `12`.
- `parcels` cascade: `all-delete-orphan`.

#### Dependencies
- Referenced classes: `shop.Order`, `Parcel`, `ExpressShipment`.
- External mapping DTD: Hibernate Mapping DTD 3.0 at `http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd`.
- Database tables: `SHIPMENT`, `EXPRESS_SHIPMENT`; sequence: `SHIPMENT_SEQ`.
