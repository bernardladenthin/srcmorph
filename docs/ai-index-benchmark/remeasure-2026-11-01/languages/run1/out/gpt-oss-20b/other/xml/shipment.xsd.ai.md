### shipment.xsd
- H: 1.0
- C: E7230D69
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T14:18:37Z
- G: 
- A: 0.0.0
- X: file
---
> Defines the XML schema for a shipment notice sent by a warehouse when parcels leave the building.

#### Purpose
- Validates shipment notice documents.
- Enforces structure and value constraints for parcels, addresses, and carrier codes.

#### Type
- XML Schema (`xs:schema` root).
- Namespaces: `xs` → `http://www.w3.org/2001/XMLSchema`, `shp` → `urn:example:shipment:1`.
- `targetNamespace` is `urn:example:shipment:1`; `elementFormDefault="qualified"`.

#### Definitions
- **Element**: `shipmentNotice` of type `shp:ShipmentNoticeType`.
- **SimpleTypes**:
  - `CarrierCode`: enumeration of `DHL`, `UPS`, `GLS`, `POST`.
  - `WeightGrams`: positive integer ≤ 31500.
- **ComplexTypes**:
  - `ParcelType`: sequence `trackingNumber` (`xs:string`), `weight` (`shp:WeightGrams`); attribute `sequence` (`xs:positiveInteger`, required).
  - `AddressType`: sequence `name`, `street`, `postalCode`, `city`, optional `country` (`xs:string`, default `DE`).
  - `ShipmentNoticeType`: sequence `orderReference`, `shippedAt`, `carrier`, `recipient`, multiple `parcel`; attribute `noticeId` (`xs:ID`, required).

#### Configuration
- `country` attribute in `AddressType` defaults to `"DE"`.

#### Dependencies
- Relies on XML Schema namespace `http://www.w3.org/2001/XMLSchema`.
- Uses `xs:positiveInteger`, `xs:string`, `xs:dateTime`, `xs:ID` from the XML Schema standard.
