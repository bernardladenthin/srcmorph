### shipment.xsd
- H: 1.0
- C: E7230D69
- D: 2026-10-31T21:26:46Z
- T: 2026-10-31T21:31:17Z
- G: 
- A: 0.0.0
- X: file
---
> Defines XML schema for a shipment notice sent by a warehouse when parcels leave.

#### Purpose
- Schema for shipment notice XML documents.
- Validates carrier, parcel, address, and shipment metadata.

#### Type
- XML Schema Definition (XSD) file.

#### Input
- XML documents with root element `shipmentNotice` in namespace `urn:example:shipment:1`.

#### Output
- Validation rules for elements: `shipmentNotice`, `orderReference`, `shippedAt`, `carrier`, `recipient`, `parcel`.
- Constraints: carrier codes, weight limit, required attributes, default country.

#### Core logic
- `CarrierCode` restricts to DHL, UPS, GLS, POST.
- `WeightGrams` limits weight to ≤ 31500 grams.
- `ParcelType` requires `trackingNumber`, `weight`, and `sequence` attribute.
- `AddressType` includes name, street, postalCode, city, optional country defaulting to DE.
- `ShipmentNoticeType` aggregates order reference, shipment time, carrier, recipient address, multiple parcels, and mandatory `noticeId` attribute.

#### Public API
- Element `shipmentNotice` (type `ShipmentNoticeType`).

#### Constants and configuration
- `CarrierCode` enumeration values: DHL, UPS, GLS, POST.
- `WeightGrams` maxInclusive: 31500.
- `AddressType` default country: DE.

#### Dependencies
- XML Schema namespace `http://www.w3.org/2001/XMLSchema`.
- Target namespace `urn:example:shipment:1`.

#### Exceptions / Errors
- XML validation errors for invalid carrier codes, overweight parcels, missing required elements or attributes, and incorrect data types.
