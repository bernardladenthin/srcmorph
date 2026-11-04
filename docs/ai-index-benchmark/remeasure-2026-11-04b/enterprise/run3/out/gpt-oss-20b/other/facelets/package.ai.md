### other/facelets
- H: 1.0
- C: 2CC30F84
- D: 2026-11-04T06:55:10Z
- T: 2026-11-04T12:42:33Z
- G: 
- A: 0.0.0
- X: package
- F: [checkout.xhtml](checkout.xhtml.ai.md)
---
> A JSF Facelets page that gathers a customer’s shipping details, lets them pick a shipping method, shows the order total, and submits the order.

#### Purpose
- Capture shipping address and method.
- Show calculated total.
- Submit order to backend.

#### Responsibilities
- **User input collection** – shipping address fields, shipping method selector.
- **Dynamic total calculation** – AJAX update when shipping method changes.
- **Order submission** – form action to place the order.
- **UI composition** – extends shop template, includes payment hints.

#### Key units
- **`checkout.xhtml`** – Facelets page rendering the checkout form.
- **`checkoutBean`** – Managed bean backing the page (address, shippingMethod, shippingMethods, total, recalculate, placeOrder).

#### Data flow
1. User enters address (`address.street`, `address.zip`) and selects `shippingMethod`.
2. Changing `shippingMethod` triggers `checkoutBean.recalculate` via `<f:ajax>`, updating `total`.
3. User clicks “Place Order”, invoking `checkoutBean.placeOrder` to process the order.

#### Dependencies
- JSF tag libraries: `jakarta.faces.html`, `jakarta.faces.core`, `jakarta.faces.facelets`.
- Template: `/WEB-INF/templates/shop.xhtml`.
- Include: `/WEB-INF/includes/payment-hints.xhtml`.
- Managed bean: `checkoutBean` (application‑scoped).

#### Cross-cutting
- EL expressions for data binding and method calls.
- AJAX listener for real‑time total updates.
- Validation on ZIP code with regex `[0-9]{5}`.
