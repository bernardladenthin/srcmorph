### shipment.xsd
- H: 1.0
- C: E7230D69
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:17:01Z
- G: 
- A: 0.0.0
- X: file
- P: D9E787D0
---
> Defines the warehouse shipment notice exchanged when parcels leave the building, including carrier, recipient, and parcel data constraints.

#### Purpose
- Validates shipment notices containing order and dispatch details, recipient address, and one or more parcels.
- Constrains carrier codes, parcel weights, and required identifiers.

#### Type
- XML Schema; root `schema`.
- Namespaces: `xs` = `http://www.w3.org/2001/XMLSchema`; `shp` = `urn:example:shipment:1`.
- Target namespace: `urn:example:shipment:1`; `elementFormDefault="qualified"`.

#### Definitions
- Top-level element: `shipmentNotice` of type `shp:ShipmentNoticeType`.
- Simple types:
  - `CarrierCode`: restriction of `xs:string`; enumeration values `DHL`, `UPS`, `GLS`, `POST`.
  - `WeightGrams`: restriction of `xs:positiveInteger`; maximum inclusive `31500`.
- Complex types:
  - `ParcelType`: sequence of `trackingNumber` (`xs:string`), `weight` (`shp:WeightGrams`); required `sequence` attribute of type `xs:positiveInteger`.
  - `AddressType`: sequence of `name`, `street`, `postalCode`, `city`, and optional `country` (all `xs:string`; `country` defaults to `DE`).
  - `ShipmentNoticeType`: sequence of `orderReference` (`xs:string`), `shippedAt` (`xs:dateTime`), `carrier` (`shp:CarrierCode`), `recipient` (`shp:AddressType`), and one or more `parcel` (`shp:ParcelType`); required `noticeId` attribute of type `xs:ID`.

#### Configuration
- `WeightGrams` maximum: `31500` grams, inclusive.
- `AddressType.country`: optional; default `DE`.
- `ShipmentNoticeType.noticeId` and `ParcelType.sequence`: required attributes.
- `ShipmentNoticeType.parcel`: minimum occurrence defaults to `1`; maximum is `unbounded`.

#### Dependencies
- Uses XML Schema built-in types `xs:string`, `xs:positiveInteger`, `xs:dateTime`, and `xs:ID`.
