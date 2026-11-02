### shipment.xsd
- H: 1.0
- C: E7230D69
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T23:55:17Z
- G: 
- A: 0.0.0
- X: file
- P: 144CA22B
---
> Defines the shipment notice contract used by warehouses to report dispatched parcels, carrier, recipient, and parcel weights.

#### Purpose
- Validates warehouse shipment notices, including recipient address details and one or more parcels.
- Restricts carrier codes and parcel weight values.

#### Type
- XML Schema; root element `xs:schema`.
- Namespaces: `xs` → `http://www.w3.org/2001/XMLSchema`; `shp` → `urn:example:shipment:1`.
- Target namespace: `urn:example:shipment:1`.

#### Definitions
- Top-level element: `shipmentNotice` of type `shp:ShipmentNoticeType`.
- Simple types:
  - `CarrierCode`: string enumeration `DHL`, `UPS`, `GLS`, `POST`.
  - `WeightGrams`: `xs:positiveInteger`, maximum inclusive `31500`.
- Complex types:
  - `ParcelType`: sequence of `trackingNumber` (`xs:string`) and `weight` (`shp:WeightGrams`); required `sequence` attribute (`xs:positiveInteger`).
  - `AddressType`: sequence of `name`, `street`, `postalCode`, `city`, and optional `country` (all `xs:string`); `country` defaults to `DE`.
  - `ShipmentNoticeType`: sequence of `orderReference` (`xs:string`), `shippedAt` (`xs:dateTime`), `carrier` (`shp:CarrierCode`), `recipient` (`shp:AddressType`), and one or more `parcel` (`shp:ParcelType`); required `noticeId` attribute (`xs:ID`).

#### Configuration
- `elementFormDefault`: `qualified`.
- `CarrierCode` values: `DHL`, `UPS`, `GLS`, `POST`.
- `WeightGrams` maximum: `31500` inclusive; base type requires a positive integer.
- `country` is optional (`minOccurs="0"`) and defaults to `DE`.
- `parcel` has `maxOccurs="unbounded"`; its minimum occurrence is the default of one.
- Required attributes: `ParcelType` `sequence`; `ShipmentNoticeType` `noticeId`.

#### Dependencies
- XML Schema built-in types: `xs:string`, `xs:positiveInteger`, `xs:dateTime`, `xs:ID`.
- Internal schema types: `shp:ShipmentNoticeType`, `shp:CarrierCode`, `shp:WeightGrams`, `shp:AddressType`, `shp:ParcelType`.
