### checkout.xhtml
- H: 1.0
- C: A3D4AA3E
- D: 2026-11-04T06:55:10Z
- T: 2026-11-04T08:23:12Z
- G: 
- A: 0.0.0
- X: file
- P: F6DC35F6
- N: 2F819391
---
**Facts (exact, whole file):** elements: 14; namespace declarations: 4; id and name attributes: 6; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0; EL expressions: 7; forms: 1; input fields: 4; EL method bindings: 2; templates and includes: 2; composite component interfaces: 0

> A checkout page that collects shipping address, selects shipping method, shows total, and places an order.

#### Purpose
- Collect shipping address and method.
- Display order total.
- Submit order.

#### Type
- Facelets page (`checkout.xhtml`).
- Uses template `/WEB-INF/templates/shop.xhtml`.
- Tag libraries: `h` = `jakarta.faces.html`, `f` = `jakarta.faces.core`, `ui` = `jakarta.faces.facelets`.

#### Data
- `#{checkoutBean.address.street}` (required).
- `#{checkoutBean.address.zip}` (validated by regex `[0-9]{5}`).
- `#{checkoutBean.shippingMethod}`.
- `#{checkoutBean.shippingMethods}` (select items).
- `#{checkoutBean.total}` (converted to currency EUR).
- Includes `/WEB-INF/includes/payment-hints.xhtml`.

#### Actions
- Form `checkoutForm` submits to `#{checkoutBean.placeOrder}` via `h:commandButton`.
- Ajax on `shipping` menu: listener `#{checkoutBean.recalculate}`, renders component `total`.

#### Structure
- Defines `content` region of template.
- Includes payment hints fragment.

#### Server code
- No scriptlets or expressions beyond EL.

#### Dependencies
- Tag libraries: `jakarta.faces.html`, `jakarta.faces.core`, `jakarta.faces.facelets`.
- Template: `/WEB-INF/templates/shop.xhtml`.
- Include: `/WEB-INF/includes/payment-hints.xhtml`.
