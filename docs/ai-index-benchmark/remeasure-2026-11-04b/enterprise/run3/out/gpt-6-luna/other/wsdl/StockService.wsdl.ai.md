### StockService.wsdl
- H: 1.0
- C: A034A9B4
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T12:39:02Z
- G: 
- A: 0.0.0
- X: file
- P: D9E787D0
- N: EC197B72
---
**Facts (exact, whole file):** elements: 38; namespace declarations: 4; id and name attributes: 22; XSD type definitions: 1; XSLT templates: 0; Spring beans: 0; services: 1; ports and endpoints: 1; port types and interfaces: 1; bindings: 1; operations: 2; messages: 3; SOAP bindings: 1

> Defines a SOAP service for querying SKU stock availability, with an `UnknownSku` fault.

#### Type
- WSDL; root `definitions`.
- Namespaces: default `http://schemas.xmlsoap.org/wsdl/`; `soap` = `http://schemas.xmlsoap.org/wsdl/soap/`; `tns` = `http://shop.example/stock`; `xs` = `http://www.w3.org/2001/XMLSchema`.

#### Definitions
- Schema target namespace `http://shop.example/stock`:
  - `StockLevelRequest`: `sku` (`xs:string`).
  - `StockLevelResponse`: `available` (`xs:int`).
  - `UnknownSkuFault`: `sku` (`xs:string`).
- Messages: `GetStockLevelInput` uses `StockLevelRequest`; `GetStockLevelOutput` uses `StockLevelResponse`; `UnknownSku` carries `UnknownSkuFault`.
- Port type `StockPortType`: operation `GetStockLevel`, input `GetStockLevelInput`, output `GetStockLevelOutput`, fault `UnknownSku`.
- Binding `StockSoapBinding` for `StockPortType`: document-style SOAP over HTTP; `GetStockLevel` uses literal input, output, and fault.
- Service `StockService`: port `StockPort` uses `StockSoapBinding`, address `https://erp.shop.example/soap/stock`.

#### Configuration
- SOAP action: `http://shop.example/stock/GetStockLevel`.
- Service endpoint: `https://erp.shop.example/soap/stock`.
- SOAP transport: `http://schemas.xmlsoap.org/soap/http`.

#### Dependencies
- WSDL message, operation, and binding references use the `http://shop.example/stock` namespace.
