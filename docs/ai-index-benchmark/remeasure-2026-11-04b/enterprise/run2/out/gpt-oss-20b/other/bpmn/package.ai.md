### other/bpmn
- H: 1.0
- C: BE07010E
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T12:36:21Z
- G: 
- A: 0.0.0
- X: package
- F: [order-fulfilment.bpmn](order-fulfilment.bpmn.ai.md)
---
> A BPMN‑based order‑fulfilment workflow that reserves stock, waits for payment, handles large‑order approval, ships goods, and creates invoices.

#### Purpose
- Model the end‑to‑end order fulfilment process.
- Coordinate stock reservation, payment receipt, approval, shipping, and invoicing.

#### Responsibilities
- **Process orchestration** – defines the sequence of tasks, gateways, and events.
- **Error handling** – manages out‑of‑stock scenarios.
- **External integration** – delegates stock reservation, external shipping, and invoicing services.

#### Key units
- **Process** `orderFulfilment` – executable BPMN flow for order handling.
- **ServiceTask** `reserveStock` – Java delegate `shop.bpm.ReserveStockDelegate`.
- **BoundaryEvent** `stockMissing` – triggers `errOutOfStock`.
- **IntermediateCatchEvent** `waitForPayment` – listens for `msgPaymentReceived`.
- **ExclusiveGateway** `amountCheck` – routes based on `${order.total > 1000}`.
- **UserTask** `approveOrder` – assigned to `backoffice` group.
- **ServiceTask** `shipOrder` – external task on topic `shipping`.
- **CallActivity** `createInvoice` – invokes `invoicing` service/process.
- **Messages** `msgPaymentReceived` – payment notification.
- **Errors** `errOutOfStock` – out‑of‑stock signal.

#### Data flow
1. **OrderPlaced** → `reserveStock` (Java delegate) → success → `waitForPayment`.
2. On **PaymentReceived** → `amountCheck` →  
   - If total > 1000 → `approveOrder` → `shipOrder`.  
   - Else → `shipOrder` directly.
3. `shipOrder` (external task) → `createInvoice` (call activity) → `orderCompleted`.
4. If `errOutOfStock` occurs → `orderCancelled`.

#### Dependencies
- Java class `shop.bpm.ReserveStockDelegate`.
- External task topic `shipping`.
- Called element `invoicing` (another BPMN or service).
- Camunda BPM engine (namespaces `bpmn`, `camunda`).

#### Cross‑cutting
- **Camunda annotations** (`camunda:class`, `camunda:type`, `camunda:topic`, `camunda:candidateGroups`) drive task behavior.
- **Error boundary** on `reserveStock` ensures graceful cancellation.
- **Message event** `msgPaymentReceived` coordinates asynchronous payment confirmation.
