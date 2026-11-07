### shipment.xsd
- H: 1.0
- C: E7230D69
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:13:33Z
- G: 
- A: 0.0.0
- X: file
- P: D9E787D0
---
> Defines the warehouse shipment notice format, including recipient details, carrier codes, parcel tracking and weight limits.

#### Purpose
- Validates shipment notices sent when parcels leave a warehouse.
- Constrains carrier, parcel weight, address, and shipment metadata.

#### Type
- XML Schema; root element `schema`.
- Namespaces: `xs` = `http://www.w3.org/2001/XMLSchema`; `shp` = `urn:example:shipment:1`.
- Target namespace: `urn:example:shipment:1`; `elementFormDefault="qualified"`.

#### Definitions
- Top-level element: `shipmentNotice` of type `shp:ShipmentNoticeType`.
- Simple types:
  - `CarrierCode`: `xs:string` enumeration `DHL`, `UPS`, `GLS`, `POST`.
  - `WeightGrams`: `xs:positiveInteger`, maximum inclusive `31500`.
- Complex types:
  - `ParcelType`: ordered `trackingNumber` (`xs:string`), `weight` (`shp:WeightGrams`); required `sequence` attribute (`xs:positiveInteger`).
  - `AddressType`: ordered `name`, `street`, `postalCode`, `city` (`xs:string`) and optional `country` (`xs:string`, default `DE`).
  - `ShipmentNoticeType`: ordered `orderReference` (`xs:string`), `shippedAt` (`xs:dateTime`), `carrier` (`shp:CarrierCode`), `recipient` (`shp:AddressType`), and one or more `parcel` elements (`shp:ParcelType`); required `noticeId` attribute (`xs:ID`).

#### Configuration
- `country` default: `DE`.
- `weight` maximum: `31500` grams.
- `parcel` has no specified upper occurrence limit.

#### Dependencies
- References XML Schema built-in types: `xs:string`, `xs:positiveInteger`, `xs:dateTime`, and `xs:ID`.
