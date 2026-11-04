### shipment.xsd
- H: 1.0
- C: E7230D69
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:20:24Z
- G: 
- A: 0.0.0
- X: file
- P: D9E787D0
---
> Defines the structure and validation rules for warehouse shipment notices, including carrier, recipient, and parcel details.

#### Purpose
- Validates shipment notices sent when parcels leave a warehouse.

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
  - `AddressType`: ordered `name`, `street`, `postalCode`, `city`, `country` (all `xs:string`); `country` is optional.
  - `ShipmentNoticeType`: ordered `orderReference` (`xs:string`), `shippedAt` (`xs:dateTime`), `carrier` (`shp:CarrierCode`), `recipient` (`shp:AddressType`), one or more `parcel` (`shp:ParcelType`); required `noticeId` attribute (`xs:ID`).

#### Configuration
- `WeightGrams`: values must be positive integers no greater than `31500`.
- `AddressType.country`: default `DE`; optional.
- `ShipmentNoticeType.parcel`: `maxOccurs="unbounded"`.

#### Dependencies
- References XML Schema built-in types: `xs:string`, `xs:positiveInteger`, `xs:dateTime`, `xs:ID`.
