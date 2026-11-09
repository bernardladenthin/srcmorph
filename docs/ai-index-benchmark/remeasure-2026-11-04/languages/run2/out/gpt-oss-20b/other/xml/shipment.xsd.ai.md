### shipment.xsd
- H: 1.0
- C: E7230D69
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T08:03:04Z
- G: 
- A: 0.0.0
- X: file
- P: 326D0B29
---
> Defines the XML schema for a shipment notice sent by a warehouse when parcels leave the building.

#### Purpose
- Specifies the structure of a shipment notice XML document.
- Validates parcel details, carrier, recipient address, and shipment metadata.

#### Type
- XML Schema (`<xs:schema>`)  
- Root element: `shipmentNotice`  
- Namespace: `urn:example:shipment:1` (prefix `shp`)

#### Definitions
- **Simple types**
  - `CarrierCode`: enumeration of `DHL`, `UPS`, `GLS`, `POST`.
  - `WeightGrams`: positive integer ≤ 31500.
- **Complex types**
  - `ParcelType`
    - Elements: `trackingNumber` (`xs:string`), `weight` (`shp:WeightGrams`).
    - Attribute: `sequence` (`xs:positiveInteger`, required).
  - `AddressType`
    - Elements: `name`, `street`, `postalCode`, `city` (`xs:string`), `country` (`xs:string`, optional, default `DE`).
  - `ShipmentNoticeType`
    - Elements: `orderReference` (`xs:string`), `shippedAt` (`xs:dateTime`), `carrier` (`shp:CarrierCode`), `recipient` (`shp:AddressType`), `parcel` (`shp:ParcelType`, unbounded).
    - Attribute: `noticeId` (`xs:ID`, required).

#### Configuration
- `country` element in `AddressType` defaults to `DE` when omitted.

#### Dependencies
- None beyond standard XML Schema (`http://www.w3.org/2001/XMLSchema`).
