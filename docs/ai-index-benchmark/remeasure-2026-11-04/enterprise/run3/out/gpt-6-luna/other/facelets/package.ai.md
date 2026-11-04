### other/facelets
- H: 1.0
- C: 2CC30F84
- D: 2026-11-04T06:55:10Z
- T: 2026-11-04T08:21:32Z
- G: 
- A: 0.0.0
- X: package
- F: [checkout.xhtml](checkout.xhtml.ai.md)
---
> The checkout flow collects delivery details and shipping preferences, recalculates the order total, and submits the order.

#### Purpose
- Provide a customer checkout form for address entry, shipping selection, total review, and order placement.

#### Responsibilities
- Capture required street and ZIP details and bind them to `checkoutBean.address`.
- Present shipping options, refresh the EUR-formatted total, and invoke order placement.
- Supply checkout content through the shop template and include payment hints.

#### Key units
- `checkout.xhtml` — Jakarta Faces Facelets checkout page with the `checkoutForm`, address inputs, shipping menu, total display, and place-order action.
- `checkoutBean` — supplies address and shipping data, total, and the `recalculate` and `placeOrder` actions.

#### Data flow
- Customer inputs bind to `checkoutBean.address.street`, `checkoutBean.address.zip`, and `checkoutBean.shippingMethod`.
- Changing the shipping selection invokes `checkoutBean.recalculate` and re-renders `total`, formatted as EUR currency.
- Submitting the form invokes `checkoutBean.placeOrder`.

#### Dependencies
- `/WEB-INF/templates/shop.xhtml` — provides the page template.
- `/WEB-INF/includes/payment-hints.xhtml` — included in the checkout content.
- `checkoutBean` — backing bean for form data and actions.
