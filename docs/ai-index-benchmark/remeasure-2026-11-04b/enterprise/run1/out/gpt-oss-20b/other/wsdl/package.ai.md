### other/wsdl
- H: 1.0
- C: DD12C338
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T12:27:55Z
- G: 
- A: 0.0.0
- X: package
- F: [StockService.wsdl](StockService.wsdl.ai.md)
---
> Provides a SOAP web service for querying stock levels by SKU.

#### Purpose
- Exposes a `GetStockLevel` operation that returns the available quantity for a given SKU.
- Declares an `UnknownSku` fault for SKUs that are not found.

#### Responsibilities
- **Service contract**: defines the WSDL structure, messages, port type, binding, and service endpoint.
- **Data model**: declares request/response elements and fault type using XML Schema.
- **Error handling**: specifies a fault message for unknown SKUs.

#### Key units
- **`StockService.wsdl`** – WSDL 1.1 document defining the stock query service.
- **`StockLevelRequest` / `StockLevelResponse`** – XSD elements for request and response payloads.
- **`UnknownSkuFault`** – XSD complex type for the fault payload.
- **`GetStockLevel` operation** – port type operation with input, output, and fault messages.
- **`StockSoapBinding`** – SOAP binding configuration (document style, HTTP transport).
- **`StockService`** – service definition with endpoint `https://erp.shop.example/soap/stock`.

#### Data flow
1. Client sends a SOAP request containing a `StockLevelRequest` (SKU).
2. Service processes the request and returns a `StockLevelResponse` with the available quantity.
3. If the SKU is unknown, the service returns an `UnknownSkuFault` via the `UnknownSku` fault message.

#### Dependencies
- XML Schema namespace `http://www.w3.org/2001/XMLSchema` for type definitions.
- SOAP namespace `http://schemas.xmlsoap.org/wsdl/soap/` for binding and address.
- HTTP transport `http://schemas.xmlsoap.org/wsdl/soap/http` for message exchange.

#### Cross-cutting
- Uses WSDL 1.1 document style SOAP binding.
- Fault handling follows standard SOAP fault structure with literal encoding.
