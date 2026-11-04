### other/bpmn
- H: 1.0
- C: BE07010E
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T12:27:05Z
- G: 
- A: 0.0.0
- X: package
- F: [order-fulfilment.bpmn](order-fulfilment.bpmn.ai.md)
---
> Provides a BPMN‑based order‑fulfilment workflow that reserves stock, processes payments, approves large orders, ships items, and creates invoices.

#### Purpose
- Model the end‑to‑end order fulfilment process.
- Coordinate stock reservation, payment receipt, approval, shipping, and invoicing.

#### Responsibilities
- **Order Placement** – start event triggers the workflow.
- **Stock Management** – service task `reserveStock` with error handling for out‑of‑stock.
- **Payment Handling** – intermediate catch event waiting for `msgPaymentReceived`.
- **Order Approval** – exclusive gateway splits large vs. small orders; user task for back‑office approval.
- **Shipping** – external task `shipOrder` on topic `shipping`.
- **Invoicing** – call activity invoking the `invoicing` process.
- **Completion** – end events for successful or cancelled orders.

#### Key units
- **Process** `orderFulfilment` – executable BPMN process.
- **Delegate** `shop.bpm.ReserveStockDelegate` – Java class for stock reservation.
- **Message** `msgPaymentReceived` – signals payment receipt.
- **Error** `errOutOfStock` – triggers cancellation when stock is unavailable.
- **Topic** `shipping` – external task queue for shipping service.
- **Called Element** `invoicing` – sub‑process for invoice creation.

#### Data flow
1. Order placed → `reserveStock` (delegate) → success or `errOutOfStock`.
2. On success, wait for `msgPaymentReceived`.
3. Evaluate `order.total` at `amountCheck` gateway.
4. Large orders → `approveOrder` (back‑office); small orders → `shipOrder` (external).
5. After approval or shipping, call `createInvoice` (invoicing).
6. End with `orderCompleted` or `orderCancelled`.

#### Dependencies
- **Internal**: BPMN definitions, diagram layout (`bpmndi`, `dc`).
- **External**: Java delegate `shop.bpm.ReserveStockDelegate`, external task topic `shipping`, called process `invoicing`.

#### Cross-cutting
- Error handling via boundary event on `reserveStock`.
- Conditional expressions `${order.total > 1000}` and `${order.total <= 1000}`.
- Camunda extensions (`camunda:class`, `camunda:type`, `camunda:topic`, `camunda:candidateGroups`).
- Single executable process with clear start and end events.
