### shipment.xsd
- H: 1.0
- C: E7230D69
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T06:47:31Z
- G: 
- A: 0.0.0
- X: file
---
> Defines the XML schema for a shipment notice sent by a warehouse when parcels leave the building.

#### Purpose
- Provides the structure for a `shipmentNotice` XML document.
- Validates shipment data such as carrier, recipient address, and parcel details.

#### Type
- XML Schema (`xs:schema`)
- Root element: `shipmentNotice`
- Namespaces:
  - `xs` → `http://www.w3.org/2001/XMLSchema`
  - `shp` → `urn:example:shipment:1`
- `targetNamespace`: `urn:example:shipment:1`
- `elementFormDefault`: `qualified`

#### Definitions
- **Simple types**
  - `CarrierCode`: enumeration of `DHL`, `UPS`, `GLS`, `POST`
  - `WeightGrams`: positive integer ≤ 31500
- **Complex types**
  - `ParcelType`
    - Sequence: `trackingNumber` (`xs:string`), `weight` (`shp:WeightGrams`)
    - Attribute: `sequence` (`xs:positiveInteger`, required)
  - `AddressType`
    - Sequence: `name`, `street`, `postalCode`, `city` (`xs:string`), `country` (`xs:string`, optional, default `DE`)
  - `ShipmentNoticeType`
    - Sequence: `orderReference` (`xs:string`), `shippedAt` (`xs:dateTime`), `carrier` (`shp:CarrierCode`), `recipient` (`shp:AddressType`), `parcel` (`shp:ParcelType`, unbounded)
    - Attribute: `noticeId` (`xs:ID`, required)
- **Root element**
  - `shipmentNotice` of type `shp:ShipmentNoticeType`

#### Configuration
- `country` default value: `DE`
- `WeightGrams` maximum inclusive: `31500`

#### Dependencies
- None (self‑contained schema).
