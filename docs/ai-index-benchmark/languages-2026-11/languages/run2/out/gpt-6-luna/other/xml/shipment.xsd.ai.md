### shipment.xsd
- H: 1.0
- C: E7230D69
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T01:33:32Z
- G: 
- A: 0.0.0
- X: file
---
> Defines the structure and validation rules for warehouse shipment notices sent when parcels leave the building.

#### Purpose
- Validates shipment notice records, recipient addresses, and parcel details.
- Constrains carrier codes, parcel weights, and required identifiers.

#### Type
- XML Schema; root element `schema`.
- Namespace `xs`: `http://www.w3.org/2001/XMLSchema`.
- Namespace `shp`: `urn:example:shipment:1`.
- Target namespace: `urn:example:shipment:1`; `elementFormDefault="qualified"`.

#### Definitions
- Top-level element `shipmentNotice`: type `shp:ShipmentNoticeType`.
- Simple type `CarrierCode`: restriction of `xs:string`; enumerations `DHL`, `UPS`, `GLS`, `POST`.
- Simple type `WeightGrams`: restriction of `xs:positiveInteger`; `maxInclusive` is `31500`.
- Complex type `ParcelType`: sequence of `trackingNumber` (`xs:string`) and `weight` (`shp:WeightGrams`); required `sequence` attribute of type `xs:positiveInteger`.
- Complex type `AddressType`: sequence of `name`, `street`, `postalCode`, `city`, and optional `country` (all `xs:string`); `country` defaults to `DE`.
- Complex type `ShipmentNoticeType`: sequence of `orderReference` (`xs:string`), `shippedAt` (`xs:dateTime`), `carrier` (`shp:CarrierCode`), `recipient` (`shp:AddressType`), and one or more `parcel` elements (`shp:ParcelType`); required `noticeId` attribute of type `xs:ID`.

#### Configuration
- `elementFormDefault`: `qualified`.
- `CarrierCode` allowed values: `DHL`, `UPS`, `GLS`, `POST`.
- `WeightGrams` maximum: `31500` inclusive; base type `xs:positiveInteger`.
- `ParcelType.sequence`: required.
- `AddressType.country`: optional; default `DE`.
- `ShipmentNoticeType.parcel`: `maxOccurs="unbounded"`.
- `ShipmentNoticeType.noticeId`: required.

#### Dependencies
- XML Schema built-in types: `xs:string`, `xs:positiveInteger`, `xs:dateTime`, `xs:ID`.
- Schema-defined types: `shp:ShipmentNoticeType`, `shp:CarrierCode`, `shp:WeightGrams`, `shp:AddressType`, `shp:ParcelType`.
