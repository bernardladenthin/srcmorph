### shipment.xsd
- H: 1.0
- C: E7230D69
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T23:42:01Z
- G: 
- A: 0.0.0
- X: file
- P: 144CA22B
---
> Defines the XML schema for a shipment notice sent by a warehouse when parcels leave the building.

#### Purpose
- Specifies the structure of a `shipmentNotice` document.
- Validates carrier codes, parcel weights, and address details.

#### Type
- XML Schema Definition (XSD)  
- Root element: `xs:schema`  
- Namespaces: `xs` → `http://www.w3.org/2001/XMLSchema`, `shp` → `urn:example:shipment:1`

#### Definitions
- **Element**: `shipmentNotice` of type `shp:ShipmentNoticeType`
- **SimpleTypes**:
  - `CarrierCode`: enumeration of `DHL`, `UPS`, `GLS`, `POST`
  - `WeightGrams`: positive integer ≤ 31500
- **ComplexTypes**:
  - `ParcelType`: sequence of `trackingNumber` (`xs:string`) and `weight` (`shp:WeightGrams`); required attribute `sequence` (`xs:positiveInteger`)
  - `AddressType`: sequence of `name`, `street`, `postalCode`, `city`, optional `country` (`xs:string`, default `DE`)
  - `ShipmentNoticeType`: sequence of `orderReference` (`xs:string`), `shippedAt` (`xs:dateTime`), `carrier` (`shp:CarrierCode`), `recipient` (`shp:AddressType`), and one or more `parcel` (`shp:ParcelType`); required attribute `noticeId` (`xs:ID`)

#### Configuration
- `country` attribute in `AddressType` defaults to `"DE"`.

#### Dependencies
- Relies on XML Schema namespace `http://www.w3.org/2001/XMLSchema`.
