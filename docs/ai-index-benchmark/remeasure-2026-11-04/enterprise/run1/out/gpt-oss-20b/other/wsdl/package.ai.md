### other/wsdl
- H: 1.0
- C: DD12C338
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T08:12:29Z
- G: 
- A: 0.0.0
- X: package
- F: [StockService.wsdl](StockService.wsdl.ai.md)
---
> Provides a SOAP web service for querying stock levels by SKU.

#### Purpose
- Exposes a `GetStockLevel` operation to retrieve available stock for a given SKU.
- Defines a fault for unknown SKUs.

#### Responsibilities
- **Service contract**: WSDL definition of the stock query service.
- **Data model**: XML schema types for request, response, and fault payloads.
- **Transport configuration**: SOAP binding and endpoint details.

#### Key units
- **`StockService.wsdl`** – WSDL 1.1 document defining the service.
- **`StockLevelRequest`** – complex type with `sku` string.
- **`StockLevelResponse`** – complex type with `available` int.
- **`UnknownSkuFault`** – fault type containing the offending `sku`.
- **`GetStockLevel` operation** – port type operation with input, output, and fault messages.
- **`StockSoapBinding`** – SOAP binding (document style, HTTP transport) for the service.
- **`StockService`** – service endpoint at `https://erp.shop.example/soap/stock`.

#### Data flow
1. Client sends a SOAP request containing a `StockLevelRequest` (SKU).
2. Service processes the request and returns a `StockLevelResponse` with the available quantity.
3. If the SKU is unknown, the service returns a `UnknownSkuFault` fault.

#### Dependencies
- XML Schema (`http://www.w3.org/2001/XMLSchema`) for type definitions.
- SOAP (`http://schemas.xmlsoap.org/wsdl/soap/`) for binding and transport.
- Target namespace `http://shop.example/stock` used throughout the WSDL.

#### Cross-cutting
- **Document style SOAP**: all messages use literal XML bodies.
- **Fault handling**: single fault type `UnknownSkuFault` for missing SKUs.
- **Endpoint configuration**: fixed HTTPS address for the service.
