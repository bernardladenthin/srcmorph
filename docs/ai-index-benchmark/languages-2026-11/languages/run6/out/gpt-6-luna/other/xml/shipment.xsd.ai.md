### shipment.xsd
- H: 1.0
- C: E7230D69
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T06:38:12Z
- G: 
- A: 0.0.0
- X: file
---
> Defines the structure and validation rules for warehouse shipment notices sent when parcels leave the building.

#### Purpose
- Validates shipment notices, including recipient details, carrier, shipment time, and one or more parcels.
- Constrains carrier codes, parcel weights, and required identifiers.

#### Type
- XML Schema; root element `schema`.
- Namespace `xs`: `http://www.w3.org/2001/XMLSchema`; namespace `shp`: `urn:example:shipment:1`.
- Target namespace: `urn:example:shipment:1`; `elementFormDefault="qualified"`.

#### Definitions
- Top-level element `shipmentNotice`: type `shp:ShipmentNoticeType`.
- Simple type `CarrierCode`: restriction of `xs:string`; enumeration values `DHL`, `UPS`, `GLS`, `POST`.
- Simple type `WeightGrams`: restriction of `xs:positiveInteger`; `maxInclusive` is `31500`.
- Complex type `ParcelType`: sequence of `trackingNumber` (`xs:string`) and `weight` (`shp:WeightGrams`); required attribute `sequence` (`xs:positiveInteger`).
- Complex type `AddressType`: sequence of `name`, `street`, `postalCode`, and `city` (`xs:string`), followed by optional `country` (`xs:string`, default `DE`).
- Complex type `ShipmentNoticeType`: sequence of `orderReference` (`xs:string`), `shippedAt` (`xs:dateTime`), `carrier` (`shp:CarrierCode`), `recipient` (`shp:AddressType`), and `parcel` (`shp:ParcelType`, unbounded); required attribute `noticeId` (`xs:ID`).

#### Configuration
- `elementFormDefault="qualified"`.
- `CarrierCode` allowed values: `DHL`, `UPS`, `GLS`, `POST`.
- `WeightGrams` maximum inclusive value: `31500`.
- `AddressType.country`: `minOccurs="0"`, `default="DE"`.
- `ShipmentNoticeType.parcel`: `maxOccurs="unbounded"`.
- `ParcelType.sequence` and `ShipmentNoticeType.noticeId`: `use="required"`.

#### Dependencies
- XML Schema built-in types: `xs:string`, `xs:positiveInteger`, `xs:dateTime`, `xs:ID`.
- Internal schema types: `shp:ShipmentNoticeType`, `shp:CarrierCode`, `shp:WeightGrams`, `shp:AddressType`, `shp:ParcelType`.
