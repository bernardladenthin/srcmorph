### shipment.xsd
- H: 1.0
- C: E7230D69
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T01:12:59Z
- G: 
- A: 0.0.0
- X: file
---
> Defines the XML schema for a shipment notice sent by a warehouse when parcels leave the building.

#### Purpose
- Validates shipment notice documents.
- Enforces structure and value constraints for shipment data.

#### Type
- XML Schema (`<xs:schema>`)
- Root element: `shipmentNotice`
- Namespaces:
  - `xs` → `http://www.w3.org/2001/XMLSchema`
  - `shp` → `urn:example:shipment:1`
- Target namespace: `urn:example:shipment:1`
- `elementFormDefault="qualified"`

#### Definitions
- **Element**: `shipmentNotice` of type `shp:ShipmentNoticeType`
- **SimpleTypes**:
  - `CarrierCode`: enumeration of `DHL`, `UPS`, `GLS`, `POST`
  - `WeightGrams`: positive integer ≤ 31500
- **ComplexTypes**:
  - `ParcelType`
    - Elements: `trackingNumber` (`xs:string`), `weight` (`shp:WeightGrams`)
    - Attribute: `sequence` (`xs:positiveInteger`, required)
  - `AddressType`
    - Elements: `name`, `street`, `postalCode`, `city` (`xs:string`), `country` (`xs:string`, optional, default `DE`)
  - `ShipmentNoticeType`
    - Elements: `orderReference` (`xs:string`), `shippedAt` (`xs:dateTime`), `carrier` (`shp:CarrierCode`), `recipient` (`shp:AddressType`), `parcel` (`shp:ParcelType`, unbounded)
    - Attribute: `noticeId` (`xs:ID`, required)

#### Configuration
- `country` element default value: `"DE"`
- `WeightGrams` maximum inclusive value: `31500`

#### Dependencies
- None (self‑contained schema).
