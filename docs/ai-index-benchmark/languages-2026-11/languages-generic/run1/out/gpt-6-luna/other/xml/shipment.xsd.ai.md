### shipment.xsd
- H: 1.0
- C: E7230D69
- D: 2026-10-31T21:26:46Z
- T: 2026-10-31T21:28:33Z
- G: 
- A: 0.0.0
- X: file
---
> Validates warehouse shipment notices containing carrier, recipient, and parcel details before parcels leave the building.

#### Purpose
- Defines the XML structure and validation rules for shipment notices in the `urn:example:shipment:1` namespace.
- Requires shipment and parcel details while constraining carrier codes and parcel weights.

#### Type
- XML Schema (XSD); declares global element `shipmentNotice` and simple and complex types.

#### Input
- XML elements in target namespace `urn:example:shipment:1`.
- `shipmentNotice` content: order reference, shipment timestamp, carrier, recipient address, and one or more parcels.
- Each parcel supplies a tracking number, weight, and required sequence attribute.

#### Output
- Defines the structure and constraints used to validate shipment-notice XML; produces no data or side effects.

#### Core logic
- `shipmentNotice` uses `ShipmentNoticeType`; its required `noticeId` attribute is an XML ID.
- `ShipmentNoticeType` requires elements in order: `orderReference`, `shippedAt`, `carrier`, `recipient`, then one or more `parcel` elements.
- `CarrierCode` restricts carrier values to `DHL`, `UPS`, `GLS`, or `POST`.
- `WeightGrams` requires a positive integer no greater than `31500`.
- `ParcelType` requires `trackingNumber` followed by `weight`, plus a required positive-integer `sequence` attribute.
- `AddressType` requires `name`, `street`, `postalCode`, and `city`; optional `country` defaults to `DE`.

#### Public API
- `shipmentNotice` — global XML element typed as `ShipmentNoticeType`.
- `CarrierCode` — simple type restricting allowed carrier strings.
- `WeightGrams` — simple type restricting parcel weight.
- `ParcelType` — complex type for tracking number, weight, and sequence.
- `AddressType` — complex type for recipient address.
- `ShipmentNoticeType` — complex type for shipment notice content and ID.

#### Constants and configuration
- Namespace: `urn:example:shipment:1`; element form is `qualified`.
- `CarrierCode` values: `DHL`, `UPS`, `GLS`, `POST`.
- `WeightGrams` maximum inclusive value: `31500`.
- `AddressType.country`: optional; default `DE`.
- `ShipmentNoticeType.noticeId`: required.
- `ParcelType.sequence`: required.
- `ShipmentNoticeType.parcel`: `maxOccurs="unbounded"`; minimum occurrence defaults to 1.

#### Dependencies
- XML Schema namespace: `http://www.w3.org/2001/XMLSchema`.
- Built-in types: `xs:string`, `xs:positiveInteger`, `xs:dateTime`, `xs:ID`.

#### Exceptions / Errors
- XML validation fails for missing required elements or attributes, elements in the wrong sequence, or invalid types.
- Validation fails for carrier values outside the enumeration, non-positive weights, weights above `31500`, or non-positive parcel sequence values.
