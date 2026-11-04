### other/wsdl
- H: 1.0
- C: DD12C338
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T12:40:29Z
- G: 
- A: 0.0.0
- X: package
- F: [StockService.wsdl](StockService.wsdl.ai.md)
---
> Provides a SOAP interface for querying stock availability by SKU and reporting unknown SKUs.

#### Purpose
- Defines the contract for requesting a SKU’s available stock and receiving an `UnknownSku` fault.
- Publishes the service at `https://erp.shop.example/soap/stock`.

#### Responsibilities
- Describe request, response, and fault data in the `http://shop.example/stock` schema.
- Specify the `GetStockLevel` SOAP operation and its document-style, literal-message binding.

#### Key units
- `StockLevelRequest` — carries the requested `sku`.
- `StockLevelResponse` — returns `available` as an integer.
- `UnknownSkuFault` — identifies an unknown `sku`.
- `StockPortType` — declares the `GetStockLevel` operation and `UnknownSku` fault.
- `StockSoapBinding` — binds the operation to document-style SOAP over HTTP.
- `StockService` / `StockPort` — expose the binding at the service endpoint.

#### Data flow
- `GetStockLevelInput` carries `StockLevelRequest`; the operation returns `GetStockLevelOutput` with `StockLevelResponse`, or the `UnknownSku` fault with `UnknownSkuFault`.

#### Dependencies
- WSDL messages, operation, and binding reference types in the `http://shop.example/stock` namespace.
- SOAP transport: `http://schemas.xmlsoap.org/soap/http`; SOAP action: `http://shop.example/stock/GetStockLevel`.
