### other/wsdl
- H: 1.0
- C: DD12C338
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T08:09:37Z
- G: 
- A: 0.0.0
- X: package
- F: [StockService.wsdl](StockService.wsdl.ai.md)
---
> Defines the SOAP contract for querying stock availability by SKU and reporting unknown-SKU faults.

#### Purpose
- Specify the request, response, and fault message shapes for stock-level lookup.
- Publish the `GetStockLevel` operation over document/literal SOAP.

#### Responsibilities
- **Stock lookup contract:** `StockLevelRequest` accepts a SKU; `StockLevelResponse` returns an available-unit count.
- **Fault contract:** `UnknownSkuFault` carries the SKU associated with an unknown-SKU error.
- **SOAP publication:** Bind and expose the operation at the stock service endpoint.

#### Key units
- `StockPortType` — declares `GetStockLevel` input, output, and `UnknownSku` fault.
- `StockSoapBinding` — binds the operation to document-style SOAP over HTTP with literal bodies and fault.
- `StockService` / `StockPort` — publish the binding at `https://erp.shop.example/soap/stock`.
- `StockLevelRequest`, `StockLevelResponse`, `UnknownSkuFault` — define the request, response, and fault data.

#### Data flow
- `GetStockLevelInput` carries `StockLevelRequest` (`sku: xs:string`); the response carries `StockLevelResponse` (`available: xs:int`); an unknown SKU returns the `UnknownSku` fault with `UnknownSkuFault`.

#### Dependencies
- XML Schema primitives `xs:string` and `xs:int`.
- SOAP over HTTP at `https://erp.shop.example/soap/stock`; action `http://shop.example/stock/GetStockLevel`.
