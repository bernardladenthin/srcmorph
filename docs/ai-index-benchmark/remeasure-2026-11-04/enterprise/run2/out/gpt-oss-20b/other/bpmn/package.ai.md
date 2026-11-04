### other/bpmn
- H: 1.0
- C: BE07010E
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T08:18:07Z
- G: 
- A: 0.0.0
- X: package
- F: [order-fulfilment.bpmn](order-fulfilment.bpmn.ai.md)
---
> Provides a Camunda‑based BPMN workflow for end‑to‑end order fulfilment, from stock reservation and payment receipt to approval, shipping, invoicing, and error handling.

#### Purpose
- Model and execute the full order fulfilment lifecycle.
- Coordinate stock, payment, approval, shipping, and invoicing steps.

#### Responsibilities
- **Order Processing** – orchestrates reservation, payment wait, and approval.
- **Stock Management** – reserves inventory and handles out‑of‑stock errors.
- **Payment Handling** – waits for a payment‑received message.
- **Approval Workflow** – routes large orders to back‑office approval.
- **Shipping** – triggers external shipping service.
- **Invoicing** – delegates invoice creation to an external process.
- **Error & Cancellation** – manages cancellations and out‑of‑stock scenarios.

#### Key units
- **Process** `orderFulfilment` – main executable BPMN flow.
- **Service Task** `reserveStock` – Java delegate `shop.bpm.ReserveStockDelegate`.
- **Boundary Event** `stockMissing` – error `errOutOfStock` (`STOCK_EMPTY`).
- **Intermediate Catch Event** `waitForPayment` – message `msgPaymentReceived`.
- **Exclusive Gateway** `amountCheck` – splits on `${order.total > 1000}`.
- **User Task** `approveOrder` – candidate group `backoffice`.
- **Service Task** `shipOrder` – external topic `shipping`.
- **Call Activity** `createInvoice` – calls element `invoicing`.
- **End Events** `orderCompleted`, `orderCancelled`.

#### Data flow
1. **Order placed** → `reserveStock` (Java delegate).  
2. If stock missing → `stockMissing` → `orderCancelled`.  
3. On success → `waitForPayment` (message).  
4. After payment → `amountCheck` →  
   - Large order → `approveOrder` (user task) → `shipOrder`.  
   - Small order → `shipOrder` directly.  
5. `shipOrder` → `createInvoice` (call activity).  
6. Completion → `orderCompleted`.

#### Dependencies
- **Java**: `shop.bpm.ReserveStockDelegate` for stock reservation.
- **Camunda BPM**: BPMN engine, external task topic `shipping`, call activity `invoicing`.
- **Messages**: `msgPaymentReceived` for payment notification.
- **Errors**: `errOutOfStock` (`STOCK_EMPTY`) for inventory issues.

#### Cross-cutting
- **Camunda annotations**: `camunda:class`, `camunda:type`, `camunda:topic`, `camunda:candidateGroups`.
- **Conditional expressions**: `${order.total > 1000}` and `${order.total <= 1000}`.
- **Error handling**: boundary event attached to `reserveStock`.
- **External integration**: external task for shipping, call activity for invoicing.
