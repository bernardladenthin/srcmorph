### shipment.xsd
- H: 1.0
- C: E7230D69
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T14:57:52Z
- G: 
- A: 0.0.0
- X: file
---
> Defines the XML schema for a shipment notice sent by a warehouse when parcels leave the building.

#### Purpose
- Validates shipment notice documents.
- Enforces structure and value constraints for shipment data.

#### Type
- XML Schema (`<xs:schema>`)  
- Root element: `shipmentNotice` (type `shp:ShipmentNoticeType`)  
- Namespaces:  
  - `xs` → `http://www.w3.org/2001/XMLSchema`  
  - `shp` → `urn:example:shipment:1` (target namespace)

#### Definitions
- **Simple types**  
  - `CarrierCode`: enumeration of `DHL`, `UPS`, `GLS`, `POST`  
  - `WeightGrams`: positive integer ≤ `31500`
- **Complex types**  
  - `ParcelType`: sequence of `trackingNumber` (`xs:string`) and `weight` (`shp:WeightGrams`); required attribute `sequence` (`xs:positiveInteger`)  
  - `AddressType`: sequence of `name`, `street`, `postalCode`, `city`, optional `country` (`xs:string`, default `DE`)  
  - `ShipmentNoticeType`: sequence of `orderReference`, `shippedAt` (`xs:dateTime`), `carrier` (`shp:CarrierCode`), `recipient` (`shp:AddressType`), multiple `parcel` (`shp:ParcelType`); required attribute `noticeId` (`xs:ID`)
- **Root element**  
  - `shipmentNotice` of type `shp:ShipmentNoticeType`

#### Configuration
- `country` element default value: `"DE"`
- `WeightGrams` maximum inclusive value: `31500`

#### Dependencies
- XML Schema namespace `http://www.w3.org/2001/XMLSchema`
- Target namespace `urn:example:shipment:1` (used for all custom types)
