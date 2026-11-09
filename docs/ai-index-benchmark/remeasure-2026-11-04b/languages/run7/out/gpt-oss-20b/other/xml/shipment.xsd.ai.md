### shipment.xsd
- H: 1.0
- C: E7230D69
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:46:45Z
- G: 
- A: 0.0.0
- X: file
- P: D9E787D0
---
> Defines the XML schema for a shipment notice sent by a warehouse when parcels leave the building.

#### Purpose
- Validates shipment notice documents.
- Provides type definitions for carrier codes, parcel details, addresses, and the overall notice structure.

#### Type
- XML Schema (`<xs:schema>`).
- Root element: `shipmentNotice`.
- Namespaces: `xs` (http://www.w3.org/2001/XMLSchema), `shp` (urn:example:shipment:1).

#### Definitions
- **Simple types**
  - `CarrierCode`: enumeration of `DHL`, `UPS`, `GLS`, `POST`.
  - `WeightGrams`: positive integer ≤ 31500.
- **Complex types**
  - `ParcelType`: sequence of `trackingNumber` (`xs:string`) and `weight` (`shp:WeightGrams`); required attribute `sequence` (`xs:positiveInteger`).
  - `AddressType`: sequence of `name`, `street`, `postalCode`, `city`, optional `country` (`xs:string`, default `DE`).
  - `ShipmentNoticeType`: sequence of `orderReference`, `shippedAt` (`xs:dateTime`), `carrier` (`shp:CarrierCode`), `recipient` (`shp:AddressType`), and one or more `parcel` (`shp:ParcelType`); required attribute `noticeId` (`xs:ID`).
- **Root element**
  - `shipmentNotice` of type `shp:ShipmentNoticeType`.

#### Configuration
- `country` attribute in `AddressType` defaults to `DE`.
- `WeightGrams` maximum inclusive value: `31500`.

#### Dependencies
- None beyond standard XML Schema namespace.
