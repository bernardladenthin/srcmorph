### other/bpmn
- H: 1.0
- C: BE07010E
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T08:24:12Z
- G: 
- A: 0.0.0
- X: package
- F: [order-fulfilment.bpmn](order-fulfilment.bpmn.ai.md)
---
> Provides a BPMN‑based workflow for handling order fulfilment, from stock reservation through payment, approval, shipping, invoicing, and cancellation.

#### Purpose
- Model the end‑to‑end order fulfilment process.
- Coordinate stock, payment, approval, shipping, and invoicing steps.

#### Responsibilities
- **Order lifecycle management**: start, payment wait, approval, shipping, invoicing, cancellation.
- **Stock handling**: reserve stock and react to out‑of‑stock errors.
- **Payment coordination**: wait for external payment message.
- **Large‑order approval**: gate for orders over €1,000.
- **External task integration**: ship order via external topic.
- **Sub‑process invocation**: call invoicing service.

#### Key units
- **Process** `orderFulfilment` – orchestrates all tasks and events.
- **Service task** `reserveStock` – Java delegate `shop.bpm.ReserveStockDelegate`.
- **User task** `approveOrder` – backoffice approval.
- **External task** `shipOrder` – topic `shipping`.
- **Call activity** `createInvoice` – invokes `invoicing` element.
- **Message** `msgPaymentReceived` – triggers payment wait.
- **Error** `errOutOfStock` – handles stock shortages.
- **Gateway** `amountCheck` – routes based on `${order.total > 1000}`.

#### Data flow
1. **Start**: `orderPlaced` event → `reserveStock`.
2. **Stock reservation**: on success → `waitForPayment`; on error (`errOutOfStock`) → `orderCancelled`.
3. **Payment wait**: receives `msgPaymentReceived` → `amountCheck`.
4. **Amount check**: if large → `approveOrder`; if small → `shipOrder`.
5. **Approval**: after `approveOrder` → `shipOrder`.
6. **Shipping**: external task completes → `createInvoice`.
7. **Invoicing**: call activity completes → `orderCompleted`.

#### Dependencies
- **Java delegate**: `shop.bpm.ReserveStockDelegate`.
- **External task topic**: `shipping`.
- **Called element**: `invoicing` (sub‑process or external service).
- **Message event**: `msgPaymentReceived`.
- **Error event**: `errOutOfStock`.

#### Cross-cutting
- Camunda BPMN 2.0 XML with `camunda` namespace for service, user, external, and call tasks.
- Conditional expressions `${order.total > 1000}` and `${order.total <= 1000}` for gateway routing.
- Error handling via boundary event attached to `reserveStock`.
