### other/wsdl
- H: 1.0
- C: DD12C338
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T08:16:09Z
- G: 
- A: 0.0.0
- X: package
- F: [StockService.wsdl](StockService.wsdl.ai.md)
---
> Defines the stock-availability lookup contract for retrieving inventory by SKU and reporting unknown SKUs.

#### Purpose
- Specify the SOAP interface for querying stock availability by SKU.
- Describe the request, response, and unknown-SKU fault exchanged with the stock service.

#### Responsibilities
- Define the `GetStockLevel` operation, its SOAP binding, and the service endpoint.
- Define XML Schema payloads for SKU requests, availability responses, and unknown-SKU faults.

#### Key units
- `StockService` / `StockPort` — expose `StockPortType` through `StockSoapBinding` at `https://erp.shop.example/soap/stock`.
- `StockPortType.GetStockLevel` — accept a `GetStockLevelInput`, return a `GetStockLevelOutput`, or raise `UnknownSku`.
- `StockLevelRequest` — carry the requested `sku` as a string.
- `StockLevelResponse` — carry `available` as an integer.
- `UnknownSkuFault` — carry the SKU associated with the fault.
- `StockSoapBinding` — define document-style SOAP over HTTP with literal input, output, and fault bodies.

#### Data flow
- A `StockLevelRequest` containing `sku` enters `GetStockLevel`; the service returns `StockLevelResponse` with `available`, or `UnknownSku` with the relevant SKU.

#### Dependencies
- WSDL, SOAP, and XML Schema namespaces.
- Service endpoint at `erp.shop.example`; SOAP action `http://shop.example/stock/GetStockLevel`.

#### Cross-cutting
- Uses document-style SOAP over HTTP with literal message bodies.
