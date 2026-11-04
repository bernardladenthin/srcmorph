### other/bpmn
- H: 1.0
- C: BE07010E
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T12:30:48Z
- G: 
- A: 0.0.0
- X: package
- F: [order-fulfilment.bpmn](order-fulfilment.bpmn.ai.md)
---
> Coordinates order fulfilment from stock reservation and payment through approval, shipping, invoicing, or out-of-stock cancellation.

#### Purpose
- Defines the executable `orderFulfilment` lifecycle, including payment handling and amount-based approval.
- Routes orders to completion or cancellation based on stock availability.

#### Responsibilities
- **Order processing:** Reserve stock, wait for `PaymentReceived`, evaluate order total, and ship approved or small orders.
- **Exceptions and completion:** Cancel orders on `OutOfStock`; otherwise call invoicing and complete the process.

#### Key units
- `orderFulfilment` — executable BPMN process for the order lifecycle.
- `reserveStock` — service task bound to `shop.bpm.ReserveStockDelegate`, with boundary error `stockMissing`.
- `amountCheck` — exclusive gateway routing totals over `1000` to `approveOrder` and totals at or below `1000` directly to shipping.
- `approveOrder` — user task assigned to candidate group `backoffice`.
- `shipOrder` — external task using topic `shipping`.
- `createInvoice` — call activity invoking process element `invoicing`.
- `msgPaymentReceived` — message caught while the process waits for payment.
- `errOutOfStock` — error with code `STOCK_EMPTY`, leading to `orderCancelled`.

#### Data flow
- `orderPlaced` starts the process and triggers stock reservation.
- Successful reservation leads to a wait for `PaymentReceived`, then order-total routing; large orders require approval before shipping.
- Shipping calls `invoicing` and ends at `orderCompleted`; a stock error instead leads to `orderCancelled`.

#### Dependencies
- Java delegate: `shop.bpm.ReserveStockDelegate`.
- External task worker topic: `shipping`.
- Called process element: `invoicing`.
- BPMN message and error: `msgPaymentReceived`, `errOutOfStock`.

#### Cross-cutting
- Routing conditions use `${order.total > 1000}` and `${order.total <= 1000}`.
- Approval is assigned to `backoffice`; the process is marked executable.
