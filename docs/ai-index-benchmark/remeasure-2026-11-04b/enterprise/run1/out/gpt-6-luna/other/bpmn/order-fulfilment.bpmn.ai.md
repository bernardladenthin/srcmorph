### order-fulfilment.bpmn
- H: 1.0
- C: 15AAB284
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T12:22:23Z
- G: 
- A: 0.0.0
- X: file
- P: D9E787D0
- N: 8D64B542
---
**Facts (exact, whole file):** elements: 27; namespace declarations: 4; id and name attributes: 35; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0; processes: 1; tasks: 3; call activities and sub-processes: 1; gateways: 1; events: 5; sequence flows: 9; messages, signals, errors and escalations (named): 2; Java delegate bindings: 1; external task topics, job types and work items: 1

> Defines an executable order-fulfilment process that reserves stock, waits for payment, routes large orders for approval, ships orders, and creates invoices, with an out-of-stock cancellation path.

#### Purpose
- Orchestrates order fulfilment from order placement through completion or stock-related cancellation.
- Integrates payment messages, a stock-reservation delegate, an external shipping worker, and an invoicing process.

#### Type
- BPMN process definition; root `definitions`.
- Target namespace: `http://shop.example/bpmn`.
- Declared namespaces: `bpmn` — `http://www.omg.org/spec/BPMN/20100524/MODEL`; `bpmndi` — `http://www.omg.org/spec/BPMN/20100524/DI`; `dc` — `http://www.omg.org/spec/DD/20100524/DC`; `camunda` — `http://camunda.org/schema/1.0/bpmn`.

#### Definitions
- Process `orderFulfilment` (`Order fulfilment`), executable.
- Message `msgPaymentReceived` (`PaymentReceived`); error `errOutOfStock` (`OutOfStock`), code `STOCK_EMPTY`.
- Events: start `orderPlaced` (`Order placed`); message catch event `waitForPayment` (`Wait for payment`) referencing `msgPaymentReceived`; boundary error event `stockMissing` attached to `reserveStock`, referencing `errOutOfStock`; end events `orderCompleted` (`Order completed`) and `orderCancelled` (`Order cancelled`).
- Tasks: service task `reserveStock` (`Reserve stock`), class `shop.bpm.ReserveStockDelegate`; user task `approveOrder` (`Approve large order`), candidate group `backoffice`; external service task `shipOrder` (`Ship order`), topic `shipping`; call activity `createInvoice` (`Create invoice`), called element `invoicing`.
- Exclusive gateway `amountCheck` (`Large order?`): flow `large` to `approveOrder` when `${order.total > 1000}`; flow `small` to `shipOrder` when `${order.total <= 1000}`.
- Sequence flows: `toReserve` (`orderPlaced` → `reserveStock`); `toWaitPayment` (`reserveStock` → `waitForPayment`); `toAmountCheck` (`waitForPayment` → `amountCheck`); `large` and `small` (gateway branches); `approvedToShip` (`approveOrder` → `shipOrder`); `toInvoice` (`shipOrder` → `createInvoice`); `toEnd` (`createInvoice` → `orderCompleted`); `toCancelled` (`stockMissing` → `orderCancelled`).

#### Configuration
- `isExecutable="true"`.
- `reserveStock`: `camunda:class="shop.bpm.ReserveStockDelegate"`.
- `approveOrder`: `camunda:candidateGroups="backoffice"`.
- `shipOrder`: `camunda:type="external"`; `camunda:topic="shipping"`.
- Gateway conditions: `${order.total > 1000}` and `${order.total <= 1000}`.

#### Dependencies
- Delegate class: `shop.bpm.ReserveStockDelegate`.
- Called process element: `invoicing`.
- External worker topic: `shipping`.
- Incoming message: `PaymentReceived` (`msgPaymentReceived`); boundary error: `OutOfStock` (`errOutOfStock`, code `STOCK_EMPTY`).
- Gateway expressions reference `order.total`.
