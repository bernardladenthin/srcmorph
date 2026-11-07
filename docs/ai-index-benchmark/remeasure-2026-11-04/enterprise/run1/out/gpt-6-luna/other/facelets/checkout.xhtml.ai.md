### checkout.xhtml
- H: 1.0
- C: A3D4AA3E
- D: 2026-11-04T06:55:10Z
- T: 2026-11-04T08:07:07Z
- G: 
- A: 0.0.0
- X: file
- P: F6DC35F6
- N: 2F819391
---
**Facts (exact, whole file):** elements: 14; namespace declarations: 4; id and name attributes: 6; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0; EL expressions: 7; forms: 1; input fields: 4; EL method bindings: 2; templates and includes: 2; composite component interfaces: 0

> The checkout page collects a delivery address and shipping method, shows the calculated EUR total, and lets the customer place an order.

#### Purpose
- Capture checkout address and shipping selection.
- Recalculate the displayed total when the shipping method changes; submit the order.

#### Type
- Jakarta Faces Facelets page; XHTML.
- Namespaces: `h` = Jakarta Faces HTML, `f` = Jakarta Faces core, `ui` = Jakarta Faces Facelets; default namespace = XHTML.

#### Data
- `checkoutBean.address.street` — street address.
- `checkoutBean.address.zip` — postal code.
- `checkoutBean.shippingMethod` — selected shipping method.
- `checkoutBean.shippingMethods` — options for the shipping selector.
- `checkoutBean.total` — displayed order total.

#### Actions
- `checkoutForm` — Faces form; no explicit action or method.
- Place order button invokes `checkoutBean.placeOrder`.
- Shipping selector invokes `checkoutBean.recalculate` through Ajax and rerenders `total`.
- Postal code uses `f:validateRegex` with pattern `[0-9]{5}`.
- Total uses `f:convertNumber` with currency type and `EUR`.
- An HTML-commented button references `checkoutBean.saveCart`; it is commented markup.

#### Structure
- Uses template `/WEB-INF/templates/shop.xhtml`; defines `content`.
- Includes `/WEB-INF/includes/payment-hints.xhtml`.

#### Dependencies
- `checkoutBean`
- `/WEB-INF/templates/shop.xhtml`
- `/WEB-INF/includes/payment-hints.xhtml`
