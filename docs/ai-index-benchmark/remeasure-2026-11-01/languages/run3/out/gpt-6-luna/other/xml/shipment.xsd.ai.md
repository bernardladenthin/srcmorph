### shipment.xsd
- H: 1.0
- C: E7230D69
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T14:56:24Z
- G: 
- A: 0.0.0
- X: file
---
> Defines the structure and validation rules for warehouse shipment notices, including recipient addresses and parcels.

#### Purpose
- Validates shipment notices sent when parcels leave a warehouse.
- Constrains carrier codes, parcel weights, required fields, and parcel sequencing.

#### Type
- XML Schema with root element `xs:schema`.
- Namespace `xs`: `http://www.w3.org/2001/XMLSchema`.
- Namespace `shp`: `urn:example:shipment:1`.
- Target namespace: `urn:example:shipment:1`.

#### Definitions
- Top-level element `shipmentNotice`: type `shp:ShipmentNoticeType`.
- Simple type `CarrierCode`: restricts `xs:string` to `DHL`, `UPS`, `GLS`, or `POST`.
- Simple type `WeightGrams`: restricts `xs:positiveInteger` to a maximum of `31500`.
- Complex type `ParcelType`: contains `trackingNumber` (`xs:string`) followed by `weight` (`shp:WeightGrams`); requires `sequence` (`xs:positiveInteger`) attribute.
- Complex type `AddressType`: contains `name`, `street`, `postalCode`, `city`, and optional `country` (`xs:string`).
- Complex type `ShipmentNoticeType`: contains `orderReference` (`xs:string`), `shippedAt` (`xs:dateTime`), `carrier` (`shp:CarrierCode`), `recipient` (`shp:AddressType`), and one or more `parcel` elements (`shp:ParcelType`); requires `noticeId` (`xs:ID`) attribute.

#### Configuration
- `elementFormDefault`: `qualified`.
- `country` element: `minOccurs="0"`, default `DE`.
- `parcel` element: `maxOccurs="unbounded"`.
- `sequence` attribute: `use="required"`.
- `noticeId` attribute: `use="required"`.
- `WeightGrams` maximum inclusive: `31500`.
- `CarrierCode` enumeration: `DHL`, `UPS`, `GLS`, `POST`.

#### Dependencies
- XML Schema built-in types: `xs:string`, `xs:positiveInteger`, `xs:dateTime`, `xs:ID`.
- Internal types: `shp:ShipmentNoticeType`, `shp:CarrierCode`, `shp:WeightGrams`, `shp:AddressType`, `shp:ParcelType`.
