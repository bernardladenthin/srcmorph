### other/bpmn
- H: 1.0
- C: BE07010E
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T12:39:30Z
- G: 
- A: 0.0.0
- X: package
- F: [order-fulfilment.bpmn](order-fulfilment.bpmn.ai.md)
---
> Coordinates order fulfilment from stock reservation and payment through approval, shipping, invoicing, or cancellation when stock is unavailable.

#### Purpose
- Defines the executable `orderFulfilment` process and its order-completion and out-of-stock paths.
- Routes large orders for back-office approval before shipping.

#### Responsibilities
- **Order intake and stock handling:** Start on order placement, reserve stock, and cancel the order if `OutOfStock` is raised.
- **Payment and approval:** Wait for `PaymentReceived`; route orders over 1000 to `backoffice` approval and smaller orders directly to shipping.
- **Fulfilment and billing:** Dispatch shipping as an external task, call the `invoicing` process, and complete the order.

#### Key units
- `orderFulfilment` — executable BPMN process for the order lifecycle.
- `reserveStock` — service task bound to `shop.bpm.ReserveStockDelegate`; its `stockMissing` boundary event catches `errOutOfStock`.
- `amountCheck` — exclusive gateway routing by `order.total` threshold of 1000.
- `approveOrder` — user task assigned to candidate group `backoffice`.
- `shipOrder` — external task on topic `shipping`.
- `createInvoice` — call activity invoking `invoicing`.
- `msgPaymentReceived` / `errOutOfStock` — payment message and stock error (`STOCK_EMPTY`).

#### Data flow
- `orderPlaced` → `reserveStock` → wait for `PaymentReceived` → evaluate `order.total`.
- Orders over 1000 pass through `approveOrder`; other orders proceed directly to `shipOrder`.
- Shipping → `createInvoice` → `orderCompleted`; a stock error instead routes to `orderCancelled`.

#### Dependencies
- `shop.bpm.ReserveStockDelegate` for stock reservation.
- External task worker topic `shipping`.
- Called process `invoicing`.
- BPMN message `msgPaymentReceived` and error `errOutOfStock`.
