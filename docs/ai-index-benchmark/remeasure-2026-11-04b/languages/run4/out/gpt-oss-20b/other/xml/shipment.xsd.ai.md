### shipment.xsd
- H: 1.0
- C: E7230D69
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:43:53Z
- G: 
- A: 0.0.0
- X: file
- P: D9E787D0
---
> Defines the XML schema for a shipment notice sent by a warehouse when parcels leave the building.

#### Purpose
- Validates shipment notice documents.
- Enforces structure and value constraints for carriers, parcels, and addresses.

#### Type
- XML Schema (`xs:schema`).
- Root namespace: `urn:example:shipment:1`.
- Declared namespaces: `xs` → `http://www.w3.org/2001/XMLSchema`, `shp` → `urn:example:shipment:1`.

#### Definitions
- **Element**: `shipmentNotice` (type `shp:ShipmentNoticeType`).
- **SimpleTypes**:
  - `CarrierCode`: enumeration of `"DHL"`, `"UPS"`, `"GLS"`, `"POST"`.
  - `WeightGrams`: positive integer ≤ 31500.
- **ComplexTypes**:
  - `ParcelType`: sequence of `trackingNumber` (`xs:string`) and `weight` (`shp:WeightGrams`); required attribute `sequence` (`xs:positiveInteger`).
  - `AddressType`: sequence of `name`, `street`, `postalCode`, `city` (`xs:string`), optional `country` (`xs:string`, default `"DE"`).
  - `ShipmentNoticeType`: sequence of `orderReference` (`xs:string`), `shippedAt` (`xs:dateTime`), `carrier` (`shp:CarrierCode`), `recipient` (`shp:AddressType`), and one or more `parcel` (`shp:ParcelType`); required attribute `noticeId` (`xs:ID`).

#### Configuration
- `country` attribute in `AddressType` defaults to `"DE"`.

#### Dependencies
- Uses standard XML Schema namespace `http://www.w3.org/2001/XMLSchema`.
