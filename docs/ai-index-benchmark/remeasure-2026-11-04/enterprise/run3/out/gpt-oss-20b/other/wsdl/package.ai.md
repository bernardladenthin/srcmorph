### other/wsdl
- H: 1.0
- C: DD12C338
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T08:25:10Z
- G: 
- A: 0.0.0
- X: package
- F: [StockService.wsdl](StockService.wsdl.ai.md)
---
> Provides a SOAP web service for querying stock availability by SKU, with fault handling for unknown SKUs.

#### Purpose
- Exposes a `GetStockLevel` operation to retrieve available stock for a given SKU.
- Supplies an `UnknownSku` fault for invalid SKUs.

#### Responsibilities
- **Service definition**: WSDL file declares the SOAP service, port, binding, and endpoint.
- **Message contracts**: Defines request/response and fault messages.
- **Schema types**: Declares XML schema types for request, response, and fault payloads.

#### Key units
- **StockService.wsdl** – WSDL document defining the service.
- **StockPortType** – Port type with `GetStockLevel` operation.
- **StockSoapBinding** – SOAP binding (document style, HTTP transport).
- **StockService** – Service exposing `StockPort` at `https://erp.shop.example/soap/stock`.
- **StockLevelRequest / StockLevelResponse** – Complex types for request/response.
- **UnknownSkuFault** – Complex type for fault payload.

#### Data flow
1. Client sends `StockLevelRequest` (SKU) to `StockService` endpoint.
2. Service processes request and returns `StockLevelResponse` (available quantity).
3. If SKU is unknown, service returns `UnknownSkuFault` via SOAP fault.

#### Dependencies
- XML Schema (`http://www.w3.org/2001/XMLSchema`) for type definitions.
- SOAP 1.1 (`http://schemas.xmlsoap.org/wsdl/soap/`) for transport and binding.

#### Cross-cutting
- All messages use `document` style with literal body.
- Fault handling follows standard SOAP fault structure with custom `UnknownSkuFault` payload.
