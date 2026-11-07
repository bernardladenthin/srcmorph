### order-fulfilment.bpmn
- H: 1.0
- C: 15AAB284
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T12:32:35Z
- G: 
- A: 0.0.0
- X: file
- P: D9E787D0
- N: 8D64B542
---
**Facts (exact, whole file):** elements: 27; namespace declarations: 4; id and name attributes: 35; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0; processes: 1; tasks: 3; call activities and sub-processes: 1; gateways: 1; events: 5; sequence flows: 9; messages, signals, errors and escalations (named): 2; Java delegate bindings: 1; external task topics, job types and work items: 1

> Defines a BPMN process for order fulfilment with payment, stock reservation, approval, shipping, and invoicing.

#### Purpose
- Models the order fulfilment workflow.
- Handles stock reservation, payment wait, large‑order approval, shipping, and invoice creation.

#### Type
- BPMN 2.0 XML (`bpmn:definitions` root).
- Namespaces: `bpmn`, `bpmndi`, `dc`, `camunda`.

#### Definitions
- **Messages**: `msgPaymentReceived` (name: `PaymentReceived`).
- **Errors**: `errOutOfStock` (name: `OutOfStock`, code: `STOCK_EMPTY`).
- **Process** `orderFulfilment` (executable):
  - `startEvent` `orderPlaced`.
  - `serviceTask` `reserveStock` (`camunda:class="shop.bpm.ReserveStockDelegate"`).
  - `boundaryEvent` `stockMissing` (error `errOutOfStock`).
  - `intermediateCatchEvent` `waitForPayment` (message `msgPaymentReceived`).
  - `exclusiveGateway` `amountCheck` (condition `${order.total > 1000}` → `approveOrder`; `${order.total <= 1000}` → `shipOrder`).
  - `userTask` `approveOrder` (`camunda:candidateGroups="backoffice"`).
  - `serviceTask` `shipOrder` (`camunda:type="external"`, topic `shipping`).
  - `callActivity` `createInvoice` (`calledElement="invoicing"`).
  - `endEvent` `orderCompleted`.
  - `endEvent` `orderCancelled` (via `stockMissing`).
- **Diagram** `fulfilmentDiagram` with shapes for `orderPlaced`, `reserveStock`, `shipOrder`.

#### Configuration
- `camunda:class` on `reserveStock`: `shop.bpm.ReserveStockDelegate`.
- `camunda:candidateGroups` on `approveOrder`: `backoffice`.
- `camunda:type` on `shipOrder`: `external`.
- `camunda:topic` on `shipOrder`: `shipping`.

#### Dependencies
- Java class `shop.bpm.ReserveStockDelegate`.
- External task topic `shipping`.
- Called element `invoicing` (likely another BPMN process or service).
