### other/bpmn
- H: 1.0
- C: BE07010E
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T08:11:36Z
- G: 
- A: 0.0.0
- X: package
- F: [order-fulfilment.bpmn](order-fulfilment.bpmn.ai.md)
---
> Provides a Camunda‑based BPMN workflow for end‑to‑end order fulfilment, coordinating stock reservation, payment, approval, shipping, and invoicing.

#### Purpose
- Model the complete order fulfilment lifecycle.
- Integrate external services (stock, payment, shipping) and internal approval.

#### Responsibilities
- **Order Placement** – entry point for new orders.
- **Stock Management** – reserve inventory and handle out‑of‑stock errors.
- **Payment Coordination** – wait for payment confirmation.
- **Large‑Order Approval** – gate for orders over a threshold.
- **Shipping** – trigger external shipping service.
- **Invoicing** – invoke separate invoicing process.
- **Completion / Cancellation** – finalise or abort the order.

#### Key units
- **Process** `orderFulfilment` – orchestrates all tasks and events.
- **Service Task** `reserveStock` (`shop.bpm.ReserveStockDelegate`) – reserves inventory.
- **Boundary Event** `stockMissing` – error handler for `errOutOfStock`.
- **Intermediate Catch Event** `waitForPayment` – listens for `msgPaymentReceived`.
- **Exclusive Gateway** `amountCheck` – routes based on `${order.total > 1000}`.
- **User Task** `approveOrder` – back‑office approval for large orders.
- **Service Task** `shipOrder` – external topic `shipping`.
- **Call Activity** `createInvoice` – delegates to `invoicing` process.
- **End Events** `orderCompleted`, `orderCancelled` – final states.

#### Data flow
1. **Start** at `orderPlaced`.
2. **Reserve stock** → if `errOutOfStock` → `orderCancelled`.
3. **Wait for payment** via `msgPaymentReceived`.
4. **Check amount** → large → `approveOrder`; small → `shipOrder`.
5. **Ship order** → external `shipping` topic.
6. **Create invoice** → call `invoicing`.
7. **End** with `orderCompleted` or `orderCancelled`.

#### Dependencies
- **External Java delegate** `shop.bpm.ReserveStockDelegate`.
- **External topic** `shipping` for shipping service.
- **Called BPMN** `invoicing` process.
- **Camunda BPM engine** (via `camunda` namespace).

#### Cross-cutting
- **Camunda annotations** (`camunda:class`, `camunda:type`, `camunda:topic`, `camunda:candidateGroups`).
- **Error handling** via boundary event `stockMissing` tied to `errOutOfStock`.
- **Message correlation** with `msgPaymentReceived`.
- **Conditional routing** using `${order.total > 1000}` expressions.
