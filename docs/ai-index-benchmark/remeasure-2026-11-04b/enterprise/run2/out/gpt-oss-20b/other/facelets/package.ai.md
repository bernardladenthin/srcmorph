### other/facelets
- H: 1.0
- C: 2CC30F84
- D: 2026-11-04T06:55:10Z
- T: 2026-11-04T12:36:29Z
- G: 
- A: 0.0.0
- X: package
- F: [checkout.xhtml](checkout.xhtml.ai.md)
---
> A Jakarta Faces checkout page that gathers shipping details, selects a shipping method, shows the total, and submits an order.

#### Purpose
- Collect shipping address and method.
- Display calculated total.
- Submit order.

#### Responsibilities
- **User input collection** – shipping address fields and shipping method selection.
- **Dynamic calculation** – recalculate total when shipping method changes.
- **Order submission** – invoke `checkoutBean.placeOrder` on form submit.

#### Key units
- **`checkout.xhtml`** – Facelets page, template `shop.xhtml`, contains form `checkoutForm`, fields `address.street`, `address.zip`, `shippingMethod`, and output `total`.
- **`checkoutBean`** – backing bean with properties `address`, `shippingMethod`, `shippingMethods`, `total`; methods `recalculate()` and `placeOrder()`.

#### Data flow
1. User enters `address.street` and `address.zip` (validated by regex).
2. User selects `shippingMethod` from `shippingMethods` list.
3. Ajax listener `checkoutBean.recalculate` updates `total` and re-renders the total component.
4. On form submit, `checkoutBean.placeOrder` processes the order.

#### Dependencies
- Jakarta Faces tag libraries: `jakarta.faces.html`, `jakarta.faces.core`, `jakarta.faces.facelets`.
- Template: `/WEB-INF/templates/shop.xhtml`.
- Include: `/WEB-INF/includes/payment-hints.xhtml`.

#### Cross-cutting
- Uses EL expressions for data binding and method calls.
- Ajax partial rendering for dynamic total update.
- Validation via EL regex on `address.zip`.
