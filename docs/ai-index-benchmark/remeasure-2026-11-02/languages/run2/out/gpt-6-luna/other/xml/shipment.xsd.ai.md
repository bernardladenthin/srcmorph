### shipment.xsd
- H: 1.0
- C: E7230D69
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T23:40:46Z
- G: 
- A: 0.0.0
- X: file
- P: 144CA22B
---
> Defines the XML contract for warehouse shipment notices, including recipient details, carrier codes, parcel tracking, and weight limits.

#### Purpose
- Validates shipment notices sent when parcels leave a warehouse.
- Constrains carrier, parcel weight, required fields, and recipient address data.

#### Type
- XML Schema; root element `schema`.
- Declares `xs` = `http://www.w3.org/2001/XMLSchema` and `shp` = `urn:example:shipment:1`; target namespace: `urn:example:shipment:1`.

#### Definitions
- Top-level element: `shipmentNotice`, typed as `shp:ShipmentNoticeType`.
- Simple types:
  - `CarrierCode`: `xs:string` enumeration `DHL`, `UPS`, `GLS`, `POST`.
  - `WeightGrams`: `xs:positiveInteger` with `maxInclusive` `31500`.
- Complex types:
  - `ParcelType`: `trackingNumber` (`xs:string`), `weight` (`shp:WeightGrams`); required `sequence` attribute (`xs:positiveInteger`).
  - `AddressType`: `name`, `street`, `postalCode`, `city`, and optional `country` (all `xs:string`); `country` defaults to `DE`.
  - `ShipmentNoticeType`: `orderReference` (`xs:string`), `shippedAt` (`xs:dateTime`), `carrier` (`shp:CarrierCode`), `recipient` (`shp:AddressType`), and one or more `parcel` elements (`shp:ParcelType`); required `noticeId` attribute (`xs:ID`).

#### Configuration
- `elementFormDefault`: `qualified`.
- `country` default: `DE`.
- `WeightGrams` maximum: `31500`; values must be positive integers.
- `parcel` has no maximum occurrence limit.

#### Dependencies
- XML Schema built-in types: `xs:string`, `xs:positiveInteger`, `xs:dateTime`, `xs:ID`.
- Namespace reference: `urn:example:shipment:1`.
