### shipment.xsd
- H: 1.0
- C: E7230D69
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T06:36:13Z
- G: 
- A: 0.0.0
- X: file
---
> Defines the XML schema for a shipment notice sent by a warehouse when parcels leave the building.

#### Purpose
- Validates shipment notice documents.
- Specifies parcel, carrier, and recipient details.

#### Type
- XML Schema (`<xs:schema>`)
- Root element: `shipmentNotice`
- Namespaces:
  - `xs` → `http://www.w3.org/2001/XMLSchema`
  - `shp` → `urn:example:shipment:1`

#### Definitions
- **Element**
  - `shipmentNotice` (`shp:ShipmentNoticeType`)
- **SimpleTypes**
  - `CarrierCode`: enumeration of `DHL`, `UPS`, `GLS`, `POST`
  - `WeightGrams`: positive integer ≤ 31500
- **ComplexTypes**
  - `ParcelType`
    - Sequence: `trackingNumber` (`xs:string`), `weight` (`shp:WeightGrams`)
    - Attribute: `sequence` (`xs:positiveInteger`, required)
  - `AddressType`
    - Sequence: `name`, `street`, `postalCode`, `city`, `country` (`xs:string`, optional, default `DE`)
  - `ShipmentNoticeType`
    - Sequence: `orderReference`, `shippedAt`, `carrier`, `recipient`, `parcel` (unbounded)
    - Attribute: `noticeId` (`xs:ID`, required)

#### Configuration
- `country` default value: `DE`
- `WeightGrams` max inclusive: `31500`

#### Dependencies
- None (self‑contained schema).
