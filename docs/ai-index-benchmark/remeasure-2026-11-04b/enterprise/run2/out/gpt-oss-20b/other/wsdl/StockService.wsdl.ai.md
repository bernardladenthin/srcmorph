### StockService.wsdl
- H: 1.0
- C: A034A9B4
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T12:35:29Z
- G: 
- A: 0.0.0
- X: file
- P: D9E787D0
- N: EC197B72
---
**Facts (exact, whole file):** elements: 38; namespace declarations: 4; id and name attributes: 22; XSD type definitions: 1; XSLT templates: 0; Spring beans: 0; services: 1; ports and endpoints: 1; port types and interfaces: 1; bindings: 1; operations: 2; messages: 3; SOAP bindings: 1

> Defines a SOAP web service for querying stock levels, including request/response messages, a fault for unknown SKUs, and the binding and endpoint configuration.

#### Purpose
- Exposes a `GetStockLevel` operation to retrieve stock availability by SKU.
- Provides a fault `UnknownSku` for invalid or missing SKUs.

#### Type
- WSDL 1.1 (`<definitions>` root)
- Namespaces:
  - `xmlns="http://schemas.xmlsoap.org/wsdl/"`
  - `xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/"`
  - `xmlns:tns="http://shop.example/stock"`
  - `xmlns:xs="http://www.w3.org/2001/XMLSchema"`

#### Definitions
- **Schema** (`<xs:schema>` targetNamespace=`http://shop.example/stock`):
  - Elements:
    - `StockLevelRequest` – complex type with `sku` (`xs:string`)
    - `StockLevelResponse` – complex type with `available` (`xs:int`)
  - ComplexType:
    - `UnknownSkuFault` – sequence with `sku` (`xs:string`)
- **Messages**:
  - `GetStockLevelInput` – part `parameters` referencing `tns:StockLevelRequest`
  - `GetStockLevelOutput` – part `parameters` referencing `tns:StockLevelResponse`
  - `UnknownSku` – fault part `fault` of type `tns:UnknownSkuFault`
- **PortType** `StockPortType`:
  - Operation `GetStockLevel`:
    - Input: `tns:GetStockLevelInput`
    - Output: `tns:GetStockLevelOutput`
    - Fault: `UnknownSku` (`tns:UnknownSku`)
- **Binding** `StockSoapBinding` (type `tns:StockPortType`):
  - SOAP binding: style `document`, transport `http://schemas.xmlsoap.org/soap/http`
  - Operation `GetStockLevel`:
    - SOAP action `http://shop.example/stock/GetStockLevel`
    - Input body `use="literal"`
    - Output body `use="literal"`
    - Fault `UnknownSku` with SOAP fault `use="literal"`
- **Service** `StockService`:
  - Port `StockPort` bound to `tns:StockSoapBinding`
  - SOAP address `location="https://erp.shop.example/soap/stock"`

#### Configuration
- SOAP transport: `http://schemas.xmlsoap.org/soap/http`
- SOAP action: `http://shop.example/stock/GetStockLevel`
- Endpoint URL: `https://erp.shop.example/soap/stock`

#### Dependencies
- XML Schema namespace `http://www.w3.org/2001/XMLSchema`
- SOAP namespace `http://schemas.xmlsoap.org/wsdl/soap/`
- Target namespace `http://shop.example/stock` (used for types, messages, binding, service).
