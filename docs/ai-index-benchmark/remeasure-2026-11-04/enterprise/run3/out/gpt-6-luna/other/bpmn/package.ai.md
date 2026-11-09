### other/bpmn
- H: 1.0
- C: BE07010E
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T08:21:22Z
- G: 
- A: 0.0.0
- X: package
- F: [order-fulfilment.bpmn](order-fulfilment.bpmn.ai.md)
---
> Orchestrates order fulfilment from stock reservation and payment through approval, shipping, invoicing, or cancellation when stock is unavailable.

#### Purpose
- Defines an executable BPMN process for completing or cancelling customer orders.
- Routes orders by total amount and coordinates payment, human approval, shipping, and invoicing.

#### Responsibilities
- Stock handling: reserve stock and cancel the order when the `OutOfStock` error is caught.
- Order processing: wait for `PaymentReceived`, route large orders for back-office approval, and send orders to shipping.
- Completion: call the invoicing process and end the order as completed.

#### Key units
- `orderFulfilment`: executable BPMN process for the order lifecycle.
- `reserveStock`: service task bound to `shop.bpm.ReserveStockDelegate`.
- `amountCheck`: exclusive gateway routing orders above or at/below 1000.
- `approveOrder`: user task assigned to candidate group `backoffice`.
- `shipOrder`: external task on topic `shipping`.
- `createInvoice`: call activity invoking process `invoicing`.
- `msgPaymentReceived` / `errOutOfStock`: payment message and stock-empty error definitions.

#### Data flow
- Order placement triggers stock reservation; catching `errOutOfStock` at the reservation task leads to order cancellation.
- The process waits for `msgPaymentReceived`, then evaluates `order.total`: amounts above 1000 require approval, while amounts at or below 1000 proceed directly to shipping.
- Shipping proceeds to `invoicing`, then the process ends as `Order completed`.

#### Dependencies
- Java delegate: `shop.bpm.ReserveStockDelegate`.
- Called BPMN process: `invoicing`.
- External task topic: `shipping`.
- BPMN message `PaymentReceived` and error code `STOCK_EMPTY`.
