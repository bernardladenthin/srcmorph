### shipment.xsd
- H: 1.0
- C: E7230D69
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T01:34:56Z
- G: 
- A: 0.0.0
- X: file
---
> Defines the XML schema for a shipment notice sent by a warehouse when parcels leave the building.

#### Purpose
- Validates shipment notice XML documents.
- Enforces structure and data constraints for shipment data.

#### Type
- XML Schema (XSD)
- Root element: `xs:schema`
- Namespaces:
  - `xs` → `http://www.w3.org/2001/XMLSchema`
  - `shp` → `urn:example:shipment:1`
- `targetNamespace`: `urn:example:shipment:1`
- `elementFormDefault`: `qualified`

#### Definitions
- **Top‑level element**
  - `shipmentNotice` (type `shp:ShipmentNoticeType`)
- **Simple types**
  - `CarrierCode` – enumeration: `DHL`, `UPS`, `GLS`, `POST`
  - `WeightGrams` – restriction: `xs:positiveInteger` ≤ `31500`
- **Complex types**
  - `ParcelType`
    - Sequence: `trackingNumber` (`xs:string`), `weight` (`shp:WeightGrams`)
    - Attribute: `sequence` (`xs:positiveInteger`, required)
  - `AddressType`
    - Sequence: `name`, `street`, `postalCode`, `city` (`xs:string`)
    - Optional `country` (`xs:string`, default `DE`)
  - `ShipmentNoticeType`
    - Sequence: `orderReference` (`xs:string`), `shippedAt` (`xs:dateTime`), `carrier` (`shp:CarrierCode`), `recipient` (`shp:AddressType`), `parcel` (`shp:ParcelType`, maxOccurs `unbounded`)
    - Attribute: `noticeId` (`xs:ID`, required)

#### Configuration
- `elementFormDefault`: `qualified`
- `targetNamespace`: `urn:example:shipment:1`
- `WeightGrams` maxInclusive: `31500`
- `AddressType` `country` default: `DE`
- `AddressType` `country` minOccurs: `0`
- `ShipmentNoticeType` `parcel` maxOccurs: `unbounded`

#### Dependencies
- Namespace `xs` (`http://www.w3.org/2001/XMLSchema`)
- Namespace `shp` (`urn:example:shipment:1`)
- Types: `xs:string`, `xs:positiveInteger`, `xs:dateTime`, `xs:ID`
