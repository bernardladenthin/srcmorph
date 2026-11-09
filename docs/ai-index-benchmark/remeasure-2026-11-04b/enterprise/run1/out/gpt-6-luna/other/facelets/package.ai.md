### other/facelets
- H: 1.0
- C: 2CC30F84
- D: 2026-11-04T06:55:10Z
- T: 2026-11-04T12:24:28Z
- G: 
- A: 0.0.0
- X: package
- F: [checkout.xhtml](checkout.xhtml.ai.md)
---
> Collects a customer’s delivery address and shipping choice, recalculates the euro order total, and submits the order.

#### Purpose
- Provide the customer checkout form for entering delivery details and placing an order.

#### Responsibilities
- Capture street and ZIP address fields, with a five-digit ZIP validator.
- Offer shipping-method selection and update the displayed total through AJAX.
- Submit the order and display the total formatted as EUR currency.

#### Key units
- `checkoutForm` — Faces form containing checkout fields and order submission.
- `checkoutBean` — supplies address, shipping methods, and total; handles recalculation and order placement.
- `shipping` — selector whose change invokes `checkoutBean.recalculate` and renders `total`.
- `Place order` command button — invokes `checkoutBean.placeOrder`.

#### Data flow
- Customer enters street and ZIP and selects a shipping method; the ZIP value is validated against `[0-9]{5}`.
- A shipping selection change triggers recalculation and refreshes the EUR-formatted total.
- Form submission invokes the order-placement action.

#### Dependencies
- `/WEB-INF/templates/shop.xhtml` — provides the `content` template.
- `/WEB-INF/includes/payment-hints.xhtml` — included payment hints.
- `checkoutBean` — backing data and actions.

#### Cross-cutting
- Jakarta Faces Facelets page using HTML, core, and Facelets namespaces, with EL-bound data and actions.
- Uses Faces validation, AJAX rendering, and number conversion for the total.
