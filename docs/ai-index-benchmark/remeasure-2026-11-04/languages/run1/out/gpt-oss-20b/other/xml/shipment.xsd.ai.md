### shipment.xsd
- H: 1.0
- C: E7230D69
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T07:59:57Z
- G: 
- A: 0.0.0
- X: file
- P: 326D0B29
---
> Defines the XML schema for a shipment notice sent by a warehouse when parcels leave the building.

#### Purpose
- Validates shipment notice documents.
- Specifies parcel, carrier, and recipient details.

#### Type
- XML Schema (`<xs:schema>`)  
- Root element: `shipmentNotice`  
- Namespace: `urn:example:shipment:1` (prefix `shp`)

#### Definitions
- **SimpleTypes**
  - `CarrierCode`: enumeration of `DHL`, `UPS`, `GLS`, `POST`.
  - `WeightGrams`: positive integer ≤ 31500.
- **ComplexTypes**
  - `ParcelType`: sequence of `trackingNumber` (`xs:string`) and `weight` (`shp:WeightGrams`); required attribute `sequence` (`xs:positiveInteger`).
  - `AddressType`: sequence of `name`, `street`, `postalCode`, `city`, optional `country` (`xs:string`, default `DE`).
  - `ShipmentNoticeType`: sequence of `orderReference`, `shippedAt` (`xs:dateTime`), `carrier` (`shp:CarrierCode`), `recipient` (`shp:AddressType`), and one or more `parcel` (`shp:ParcelType`); required attribute `noticeId` (`xs:ID`).
- **Element**
  - `shipmentNotice`: type `shp:ShipmentNoticeType`.

#### Configuration
- `AddressType.country` default value: `DE`.

#### Dependencies
- None.
