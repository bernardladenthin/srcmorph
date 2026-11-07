### StockService.wsdl
- H: 1.0
- C: A034A9B4
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T12:30:21Z
- G: 
- A: 0.0.0
- X: file
- P: D9E787D0
- N: EC197B72
---
**Facts (exact, whole file):** elements: 38; namespace declarations: 4; id and name attributes: 22; XSD type definitions: 1; XSLT templates: 0; Spring beans: 0; services: 1; ports and endpoints: 1; port types and interfaces: 1; bindings: 1; operations: 2; messages: 3; SOAP bindings: 1

> Defines a SOAP stock lookup service that accepts an SKU, returns its available quantity, and can report an unknown-SKU fault.

#### Type
- WSDL; root `definitions`, named `StockService`.
- Namespaces: default `http://schemas.xmlsoap.org/wsdl/`; `soap` = `http://schemas.xmlsoap.org/wsdl/soap/`; `tns` = `http://shop.example/stock`; `xs` = `http://www.w3.org/2001/XMLSchema`.

#### Definitions
- Target namespace: `http://shop.example/stock`.
- Schema elements: `StockLevelRequest` contains `sku` (`xs:string`); `StockLevelResponse` contains `available` (`xs:int`).
- Schema complex type: `UnknownSkuFault` contains `sku` (`xs:string`).
- Messages: `GetStockLevelInput` uses `StockLevelRequest`; `GetStockLevelOutput` uses `StockLevelResponse`; `UnknownSku` carries `UnknownSkuFault`.
- Port type `StockPortType`: operation `GetStockLevel`, input `GetStockLevelInput`, output `GetStockLevelOutput`, fault `UnknownSku` (`UnknownSku` message).
- Binding `StockSoapBinding`: document-style SOAP over HTTP; operation `GetStockLevel` uses literal input, output, and `UnknownSku` fault.
- Service `StockService`: port `StockPort` bound to `StockSoapBinding`, address `https://erp.shop.example/soap/stock`.

#### Configuration
- SOAP transport: `http://schemas.xmlsoap.org/soap/http`.
- SOAP action: `http://shop.example/stock/GetStockLevel`.
- SOAP body and fault use: `literal`.
- Service address: `https://erp.shop.example/soap/stock`.

#### Dependencies
- XML Schema built-in types: `xs:string`, `xs:int`.
- WSDL messages reference `StockLevelRequest`, `StockLevelResponse`, and `UnknownSkuFault` in `http://shop.example/stock`.
