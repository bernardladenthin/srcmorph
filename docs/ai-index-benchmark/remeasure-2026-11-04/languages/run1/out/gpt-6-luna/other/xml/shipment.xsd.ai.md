### shipment.xsd
- H: 1.0
- C: E7230D69
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T07:58:34Z
- G: 
- A: 0.0.0
- X: file
- P: 326D0B29
---
> Defines the warehouse shipment notice format, including recipient details, carrier codes, parcel tracking, and weight limits.

#### Purpose
- Validates shipment notices sent when parcels leave the warehouse.
- Constrains carrier values, parcel weights, required fields, and recipient address data.

#### Type
- XML Schema; root element `schema`.
- Namespaces: `xs` → `http://www.w3.org/2001/XMLSchema`; `shp` → `urn:example:shipment:1`.
- Target namespace: `urn:example:shipment:1`.

#### Definitions
- Top-level element `shipmentNotice`: type `shp:ShipmentNoticeType`.
- Simple type `CarrierCode`: `xs:string` enumeration `DHL`, `UPS`, `GLS`, `POST`.
- Simple type `WeightGrams`: `xs:positiveInteger`, maximum inclusive `31500`.
- Complex type `ParcelType`: ordered elements `trackingNumber` (`xs:string`), `weight` (`shp:WeightGrams`); required attribute `sequence` (`xs:positiveInteger`).
- Complex type `AddressType`: ordered elements `name`, `street`, `postalCode`, `city` (`xs:string`), and optional `country` (`xs:string`, default `DE`).
- Complex type `ShipmentNoticeType`: ordered elements `orderReference` (`xs:string`), `shippedAt` (`xs:dateTime`), `carrier` (`shp:CarrierCode`), `recipient` (`shp:AddressType`), and one-or-more `parcel` (`shp:ParcelType`); required attribute `noticeId` (`xs:ID`).

#### Configuration
- `elementFormDefault`: `qualified`.
- `AddressType.country`: `minOccurs=0`, default `DE`.
- `ShipmentNoticeType.parcel`: `maxOccurs=unbounded`.
- `ParcelType.sequence` and `ShipmentNoticeType.noticeId`: required.

#### Dependencies
- XML Schema built-in types: `xs:string`, `xs:positiveInteger`, `xs:dateTime`, `xs:ID`.
- Internal schema types: `shp:ShipmentNoticeType`, `shp:CarrierCode`, `shp:WeightGrams`, `shp:AddressType`, `shp:ParcelType`.
