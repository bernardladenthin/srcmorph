### shipment.xsd
- H: 1.0
- C: E7230D69
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T08:01:41Z
- G: 
- A: 0.0.0
- X: file
- P: 326D0B29
---
> Defines the warehouse shipment notice contract, including recipient details, carrier codes, parcel tracking, and weight limits.

#### Type
- XML Schema; root `xs:schema`.
- Namespaces: `xs` = `http://www.w3.org/2001/XMLSchema`; `shp` = `urn:example:shipment:1`.
- Target namespace: `urn:example:shipment:1`.

#### Definitions
- Top-level element `shipmentNotice`: type `shp:ShipmentNoticeType`.
- Simple type `CarrierCode`: string enumeration `DHL`, `UPS`, `GLS`, `POST`.
- Simple type `WeightGrams`: positive integer, maximum inclusive `31500`.
- Complex type `ParcelType`: ordered `trackingNumber` (`xs:string`), `weight` (`shp:WeightGrams`); required `sequence` attribute (`xs:positiveInteger`).
- Complex type `AddressType`: ordered `name`, `street`, `postalCode`, `city`, `country` (all `xs:string`); `country` is optional.
- Complex type `ShipmentNoticeType`: ordered `orderReference` (`xs:string`), `shippedAt` (`xs:dateTime`), `carrier` (`shp:CarrierCode`), `recipient` (`shp:AddressType`), one or more `parcel` (`shp:ParcelType`); required `noticeId` attribute (`xs:ID`).

#### Configuration
- `AddressType.country`: default `DE`.
- `WeightGrams`: maximum inclusive value `31500`.
- `ShipmentNoticeType.parcel`: `maxOccurs="unbounded"`.

#### Dependencies
- XML Schema namespace `http://www.w3.org/2001/XMLSchema`; built-in types include `xs:string`, `xs:positiveInteger`, `xs:dateTime`, and `xs:ID`.
