### StockService.wsdl
- H: 1.0
- C: A034A9B4
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T08:08:15Z
- G: 
- A: 0.0.0
- X: file
- P: 326D0B29
- N: EC197B72
---
**Facts (exact, whole file):** elements: 38; namespace declarations: 4; id and name attributes: 22; XSD type definitions: 1; XSLT templates: 0; Spring beans: 0; services: 1; ports and endpoints: 1; port types and interfaces: 1; bindings: 1; operations: 2; messages: 3; SOAP bindings: 1

> Defines a SOAP service for retrieving stock availability by SKU, including an unknown-SKU fault.

#### Type
- WSDL; root `definitions`; namespaces: default `http://schemas.xmlsoap.org/wsdl/`, `soap` `http://schemas.xmlsoap.org/wsdl/soap/`, `tns` `http://shop.example/stock`, `xs` `http://www.w3.org/2001/XMLSchema`.

#### Definitions
- Schema target namespace `http://shop.example/stock`:
  - Elements: `StockLevelRequest` contains `sku` (`xs:string`); `StockLevelResponse` contains `available` (`xs:int`).
  - Complex type `UnknownSkuFault` contains `sku` (`xs:string`).
- Messages: `GetStockLevelInput` references `tns:StockLevelRequest`; `GetStockLevelOutput` references `tns:StockLevelResponse`; `UnknownSku` carries `tns:UnknownSkuFault`.
- Port type `StockPortType`: operation `GetStockLevel`, input `tns:GetStockLevelInput`, output `tns:GetStockLevelOutput`, fault `UnknownSku` (`tns:UnknownSku`).
- Binding `StockSoapBinding` for `tns:StockPortType`: document style, SOAP over HTTP; operation `GetStockLevel` uses literal input, output, and `UnknownSku` fault.
- Service `StockService`: port `StockPort` uses `tns:StockSoapBinding`, address `https://erp.shop.example/soap/stock`.

#### Configuration
- `soapAction`: `http://shop.example/stock/GetStockLevel`
- SOAP address: `https://erp.shop.example/soap/stock`
- Binding style: `document`; transport: `http://schemas.xmlsoap.org/soap/http`; body and fault use: `literal`.

#### Dependencies
- SOAP endpoint: `https://erp.shop.example/soap/stock`
- XML Schema types: `xs:string`, `xs:int`.
