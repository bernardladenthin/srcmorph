### shipment.xsd
- H: 1.0
- C: E7230D69
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T01:54:28Z
- G: 
- A: 0.0.0
- X: file
---
> Defines the shipment notice sent by a warehouse when parcels leave the building.

#### Purpose
- Validates shipment notices, including recipient address, carrier, shipment time, and one or more parcels.
- Constrains carrier codes, parcel weights, and required identifiers.

#### Type
- XML Schema; root element `schema`.
- Namespaces: `xs` = `http://www.w3.org/2001/XMLSchema`; `shp` = `urn:example:shipment:1`.
- Target namespace: `urn:example:shipment:1`; `elementFormDefault="qualified"`.

#### Definitions
- Top-level element `shipmentNotice`: type `shp:ShipmentNoticeType`.
- Simple type `CarrierCode`: restriction of `xs:string`; enumeration values `DHL`, `UPS`, `GLS`, `POST`.
- Simple type `WeightGrams`: restriction of `xs:positiveInteger`; maximum inclusive value `31500`.
- Complex type `ParcelType`: sequence of `trackingNumber` (`xs:string`) and `weight` (`shp:WeightGrams`); required attribute `sequence` (`xs:positiveInteger`).
- Complex type `AddressType`: sequence of `name`, `street`, `postalCode`, `city` (each `xs:string`), and optional `country` (`xs:string`, default `DE`).
- Complex type `ShipmentNoticeType`: sequence of `orderReference` (`xs:string`), `shippedAt` (`xs:dateTime`), `carrier` (`shp:CarrierCode`), `recipient` (`shp:AddressType`), and `parcel` (`shp:ParcelType`, unbounded); required attribute `noticeId` (`xs:ID`).

#### Configuration
- `elementFormDefault`: `qualified`.
- `country`: optional; default `DE`.
- `parcel`: `maxOccurs="unbounded"`.
- `sequence` and `noticeId`: required.
- `WeightGrams`: maximum inclusive `31500`.
- `CarrierCode` values: `DHL`, `UPS`, `GLS`, `POST`.

#### Dependencies
- XML Schema built-in types: `xs:string`, `xs:positiveInteger`, `xs:dateTime`, `xs:ID`.
- Schema namespace: `urn:example:shipment:1` (`shp`).
