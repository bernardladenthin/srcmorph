### order-fulfilment.bpmn
- H: 1.0
- C: 15AAB284
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T08:10:16Z
- G: 
- A: 0.0.0
- X: file
- P: 326D0B29
- N: 7ADEA688
---
**Facts (exact, whole file):** elements: 27; namespace declarations: 4; id and name attributes: 35; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0; processes: 1; tasks: 3; call activities and sub-processes: 1; gateways: 1; events: 5; sequence flows: 9; messages, signals, errors and escalations (named): 2; Java delegate bindings: 1; external task topics, job types and work items: 1; decisions: 0; decision rules: 0

> Defines a BPMN process for order fulfilment with payment, stock reservation, approval, shipping, and invoicing.

#### Purpose
- Models the order fulfilment workflow.
- Handles stock reservation, payment wait, large‑order approval, shipping, and invoice creation.

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
  - **Start Event** `orderPlaced` (`name="Order placed"`)  
  - **Service Task** `reserveStock` (`name="Reserve stock"`, `camunda:class="shop.bpm.ReserveStockDelegate"`)  
  - **Boundary Event** `stockMissing` (attached to `reserveStock`, error `errOutOfStock`)  
  - **Intermediate Catch Event** `waitForPayment` (`name="Wait for payment"`, message `msgPaymentReceived`)  
  - **Exclusive Gateway** `amountCheck` (`name="Large order?"`)  
    - **Sequence Flow** `large` (`conditionExpression="${order.total > 1000}"`) → `approveOrder`  
    - **Sequence Flow** `small` (`conditionExpression="${order.total <= 1000}"`) → `shipOrder`  
  - **User Task** `approveOrder` (`name="Approve large order"`, `camunda:candidateGroups="backoffice"`)  
  - **Service Task** `shipOrder` (`name="Ship order"`, `camunda:type="external"`, `camunda:topic="shipping"`)  
  - **Call Activity** `createInvoice` (`name="Create invoice"`, `calledElement="invoicing"`)  
  - **End Events**  
    - `orderCompleted` (`name="Order completed"`)  
    - `orderCancelled` (`name="Order cancelled"`)  
- **Diagram** `fulfilmentDiagram` with plane `plane` containing shapes for `orderPlaced`, `reserveStock`, `shipOrder`.

#### Configuration
- `camunda:class="shop.bpm.ReserveStockDelegate"`
- `camunda:candidateGroups="backoffice"`
- `camunda:type="external"`, `camunda:topic="shipping"`
- `calledElement="invoicing"`

#### Dependencies
- External delegate class `shop.bpm.ReserveStockDelegate`.  
- External topic `shipping` for shipping service.  
- Called element `invoicing` (another BPMN process).  
- Camunda BPM engine (via `camunda` namespace).
