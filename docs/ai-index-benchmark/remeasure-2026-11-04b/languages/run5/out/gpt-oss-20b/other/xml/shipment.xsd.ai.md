### shipment.xsd
- H: 1.0
- C: E7230D69
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:44:49Z
- G: 
- A: 0.0.0
- X: file
- P: D9E787D0
---
> Defines the XML schema for a shipment notice sent by a warehouse when parcels leave the building.

#### Purpose
- Specifies the structure of a shipment notice XML document.
- Validates shipment data such as carrier, recipient address, and parcel details.

#### Type
- XML Schema (`xs:schema`).
- Root element: `shipmentNotice`.
- Namespaces: `xs` → `http://www.w3.org/2001/XMLSchema`; `shp` → `urn:example:shipment:1`.

#### Definitions
- **Simple types**
  - `CarrierCode`: enumeration of `"DHL"`, `"UPS"`, `"GLS"`, `"POST"`.
  - `WeightGrams`: positive integer ≤ 31500.
- **Complex types**
  - `ParcelType`: sequence of `trackingNumber` (`xs:string`) and `weight` (`shp:WeightGrams`); required attribute `sequence` (`xs:positiveInteger`).
  - `AddressType`: sequence of `name`, `street`, `postalCode`, `city` (`xs:string`); optional `country` (`xs:string`, default `"DE"`).
  - `ShipmentNoticeType`: sequence of `orderReference` (`xs:string`), `shippedAt` (`xs:dateTime`), `carrier` (`shp:CarrierCode`), `recipient` (`shp:AddressType`), and one or more `parcel` (`shp:ParcelType`); required attribute `noticeId` (`xs:ID`).
- **Root element**
  - `shipmentNotice` of type `shp:ShipmentNoticeType`.

#### Configuration
- `country` attribute in `AddressType` defaults to `"DE"`.

#### Dependencies
- Relies on XML Schema namespace `http://www.w3.org/2001/XMLSchema`.
- Uses target namespace `urn:example:shipment:1` for all custom types.
