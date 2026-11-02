### shipment.xsd
- H: 1.0
- C: E7230D69
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T14:39:20Z
- G: 
- A: 0.0.0
- X: file
---
> Defines the warehouse shipment notice format, including recipient details, carrier codes, parcel weights, and tracking identifiers.

#### Purpose
- Validates shipment notices sent when parcels leave a warehouse.
- Constrains carrier, parcel, address, and shipment notice data.

#### Type
- XML Schema; root element `xs:schema`.
- Target namespace: `urn:example:shipment:1`.
- Namespaces: `xs` → `http://www.w3.org/2001/XMLSchema`; `shp` → `urn:example:shipment:1`.

#### Definitions
- Top-level element: `shipmentNotice`, type `shp:ShipmentNoticeType`.
- `CarrierCode` simple type: `xs:string` enumeration `DHL`, `UPS`, `GLS`, `POST`.
- `WeightGrams` simple type: `xs:positiveInteger`, maximum inclusive `31500`.
- `ParcelType` complex type:
  - Sequence: `trackingNumber` (`xs:string`), `weight` (`shp:WeightGrams`).
  - Required attribute `sequence` (`xs:positiveInteger`).
- `AddressType` complex type:
  - Sequence: `name`, `street`, `postalCode`, `city` (`xs:string`); optional `country` (`xs:string`), default `DE`.
- `ShipmentNoticeType` complex type:
  - Sequence: `orderReference` (`xs:string`), `shippedAt` (`xs:dateTime`), `carrier` (`shp:CarrierCode`), `recipient` (`shp:AddressType`), one or more `parcel` (`shp:ParcelType`).
  - Required attribute `noticeId` (`xs:ID`).

#### Configuration
- `elementFormDefault`: `qualified`.
- `country` default: `DE`; `minOccurs`: `0`.
- `weight` limit: maximum inclusive `31500`.
- `parcel` `maxOccurs`: `unbounded`.
- Required attributes: `sequence`, `noticeId`.

#### Dependencies
- XML Schema built-in types: `xs:string`, `xs:positiveInteger`, `xs:dateTime`, `xs:ID`.
- Internal schema types: `shp:ShipmentNoticeType`, `shp:CarrierCode`, `shp:WeightGrams`, `shp:AddressType`, `shp:ParcelType`.
