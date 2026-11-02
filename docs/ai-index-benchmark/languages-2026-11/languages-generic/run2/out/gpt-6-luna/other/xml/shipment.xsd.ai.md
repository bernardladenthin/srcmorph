### shipment.xsd
- H: 1.0
- C: E7230D69
- D: 2026-10-31T21:26:46Z
- T: 2026-10-31T21:33:13Z
- G: 
- A: 0.0.0
- X: file
---
> Defines the required structure and validation rules for warehouse shipment notices, including recipient, carrier, and parcel details.

#### Purpose
- Validates shipment-notice XML sent when parcels leave a warehouse.
- Restricts carrier codes, parcel weights, required fields, and element order.

#### Type
- XML Schema (XSD); target namespace `urn:example:shipment:1`; elements are namespace-qualified.

#### Input
- XML instances in the `urn:example:shipment:1` namespace.
- `shipmentNotice` content: `orderReference`, `shippedAt`, `carrier`, `recipient`, and one or more `parcel` elements.

#### Output
- Defines the `shipmentNotice` root element and schema types for validating shipment-notice XML.
- No runtime output or side effects.

#### Core logic
- Requires shipment fields in sequence: order reference, shipment date/time, carrier, recipient, then parcels.
- Requires each parcel to contain a tracking number and positive weight up to 31,500 grams; its `sequence` attribute is required and positive.
- Requires recipient name, street, postal code, and city; country is optional and defaults to `DE`.
- Requires a shipment `noticeId` attribute of type `xs:ID`.

#### Public API
- `shipmentNotice` (`shp:ShipmentNoticeType`) — shipment-notice root element.
- `CarrierCode` — simple type restricting carrier values.
- `WeightGrams` — positive parcel weight in grams, capped at 31,500.
- `ParcelType` — parcel tracking number, weight, and sequence.
- `AddressType` — recipient address fields.
- `ShipmentNoticeType` — shipment details, recipient, parcels, and notice ID.

#### Constants and configuration
- `CarrierCode` allowed values: `DHL`, `UPS`, `GLS`, `POST`.
- `WeightGrams` maximum inclusive value: `31500`.
- `AddressType.country` default: `DE`.
- `ParcelType.sequence`: required, `xs:positiveInteger`.
- `ShipmentNoticeType.noticeId`: required, `xs:ID`.
- Parcel occurrence: `minOccurs` defaults to `1`; `maxOccurs="unbounded"`.

#### Dependencies
- XML Schema namespace: `http://www.w3.org/2001/XMLSchema`.
- Namespace prefix `shp` maps to `urn:example:shipment:1`.
- Built-in types: `xs:string`, `xs:positiveInteger`, `xs:dateTime`, `xs:ID`.

#### Exceptions / Errors
- XML instances are invalid if required fields or attributes are absent, elements are out of sequence, or values violate the declared types and restrictions.
