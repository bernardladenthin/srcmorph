### other/bpmn
- H: 1.0
- C: BE07010E
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T12:24:18Z
- G: 
- A: 0.0.0
- X: package
- F: [order-fulfilment.bpmn](order-fulfilment.bpmn.ai.md)
---
> Coordinates order fulfilment from stock reservation and payment through approval, shipping, invoicing, or out-of-stock cancellation.

#### Purpose
- Defines the executable `orderFulfilment` process for completing orders or cancelling them when stock is unavailable.
- Connects payment receipt, stock reservation, large-order approval, external shipping, and invoice creation.

#### Responsibilities
- **Order and stock handling:** Start on `orderPlaced`, reserve stock via a Java delegate, and cancel on the `OutOfStock` boundary error.
- **Payment and routing:** Wait for `PaymentReceived`, then route orders by `order.total`; large orders require back-office approval.
- **Fulfilment completion:** Ship orders using the `shipping` external task topic and invoke the `invoicing` process.

#### Key units
- `orderFulfilment` — executable BPMN process coordinating the order lifecycle.
- `reserveStock` — service task bound to `shop.bpm.ReserveStockDelegate`.
- `waitForPayment` — message catch event for `PaymentReceived`.
- `amountCheck` — exclusive gateway routing orders above or at/below `1000`.
- `approveOrder` — user task assigned to candidate group `backoffice`.
- `shipOrder` — external service task using topic `shipping`.
- `createInvoice` — call activity invoking process element `invoicing`.
- `stockMissing` — boundary error event for `OutOfStock` (`STOCK_EMPTY`), leading to `orderCancelled`.

#### Data flow
- `orderPlaced` → `reserveStock` → `waitForPayment` → `amountCheck`.
- Orders with `order.total > 1000` pass through `approveOrder`; orders with `order.total <= 1000` proceed directly to `shipOrder`.
- Shipping → `createInvoice` → `orderCompleted`; a stock error at `reserveStock` leads to `orderCancelled`.

#### Dependencies
- `shop.bpm.ReserveStockDelegate` for stock reservation.
- `PaymentReceived` message and `OutOfStock` error definitions.
- External worker subscribed to the `shipping` topic.
- Called process element `invoicing`.

#### Cross-cutting
- BPMN definitions use the `http://shop.example/bpmn` target namespace and Camunda extensions for delegate, candidate group, and external-task configuration.
- Gateway routing is expressed using `${order.total > 1000}` and `${order.total <= 1000}` conditions.
