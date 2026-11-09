### order-fulfilment.bpmn
- H: 1.0
- C: 15AAB284
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T08:06:56Z
- G: 
- A: 0.0.0
- X: file
- P: 326D0B29
- N: 7ADEA688
---
**Facts (exact, whole file):** elements: 27; namespace declarations: 4; id and name attributes: 35; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0; processes: 1; tasks: 3; call activities and sub-processes: 1; gateways: 1; events: 5; sequence flows: 9; messages, signals, errors and escalations (named): 2; Java delegate bindings: 1; external task topics, job types and work items: 1; decisions: 0; decision rules: 0

> Defines executable order fulfilment with stock reservation, payment-dependent approval, shipping, invoicing, and cancellation paths.

#### Purpose
- Orchestrates an order from placement through fulfilment or out-of-stock cancellation.
- Routes large orders for back-office approval and delegates shipping and invoicing.

#### Type
- BPMN process file; root `bpmn:definitions`.
- Namespaces: `bpmn` = `http://www.omg.org/spec/BPMN/20100524/MODEL`; `bpmndi` = `http://www.omg.org/spec/BPMN/20100524/DI`; `dc` = `http://www.omg.org/spec/DD/20100524/DC`; `camunda` = `http://camunda.org/schema/1.0/bpmn`.

#### Definitions
- Process `orderFulfilment` (`Order fulfilment`), executable:
  - Start event `orderPlaced` → service task `reserveStock` → message catch event `waitForPayment` → exclusive gateway `amountCheck`.
  - Gateway routes `large` to user task `approveOrder` when `${order.total > 1000}`; routes `small` to service task `shipOrder` when `${order.total <= 1000}`. Approval also leads to `shipOrder`.
  - `shipOrder` → call activity `createInvoice` → end event `orderCompleted`.
  - Error boundary event `stockMissing` on `reserveStock` → end event `orderCancelled`.
- Service task `reserveStock` calls class `shop.bpm.ReserveStockDelegate`.
- User task `approveOrder` is assigned to candidate group `backoffice`.
- Service task `shipOrder` uses external topic `shipping`.
- Call activity `createInvoice` invokes `invoicing`.
- Message `msgPaymentReceived` is named `PaymentReceived`; catch event `waitForPayment` waits for it.
- Error `errOutOfStock` is named `OutOfStock`, with error code `STOCK_EMPTY`; boundary event `stockMissing` references it.

#### Configuration
- `orderFulfilment`: `isExecutable="true"`.
- Gateway conditions: `${order.total > 1000}` and `${order.total <= 1000}`.
- `reserveStock`: `camunda:class="shop.bpm.ReserveStockDelegate"`.
- `approveOrder`: `camunda:candidateGroups="backoffice"`.
- `shipOrder`: `camunda:type="external"`, `camunda:topic="shipping"`.
- `createInvoice`: `calledElement="invoicing"`.

#### Dependencies
- Delegate class: `shop.bpm.ReserveStockDelegate`.
- External task topic: `shipping`.
- Called process: `invoicing`.
- Message dependency: `PaymentReceived` (`msgPaymentReceived`).
- Error dependency: `OutOfStock` (`errOutOfStock`; code `STOCK_EMPTY`).
