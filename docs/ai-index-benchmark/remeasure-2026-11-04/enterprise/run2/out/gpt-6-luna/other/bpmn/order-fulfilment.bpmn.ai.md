### order-fulfilment.bpmn
- H: 1.0
- C: 15AAB284
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T08:13:19Z
- G: 
- A: 0.0.0
- X: file
- P: 326D0B29
- N: 7ADEA688
---
**Facts (exact, whole file):** elements: 27; namespace declarations: 4; id and name attributes: 35; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0; processes: 1; tasks: 3; call activities and sub-processes: 1; gateways: 1; events: 5; sequence flows: 9; messages, signals, errors and escalations (named): 2; Java delegate bindings: 1; external task topics, job types and work items: 1; decisions: 0; decision rules: 0

> Defines an executable order-fulfilment workflow that reserves stock, waits for payment, routes large orders for approval, ships orders, and creates invoices, with an out-of-stock cancellation path.

#### Purpose
- Orchestrates order fulfilment from order placement through completion or cancellation.
- Integrates stock reservation, payment receipt, shipping, and invoice creation.

#### Type
- BPMN process definition; root `bpmn:definitions`.
- Namespaces: `bpmn` — `http://www.omg.org/spec/BPMN/20100524/MODEL`; `bpmndi` — `http://www.omg.org/spec/BPMN/20100524/DI`; `dc` — `http://www.omg.org/spec/DD/20100524/DC`; `camunda` — `http://camunda.org/schema/1.0/bpmn`.

#### Definitions
- Process: `orderFulfilment`, name `Order fulfilment`, executable.
- Message: `msgPaymentReceived`, name `PaymentReceived`.
- Error: `errOutOfStock`, name `OutOfStock`, code `STOCK_EMPTY`.
- Start event: `orderPlaced`, name `Order placed`.
- Service task: `reserveStock`, name `Reserve stock`, class `shop.bpm.ReserveStockDelegate`.
- Boundary event: `stockMissing`, attached to `reserveStock`, catches `errOutOfStock`.
- Intermediate catch event: `waitForPayment`, name `Wait for payment`, waits for `msgPaymentReceived`.
- Exclusive gateway: `amountCheck`, name `Large order?`; flow `large` to `approveOrder` when `${order.total > 1000}`, flow `small` to `shipOrder` when `${order.total <= 1000}`.
- User task: `approveOrder`, name `Approve large order`, candidate group `backoffice`.
- Service task: `shipOrder`, name `Ship order`, external task topic `shipping`.
- Call activity: `createInvoice`, name `Create invoice`, calls `invoicing`.
- End events: `orderCompleted` (`Order completed`), reached after invoice creation; `orderCancelled` (`Order cancelled`), reached from `stockMissing`.
- Sequence flows: `toReserve` (`orderPlaced` → `reserveStock`), `toWaitPayment` (`reserveStock` → `waitForPayment`), `toAmountCheck` (`waitForPayment` → `amountCheck`), `large` and `small` (gateway branches), `approvedToShip` (`approveOrder` → `shipOrder`), `toInvoice` (`shipOrder` → `createInvoice`), `toEnd` (`createInvoice` → `orderCompleted`), `toCancelled` (`stockMissing` → `orderCancelled`).

#### Configuration
- `isExecutable`: `true`.
- Gateway condition thresholds: `order.total > 1000` routes to approval; `order.total <= 1000` routes to shipping.
- `camunda:candidateGroups`: `backoffice`.
- `camunda:type`: `external`; `camunda:topic`: `shipping`.
- `calledElement`: `invoicing`.

#### Dependencies
- Delegate class: `shop.bpm.ReserveStockDelegate`.
- Camunda external task topic: `shipping`.
- Called BPMN element: `invoicing`.
- Message: `PaymentReceived` (`msgPaymentReceived`); error: `OutOfStock` (`errOutOfStock`).
