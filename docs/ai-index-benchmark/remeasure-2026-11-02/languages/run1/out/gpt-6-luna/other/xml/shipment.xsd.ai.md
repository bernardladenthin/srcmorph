### shipment.xsd
- H: 1.0
- C: E7230D69
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T23:24:20Z
- G: 
- A: 0.0.0
- X: file
- P: 144CA22B
---
> Defines the structure and validation rules for warehouse shipment notices, including recipient details, carrier, shipment timing, and parcel information.

#### Purpose
- Validates shipment notices sent when parcels leave a warehouse.
- Constrains carrier codes, parcel weights, required fields, and parcel sequencing.

#### Type
- XML Schema with root element `xs:schema`.
- Namespaces: `xs` → `http://www.w3.org/2001/XMLSchema`; `shp` → `urn:example:shipment:1`.
- Target namespace: `urn:example:shipment:1`; `elementFormDefault="qualified"`.

#### Definitions
- Top-level element: `shipmentNotice` of type `shp:ShipmentNoticeType`.
- Simple types:
  - `CarrierCode`: enumeration values `DHL`, `UPS`, `GLS`, `POST`.
  - `WeightGrams`: `xs:positiveInteger`, maximum inclusive `31500`.
- Complex types:
  - `ParcelType`: ordered `trackingNumber` (`xs:string`), `weight` (`shp:WeightGrams`); required `sequence` attribute (`xs:positiveInteger`).
  - `AddressType`: ordered `name`, `street`, `postalCode`, `city`, `country` (all `xs:string`); `country` is optional, default `DE`.
  - `ShipmentNoticeType`: ordered `orderReference` (`xs:string`), `shippedAt` (`xs:dateTime`), `carrier` (`shp:CarrierCode`), `recipient` (`shp:AddressType`), one or more `parcel` elements (`shp:ParcelType`); required `noticeId` attribute (`xs:ID`).

#### Configuration
- `elementFormDefault`: `qualified`.
- `WeightGrams` maximum: `31500`.
- `AddressType.country`: `minOccurs="0"`, default `DE`.
- `ShipmentNoticeType.parcel`: `maxOccurs="unbounded"`.
- Required attributes: `ParcelType.sequence`, `ShipmentNoticeType.noticeId`.

#### Dependencies
- XML Schema built-in types: `xs:string`, `xs:positiveInteger`, `xs:dateTime`, `xs:ID`.
- Internal type references: `shp:ShipmentNoticeType`, `shp:CarrierCode`, `shp:WeightGrams`, `shp:AddressType`, `shp:ParcelType`.
