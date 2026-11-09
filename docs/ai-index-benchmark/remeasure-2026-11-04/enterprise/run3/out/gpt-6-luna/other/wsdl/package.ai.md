### other/wsdl
- H: 1.0
- C: DD12C338
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T08:22:19Z
- G: 
- A: 0.0.0
- X: package
- F: [StockService.wsdl](StockService.wsdl.ai.md)
---
> Defines the contract clients use to query a product’s available stock by SKU and handle unknown-SKU faults.

#### Purpose
- Describes the `GetStockLevel` SOAP request, response, and `UnknownSku` fault.
- Publishes the stock service endpoint and SOAP binding.

#### Responsibilities
- Specify stock lookup messages and schema types: `StockLevelRequest`, `StockLevelResponse`, and `UnknownSkuFault`.
- Expose the operation through a document/literal SOAP-over-HTTP service.

#### Key units
- `StockService` / `StockPort` — service and endpoint at `https://erp.shop.example/soap/stock`.
- `StockPortType.GetStockLevel` — accepts a SKU and returns availability, with an `UnknownSku` fault.
- `StockSoapBinding` — binds the operation to SOAP/HTTP with literal bodies and fault.

#### Data flow
- Client sends `StockLevelRequest.sku`; `GetStockLevel` returns `StockLevelResponse.available` or an `UnknownSkuFault` containing the SKU.

#### Dependencies
- XML Schema built-in types `xs:string` and `xs:int`.
- SOAP/HTTP transport; `GetStockLevel` SOAP action is `http://shop.example/stock/GetStockLevel`.
