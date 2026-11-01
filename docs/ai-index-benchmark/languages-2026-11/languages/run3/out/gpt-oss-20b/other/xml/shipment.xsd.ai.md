### shipment.xsd
- H: 1.0
- C: E7230D69
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T01:56:07Z
- G: 
- A: 0.0.0
- X: file
---
> Defines the XML schema for a warehouse shipment notice, including carrier codes, parcel details, and recipient address.

#### Purpose
- Validates shipment notice documents.
- Enforces structure for parcels, addresses, and carrier codes.

#### Type
- XML Schema (`xs:schema`).
- Root element: `shipmentNotice` of type `shp:ShipmentNoticeType`.
- Namespaces: `xs` → `http://www.w3.org/2001/XMLSchema`; `shp` → `urn:example:shipment:1`.

#### Definitions
- **SimpleTypes**
  - `CarrierCode`: enumeration of `DHL`, `UPS`, `GLS`, `POST`.
  - `WeightGrams`: positive integer ≤ `31500`.
- **ComplexTypes**
  - `ParcelType`: sequence `trackingNumber` (`xs:string`), `weight` (`shp:WeightGrams`); attribute `sequence` (`xs:positiveInteger`, required).
  - `AddressType`: sequence `name`, `street`, `postalCode`, `city`, optional `country` (`xs:string`, default `DE`).
  - `ShipmentNoticeType`: sequence `orderReference`, `shippedAt`, `carrier`, `recipient`, `parcel` (unbounded); attribute `noticeId` (`xs:ID`, required).

#### Configuration
- No configurable properties; all values are fixed by restrictions or defaults.

#### Dependencies
- `xs` namespace for core XML Schema types.
- `shp` namespace for custom types defined within this schema.
