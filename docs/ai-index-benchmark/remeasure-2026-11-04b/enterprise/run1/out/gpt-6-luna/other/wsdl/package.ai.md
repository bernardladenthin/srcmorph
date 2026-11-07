### other/wsdl
- H: 1.0
- C: DD12C338
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T12:25:18Z
- G: 
- A: 0.0.0
- X: package
- F: [StockService.wsdl](StockService.wsdl.ai.md)
---
> Provides a SOAP interface for retrieving a stock item’s available quantity by SKU and reporting unknown-SKU faults.

#### Purpose
- Defines the `StockService` contract for stock-level lookup.

#### Responsibilities
- Accept an SKU in `StockLevelRequest` and return its available quantity in `StockLevelResponse`.
- Describe the `UnknownSku` fault, which carries the SKU associated with the lookup failure.

#### Key units
- `StockService` — exposes the `StockPort` endpoint.
- `StockPortType` — defines the `GetStockLevel` operation and its input, output, and fault.
- `StockSoapBinding` — binds the operation to document-style SOAP over HTTP with literal messages.
- `StockLevelRequest` — schema type containing `sku` (`xs:string`).
- `StockLevelResponse` — schema type containing `available` (`xs:int`).
- `UnknownSkuFault` — schema type containing `sku` (`xs:string`).

#### Data flow
- `GetStockLevelInput` carries `StockLevelRequest` with an SKU; `GetStockLevelOutput` carries `StockLevelResponse` with the available quantity.
- An unknown SKU is reported through the `UnknownSku` fault message.

#### Dependencies
- SOAP endpoint: `https://erp.shop.example/soap/stock`.
- Uses WSDL SOAP and XML Schema namespaces.

#### Cross-cutting
- SOAP messages use document style and literal encoding; `GetStockLevel` has SOAP action `http://shop.example/stock/GetStockLevel`.
