### other/facelets
- H: 1.0
- C: 2CC30F84
- D: 2026-11-04T06:55:10Z
- T: 2026-11-04T12:30:57Z
- G: 
- A: 0.0.0
- X: package
- F: [checkout.xhtml](checkout.xhtml.ai.md)
---
> Customers provide delivery details, select shipping, review the EUR order total, and submit an order.

#### Purpose
- Provide the checkout page for entering an address, choosing a shipping method, and placing an order.
- Display the order total and recalculate it when shipping selection changes.

#### Responsibilities
- Collect street and ZIP code, with a five-digit ZIP validator.
- Populate shipping choices from `checkoutBean.shippingMethods`; show the total in EUR.
- Submit the order and refresh the total through Faces actions.
- Render checkout content within the shop template and include payment hints.

#### Key units
- `checkout.xhtml` — Facelets checkout form bound to `checkoutBean`.
- `checkoutBean.recalculate` — AJAX listener for shipping changes, rerendering the total.
- `checkoutBean.placeOrder` — action invoked by the Place order button.

#### Data flow
- Address fields and shipping selection bind to `checkoutBean`; shipping choices come from `checkoutBean.shippingMethods`.
- Changing shipping invokes `recalculate` and updates the rendered total; the Place order button invokes `placeOrder`.

#### Dependencies
- Jakarta Faces HTML, core, and Facelets tag libraries.
- `checkoutBean` and its `recalculate` and `placeOrder` methods.
- Template `/WEB-INF/templates/shop.xhtml` and include `/WEB-INF/includes/payment-hints.xhtml`.
