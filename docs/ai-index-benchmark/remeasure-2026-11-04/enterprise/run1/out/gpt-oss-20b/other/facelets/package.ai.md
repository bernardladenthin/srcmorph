### other/facelets
- H: 1.0
- C: 2CC30F84
- D: 2026-11-04T06:55:10Z
- T: 2026-11-04T08:11:44Z
- G: 
- A: 0.0.0
- X: package
- F: [checkout.xhtml](checkout.xhtml.ai.md)
---
> A JSF Facelets page that gathers shipping details, lets the user pick a shipping method, shows the order total, and submits the order.

#### Purpose
- Capture shipping address and method.
- Show calculated total.
- Submit order via `checkoutBean`.

#### Responsibilities
- **User input collection** – shipping address fields, shipping method selector.
- **Dynamic calculation** – Ajax listener to recalculate total when shipping changes.
- **Order submission** – form submission to `checkoutBean.placeOrder`.

#### Key units
- **`checkout.xhtml`** – Facelets page, template `shop.xhtml`, includes payment hints.
- **`checkoutBean`** – Managed bean providing `address`, `shippingMethod`, `shippingMethods`, `total`, `recalculate()`, `placeOrder()`.

#### Data flow
1. User enters address (`address.street`, `address.zip`) and selects `shippingMethod`.
2. Ajax listener `recalculate()` updates `total`.
3. `total` displayed in EUR format.
4. On submit, `placeOrder()` processes the order.

#### Dependencies
- JSF tag libraries: `jakarta.faces.html`, `jakarta.faces.core`, `jakarta.faces.facelets`.
- Included fragment: `/WEB-INF/includes/payment-hints.xhtml`.
- Managed bean: `checkoutBean`.

#### Cross-cutting
- EL expressions for data binding and method calls.
- Validation on ZIP via regex `[0-9]{5}`.
- Currency conversion for `total` to EUR.
