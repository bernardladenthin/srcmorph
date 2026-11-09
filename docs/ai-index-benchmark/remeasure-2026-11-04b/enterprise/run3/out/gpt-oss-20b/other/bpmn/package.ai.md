### other/bpmn
- H: 1.0
- C: BE07010E
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T12:42:21Z
- G: 
- A: 0.0.0
- X: package
- F: [order-fulfilment.bpmn](order-fulfilment.bpmn.ai.md)
---
> Orchestrates end‑to‑end order fulfilment, from stock reservation and payment capture to approval, shipping, invoicing, and error handling.

#### Purpose
- Model the complete order fulfilment workflow.
- Coordinate stock, payment, approval, shipping, invoicing, and cancellation.

#### Responsibilities
- **Order lifecycle management** – start, payment, approval, shipping, invoicing, completion or cancellation.
- **Stock and payment coordination** – reserve stock, wait for payment, handle out‑of‑stock errors.
- **Approval workflow** – route large orders to back‑office approval.
- **External integration** – invoke shipping via external task and invoicing via call activity.

#### Key units
- **Process** `orderFulfilment` – executable BPMN process driving the workflow.
- **Delegate** `shop.bpm.ReserveStockDelegate` – Java class handling stock reservation.
- **External task topic** `shipping` – Camunda external task for shipping.
- **Call activity** `invoicing` – referenced process for invoice creation.
- **Message** `msgPaymentReceived` – triggers payment arrival.
- **Error** `errOutOfStock` (`STOCK_EMPTY`) – signals insufficient stock.

#### Data flow
1. **Start**: `orderPlaced` event triggers `reserveStock` service task.  
2. **Stock reservation**: `ReserveStockDelegate` reserves stock; on failure, `stockMissing` error event leads to `orderCancelled`.  
3. **Payment wait**: `waitForPayment` catches `msgPaymentReceived`.  
4. **Amount check**: `amountCheck` gateway routes large orders to `approveOrder` (user task) and small orders directly to `shipOrder`.  
5. **Approval**: Back‑office approves large orders, then `shipOrder` is executed.  
6. **Shipping**: External task `shipping` processes shipment.  
7. **Invoicing**: `createInvoice` call activity invokes `invoicing` process.  
8. **Completion**: `orderCompleted` end event; any error path leads to `orderCancelled`.

#### Dependencies
- **Java**: `shop.bpm.ReserveStockDelegate` for stock logic.  
- **Camunda**: external task topic `shipping`; call activity `invoicing`.  
- **Messaging**: `msgPaymentReceived` for payment notification.  
- **Error handling**: `errOutOfStock` for stock shortages.

#### Cross-cutting
- **Camunda annotations**: `camunda:class`, `camunda:candidateGroups`, `camunda:type`, `camunda:topic`, `calledElement`.  
- **Conditional flow**: Gateway uses `${order.total > 1000}` to distinguish large orders.  
- **Error boundary**: `stockMissing` boundary event on `reserveStock`.  
- **Diagram metadata**: BPMN diagram and plane definitions for visual representation.
