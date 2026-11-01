### shipment.xsd
- H: 1.0
- C: E7230D69
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T14:16:21Z
- G: 
- A: 0.0.0
- X: file
---
> Defines the structure and validation rules for warehouse shipment notices, including recipient addresses and parcel details.

#### Purpose
- Validates shipment notices sent when parcels leave a warehouse.
- Constrains carrier codes, parcel weights, required fields, and parcel sequencing.

#### Type
- XML Schema; root element `schema`.
- Target namespace: `urn:example:shipment:1`.
- Namespaces: `xs` = `http://www.w3.org/2001/XMLSchema`; `shp` = `urn:example:shipment:1`.
- `elementFormDefault="qualified"`.

#### Definitions
- Top-level element `shipmentNotice`: type `shp:ShipmentNoticeType`.
- Simple type `CarrierCode`: `xs:string` enumeration: `DHL`, `UPS`, `GLS`, `POST`.
- Simple type `WeightGrams`: `xs:positiveInteger`, maximum inclusive `31500`.
- Complex type `ParcelType`: ordered elements `trackingNumber` (`xs:string`), `weight` (`shp:WeightGrams`); required `sequence` attribute (`xs:positiveInteger`).
- Complex type `AddressType`: ordered elements `name`, `street`, `postalCode`, `city` (`xs:string`), then optional `country` (`xs:string`, default `DE`).
- Complex type `ShipmentNoticeType`: ordered elements `orderReference` (`xs:string`), `shippedAt` (`xs:dateTime`), `carrier` (`shp:CarrierCode`), `recipient` (`shp:AddressType`), one or more `parcel` elements (`shp:ParcelType`); required `noticeId` attribute (`xs:ID`).

#### Configuration
- `elementFormDefault`: `qualified`.
- `CarrierCode` allowed values: `DHL`, `UPS`, `GLS`, `POST`.
- `WeightGrams`: positive integer, maximum inclusive `31500`.
- `AddressType.country`: minimum occurrences `0`; default `DE`.
- `ShipmentNoticeType.parcel`: maximum occurrences `unbounded`.
- `ParcelType.sequence` and `ShipmentNoticeType.noticeId`: required.

#### Dependencies
- XML Schema built-in types: `xs:string`, `xs:positiveInteger`, `xs:dateTime`, `xs:ID`.
- Internal schema types: `shp:ShipmentNoticeType`, `shp:CarrierCode`, `shp:WeightGrams`, `shp:AddressType`, `shp:ParcelType`.
