### order-fulfilment.bpmn
- H: 1.0
- C: 15AAB284
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T12:25:54Z
- G: 
- A: 0.0.0
- X: file
- P: D9E787D0
- N: 8D64B542
---
**Facts (exact, whole file):** elements: 27; namespace declarations: 4; id and name attributes: 35; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0; processes: 1; tasks: 3; call activities and sub-processes: 1; gateways: 1; events: 5; sequence flows: 9; messages, signals, errors and escalations (named): 2; Java delegate bindings: 1; external task topics, job types and work items: 1

> Defines a BPMN process for order fulfilment, including payment handling, stock reservation, large‑order approval, shipping, and invoicing.

#### Purpose
- Models the order fulfilment workflow.
- Handles stock reservation, payment receipt, order approval, shipping, and invoicing.

#### Type
- BPMN 2.0 XML (`bpmn:definitions` root).  
- Namespaces:  
  - `bpmn` → `http://www.omg.org/spec/BPMN/20100524/MODEL`  
  - `bpmndi` → `http://www.omg.org/spec/BPMN/20100524/DI`  
  - `dc` → `http://www.omg.org/spec/DD/20100524/DC`  
  - `camunda` → `http://camunda.org/schema/1.0/bpmn`

#### Definitions
- **Message**: `msgPaymentReceived` (name: `PaymentReceived`).  
- **Error**: `errOutOfStock` (name: `OutOfStock`, errorCode: `STOCK_EMPTY`).  
- **Process**: `orderFulfilment` (name: `Order fulfilment`, executable).  
  - **Start Event**: `orderPlaced` (name: `Order placed`).  
  - **Service Task**: `reserveStock` (class: `shop.bpm.ReserveStockDelegate`).  
  - **Boundary Event**: `stockMissing` (attached to `reserveStock`, errorRef: `errOutOfStock`).  
  - **Intermediate Catch Event**: `waitForPayment` (messageRef: `msgPaymentReceived`).  
  - **Exclusive Gateway**: `amountCheck` (name: `Large order?`).  
    - **Sequence Flow** `large` (condition: `${order.total > 1000}`) → `approveOrder`.  
    - **Sequence Flow** `small` (condition: `${order.total <= 1000}`) → `shipOrder`.  
  - **User Task**: `approveOrder` (candidateGroups: `backoffice`).  
  - **Service Task**: `shipOrder` (type: `external`, topic: `shipping`).  
  - **Call Activity**: `createInvoice` (calledElement: `invoicing`).  
  - **End Events**: `orderCompleted` (name: `Order completed`), `orderCancelled` (name: `Order cancelled`).  
- **Diagram**: `fulfilmentDiagram` with plane `plane` containing shapes for `orderPlaced`, `reserveStock`, `shipOrder`.

#### Configuration
- `camunda:class` on `reserveStock`: `shop.bpm.ReserveStockDelegate`.  
- `camunda:candidateGroups` on `approveOrder`: `backoffice`.  
- `camunda:type` on `shipOrder`: `external`.  
- `camunda:topic` on `shipOrder`: `shipping`.  
- `calledElement` on `createInvoice`: `invoicing`.  
- Condition expressions: `${order.total > 1000}` and `${order.total <= 1000}`.

#### Dependencies
- External service class: `shop.bpm.ReserveStockDelegate`.  
- External topic: `shipping`.  
- Called element: `invoicing`.  
- BPMN diagram layout references (`bpmndi`, `dc` bounds).
