### shipment.xsd
- H: 1.0
- C: E7230D69
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T06:32:50Z
- G: 
- A: 0.0.0
- X: file
---
> Defines the XML schema for a shipment notice, specifying parcel details, carrier codes, and recipient addresses.

#### Purpose
- Provides a formal structure for shipment notices exchanged by warehouses.
- Validates XML documents containing shipment information.

#### Type
- XML Schema (`xs:schema`)  
- Root element: `shipmentNotice` (type `shp:ShipmentNoticeType`)  
- Namespaces:  
  - `xs` → `http://www.w3.org/2001/XMLSchema`  
  - `shp` → `urn:example:shipment:1`  
- Target namespace: `urn:example:shipment:1`  
- Element form default: `qualified`

#### Definitions
- **Top‑level element**: `shipmentNotice` (`shp:ShipmentNoticeType`)
- **Simple types**  
  - `CarrierCode`: enumeration values `DHL`, `UPS`, `GLS`, `POST`  
  - `WeightGrams`: positive integer ≤ `31500`
- **Complex types**  
  - `ParcelType`: sequence `trackingNumber` (`xs:string`), `weight` (`shp:WeightGrams`); attribute `sequence` (`xs:positiveInteger`, required)  
  - `AddressType`: sequence `name`, `street`, `postalCode`, `city`, `country` (`xs:string`, optional, default `DE`)  
  - `ShipmentNoticeType`: sequence `orderReference`, `shippedAt`, `carrier`, `recipient`, `parcel` (unbounded); attribute `noticeId` (`xs:ID`, required)

#### Configuration
- `WeightGrams` max inclusive: `31500`
- `AddressType` `country` default: `DE`
- `ParcelType` attribute `sequence` required
- `ShipmentNoticeType` attribute `noticeId` required
- `parcel` element maxOccurs: `unbounded`

#### Dependencies
- Namespace `xs` (`http://www.w3.org/2001/XMLSchema`)  
- Namespace `shp` (`urn:example:shipment:1`) – self‑referenced for type definitions.
