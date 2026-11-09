### other/facelets
- H: 1.0
- C: 2CC30F84
- D: 2026-11-04T06:55:10Z
- T: 2026-11-04T08:08:47Z
- G: 
- A: 0.0.0
- X: package
- F: [checkout.xhtml](checkout.xhtml.ai.md)
---
> Collects a customer’s delivery details and shipping choice, updates the order total, and submits the order.

#### Purpose
- Capture checkout address and shipping method.
- Display the EUR total and let the customer place an order.

#### Responsibilities
- `checkout.xhtml` provides the checkout form, validates the five-digit postal code, and formats the total as EUR.
- Shipping-method changes trigger recalculation and update the displayed total through Ajax.

#### Key units
- `checkout.xhtml` — Jakarta Faces Facelets page defining the checkout form and `content` template section.
- `checkoutBean` — supplies address, shipping options, selected method, and total; handles recalculation and order placement.

#### Data flow
- The customer enters street and ZIP code and selects a shipping method; the page binds these values to `checkoutBean`.
- Shipping selection invokes `checkoutBean.recalculate` and rerenders the total; the place-order button invokes `checkoutBean.placeOrder`.

#### Dependencies
- `/WEB-INF/templates/shop.xhtml` — page template.
- `/WEB-INF/includes/payment-hints.xhtml` — included payment guidance.
- Jakarta Faces HTML, core, and Facelets namespaces.

#### Cross-cutting
- Postal-code validation uses `f:validateRegex` with `[0-9]{5}`; total display uses `f:convertNumber` with currency `EUR`.
- A commented-out button references `checkoutBean.saveCart`; it is not active markup.
