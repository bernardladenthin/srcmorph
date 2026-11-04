### other/bpmn
- H: 1.0
- C: BE07010E
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T08:15:09Z
- G: 
- A: 0.0.0
- X: package
- F: [order-fulfilment.bpmn](order-fulfilment.bpmn.ai.md)
---
> Coordinates order fulfilment from stock reservation and payment through approval, shipping, invoicing, or out-of-stock cancellation.

#### Purpose
- Defines an executable BPMN workflow for completing or cancelling an order.
- Integrates stock reservation, payment receipt, large-order approval, shipping, and invoice creation.

#### Responsibilities
- **Order processing:** Starts on order placement, reserves stock, and waits for payment.
- **Routing and fulfilment:** Routes orders over 1000 for back-office approval; smaller orders proceed directly to shipping. Both paths continue to invoicing.
- **Exception handling:** Catches an out-of-stock error during stock reservation and ends the order as cancelled.

#### Key units
- `orderFulfilment` — executable BPMN process named `Order fulfilment`.
- `reserveStock` — service task bound to `shop.bpm.ReserveStockDelegate`.
- `waitForPayment` — intermediate catch event for `PaymentReceived`.
- `amountCheck` — exclusive gateway routing by `order.total` threshold.
- `approveOrder` — user task assigned to candidate group `backoffice`.
- `shipOrder` — external task using the `shipping` topic.
- `createInvoice` — call activity invoking `invoicing`.
- `stockMissing` / `errOutOfStock` — boundary error handling for `STOCK_EMPTY`, leading to `orderCancelled`.

#### Data flow
- `orderPlaced` → `reserveStock` → `waitForPayment` → `amountCheck`.
- Orders with `order.total > 1000` go through `approveOrder`; orders with `order.total <= 1000` proceed directly to `shipOrder`.
- Shipping → `createInvoice` → `orderCompleted`.
- An `OutOfStock` error during reservation → `orderCancelled`.

#### Dependencies
- Camunda BPMN extensions for the `shipping` external task topic and `backoffice` candidate group.
- Delegate class `shop.bpm.ReserveStockDelegate`.
- Called BPMN element `invoicing`; message `PaymentReceived` and error `OutOfStock`.

#### Cross-cutting
- Uses Camunda BPMN configuration, including an external task binding and a user-task candidate group.
- Routes and handles outcomes through BPMN sequence flows, an exclusive gateway, and a boundary error event.
