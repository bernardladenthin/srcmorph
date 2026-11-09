### StockService.wsdl
- H: 1.0
- C: A034A9B4
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T12:23:52Z
- G: 
- A: 0.0.0
- X: file
- P: D9E787D0
- N: EC197B72
---
**Facts (exact, whole file):** elements: 38; namespace declarations: 4; id and name attributes: 22; XSD type definitions: 1; XSLT templates: 0; Spring beans: 0; services: 1; ports and endpoints: 1; port types and interfaces: 1; bindings: 1; operations: 2; messages: 3; SOAP bindings: 1

> Defines a SOAP stock-level lookup service that accepts an SKU, returns its available quantity, and can report an unknown-SKU fault.

#### Type
- WSDL; root element `definitions`; service namespace `http://shop.example/stock`.
- Namespaces: default `http://schemas.xmlsoap.org/wsdl/`; `soap` → `http://schemas.xmlsoap.org/wsdl/soap/`; `tns` → `http://shop.example/stock`; `xs` → `http://www.w3.org/2001/XMLSchema`.

#### Definitions
- Service `StockService`; port `StockPort` uses binding `tns:StockSoapBinding`.
- Port type `StockPortType`; operation `GetStockLevel`:
  - Input `tns:GetStockLevelInput`; output `tns:GetStockLevelOutput`; fault `UnknownSku` uses `tns:UnknownSku`.
- Binding `StockSoapBinding`: document-style SOAP over HTTP; operation `GetStockLevel` uses literal input, output, and `UnknownSku` fault.
- Messages: `GetStockLevelInput` carries element `tns:StockLevelRequest`; `GetStockLevelOutput` carries element `tns:StockLevelResponse`; `UnknownSku` carries type `tns:UnknownSkuFault`.
- Embedded schema: `StockLevelRequest` contains `sku` (`xs:string`); `StockLevelResponse` contains `available` (`xs:int`); `UnknownSkuFault` contains `sku` (`xs:string`).

#### Configuration
- SOAP address: `https://erp.shop.example/soap/stock`.
- SOAP action for `GetStockLevel`: `http://shop.example/stock/GetStockLevel`.
- SOAP transport: `http://schemas.xmlsoap.org/soap/http`; message use: `literal`.

#### Dependencies
- External SOAP endpoint: `https://erp.shop.example/soap/stock`.
- Referenced SOAP and XML Schema namespaces: `http://schemas.xmlsoap.org/wsdl/soap/`, `http://www.w3.org/2001/XMLSchema`.
