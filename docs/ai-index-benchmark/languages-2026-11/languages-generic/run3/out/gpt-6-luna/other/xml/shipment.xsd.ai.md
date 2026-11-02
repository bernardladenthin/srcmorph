### shipment.xsd
- H: 1.0
- C: E7230D69
- D: 2026-10-31T21:26:46Z
- T: 2026-10-31T21:39:19Z
- G: 
- A: 0.0.0
- X: file
---
> Validates warehouse shipment notices containing order, dispatch, recipient, carrier, and parcel details.

#### Purpose
- Defines the XML structure and validation constraints for shipment notices sent when parcels leave a warehouse.
- Places shipment elements in the `urn:example:shipment:1` namespace.

#### Type
- XML Schema (XSD), with global element `shipmentNotice` and named simple and complex types.
- `elementFormDefault="qualified"`.

#### Input
- XML shipment notice with an `xs:ID` notice identifier, order reference, dispatch timestamp, carrier, recipient address, and one or more parcels.
- Parcel data includes a tracking number, positive weight in grams, and required positive sequence number.

#### Output
- Validation of XML structure, element order, required attributes, and declared value constraints; no runtime output or side effects.

#### Core logic
- Defines `ShipmentNoticeType` sequence: `orderReference`, `shippedAt`, `carrier`, `recipient`, then one or more `parcel` elements.
- Requires `noticeId` on the shipment notice and `sequence` on each parcel.
- Restricts carrier codes to four enumerated values.
- Restricts parcel weight to positive integers no greater than 31,500 grams.
- Defines recipient address sequence with required name, street, postal code, and city; country is optional and defaults to `DE`.

#### Public API
- `shipmentNotice` — global XML element of type `ShipmentNoticeType`.
- `CarrierCode` — simple type restricting carrier string values.
- `WeightGrams` — simple type restricting parcel weights.
- `ParcelType` — complex type for parcel tracking, weight, and sequence.
- `AddressType` — complex type for recipient address.
- `ShipmentNoticeType` — complex type for shipment notice contents and identifier.

#### Constants and configuration
- `targetNamespace`: `urn:example:shipment:1`.
- `elementFormDefault`: `qualified`.
- `CarrierCode` values: `DHL`, `UPS`, `GLS`, `POST`.
- `WeightGrams` maximum inclusive: `31500`.
- `AddressType.country` default: `DE`; optional (`minOccurs="0"`).
- `ShipmentNoticeType.parcel` maximum occurrences: `unbounded`.

#### Dependencies
- XML Schema namespace: `http://www.w3.org/2001/XMLSchema`.
- Built-in XSD types: `xs:string`, `xs:positiveInteger`, `xs:dateTime`, `xs:ID`.
- Schema-qualified types use the `shp` prefix for `urn:example:shipment:1`.

#### Exceptions / Errors
- Validation fails for missing required `noticeId` or parcel `sequence`.
- Validation fails if carrier is not `DHL`, `UPS`, `GLS`, or `POST`.
- Validation fails for non-positive parcel weight or weight above 31,500 grams.
- Validation fails if required elements are absent or appear out of sequence.
