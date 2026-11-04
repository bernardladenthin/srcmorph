### StockService.wsdl
- H: 1.0
- C: A034A9B4
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T08:11:10Z
- G: 
- A: 0.0.0
- X: file
- P: 326D0B29
- N: EC197B72
---
**Facts (exact, whole file):** elements: 38; namespace declarations: 4; id and name attributes: 22; XSD type definitions: 1; XSLT templates: 0; Spring beans: 0; services: 1; ports and endpoints: 1; port types and interfaces: 1; bindings: 1; operations: 2; messages: 3; SOAP bindings: 1

> Provides a SOAP web service for querying stock levels by SKU.

#### Purpose
- Exposes a `GetStockLevel` operation to retrieve available stock for a given SKU.
- Defines a fault for unknown SKUs.

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
  - Elements:
    - `StockLevelRequest` – complex type with `sku` (`xs:string`)
    - `StockLevelResponse` – complex type with `available` (`xs:int`)
  - ComplexType:
    - `UnknownSkuFault` – sequence with `sku` (`xs:string`)
- **Messages**:
  - `GetStockLevelInput` – part `parameters` element `tns:StockLevelRequest`
  - `GetStockLevelOutput` – part `parameters` element `tns:StockLevelResponse`
  - `UnknownSku` – part `fault` type `tns:UnknownSkuFault`
- **PortType** `StockPortType`:
  - Operation `GetStockLevel`:
    - Input: `tns:GetStockLevelInput`
    - Output: `tns:GetStockLevelOutput`
    - Fault: `UnknownSku` message `tns:UnknownSku`
- **Binding** `StockSoapBinding` (type `tns:StockPortType`):
  - `soap:binding` style `document`, transport `http://schemas.xmlsoap.org/soap/http`
  - Operation `GetStockLevel`:
    - `soap:operation` soapAction `http://shop.example/stock/GetStockLevel`
    - Input/Output: `soap:body` use `literal`
    - Fault `UnknownSku`: `soap:fault` name `UnknownSku` use `literal`
- **Service** `StockService`:
  - Port `StockPort` binding `tns:StockSoapBinding`
  - `soap:address` location `https://erp.shop.example/soap/stock`

#### Configuration
- `soap:binding` style: `document`
- `soap:binding` transport: `http://schemas.xmlsoap.org/soap/http`
- `soap:operation` soapAction: `http://shop.example/stock/GetStockLevel`
- `soap:address` location: `https://erp.shop.example/soap/stock`

#### Dependencies
- XML Schema namespace `http://www.w3.org/2001/XMLSchema`
- SOAP namespace `http://schemas.xmlsoap.org/wsdl/soap/`
- Target namespace `http://shop.example/stock` (used for elements, types, messages, portType, binding, service).
