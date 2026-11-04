### StockService.wsdl
- H: 1.0
- C: A034A9B4
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T08:14:41Z
- G: 
- A: 0.0.0
- X: file
- P: 326D0B29
- N: EC197B72
---
**Facts (exact, whole file):** elements: 38; namespace declarations: 4; id and name attributes: 22; XSD type definitions: 1; XSLT templates: 0; Spring beans: 0; services: 1; ports and endpoints: 1; port types and interfaces: 1; bindings: 1; operations: 2; messages: 3; SOAP bindings: 1

> Defines a SOAP service for retrieving stock availability by SKU, including an unknown-SKU fault.

#### Type
- WSDL; root `definitions`
- Namespaces: default `http://schemas.xmlsoap.org/wsdl/`; `soap` = `http://schemas.xmlsoap.org/wsdl/soap/`; `tns` = `http://shop.example/stock`; `xs` = `http://www.w3.org/2001/XMLSchema`

#### Definitions
- Service `StockService`
  - Port `StockPort` uses binding `tns:StockSoapBinding`; address `https://erp.shop.example/soap/stock`
- Port type `StockPortType`
  - Operation `GetStockLevel`: input `tns:GetStockLevelInput`, output `tns:GetStockLevelOutput`, fault `UnknownSku` (`tns:UnknownSku`)
- Binding `StockSoapBinding`
  - Type `tns:StockPortType`; document style, SOAP/HTTP transport
  - Operation `GetStockLevel`: SOAP action `http://shop.example/stock/GetStockLevel`; input, output and `UnknownSku` fault use literal bodies
- Schema target namespace `http://shop.example/stock`
  - Element `StockLevelRequest`: contains `sku` (`xs:string`)
  - Element `StockLevelResponse`: contains `available` (`xs:int`)
  - Complex type `UnknownSkuFault`: contains `sku` (`xs:string`)
- Messages: `GetStockLevelInput` uses element `tns:StockLevelRequest`; `GetStockLevelOutput` uses element `tns:StockLevelResponse`; `UnknownSku` uses type `tns:UnknownSkuFault`

#### Configuration
- SOAP endpoint: `https://erp.shop.example/soap/stock`
- SOAP action: `http://shop.example/stock/GetStockLevel`
- Binding style: `document`; transport: `http://schemas.xmlsoap.org/soap/http`; body use: `literal`

#### Dependencies
- WSDL and SOAP namespaces; XML Schema namespace
- Service endpoint: `erp.shop.example`
