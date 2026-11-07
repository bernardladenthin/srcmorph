### order-fulfilment.bpmn
- H: 1.0
- C: 15AAB284
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T12:41:09Z
- G: 
- A: 0.0.0
- X: file
- P: D9E787D0
- N: 8D64B542
---
**Facts (exact, whole file):** elements: 27; namespace declarations: 4; id and name attributes: 35; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0; processes: 1; tasks: 3; call activities and sub-processes: 1; gateways: 1; events: 5; sequence flows: 9; messages, signals, errors and escalations (named): 2; Java delegate bindings: 1; external task topics, job types and work items: 1

> Defines a BPMN process for order fulfilment, including payment handling, stock reservation, approval workflow, shipping, invoicing, and error handling.

#### Purpose
- Models the order fulfilment workflow.
- Handles stock reservation, payment receipt, large order approval, shipping, invoicing, and cancellation.

#### Type
- BPMN 2.0 XML (`bpmn:definitions` root).
- Namespaces:
  - `bpmn` → `http://www.omg.org/spec/BPMN/20100524/MODEL`
  - `bpmndi` → `http://www.omg.org/spec/BPMN/20100524/DI`
  - `dc` → `http://www.omg.org/spec/DD/20100524/DC`
  - `camunda` → `http://camunda.org/schema/1.0/bpmn`

#### Definitions
- **Messages**
  - `msgPaymentReceived` (`name="PaymentReceived"`)
- **Errors**
  - `errOutOfStock` (`name="OutOfStock"`, `errorCode="STOCK_EMPTY"`)
- **Process** `orderFulfilment` (`name="Order fulfilment"`, `isExecutable="true"`)
  - **Events & Tasks**
    - `orderPlaced` (start event)
    - `reserveStock` (service task, `camunda:class="shop.bpm.ReserveStockDelegate"`)
    - `stockMissing` (boundary error event on `reserveStock`, references `errOutOfStock`)
    - `waitForPayment` (intermediate catch event, message `msgPaymentReceived`)
    - `amountCheck` (exclusive gateway, `name="Large order?"`)
    - `approveOrder` (user task, `camunda:candidateGroups="backoffice"`)
    - `shipOrder` (service task, `camunda:type="external"`, `camunda:topic="shipping"`)
    - `createInvoice` (call activity, `calledElement="invoicing"`)
    - `orderCompleted` (end event)
    - `orderCancelled` (end event)
  - **Sequence Flows**
    - `toReserve` (`orderPlaced → reserveStock`)
    - `toWaitPayment` (`reserveStock → waitForPayment`)
    - `toAmountCheck` (`waitForPayment → amountCheck`)
    - `large` (`amountCheck → approveOrder`, condition `${order.total > 1000}`)
    - `small` (`amountCheck → shipOrder`, condition `${order.total <= 1000}`)
    - `approvedToShip` (`approveOrder → shipOrder`)
    - `toInvoice` (`shipOrder → createInvoice`)
    - `toEnd` (`createInvoice → orderCompleted`)
    - `toCancelled` (`stockMissing → orderCancelled`)
- **Diagram**
  - `fulfilmentDiagram` (`bpmndi:BPMNDiagram`)
  - `plane` (`bpmndi:BPMNPlane`, `bpmnElement="orderFulfilment"`)
  - Shapes for `orderPlaced`, `reserveStock`, `shipOrder` with bounds.

#### Configuration
- `camunda:class="shop.bpm.ReserveStockDelegate"`
- `camunda:candidateGroups="backoffice"`
- `camunda:type="external"`, `camunda:topic="shipping"`
- `calledElement="invoicing"`

#### Dependencies
- Java delegate class `shop.bpm.ReserveStockDelegate`.
- External topic `shipping` (Camunda external task).
- Call activity references process `invoicing` (another BPMN process).
