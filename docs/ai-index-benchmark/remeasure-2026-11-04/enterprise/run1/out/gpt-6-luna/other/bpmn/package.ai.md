### other/bpmn
- H: 1.0
- C: BE07010E
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T08:08:39Z
- G: 
- A: 0.0.0
- X: package
- F: [order-fulfilment.bpmn](order-fulfilment.bpmn.ai.md)
---
> Coordinates order fulfilment from stock reservation and payment receipt through approval, shipping, invoicing, or out-of-stock cancellation.

#### Purpose
- Defines an executable BPMN process for order completion and cancellation.
- Applies amount-based approval routing before shipping.

#### Responsibilities
- Reserve stock and cancel the order when stock is unavailable.
- Wait for payment, route orders over 1000 to back-office approval, and ship eligible or approved orders.
- Invoke invoicing after shipping and mark the order complete.

#### Key units
- `orderFulfilment` — executable process coordinating the order lifecycle.
- `reserveStock` — service task bound to `shop.bpm.ReserveStockDelegate`.
- `amountCheck` — exclusive gateway routing orders by `order.total`.
- `approveOrder` — user task assigned to candidate group `backoffice`.
- `shipOrder` — external task using topic `shipping`.
- `createInvoice` — call activity invoking `invoicing`.
- `waitForPayment` — message catch event waiting for `PaymentReceived`.
- `stockMissing` — boundary error event for `OutOfStock` (`STOCK_EMPTY`).

#### Data flow
- `orderPlaced` starts the process; `reserveStock` runs before `waitForPayment`.
- After payment, `amountCheck` routes orders over 1000 to `approveOrder`; orders at or below 1000 proceed directly to `shipOrder`.
- Shipping leads to `createInvoice` and `orderCompleted`; an `OutOfStock` error from stock reservation leads to `orderCancelled`.

#### Dependencies
- Java delegate: `shop.bpm.ReserveStockDelegate`.
- External task topic: `shipping`; called process: `invoicing`.
- BPMN message: `PaymentReceived`; BPMN error: `OutOfStock` with code `STOCK_EMPTY`.

#### Cross-cutting
- Camunda configuration binds the stock delegate, shipping topic, and back-office candidate group.
- Routing uses the conditions `${order.total > 1000}` and `${order.total <= 1000}`.
