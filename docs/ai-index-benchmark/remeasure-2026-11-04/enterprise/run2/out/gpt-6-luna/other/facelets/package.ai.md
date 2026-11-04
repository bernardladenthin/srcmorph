### other/facelets
- H: 1.0
- C: 2CC30F84
- D: 2026-11-04T06:55:10Z
- T: 2026-11-04T08:15:19Z
- G: 
- A: 0.0.0
- X: package
- F: [checkout.xhtml](checkout.xhtml.ai.md)
---
> Customers provide delivery and shipping details, review an updated EUR order total, and submit checkout.

#### Purpose
- Provide the customer checkout form for delivery details, shipping selection, total review, and order submission.

#### Responsibilities
- Collect street and ZIP code, offer shipping methods, and display the order total.
- Recalculate the total when shipping changes and submit the order from `checkoutForm`.

#### Key units
- `checkout.xhtml` — Facelets composition defining the checkout form and its interactions with `checkoutBean`.

#### Data flow
- `checkoutBean.shippingMethods` populates the shipping selector; the selection is stored in `checkoutBean.shippingMethod`.
- Changing shipping invokes `checkoutBean.recalculate` via Ajax and rerenders `total`, displaying `checkoutBean.total` as EUR currency.
- The form binds street and ZIP to `checkoutBean.address` and submits through `checkoutBean.placeOrder`; ZIP input is validated against `[0-9]{5}`.

#### Dependencies
- `checkoutBean` fields and methods: `address.street`, `address.zip`, `shippingMethod`, `shippingMethods`, `total`, `recalculate`, `placeOrder`.
- Facelets template `/WEB-INF/templates/shop.xhtml` and include `/WEB-INF/includes/payment-hints.xhtml`.

#### Cross-cutting
- Uses Jakarta Faces HTML, core, and Facelets namespaces; combines server-side form submission with Ajax recalculation.
- Applies ZIP regex validation and EUR currency formatting to the displayed total.
