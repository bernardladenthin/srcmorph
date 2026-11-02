### shipment.xsd
- H: 1.0
- C: E7230D69
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T01:11:20Z
- G: 
- A: 0.0.0
- X: file
---
> Defines the warehouse shipment notice format, including carrier codes, recipient addresses, parcel details, and weight limits.

#### Purpose
- Validates shipment notices sent when parcels leave the warehouse.
- Constrains notice, address, carrier, and parcel data.

#### Type
- XML Schema with root element `schema`.
- Namespaces: `xs` → `http://www.w3.org/2001/XMLSchema`; `shp` → `urn:example:shipment:1`.
- Target namespace: `urn:example:shipment:1`.

#### Definitions
- Top-level element `shipmentNotice`: type `shp:ShipmentNoticeType`.
- Simple type `CarrierCode`: restriction of `xs:string`; enumeration values `DHL`, `UPS`, `GLS`, `POST`.
- Simple type `WeightGrams`: restriction of `xs:positiveInteger`; `maxInclusive` is `31500`.
- Complex type `ParcelType`: sequence of `trackingNumber` (`xs:string`) and `weight` (`shp:WeightGrams`); required `sequence` attribute of type `xs:positiveInteger`.
- Complex type `AddressType`: sequence of `name`, `street`, `postalCode`, and `city` (`xs:string`), followed by optional `country` (`xs:string`, default `DE`).
- Complex type `ShipmentNoticeType`: sequence of `orderReference` (`xs:string`), `shippedAt` (`xs:dateTime`), `carrier` (`shp:CarrierCode`), `recipient` (`shp:AddressType`), and one or more `parcel` elements (`shp:ParcelType`); required `noticeId` attribute of type `xs:ID`.

#### Configuration
- `elementFormDefault`: `qualified`.
- `WeightGrams` maximum inclusive value: `31500`.
- `AddressType.country`: `minOccurs="0"`, default `DE`.
- `ParcelType.sequence`: `use="required"`.
- `ShipmentNoticeType.parcel`: `maxOccurs="unbounded"`.
- `ShipmentNoticeType.noticeId`: `use="required"`.

#### Dependencies
- Schema namespace: `http://www.w3.org/2001/XMLSchema`.
- Referenced schema types: `xs:string`, `xs:positiveInteger`, `xs:dateTime`, `xs:ID`.
- Internal types: `shp:ShipmentNoticeType`, `shp:CarrierCode`, `shp:WeightGrams`, `shp:AddressType`, `shp:ParcelType`.
