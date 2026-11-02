### shipment.xsd
- H: 1.0
- C: E7230D69
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T06:34:37Z
- G: 
- A: 0.0.0
- X: file
---
> Defines the structure and validation rules for warehouse shipment notices, including recipients, carriers, and parcels.

#### Purpose
- Validates shipment notices sent when parcels leave the warehouse.
- Constrains carrier codes, parcel weights, addresses, and required shipment data.

#### Type
- XML Schema; root element: `xs:schema`.
- Namespaces: `xs` → `http://www.w3.org/2001/XMLSchema`; `shp` → `urn:example:shipment:1`.
- Target namespace: `urn:example:shipment:1`.

#### Definitions
- Top-level element `shipmentNotice`: type `shp:ShipmentNoticeType`.
- Simple type `CarrierCode`: `xs:string` enumeration: `DHL`, `UPS`, `GLS`, `POST`.
- Simple type `WeightGrams`: `xs:positiveInteger`, maximum inclusive `31500`.
- Complex type `ParcelType`: sequence of `trackingNumber` (`xs:string`) and `weight` (`shp:WeightGrams`); required `sequence` attribute (`xs:positiveInteger`).
- Complex type `AddressType`: sequence of `name`, `street`, `postalCode`, `city` (`xs:string`), then optional `country` (`xs:string`, default `DE`).
- Complex type `ShipmentNoticeType`: sequence of `orderReference` (`xs:string`), `shippedAt` (`xs:dateTime`), `carrier` (`shp:CarrierCode`), `recipient` (`shp:AddressType`), and one or more `parcel` (`shp:ParcelType`); required `noticeId` attribute (`xs:ID`).

#### Configuration
- `elementFormDefault`: `qualified`.
- `AddressType.country`: `minOccurs="0"`, default `DE`.
- `ShipmentNoticeType.parcel`: `maxOccurs="unbounded"`.
- `ShipmentNoticeType.noticeId` and `ParcelType.sequence`: `use="required"`.
- `WeightGrams`: `maxInclusive="31500"`.

#### Dependencies
- XML Schema built-in namespace `http://www.w3.org/2001/XMLSchema`: `xs:string`, `xs:positiveInteger`, `xs:dateTime`, `xs:ID`.
- Internal types: `shp:ShipmentNoticeType`, `shp:CarrierCode`, `shp:WeightGrams`, `shp:AddressType`, `shp:ParcelType`.
