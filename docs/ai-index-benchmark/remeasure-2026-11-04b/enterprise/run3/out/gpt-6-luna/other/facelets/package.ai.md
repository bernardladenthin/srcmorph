### other/facelets
- H: 1.0
- C: 2CC30F84
- D: 2026-11-04T06:55:10Z
- T: 2026-11-04T12:39:39Z
- G: 
- A: 0.0.0
- X: package
- F: [checkout.xhtml](checkout.xhtml.ai.md)
---
> Collects delivery details and shipping preferences, recalculates the order total, and submits the order for checkout.

#### Purpose
- Provide the checkout form for entering an address and choosing a shipping method.
- Show the total in euros and payment hints.

#### Responsibilities
- Address and shipping input, including ZIP-code validation and shipping-method selection.
- Order-total display and recalculation when the shipping method changes.
- Order submission through the checkout form.

#### Key units
- `checkout.xhtml` — Facelets page binding delivery, shipping, and total fields to `checkoutBean`.

#### Data flow
- Form inputs bind to `checkoutBean.address.street`, `checkoutBean.address.zip`, and `checkoutBean.shippingMethod`; shipping options come from `checkoutBean.shippingMethods`.
- Changing the shipping method invokes `checkoutBean.recalculate` via Ajax and rerenders the total.
- The displayed `checkoutBean.total` is formatted as EUR; submitting invokes `checkoutBean.placeOrder`.

#### Dependencies
- `checkoutBean` — supplies checkout data and actions.
- `/WEB-INF/templates/shop.xhtml` — page template providing the `content` insertion point.
- `/WEB-INF/includes/payment-hints.xhtml` — included payment hints.
- Jakarta Faces HTML, core, and Facelets namespaces.

#### Cross-cutting
- ZIP input uses `f:validateRegex` with the five-digit pattern `[0-9]{5}`.
- Uses Facelets templating and an included fragment; the “Save cart” button is commented out and inactive.
