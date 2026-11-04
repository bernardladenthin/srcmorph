### StockService.wsdl
- H: 1.0
- C: A034A9B4
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T08:17:44Z
- G: 
- A: 0.0.0
- X: file
- P: 326D0B29
- N: EC197B72
---
**Facts (exact, whole file):** elements: 38; namespace declarations: 4; id and name attributes: 22; XSD type definitions: 1; XSLT templates: 0; Spring beans: 0; services: 1; ports and endpoints: 1; port types and interfaces: 1; bindings: 1; operations: 2; messages: 3; SOAP bindings: 1

> Defines a SOAP web service for querying stock levels by SKU, including request/response types and a fault for unknown SKUs.

#### Purpose
- Exposes a `GetStockLevel` operation to retrieve available stock for a given SKU.
- Provides a fault message `UnknownSku` when the SKU is not found.

#### Type
- WSDL 1.1
- Root element: `definitions`
- Namespaces:
  - `xmlns` → `http://schemas.xmlsoap.org/wsdl/`
  - `xmlns:soap` → `http://schemas.xmlsoap.org/wsdl/soap/`
  - `xmlns:tns` → `http://shop.example/stock`
  - `xmlns:xs` → `http://www.w3.org/2001/XMLSchema`

#### Definitions
- **Types**
  - `StockLevelRequest` element: contains `sku` (`xs:string`)
  - `StockLevelResponse` element: contains `available` (`xs:int`)
  - `UnknownSkuFault` complexType: contains `sku` (`xs:string`)
- **Messages**
  - `GetStockLevelInput`: part `parameters` → `tns:StockLevelRequest`
  - `GetStockLevelOutput`: part `parameters` → `tns:StockLevelResponse`
  - `UnknownSku`: part `fault` → `tns:UnknownSkuFault`
- **PortType**
  - `StockPortType` with operation `GetStockLevel` (input, output, fault)
- **Binding**
  - `StockSoapBinding` (document style, HTTP transport)
  - Operation `GetStockLevel` with SOAP action `http://shop.example/stock/GetStockLevel`
  - Fault `UnknownSku` mapped to SOAP fault `UnknownSku`
- **Service**
  - `StockService` with port `StockPort` bound to `StockSoapBinding`
  - SOAP address: `https://erp.shop.example/soap/stock`

#### Configuration
- SOAP binding style: `document`
- Transport: `http://schemas.xmlsoap.org/soap/http`
- SOAP action: `http://shop.example/stock/GetStockLevel`
- Service endpoint: `https://erp.shop.example/soap/stock`

#### Dependencies
- XML Schema namespace `http://www.w3.org/2001/XMLSchema`
- Target namespace `http://shop.example/stock` used for types, messages, portType, binding, and service.
