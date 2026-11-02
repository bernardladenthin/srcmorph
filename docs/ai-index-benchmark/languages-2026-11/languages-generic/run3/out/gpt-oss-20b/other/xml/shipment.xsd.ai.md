### shipment.xsd
- H: 1.0
- C: E7230D69
- D: 2026-10-31T21:26:46Z
- T: 2026-10-31T21:41:06Z
- G: 
- A: 0.0.0
- X: file
---
> Defines the XML schema for a shipment notice sent by a warehouse when parcels leave the building.

#### Purpose
- Validates shipment notice documents.
- Enforces carrier codes, parcel weight limits, and address structure.

#### Type
- XML Schema Definition (XSD) file.

#### Input
- XML documents with root element `shipmentNotice` in namespace `urn:example:shipment:1`.

#### Output
- Schema definitions for elements and types used in shipment notices.

#### Core logic
- Declares `shipmentNotice` element of type `ShipmentNoticeType`.
- Defines simple types:
  - `CarrierCode` with enumerated values `DHL`, `UPS`, `GLS`, `POST`.
  - `WeightGrams` restricted to positive integers ≤ 31500.
- Defines complex types:
  - `ParcelType` with `trackingNumber`, `weight`, and required `sequence` attribute.
  - `AddressType` with `name`, `street`, `postalCode`, `city`, optional `country` defaulting to `DE`.
  - `ShipmentNoticeType` with `orderReference`, `shippedAt`, `carrier`, `recipient`, multiple `parcel` elements, and required `noticeId` attribute.

#### Public API
- Element: `shipmentNotice` (root of shipment notice XML).

#### Constants and configuration
- Enumeration values for `CarrierCode`: `DHL`, `UPS`, `GLS`, `POST`.
- `WeightGrams` max inclusive: `31500`.
- `AddressType.country` default: `DE`.

#### Dependencies
- XML Schema namespace `http://www.w3.org/2001/XMLSchema`.
- Target namespace `urn:example:shipment:1`.

#### Exceptions / Errors
- Schema validation errors for invalid carrier codes, overweight parcels, missing required attributes, or malformed XML.
