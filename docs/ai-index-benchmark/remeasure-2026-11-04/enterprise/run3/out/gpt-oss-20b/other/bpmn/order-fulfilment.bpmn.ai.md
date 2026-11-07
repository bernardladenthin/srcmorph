### order-fulfilment.bpmn
- H: 1.0
- C: 15AAB284
- D: 2026-11-04T06:55:09Z
- T: 2026-11-04T08:22:53Z
- G: 
- A: 0.0.0
- X: file
- P: 326D0B29
- N: 7ADEA688
---
**Facts (exact, whole file):** elements: 27; namespace declarations: 4; id and name attributes: 35; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0; processes: 1; tasks: 3; call activities and sub-processes: 1; gateways: 1; events: 5; sequence flows: 9; messages, signals, errors and escalations (named): 2; Java delegate bindings: 1; external task topics, job types and work items: 1; decisions: 0; decision rules: 0

> Defines a BPMN process for order fulfilment, including stock reservation, payment wait, amount check, approval, shipping, invoicing, and cancellation paths.

#### Purpose
- Models the order fulfilment workflow.
- Handles stock reservation, payment, large‑order approval, shipping, invoicing, and cancellation.

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
  - **Events**  
    - `orderPlaced` (start)  
    - `waitForPayment` (intermediate catch, message event)  
    - `orderCompleted` (end)  
    - `orderCancelled` (end)  
  - **Tasks**  
    - `reserveStock` (service, `camunda:class="shop.bpm.ReserveStockDelegate"`)  
    - `approveOrder` (user, `camunda:candidateGroups="backoffice"`)  
    - `shipOrder` (service, `camunda:type="external"`, `camunda:topic="shipping"`)  
    - `createInvoice` (call, `calledElement="invoicing"`)  
  - **Gateways**  
    - `amountCheck` (exclusive, `name="Large order?"`)  
  - **Boundary Event**  
    - `stockMissing` (attached to `reserveStock`, error event `errOutOfStock`)  
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

#### Configuration
- `camunda:class="shop.bpm.ReserveStockDelegate"`  
- `camunda:candidateGroups="backoffice"`  
- `camunda:type="external"`  
- `camunda:topic="shipping"`  
- Condition expressions: `${order.total > 1000}`, `${order.total <= 1000}`  

#### Dependencies
- External service: `shop.bpm.ReserveStockDelegate` (Java class).  
- External topic: `shipping` (Camunda external task).  
- Called element: `invoicing` (sub‑process or external service).  
- Message event definition references `msgPaymentReceived`.  
- Error event definition references `errOutOfStock`.  

#### Diagram
- BPMN diagram `fulfilmentDiagram` with plane `plane` containing shapes for `orderPlaced`, `reserveStock`, and `shipOrder` with specified bounds.
