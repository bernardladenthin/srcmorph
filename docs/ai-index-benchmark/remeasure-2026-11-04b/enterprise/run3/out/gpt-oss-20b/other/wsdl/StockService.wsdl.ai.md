### StockService.wsdl
- H: 1.0
- C: A034A9B4
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T12:42:01Z
- G: 
- A: 0.0.0
- X: file
- P: D9E787D0
- N: EC197B72
---
**Facts (exact, whole file):** elements: 38; namespace declarations: 4; id and name attributes: 22; XSD type definitions: 1; XSLT templates: 0; Spring beans: 0; services: 1; ports and endpoints: 1; port types and interfaces: 1; bindings: 1; operations: 2; messages: 3; SOAP bindings: 1

> Defines a SOAP web service for querying stock levels by SKU, including request/response types and a fault for unknown SKUs.

#### Purpose
- Exposes a `GetStockLevel` operation over SOAP.
- Provides fault handling for unknown SKUs.

#### Type
- WSDL 1.1
- Root element: `definitions`
- Namespaces:
  - `xmlns` → `http://schemas.xmlsoap.org/wsdl/`
  - `xmlns:soap` → `http://schemas.xmlsoap.org/wsdl/soap/`
  - `xmlns:tns` → `http://shop.example/stock`
  - `xmlns:xs` → `http://www.w3.org/2001/XMLSchema`

#### Definitions
- **Schema** (`xs:schema` targetNamespace=`http://shop.example/stock`):
  - Element `StockLevelRequest` (complexType: sequence of `sku` string)
  - Element `StockLevelResponse` (complexType: sequence of `available` int)
  - ComplexType `UnknownSkuFault` (sequence of `sku` string)
- **Messages**:
  - `GetStockLevelInput` – part `parameters` element `tns:StockLevelRequest`
  - `GetStockLevelOutput` – part `parameters` element `tns:StockLevelResponse`
  - `UnknownSku` – part `fault` type `tns:UnknownSkuFault`
- **PortType** `StockPortType`:
  - Operation `GetStockLevel`:
    - Input `tns:GetStockLevelInput`
    - Output `tns:GetStockLevelOutput`
    - Fault `UnknownSku` message `tns:UnknownSku`
- **Binding** `StockSoapBinding` (type `tns:StockPortType`):
  - SOAP binding style `document`, transport `http://schemas.xmlsoap.org/soap/http`
  - Operation `GetStockLevel`:
    - SOAP action `http://shop.example/stock/GetStockLevel`
    - Input body `use="literal"`
    - Output body `use="literal"`
    - Fault `UnknownSku` SOAP fault `use="literal"`
- **Service** `StockService`:
  - Port `StockPort` binding `tns:StockSoapBinding`
  - SOAP address location `https://erp.shop.example/soap/stock`

#### Configuration
- SOAP action: `http://shop.example/stock/GetStockLevel`
- Service endpoint: `https://erp.shop.example/soap/stock`

#### Dependencies
- XML Schema namespace `http://www.w3.org/2001/XMLSchema`
- SOAP namespace `http://schemas.xmlsoap.org/wsdl/soap/`
- Target namespace `http://shop.example/stock` used for types, messages, portType, binding, and service.
