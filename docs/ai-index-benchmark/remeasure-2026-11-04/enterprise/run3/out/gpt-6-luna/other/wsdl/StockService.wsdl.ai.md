### StockService.wsdl
- H: 1.0
- C: A034A9B4
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T08:20:54Z
- G: 
- A: 0.0.0
- X: file
- P: 326D0B29
- N: EC197B72
---
**Facts (exact, whole file):** elements: 38; namespace declarations: 4; id and name attributes: 22; XSD type definitions: 1; XSLT templates: 0; Spring beans: 0; services: 1; ports and endpoints: 1; port types and interfaces: 1; bindings: 1; operations: 2; messages: 3; SOAP bindings: 1

> Defines a SOAP service for retrieving a product’s available stock by SKU, with an `UnknownSku` fault.

#### Purpose
- Describes the `GetStockLevel` operation and its request, response, and fault contract.
- Publishes the SOAP endpoint used by stock clients.

#### Type
- WSDL; root element `definitions`.
- Namespaces: default `http://schemas.xmlsoap.org/wsdl/`; `soap` = `http://schemas.xmlsoap.org/wsdl/soap/`; `tns` = `http://shop.example/stock`; `xs` = `http://www.w3.org/2001/XMLSchema`.

#### Definitions
- Service `StockService`; port `StockPort` uses binding `tns:StockSoapBinding`.
- Port type `StockPortType`: operation `GetStockLevel` takes `tns:GetStockLevelInput`, returns `tns:GetStockLevelOutput`, and declares fault `UnknownSku` (`tns:UnknownSku`).
- Messages:
  - `GetStockLevelInput`: `parameters` references `tns:StockLevelRequest`.
  - `GetStockLevelOutput`: `parameters` references `tns:StockLevelResponse`.
  - `UnknownSku`: `fault` uses type `tns:UnknownSkuFault`.
- Schema types in `http://shop.example/stock`:
  - `StockLevelRequest`: `sku` (`xs:string`).
  - `StockLevelResponse`: `available` (`xs:int`).
  - `UnknownSkuFault`: `sku` (`xs:string`).
- Binding `StockSoapBinding` for `tns:StockPortType`: document style, SOAP/HTTP transport, literal input/output bodies, literal `UnknownSku` fault.

#### Configuration
- SOAP address: `https://erp.shop.example/soap/stock`.
- SOAP action for `GetStockLevel`: `http://shop.example/stock/GetStockLevel`.

#### Dependencies
- XML Schema built-in types: `xs:string`, `xs:int`.
- SOAP/HTTP transport: `http://schemas.xmlsoap.org/soap/http`.
