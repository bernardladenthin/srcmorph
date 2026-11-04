### other/wsdl
- H: 1.0
- C: DD12C338
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T12:31:48Z
- G: 
- A: 0.0.0
- X: package
- F: [StockService.wsdl](StockService.wsdl.ai.md)
---
> Defines the SOAP contract for retrieving a stock quantity by SKU and reporting unknown-SKU faults.

#### Purpose
- Specifies the request, response, and fault contract for stock-level lookup.
- Publishes the service endpoint and SOAP-over-HTTP binding.

#### Responsibilities
- Define stock lookup messages and schema types.
- Bind the `GetStockLevel` operation to document-style, literal SOAP input, output, and fault.
- Expose the operation through the `StockPort` endpoint.

#### Key units
- `StockService` — WSDL service exposing `StockPort` at `https://erp.shop.example/soap/stock`.
- `StockPortType` — declares `GetStockLevel` with input, output, and `UnknownSku` fault.
- `StockSoapBinding` — configures document-style SOAP over HTTP with literal bodies and fault.
- `StockLevelRequest` — carries the requested `sku` as `xs:string`.
- `StockLevelResponse` — returns `available` as `xs:int`.
- `UnknownSkuFault` — carries the unrecognized `sku` as `xs:string`.

#### Data flow
- `GetStockLevelInput` references `StockLevelRequest`; the operation returns `GetStockLevelOutput` referencing `StockLevelResponse`, or the `UnknownSku` fault referencing `UnknownSkuFault`.

#### Dependencies
- Uses XML Schema built-in types `xs:string` and `xs:int`.
- Defines its contract in target namespace `http://shop.example/stock`; SOAP action is `http://shop.example/stock/GetStockLevel`.

#### Cross-cutting
- SOAP transport is HTTP; request, response, and fault use literal encoding.
