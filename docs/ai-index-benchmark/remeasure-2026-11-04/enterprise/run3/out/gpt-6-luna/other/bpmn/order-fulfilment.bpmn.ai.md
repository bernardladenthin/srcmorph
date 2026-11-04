### order-fulfilment.bpmn
- H: 1.0
- C: 15AAB284
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T08:19:35Z
- G: 
- A: 0.0.0
- X: file
- P: 326D0B29
- N: 7ADEA688
---
**Facts (exact, whole file):** elements: 27; namespace declarations: 4; id and name attributes: 35; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0; processes: 1; tasks: 3; call activities and sub-processes: 1; gateways: 1; events: 5; sequence flows: 9; messages, signals, errors and escalations (named): 2; Java delegate bindings: 1; external task topics, job types and work items: 1; decisions: 0; decision rules: 0

> Defines executable order fulfilment from stock reservation and payment through approval, shipping, invoicing, or cancellation when stock is unavailable.

#### Purpose
- Orchestrates order fulfilment, including payment waiting, amount-based approval, shipping, and out-of-stock cancellation.

#### Type
- BPMN process file; root element `bpmn:definitions`.
- Namespaces: `bpmn` = `http://www.omg.org/spec/BPMN/20100524/MODEL`; `bpmndi` = `http://www.omg.org/spec/BPMN/20100524/DI`; `dc` = `http://www.omg.org/spec/DD/20100524/DC`; `camunda` = `http://camunda.org/schema/1.0/bpmn`.

#### Definitions
- Process `orderFulfilment` (`Order fulfilment`), executable:
  - Start event `orderPlaced` (`Order placed`) → service task `reserveStock` (`Reserve stock`, class `shop.bpm.ReserveStockDelegate`).
  - Boundary event `stockMissing`, attached to `reserveStock`, catches error `errOutOfStock` → end event `orderCancelled` (`Order cancelled`).
  - Intermediate catch event `waitForPayment` (`Wait for payment`) catches message `msgPaymentReceived`.
  - Exclusive gateway `amountCheck` (`Large order?`): flow `large` to `approveOrder` when `${order.total > 1000}`; flow `small` to `shipOrder` when `${order.total <= 1000}`.
  - User task `approveOrder` (`Approve large order`), candidate group `backoffice`; flow `approvedToShip` continues to `shipOrder`.
  - Service task `shipOrder` (`Ship order`), external type, topic `shipping`; continues to call activity `createInvoice` (`Create invoice`), which calls `invoicing`.
  - End event `orderCompleted` (`Order completed`) follows invoicing.
- Message `msgPaymentReceived` (`PaymentReceived`).
- Error `errOutOfStock` (`OutOfStock`), error code `STOCK_EMPTY`.

#### Configuration
- `orderFulfilment`: `isExecutable="true"`.
- Gateway conditions: `${order.total > 1000}` and `${order.total <= 1000}`.
- `reserveStock`: `camunda:class="shop.bpm.ReserveStockDelegate"`.
- `approveOrder`: `camunda:candidateGroups="backoffice"`.
- `shipOrder`: `camunda:type="external"`, `camunda:topic="shipping"`.
- `createInvoice`: `calledElement="invoicing"`.

#### Dependencies
- Delegate class: `shop.bpm.ReserveStockDelegate`.
- Called process: `invoicing`.
- External task topic: `shipping`.
- Message: `msgPaymentReceived`; error: `errOutOfStock`.
