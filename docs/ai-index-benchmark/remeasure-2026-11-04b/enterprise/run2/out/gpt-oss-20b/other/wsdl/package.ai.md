### other/wsdl
- H: 1.0
- C: DD12C338
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T12:37:01Z
- G: 
- A: 0.0.0
- X: package
- F: [StockService.wsdl](StockService.wsdl.ai.md)
---
> Provides a SOAP web service for querying stock availability by SKU, including fault handling for unknown SKUs.

#### Purpose
- Exposes a `GetStockLevel` operation to retrieve stock quantity for a given SKU.
- Supplies an `UnknownSku` fault for invalid or missing SKUs.

#### Responsibilities
- **Service definition**: Declares the WSDL structure, namespaces, and endpoint.
- **Message contracts**: Defines request/response and fault message parts.
- **Operation contract**: Specifies input, output, and fault for `GetStockLevel`.
- **Binding & transport**: Configures SOAP document style over HTTP with literal encoding.
- **Endpoint configuration**: Sets the service URL for client consumption.

#### Key units
- **`StockService.wsdl`** – WSDL 1.1 document defining the service.
- **`StockPortType`** – Port type exposing `GetStockLevel`.
- **`StockSoapBinding`** – SOAP binding for `StockPortType`.
- **`GetStockLevelInput` / `GetStockLevelOutput`** – Message parts for request/response.
- **`UnknownSkuFault`** – Complex type for fault payload.
- **`UnknownSku`** – Fault message part.

#### Data flow
1. Client sends `StockLevelRequest` (containing `sku`) to `https://erp.shop.example/soap/stock`.
2. Service processes request and returns `StockLevelResponse` with `available` quantity.
3. If SKU is invalid, service returns `UnknownSkuFault` via the `UnknownSku` fault.

#### Dependencies
- XML Schema (`http://www.w3.org/2001/XMLSchema`) for type definitions.
- SOAP namespace (`http://schemas.xmlsoap.org/wsdl/soap/`) for binding and transport.
- Target namespace `http://shop.example/stock` for all types, messages, and service elements.

#### Cross-cutting
- Uses WSDL 1.1 document structure with a single port and SOAP 1.1 binding.
- All messages and faults are defined with literal XML encoding.
- Endpoint URL is hard‑coded, implying a fixed deployment location.
