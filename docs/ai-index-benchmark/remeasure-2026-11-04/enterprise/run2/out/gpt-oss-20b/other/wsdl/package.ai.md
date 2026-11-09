### other/wsdl
- H: 1.0
- C: DD12C338
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T08:18:49Z
- G: 
- A: 0.0.0
- X: package
- F: [StockService.wsdl](StockService.wsdl.ai.md)
---
> Provides a SOAP web service for querying stock availability by SKU, exposing a single operation and fault handling.

#### Purpose
- Expose a `GetStockLevel` operation to retrieve available stock for a given SKU.
- Return an `UnknownSku` fault when the SKU is not found.

#### Responsibilities
- **Service contract**: Define request/response types and fault for stock queries.
- **Transport binding**: Configure SOAP document style over HTTP.
- **Endpoint exposure**: Publish the service at a fixed HTTPS URL.

#### Key units
- **`StockService.wsdl`** – WSDL 1.1 definition of the stock query service.
- **`StockLevelRequest`** – request element with `sku` string.
- **`StockLevelResponse`** – response element with `available` int.
- **`UnknownSkuFault`** – fault type containing the offending `sku`.
- **`StockPortType`** – port type with `GetStockLevel` operation.
- **`StockSoapBinding`** – SOAP binding mapping the operation and fault.
- **`StockService`** – service exposing the `StockPort` endpoint.

#### Data flow
1. Client sends a SOAP message to `https://erp.shop.example/soap/stock` with a `StockLevelRequest` containing a SKU.
2. Service processes the request and returns a `StockLevelResponse` with the available quantity.
3. If the SKU is unknown, the service returns a SOAP fault mapped to `UnknownSkuFault`.

#### Dependencies
- XML Schema (`http://www.w3.org/2001/XMLSchema`) for type definitions.
- Target namespace `http://shop.example/stock` for all WSDL elements.
- SOAP over HTTP transport (`http://schemas.xmlsoap.org/wsdl/soap/`).

#### Cross-cutting
- Consistent use of the `http://shop.example/stock` namespace across types, messages, portType, binding, and service.
- Document-style SOAP binding with a single HTTP transport.
- Fault handling via a dedicated `UnknownSkuFault` complex type.
