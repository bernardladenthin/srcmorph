### checkout.xhtml
- H: 1.0
- C: A3D4AA3E
- D: 2026-11-04T06:55:10Z
- T: 2026-11-04T12:29:08Z
- G: 
- A: 0.0.0
- X: file
- P: C9F70393
- N: 2F819391
---
**Facts (exact, whole file):** elements: 14; namespace declarations: 4; id and name attributes: 6; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0; EL expressions: 7; forms: 1; input fields: 4; EL method bindings: 2; templates and includes: 2; composite component interfaces: 0

> Customers enter a delivery address, choose shipping, review the EUR total, and place an order.

#### Purpose
- Collect checkout address and shipping details, update the displayed total, and submit the order.

#### Type
- Facelets page; XHTML content type.
- `h` = Jakarta Faces HTML; `f` = Jakarta Faces core; `ui` = Jakarta Faces Facelets.

#### Data
- `checkoutBean.address.street` — address street, required text input.
- `checkoutBean.address.zip` — ZIP code text input.
- `checkoutBean.shippingMethod` — selected shipping method.
- `checkoutBean.shippingMethods` — collection supplying shipping menu items.
- `checkoutBean.total` — displayed order total.
- Iteration: `f:selectItems` reads `checkoutBean.shippingMethods`; no explicit iteration variable.

#### Actions
- `checkoutForm` — Faces form; no explicit action or method.
- `shipping` selection — AJAX listener `checkoutBean.recalculate`; rerenders `total`.
- `Place order` button — invokes `checkoutBean.placeOrder`.
- `zip` — regex validator `[0-9]{5}`.
- `total` — currency converter with type `currency`, currency code `EUR`.
- Commented-out `Save cart` button is not active.

#### Structure
- Uses template `/WEB-INF/templates/shop.xhtml`; defines `content`.
- Includes `/WEB-INF/includes/payment-hints.xhtml`.

#### Dependencies
- Tag libraries: `jakarta.faces.html`, `jakarta.faces.core`, `jakarta.faces.facelets`.
- Template: `/WEB-INF/templates/shop.xhtml`.
- Included file: `/WEB-INF/includes/payment-hints.xhtml`.
- Bean: `checkoutBean`; methods: `recalculate`, `placeOrder`.
