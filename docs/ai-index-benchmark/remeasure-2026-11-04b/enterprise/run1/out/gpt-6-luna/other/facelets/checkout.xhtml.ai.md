### checkout.xhtml
- H: 1.0
- C: A3D4AA3E
- D: 2026-11-04T06:55:10Z
- T: 2026-11-04T12:22:40Z
- G: 
- A: 0.0.0
- X: file
- P: C9F70393
- N: 2F819391
---
**Facts (exact, whole file):** elements: 14; namespace declarations: 4; id and name attributes: 6; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0; EL expressions: 7; forms: 1; input fields: 4; EL method bindings: 2; templates and includes: 2; composite component interfaces: 0

> The checkout page collects a delivery address and shipping method, updates the euro total, and submits the order.

#### Purpose
- Lets the customer enter checkout details and place an order.

#### Type
- Jakarta Faces Facelets page; XHTML.
- `h` = Jakarta Faces HTML; `f` = Jakarta Faces core; `ui` = Jakarta Faces Facelets.

#### Data
- `checkoutBean.address.street` — street input.
- `checkoutBean.address.zip` — ZIP input.
- `checkoutBean.shippingMethod` — selected shipping method.
- `checkoutBean.shippingMethods` — options for the shipping selector.
- `checkoutBean.total` — displayed order total.

#### Actions
- Form `checkoutForm` — submits checkout fields using the default Faces form method.
- `checkoutBean.recalculate` — AJAX listener when `shipping` changes; renders `total`.
- `checkoutBean.placeOrder` — command button action for “Place order”.
- `zip` — regex validator `[0-9]{5}`.
- `total` — number converter formatted as currency with currency code `EUR`.

#### Structure
- Uses `/WEB-INF/templates/shop.xhtml`; defines `content`.
- Includes `/WEB-INF/includes/payment-hints.xhtml`.
- An HTML comment contains a commented-out “Save cart” command for `checkoutBean.saveCart`.

#### Dependencies
- `/WEB-INF/templates/shop.xhtml`
- `/WEB-INF/includes/payment-hints.xhtml`
- `checkoutBean`
