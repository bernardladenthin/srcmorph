### StockService.wsdl
- H: 1.0
- C: A034A9B4
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T12:26:43Z
- G: 
- A: 0.0.0
- X: file
- P: D9E787D0
- N: EC197B72
---
**Facts (exact, whole file):** elements: 38; namespace declarations: 4; id and name attributes: 22; XSD type definitions: 1; XSLT templates: 0; Spring beans: 0; services: 1; ports and endpoints: 1; port types and interfaces: 1; bindings: 1; operations: 2; messages: 3; SOAP bindings: 1

> Defines a SOAP web service for querying stock levels by SKU.

#### Purpose
- Exposes a `GetStockLevel` operation that returns available quantity.
- Declares a fault for unknown SKUs.

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
  - `xs:element` `StockLevelRequest` – contains `sku` (`xs:string`).
  - `xs:element` `StockLevelResponse` – contains `available` (`xs:int`).
  - `xs:complexType` `UnknownSkuFault` – contains `sku` (`xs:string`).
- **Messages**
  - `GetStockLevelInput` – part `parameters` of element `tns:StockLevelRequest`.
  - `GetStockLevelOutput` – part `parameters` of element `tns:StockLevelResponse`.
  - `UnknownSku` – fault part `fault` of type `tns:UnknownSkuFault`.
- **PortType**
  - `StockPortType` with operation `GetStockLevel`:
    - input `tns:GetStockLevelInput`
    - output `tns:GetStockLevelOutput`
    - fault `UnknownSku` with message `tns:UnknownSku`.
- **Binding**
  - `StockSoapBinding` (document style, HTTP transport):
    - operation `GetStockLevel`:
      - `soapAction` `http://shop.example/stock/GetStockLevel`
      - input body `use="literal"`
      - output body `use="literal"`
      - fault `UnknownSku` with `soap:fault` `use="literal"`.
- **Service**
  - `StockService` with port `StockPort` bound to `tns:StockSoapBinding`:
    - `soap:address` location `https://erp.shop.example/soap/stock`.

#### Configuration
- `soap:binding` style `document`, transport `http://schemas.xmlsoap.org/soap/http`.
- `soap:operation` action `http://shop.example/stock/GetStockLevel`.
- `soap:address` location `https://erp.shop.example/soap/stock`.

#### Dependencies
- XML Schema namespace `http://www.w3.org/2001/XMLSchema` for type definitions.
- SOAP namespace `http://schemas.xmlsoap.org/wsdl/soap/` for binding and address.
