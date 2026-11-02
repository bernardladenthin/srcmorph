### shipment.xsd
- H: 1.0
- C: E7230D69
- D: 2026-10-31T21:26:46Z
- T: 2026-10-31T21:35:03Z
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
- Schema definitions for elements: `shipmentNotice`, `parcel`, `recipient`, etc.
- Simple types: `CarrierCode`, `WeightGrams`.

#### Core logic
- Declares root element `shipmentNotice` of type `ShipmentNoticeType`.
- `ShipmentNoticeType` sequence: `orderReference`, `shippedAt`, `carrier`, `recipient`, multiple `parcel`.
- `carrier` restricted to enumerated codes: DHL, UPS, GLS, POST.
- `parcel` includes `trackingNumber`, `weight` (max 31500 g), and required `sequence` attribute.
- `recipient` address fields with optional `country` defaulting to "DE".
- `noticeId` attribute required on `shipmentNotice`.

#### Public API
- Element `shipmentNotice` (root) → validates entire notice.
- Type `ShipmentNoticeType` → structure of notice.
- Type `ParcelType` → structure of each parcel.
- Type `AddressType` → structure of recipient address.
- Simple type `CarrierCode` → allowed carrier codes.
- Simple type `WeightGrams` → weight constraint.

#### Constants and configuration
- Enumeration values for `CarrierCode`: `DHL`, `UPS`, `GLS`, `POST`.
- `WeightGrams` max inclusive: `31500`.
- `AddressType.country` default: `"DE"`.

#### Dependencies
- XML Schema namespace `http://www.w3.org/2001/XMLSchema`.
- Target namespace `urn:example:shipment:1`.

#### Exceptions / Errors
- XML validation errors for invalid carrier codes, overweight parcels, missing required attributes, or malformed dates.
