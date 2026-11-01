### shipment.xsd
- H: 1.0
- C: E7230D69
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T06:27:49Z
- G: 
- A: 0.0.0
- X: file
---
> Defines the shipment notice data contract for warehouse dispatches, including recipient details, carrier codes, parcel tracking, and weight limits.

#### Purpose
- Validates shipment notices sent when parcels leave a warehouse.
- Defines required notice, recipient, carrier, and parcel data.

#### Type
- XML Schema; root element `schema`.
- Target namespace: `urn:example:shipment:1`.
- Namespaces: `xs` = `http://www.w3.org/2001/XMLSchema`; `shp` = `urn:example:shipment:1`.

#### Definitions
- Top-level element `shipmentNotice`: type `shp:ShipmentNoticeType`.
- `CarrierCode` (`simpleType`): `xs:string` enumeration: `DHL`, `UPS`, `GLS`, `POST`.
- `WeightGrams` (`simpleType`): `xs:positiveInteger`, maximum inclusive `31500`.
- `ParcelType` (`complexType`): ordered `trackingNumber` (`xs:string`), `weight` (`shp:WeightGrams`); required `sequence` attribute (`xs:positiveInteger`).
- `AddressType` (`complexType`): ordered `name`, `street`, `postalCode`, `city`, `country` elements; all `xs:string`, with `country` optional and default `DE`.
- `ShipmentNoticeType` (`complexType`): ordered `orderReference` (`xs:string`), `shippedAt` (`xs:dateTime`), `carrier` (`shp:CarrierCode`), `recipient` (`shp:AddressType`), one or more `parcel` elements (`shp:ParcelType`); required `noticeId` attribute (`xs:ID`).

#### Configuration
- `elementFormDefault`: `qualified`.
- `shipmentNotice` uses `shp:ShipmentNoticeType`.
- `CarrierCode` allowed values: `DHL`, `UPS`, `GLS`, `POST`.
- `WeightGrams` maximum: `31500`; base type `xs:positiveInteger`.
- `ParcelType.sequence`: required.
- `AddressType.country`: `minOccurs="0"`, default `DE`.
- `ShipmentNoticeType.parcel`: `maxOccurs="unbounded"`.
- `ShipmentNoticeType.noticeId`: required.

#### Dependencies
- Schema namespace: `http://www.w3.org/2001/XMLSchema`.
- Internal types: `shp:ShipmentNoticeType`, `shp:CarrierCode`, `shp:WeightGrams`, `shp:AddressType`, `shp:ParcelType`.
- Built-in types: `xs:string`, `xs:positiveInteger`, `xs:dateTime`, `xs:ID`.
