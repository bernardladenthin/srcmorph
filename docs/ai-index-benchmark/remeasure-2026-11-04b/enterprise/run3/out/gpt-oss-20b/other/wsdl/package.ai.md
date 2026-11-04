### other/wsdl
- H: 1.0
- C: DD12C338
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T12:43:08Z
- G: 
- A: 0.0.0
- X: package
- F: [StockService.wsdl](StockService.wsdl.ai.md)
---
> Provides a SOAP web service for querying stock levels by SKU, with fault handling for unknown SKUs.

#### Purpose
- Exposes a `GetStockLevel` operation over SOAP.
- Supplies a fault for unknown SKUs.

#### Responsibilities
- **Service definition**: WSDL 1.1 file declaring request/response types, fault, and service endpoint.
- **Data contract**: XML Schema types for request, response, and fault payloads.
- **Transport configuration**: SOAP binding, action, and endpoint URL.

#### Key units
- **StockService.wsdl** – root `definitions` element, contains schema, messages, portType, binding, and service.
- **Schema** (`http://shop.example/stock`):  
  - `StockLevelRequest` (sku string)  
  - `StockLevelResponse` (available int)  
  - `UnknownSkuFault` (sku string)
- **Messages**: `GetStockLevelInput`, `GetStockLevelOutput`, `UnknownSku`.
- **PortType** `StockPortType` with operation `GetStockLevel`.
- **Binding** `StockSoapBinding` (document style, HTTP transport).
- **Service** `StockService` exposing `StockPort` at `https://erp.shop.example/soap/stock`.

#### Data flow
1. Client sends `StockLevelRequest` (SKU) to `https://erp.shop.example/soap/stock`.
2. Service processes request and returns `StockLevelResponse` (available quantity) or raises `UnknownSkuFault` if SKU not found.

#### Dependencies
- XML Schema namespace `http://www.w3.org/2001/XMLSchema`.
- SOAP namespace `http://schemas.xmlsoap.org/wsdl/soap/`.
- Target namespace `http://shop.example/stock` for all types, messages, portType, binding, and service.

#### Cross-cutting
- All messages use `use="literal"` SOAP body.
- Fault handling follows SOAP 1.1 fault conventions with a custom `UnknownSkuFault` type.
