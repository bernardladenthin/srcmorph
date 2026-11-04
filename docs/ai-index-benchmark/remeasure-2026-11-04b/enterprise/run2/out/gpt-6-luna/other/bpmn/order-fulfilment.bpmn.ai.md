### order-fulfilment.bpmn
- H: 1.0
- C: 15AAB284
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T12:28:58Z
- G: 
- A: 0.0.0
- X: file
- P: D9E787D0
- N: 8D64B542
---
**Facts (exact, whole file):** elements: 27; namespace declarations: 4; id and name attributes: 35; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0; processes: 1; tasks: 3; call activities and sub-processes: 1; gateways: 1; events: 5; sequence flows: 9; messages, signals, errors and escalations (named): 2; Java delegate bindings: 1; external task topics, job types and work items: 1

> Defines an executable order-fulfilment process that reserves stock, waits for payment, routes large orders for approval, ships and invoices orders, and cancels orders when stock is unavailable.

#### Purpose
- Models the order-fulfilment lifecycle, including payment, amount-based approval, shipping, invoicing, and out-of-stock cancellation.
- Declares the payment message and stock error used by the process.

#### Type
- BPMN process definition; root element `bpmn:definitions`.
- Namespaces: `bpmn` → `http://www.omg.org/spec/BPMN/20100524/MODEL`; `bpmndi` → `http://www.omg.org/spec/BPMN/20100524/DI`; `dc` → `http://www.omg.org/spec/DD/20100524/DC`; `camunda` → `http://camunda.org/schema/1.0/bpmn`.

#### Definitions
- Process `orderFulfilment` (`Order fulfilment`), executable:
  - Start event `orderPlaced` → service task `reserveStock` (`Reserve stock`; class `shop.bpm.ReserveStockDelegate`).
  - Boundary error event `stockMissing` on `reserveStock`, referencing `errOutOfStock`; sequence flow `toCancelled` leads to end event `orderCancelled` (`Order cancelled`).
  - Normal path: `reserveStock` → intermediate message catch event `waitForPayment` (`Wait for payment`; message `msgPaymentReceived`) → exclusive gateway `amountCheck` (`Large order?`).
  - Gateway flow `large` routes to user task `approveOrder` (`Approve large order`) when `${order.total > 1000}`; candidate group `backoffice`. Flow `approvedToShip` leads to `shipOrder`.
  - Gateway flow `small` routes directly to `shipOrder` when `${order.total <= 1000}`.
  - Service task `shipOrder` (`Ship order`; external type, topic `shipping`) → call activity `createInvoice` (`Create invoice`; called element `invoicing`) → end event `orderCompleted` (`Order completed`).
- Message `msgPaymentReceived` (`PaymentReceived`).
- Error `errOutOfStock` (`OutOfStock`), error code `STOCK_EMPTY`.

#### Configuration
- Process executable: `isExecutable="true"`.
- Large-order condition: `${order.total > 1000}`; small-order condition: `${order.total <= 1000}`.
- `reserveStock` delegate class: `shop.bpm.ReserveStockDelegate`.
- `approveOrder` candidate group: `backoffice`.
- `shipOrder` external-task topic: `shipping`.
- `createInvoice` called element: `invoicing`.

#### Dependencies
- Delegate class: `shop.bpm.ReserveStockDelegate`.
- External task worker topic: `shipping`.
- Called process element: `invoicing`.
- Referenced BPMN message: `msgPaymentReceived`; error: `errOutOfStock`.
