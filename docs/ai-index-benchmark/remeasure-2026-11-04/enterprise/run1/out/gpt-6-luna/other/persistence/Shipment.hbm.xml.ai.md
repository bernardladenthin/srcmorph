### Shipment.hbm.xml
- H: 1.0
- C: 608896F9
- D: 2026-11-04T06:47:45Z
- T: 2026-11-04T08:08:09Z
- G: 
- A: 0.0.0
- X: file
- P: 326D0B29
- N: 13E13638
---
**Facts (exact, whole file):** elements: 17; namespace declarations: 0; id and name attributes: 12; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0; persistence units: 0; JTA data sources: 0; non-JTA data sources: 0; listed classes: 0; entity mappings: 2; embeddables and mapped superclasses: 0; named queries: 2; associations: 2; mapping files: 0; properties: 4

> Maps shipment and express-shipment records, their order and parcel associations, and queries for undelivered shipments and carrier counts.

#### Purpose
- Define Hibernate persistence mappings for shipments and the `ExpressShipment` subclass.
- Provide HQL and native SQL queries for shipment lookups and aggregation.

#### Type
- Hibernate mapping XML; root element `hibernate-mapping`; package `shop.shipping`.
- Declares no XML namespaces; references Hibernate Mapping DTD 3.0 at `http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd`.

#### Definitions
- Entity `Shipment` maps to `SHIPMENT`:
  - Identifier `id` → `SHIPMENT_ID`; sequence generator uses `SHIPMENT_SEQ`.
  - Properties: `trackingCode` → `TRACKING_CODE` (length 40, not null); `carrier` → `CARRIER` (length 12); `shippedAt` → `SHIPPED_AT` (type `timestamp`).
  - Many-to-one `order` uses class `shop.Order`, column `ORDER_ID`, not null.
  - Set `parcels` is inverse with cascade `all-delete-orphan`; key column `SHIPMENT_ID`; one-to-many class `Parcel`.
- Joined subclass `ExpressShipment` maps to `EXPRESS_SHIPMENT`; key column `SHIPMENT_ID`; property `deliveryWindow` → `DELIVERY_WINDOW`.
- Named query `Shipment.undelivered`: selects shipments where `deliveredAt` is null and `shippedAt` precedes `:cutoff`.
- Native SQL query `Shipment.countByCarrier`: groups rows in `SHIPMENT` by `CARRIER` and counts each group.

#### Configuration
- ID generation: `class="sequence"`, `sequence_name=SHIPMENT_SEQ`.
- `trackingCode`: `length=40`, `not-null=true`.
- `carrier`: `length=12`.
- `shippedAt`: `type=timestamp`.
- `order`: `not-null=true`.
- `parcels`: `inverse=true`, `cascade=all-delete-orphan`.
- Undelivered query parameter: `:cutoff`.

#### Dependencies
- DTD: `http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd`.
- Mapped classes: `shop.shipping.Shipment`, `shop.shipping.ExpressShipment`, `shop.shipping.Parcel`, `shop.Order`.
- Database tables: `SHIPMENT`, `EXPRESS_SHIPMENT`; sequence: `SHIPMENT_SEQ`.
