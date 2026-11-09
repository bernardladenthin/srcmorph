### order-fulfilment.bpmn
- H: 1.0
- C: 15AAB284
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T12:37:38Z
- G: 
- A: 0.0.0
- X: file
- P: D9E787D0
- N: 8D64B542
---
**Facts (exact, whole file):** elements: 27; namespace declarations: 4; id and name attributes: 35; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0; processes: 1; tasks: 3; call activities and sub-processes: 1; gateways: 1; events: 5; sequence flows: 9; messages, signals, errors and escalations (named): 2; Java delegate bindings: 1; external task topics, job types and work items: 1

> Defines an executable order-fulfilment process that reserves stock, handles payment and large-order approval, ships orders, creates invoices, and cancels orders when stock is unavailable.

#### Purpose
- Models the order lifecycle, including payment receipt, conditional approval, shipping, invoicing, and out-of-stock cancellation.
- Declares the messages and error used by the process.

#### Type
- BPMN process; root element `bpmn:definitions`.
- Namespaces: `bpmn` = `http://www.omg.org/spec/BPMN/20100524/MODEL`; `bpmndi` = `http://www.omg.org/spec/BPMN/20100524/DI`; `dc` = `http://www.omg.org/spec/DD/20100524/DC`; `camunda` = `http://camunda.org/schema/1.0/bpmn`.

#### Definitions
- Process `orderFulfilment` (`Order fulfilment`), executable:
  - Starts at `orderPlaced`; sequence flow `toReserve` leads to `reserveStock`.
  - Service task `reserveStock` invokes `shop.bpm.ReserveStockDelegate`. Boundary event `stockMissing` catches `errOutOfStock` and flows via `toCancelled` to end event `orderCancelled`.
  - Flow `toWaitPayment` leads to message catch event `waitForPayment`, which waits for `msgPaymentReceived`; flow `toAmountCheck` leads to exclusive gateway `amountCheck`.
  - Gateway flow `large` proceeds to user task `approveOrder` when `${order.total > 1000}`; `approveOrder` has candidate group `backoffice`. Flow `approvedToShip` leads to `shipOrder`.
  - Gateway flow `small` proceeds directly to `shipOrder` when `${order.total <= 1000}`.
  - Service task `shipOrder` is an external task on topic `shipping`; flow `toInvoice` leads to call activity `createInvoice`, which calls `invoicing`; flow `toEnd` leads to end event `orderCompleted`.
- Message `msgPaymentReceived` has name `PaymentReceived`.
- Error `errOutOfStock` has name `OutOfStock` and error code `STOCK_EMPTY`.

#### Configuration
- `orderFulfilment`: `isExecutable="true"`.
- `amountCheck` thresholds: `order.total > 1000` for `large`; `order.total <= 1000` for `small`.
- `reserveStock`: `camunda:class="shop.bpm.ReserveStockDelegate"`.
- `approveOrder`: `camunda:candidateGroups="backoffice"`.
- `shipOrder`: `camunda:type="external"`; `camunda:topic="shipping"`.
- `createInvoice`: `calledElement="invoicing"`.

#### Dependencies
- Delegate class: `shop.bpm.ReserveStockDelegate`.
- External task topic: `shipping`.
- Called process: `invoicing`.
- Message reference: `msgPaymentReceived`; error reference: `errOutOfStock`.
