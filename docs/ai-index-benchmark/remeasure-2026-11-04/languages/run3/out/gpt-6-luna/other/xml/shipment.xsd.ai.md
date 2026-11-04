### shipment.xsd
- H: 1.0
- C: E7230D69
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T08:04:41Z
- G: 
- A: 0.0.0
- X: file
- P: 326D0B29
---
> Defines the warehouse shipment notice format, including recipient details, carrier codes, parcel tracking, and weight limits.

#### Purpose
- Validates shipment notices sent when parcels leave a warehouse.
- Specifies required shipment and parcel data, with optional country defaulting to Germany.

#### Type
- XML Schema; root element `schema`.
- Namespaces: `xs` = `http://www.w3.org/2001/XMLSchema`; `shp` = `urn:example:shipment:1`.
- Target namespace: `urn:example:shipment:1`.

#### Definitions
- Top-level element: `shipmentNotice` of type `shp:ShipmentNoticeType`.
- Simple types:
  - `CarrierCode`: enumeration of `DHL`, `UPS`, `GLS`, `POST`.
  - `WeightGrams`: positive integer, maximum inclusive `31500`.
- Complex types:
  - `ParcelType`: ordered `trackingNumber` (`xs:string`), `weight` (`shp:WeightGrams`); required `sequence` attribute (`xs:positiveInteger`).
  - `AddressType`: ordered `name`, `street`, `postalCode`, `city`, `country` (all `xs:string`); `country` is optional, default `DE`.
  - `ShipmentNoticeType`: ordered `orderReference` (`xs:string`), `shippedAt` (`xs:dateTime`), `carrier` (`shp:CarrierCode`), `recipient` (`shp:AddressType`), one or more `parcel` (`shp:ParcelType`); required `noticeId` attribute (`xs:ID`).

#### Configuration
- `elementFormDefault`: `qualified`.
- `country`: `minOccurs="0"`; default `DE`.
- `parcel`: `maxOccurs="unbounded"` (minimum occurrence defaults to one).
- `CarrierCode` allowed values: `DHL`, `UPS`, `GLS`, `POST`.
- `WeightGrams`: positive integer with `maxInclusive="31500"`.

#### Dependencies
- XML Schema built-in types: `xs:string`, `xs:positiveInteger`, `xs:dateTime`, `xs:ID`.
- Internal type references: `shp:ShipmentNoticeType`, `shp:CarrierCode`, `shp:WeightGrams`, `shp:AddressType`, `shp:ParcelType`.
